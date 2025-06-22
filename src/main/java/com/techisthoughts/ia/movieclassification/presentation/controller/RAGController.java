package com.techisthoughts.ia.movieclassification.presentation.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.techisthoughts.ia.movieclassification.domain.model.Movie;
import com.techisthoughts.ia.movieclassification.domain.port.LLMServicePort;
import com.techisthoughts.ia.movieclassification.domain.port.MovieRepositoryPort;
import com.techisthoughts.ia.movieclassification.domain.port.VectorDatabasePort;
import com.techisthoughts.ia.movieclassification.infrastructure.observability.MetricsService;
import io.micrometer.core.instrument.Timer;

/**
 * Advanced RAG (Retrieval-Augmented Generation) Controller
 * Provides intelligent query processing, similarity search, and response generation
 */
@RestController
@RequestMapping("/api/rag")
@CrossOrigin(origins = "*")
public class RAGController {

    private static final Logger logger = LoggerFactory.getLogger(RAGController.class);

    private final LLMServicePort llmService;
    private final MovieRepositoryPort movieRepository;
    private final VectorDatabasePort vectorDatabase;
    private final MetricsService metricsService;

    // RAG Configuration
    private static final int DEFAULT_RETRIEVAL_LIMIT = 10;
    private static final double SIMILARITY_THRESHOLD = 0.5;
    private static final int MAX_CONTEXT_LENGTH = 4000;

    public RAGController(LLMServicePort llmService,
                        MovieRepositoryPort movieRepository,
                        VectorDatabasePort vectorDatabase,
                        MetricsService metricsService) {
        this.llmService = llmService;
        this.movieRepository = movieRepository;
        this.vectorDatabase = vectorDatabase;
        this.metricsService = metricsService;
    }

    /**
     * Simple RAG query endpoint for quick testing
     */
    @GetMapping("/ask")
    public ResponseEntity<Map<String, Object>> simpleRAGQuery(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "detailed") String style) {

        Timer.Sample sample = metricsService.startEndToEndTimer();
        logger.info("Processing simple RAG query: '{}'", q);

        try {
            // Step 1: Query enrichment
            String enrichedQuery = enrichQuery(q);

            // Step 2: Retrieval
            List<VectorDatabasePort.SimilarityResult> results = performRetrieval(enrichedQuery, limit);
            List<Movie> directMatches = findDirectMatches(q);

            // Step 3: Context assembly
            String context = assembleContext(results, directMatches);

            // Step 4: Response generation
            String response = generateResponse(q, context, style);

            // Step 5: Build result
            Map<String, Object> result = Map.of(
                "success", true,
                "query", q,
                "enrichedQuery", enrichedQuery,
                "response", response,
                "sources", formatSources(results, directMatches),
                "timestamp", Instant.now().toString(),
                "chainOfThought", Arrays.asList(
                    "1. Enhanced query: " + enrichedQuery,
                    "2. Found " + results.size() + " similar items + " + directMatches.size() + " direct matches",
                    "3. Assembled context (" + context.length() + " chars)",
                    "4. Generated response using " + style + " style"
                )
            );

            metricsService.recordEndToEndProcessing(sample);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Error processing RAG query", e);
            metricsService.recordError("rag_query");
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Enhanced query processing with full RAG pipeline
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> processRAGQuery(@RequestBody Map<String, Object> request) {
        Timer.Sample sample = metricsService.startEndToEndTimer();

        String originalQuery = (String) request.get("query");
        Integer limit = (Integer) request.getOrDefault("limit", DEFAULT_RETRIEVAL_LIMIT);
        String style = (String) request.getOrDefault("responseStyle", "detailed");
        Boolean enableEnrichment = (Boolean) request.getOrDefault("enableEnrichment", true);
        Boolean enableCuration = (Boolean) request.getOrDefault("enableCuration", true);

        logger.info("Processing advanced RAG query: '{}'", originalQuery);

        try {
            // Enhanced pipeline
            String enrichedQuery = enableEnrichment ? enrichQuery(originalQuery) : originalQuery;
            List<VectorDatabasePort.SimilarityResult> results = performRetrieval(enrichedQuery, limit);
            List<Movie> directMatches = findDirectMatches(originalQuery);
            String context = assembleContext(results, directMatches);
            String rawResponse = generateResponse(originalQuery, context, style);
            String finalResponse = enableCuration ? curateResponse(rawResponse, originalQuery) : rawResponse;

            Map<String, Object> result = buildComprehensiveResponse(
                originalQuery, enrichedQuery, results, directMatches,
                context, rawResponse, finalResponse, enableEnrichment, enableCuration
            );

            metricsService.recordEndToEndProcessing(sample);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Error in advanced RAG processing", e);
            metricsService.recordError("advanced_rag");
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    private String enrichQuery(String originalQuery) {
        try {
            String prompt = String.format(
                "Enhance this movie query with relevant synonyms and film terms. Keep it concise: %s",
                originalQuery);

            Timer.Sample sample = metricsService.startQuestionGenerationTimer();
            String enhanced = llmService.generateQuestions(prompt, 1).stream()
                .findFirst()
                .orElse(originalQuery);
            metricsService.recordQuestionGeneration(sample);

            return enhanced.replaceAll("^[Qq]\\d*[:.\\s]*", "").replaceAll("\\?$", "").trim();
        } catch (Exception e) {
            logger.warn("Query enrichment failed", e);
            return originalQuery;
        }
    }

    private List<VectorDatabasePort.SimilarityResult> performRetrieval(String query, int limit) {
        try {
            Timer.Sample sample = metricsService.startEmbeddingTimer();
            List<Double> embedding = llmService.createEmbedding(query);
            metricsService.recordEmbeddingCreation(sample);

            if (embedding.isEmpty()) {
                return Collections.emptyList();
            }

            return vectorDatabase.findSimilar(embedding, limit);
        } catch (Exception e) {
            logger.error("Retrieval failed", e);
            return Collections.emptyList();
        }
    }

        private List<Movie> findDirectMatches(String query) {
        String lowerQuery = query.toLowerCase();
        return movieRepository.findAll().stream()
            .filter(movie ->
                movie.getMovieTitle().toLowerCase().contains(lowerQuery) ||
                movie.getGenre().toLowerCase().contains(lowerQuery))
            .limit(5)
            .collect(Collectors.toList());
    }

    private String assembleContext(List<VectorDatabasePort.SimilarityResult> results, List<Movie> directMatches) {
        StringBuilder context = new StringBuilder();

        // Add direct matches first
        if (!directMatches.isEmpty()) {
            context.append("=== RELEVANT MOVIES ===\n");
                         for (Movie movie : directMatches) {
                 if (context.length() < MAX_CONTEXT_LENGTH) {
                     context.append(String.format("Title: %s | Genre: %s | Insights: %s\n\n",
                         movie.getMovieTitle(), movie.getGenre(),
                         movie.getMinuteOfLifeChangingInsight() != null && movie.getMinuteOfLifeChangingInsight().length() > 200 ?
                             movie.getMinuteOfLifeChangingInsight().substring(0, 200) + "..." :
                             movie.getMinuteOfLifeChangingInsight()));
                 }
             }
        }

        // Add similarity results
        if (!results.isEmpty()) {
            context.append("=== RELATED CONTENT ===\n");
            for (VectorDatabasePort.SimilarityResult result : results) {
                if (result.similarity() >= SIMILARITY_THRESHOLD && context.length() < MAX_CONTEXT_LENGTH) {
                    context.append(String.format("[Similarity: %.2f] %s\n",
                        result.similarity(), result.id()));
                }
            }
        }

        return context.toString().trim();
    }

    private String generateResponse(String query, String context, String style) {
        String systemPrompt = getSystemPrompt(style);
        String fullPrompt = String.format("""
            %s

            CONTEXT:
            %s

            USER QUESTION: %s

            RESPONSE:""", systemPrompt, context.isEmpty() ? "No specific context available." : context, query);

        try {
            Timer.Sample sample = metricsService.startQuestionGenerationTimer();
            String response = llmService.generateQuestions(fullPrompt, 1).stream()
                .findFirst()
                .orElse("I couldn't generate a response for your query.");
            metricsService.recordQuestionGeneration(sample);

            return response.replaceAll("^[Qq]\\d*[:.\\s]*", "").trim();
        } catch (Exception e) {
            logger.error("Response generation failed", e);
            return "I apologize, but I encountered an error generating a response.";
        }
    }

    private String getSystemPrompt(String style) {
        return switch (style.toLowerCase()) {
            case "concise" -> "You are a movie expert. Provide brief, direct answers about movies.";
            case "detailed" -> "You are a comprehensive movie critic. Provide detailed, insightful responses about films.";
            case "casual" -> "You are a friendly movie enthusiast. Chat casually about movies like talking to a friend.";
            case "analytical" -> "You are a film analyst. Provide analytical, technical responses about cinema.";
            default -> "You are a knowledgeable movie expert. Provide helpful information about films.";
        };
    }

    private String curateResponse(String rawResponse, String query) {
        try {
            String prompt = String.format(
                "Improve this movie response to be more accurate and helpful for the query '%s': %s",
                query, rawResponse);

            Timer.Sample sample = metricsService.startQuestionGenerationTimer();
            String curated = llmService.generateQuestions(prompt, 1).stream()
                .findFirst()
                .orElse(rawResponse);
            metricsService.recordQuestionGeneration(sample);

            return curated.replaceAll("^[Qq]\\d*[:.\\s]*", "").trim();
        } catch (Exception e) {
            logger.warn("Response curation failed", e);
            return rawResponse;
        }
    }

    private List<Map<String, Object>> formatSources(List<VectorDatabasePort.SimilarityResult> results, List<Movie> directMatches) {
        List<Map<String, Object>> sources = new ArrayList<>();

                 for (Movie movie : directMatches) {
             sources.add(Map.of(
                 "type", "direct_match",
                 "title", movie.getMovieTitle(),
                 "genre", movie.getGenre(),
                 "confidence", 1.0
             ));
         }

        for (VectorDatabasePort.SimilarityResult result : results) {
            if (result.similarity() >= SIMILARITY_THRESHOLD) {
                sources.add(Map.of(
                    "type", "similarity_match",
                    "id", result.id(),
                    "similarity", result.similarity()
                ));
            }
        }

        return sources.stream().limit(10).collect(Collectors.toList());
    }

    private Map<String, Object> buildComprehensiveResponse(
            String originalQuery, String enrichedQuery,
            List<VectorDatabasePort.SimilarityResult> results, List<Movie> directMatches,
            String context, String rawResponse, String finalResponse,
            boolean enrichmentEnabled, boolean curationEnabled) {

        return Map.of(
            "success", true,
            "query", originalQuery,
            "response", finalResponse,
            "processing", Map.of(
                "enrichmentEnabled", enrichmentEnabled,
                "curationEnabled", curationEnabled,
                "enrichedQuery", enrichedQuery,
                "queryEnriched", !originalQuery.equals(enrichedQuery),
                "responseCurated", !rawResponse.equals(finalResponse)
            ),
            "retrieval", Map.of(
                "similarityResults", results.size(),
                "directMatches", directMatches.size(),
                "contextLength", context.length()
            ),
            "sources", formatSources(results, directMatches),
            "chainOfThought", Arrays.asList(
                "1. Query Analysis: " + (enrichmentEnabled ? "Enhanced query" : "Used original"),
                "2. Retrieval: Found " + (results.size() + directMatches.size()) + " relevant items",
                "3. Context Assembly: " + context.length() + " characters",
                "4. Response Generation: Created initial response",
                "5. Curation: " + (curationEnabled ? "Applied improvements" : "Used raw response")
            ),
            "timestamp", Instant.now().toString()
        );
    }

    /**
     * Get RAG capabilities
     */
    @GetMapping("/capabilities")
    public ResponseEntity<Map<String, Object>> getCapabilities() {
        return ResponseEntity.ok(Map.of(
            "features", Arrays.asList(
                "Query Enrichment", "Multi-Strategy Retrieval", "Context Assembly",
                "Response Generation", "Response Curation", "Source Attribution"
            ),
            "responseStyles", Arrays.asList("concise", "detailed", "casual", "analytical"),
            "endpoints", Map.of(
                "simpleQuery", "GET /api/rag/ask?q=your_question",
                "advancedQuery", "POST /api/rag/query",
                "capabilities", "GET /api/rag/capabilities"
            ),
            "configuration", Map.of(
                "defaultLimit", DEFAULT_RETRIEVAL_LIMIT,
                "similarityThreshold", SIMILARITY_THRESHOLD,
                "maxContextLength", MAX_CONTEXT_LENGTH
            )
        ));
    }
}
