package com.techisthoughts.ia.movieclassification.application.usecase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.techisthoughts.ia.movieclassification.application.service.ChunkingService;
import com.techisthoughts.ia.movieclassification.application.service.QuestionGenerationService;
import com.techisthoughts.ia.movieclassification.domain.model.Movie;
import com.techisthoughts.ia.movieclassification.domain.model.MovieChunk;
import com.techisthoughts.ia.movieclassification.domain.model.Question;
import com.techisthoughts.ia.movieclassification.domain.port.LLMServicePort;
import com.techisthoughts.ia.movieclassification.domain.port.MovieRepositoryPort;
import com.techisthoughts.ia.movieclassification.domain.port.VectorDatabasePort;

/**
 * Use case for processing movies: chunking, question generation, and embedding creation
 * Optimized for parallel processing and batch operations
 */
@Component
public class ProcessMoviesUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessMoviesUseCase.class);
    private static final int BATCH_SIZE = 10; // Process chunks in batches

    private final MovieRepositoryPort movieRepository;
    private final VectorDatabasePort vectorDatabase;
    private final LLMServicePort llmService;
    private final ChunkingService chunkingService;
    private final QuestionGenerationService questionGenerationService;

    public ProcessMoviesUseCase(MovieRepositoryPort movieRepository,
                               VectorDatabasePort vectorDatabase,
                               LLMServicePort llmService,
                               ChunkingService chunkingService,
                               QuestionGenerationService questionGenerationService) {
        this.movieRepository = movieRepository;
        this.vectorDatabase = vectorDatabase;
        this.llmService = llmService;
        this.chunkingService = chunkingService;
        this.questionGenerationService = questionGenerationService;
    }

    /**
     * Execute the complete movie processing pipeline with parallel processing
     */
    public ProcessingResult execute(ProcessingRequest request) {
        logger.info("Starting optimized movie processing pipeline with strategy: {}", request.chunkingStrategy());

        try {
            // Step 1: Load movies
            List<Movie> movies = movieRepository.findAll();
            if (movies.isEmpty()) {
                logger.warn("No movies found in repository");
                return new ProcessingResult(false, 0, 0, 0, "No movies found");
            }

            // Step 2: Create chunks
            List<MovieChunk> chunks = chunkingService.createChunks(movies, request.chunkingStrategy());
            logger.info("Created {} chunks from {} movies", chunks.size(), movies.size());

            // Step 3: Process chunks in parallel batches
            AtomicInteger totalQuestions = new AtomicInteger(0);
            AtomicInteger successfulEmbeddings = new AtomicInteger(0);

            // Split chunks into batches for parallel processing
            List<List<MovieChunk>> batches = partitionList(chunks, BATCH_SIZE);

            List<CompletableFuture<BatchResult>> futures = batches.stream()
                .map(batch -> CompletableFuture.supplyAsync(() -> processBatch(batch, request.questionsPerChunk())))
                .collect(Collectors.toList());

            // Wait for all batches to complete and aggregate results
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();

            // Aggregate results
            for (CompletableFuture<BatchResult> future : futures) {
                try {
                    BatchResult result = future.get();
                    totalQuestions.addAndGet(result.questionsGenerated);
                    successfulEmbeddings.addAndGet(result.embeddingsCreated);
                } catch (Exception e) {
                    logger.error("Error getting batch result", e);
                }
            }

            logger.info("Processing completed: {} chunks, {} questions, {} embeddings",
                       chunks.size(), totalQuestions.get(), successfulEmbeddings.get());

            return new ProcessingResult(true, chunks.size(), totalQuestions.get(),
                                      successfulEmbeddings.get(), "Processing completed successfully");

        } catch (Exception e) {
            logger.error("Error in movie processing pipeline", e);
            return new ProcessingResult(false, 0, 0, 0, "Processing failed: " + e.getMessage());
        }
    }

    /**
     * Process a batch of chunks in parallel
     */
    private BatchResult processBatch(List<MovieChunk> chunks, int questionsPerChunk) {
        logger.info("Processing batch of {} chunks", chunks.size());

        AtomicInteger questionsGenerated = new AtomicInteger(0);
        AtomicInteger embeddingsCreated = new AtomicInteger(0);

        // Process chunks in parallel within the batch
        chunks.parallelStream().forEach(chunk -> {
            try {
                // Generate questions for chunk
                List<Question> questions = questionGenerationService.generateQuestions(chunk, questionsPerChunk);
                questionsGenerated.addAndGet(questions.size());

                // Batch create embeddings for chunk and questions
                List<String> textsToEmbed = questions.stream()
                    .map(Question::toEmbeddingText)
                    .collect(Collectors.toList());
                textsToEmbed.add(0, chunk.getContent()); // Add chunk content at the beginning

                // Create embeddings in batch
                List<List<Double>> embeddings = llmService.createEmbeddings(textsToEmbed);

                if (!embeddings.isEmpty()) {
                    // Store chunk embedding
                    Map<String, Object> chunkMetadata = Map.of(
                        "type", "chunk",
                        "chunkId", chunk.getChunkId(),
                        "chunkType", chunk.getChunkType().getValue(),
                        "movieCount", chunk.getMovieCount(),
                        "genre", chunk.getGenre() != null ? chunk.getGenre() : "unknown"
                    );
                    vectorDatabase.storeEmbedding(chunk.getChunkId(), embeddings.get(0), chunkMetadata);
                    embeddingsCreated.incrementAndGet();

                    // Store question embeddings
                    for (int i = 0; i < questions.size() && i + 1 < embeddings.size(); i++) {
                        Question question = questions.get(i);
                        List<Double> questionEmbedding = embeddings.get(i + 1);

                        Map<String, Object> questionMetadata = Map.of(
                            "type", "question",
                            "questionId", question.getQuestionId(),
                            "chunkId", question.getChunkId(),
                            "questionType", question.getQuestionType().getValue(),
                            "difficulty", question.getDifficulty().getValue(),
                            "category", question.getCategory() != null ? question.getCategory() : "general"
                        );
                        vectorDatabase.storeEmbedding(question.getQuestionId(), questionEmbedding, questionMetadata);
                        embeddingsCreated.incrementAndGet();
                    }
                }

            } catch (Exception e) {
                logger.error("Error processing chunk: {}", chunk.getChunkId(), e);
            }
        });

        return new BatchResult(questionsGenerated.get(), embeddingsCreated.get());
    }

    /**
     * Partition a list into smaller sublists
     */
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        return list.stream()
            .collect(Collectors.groupingBy(item -> list.indexOf(item) / batchSize))
            .values()
            .stream()
            .collect(Collectors.toList());
    }

    /**
     * Result of processing a batch
     */
    private record BatchResult(int questionsGenerated, int embeddingsCreated) {}

    /**
     * Request for movie processing
     */
    public record ProcessingRequest(
        MovieChunk.ChunkType chunkingStrategy,
        int questionsPerChunk
    ) {
        public ProcessingRequest {
            if (chunkingStrategy == null) {
                throw new IllegalArgumentException("Chunking strategy cannot be null");
            }
            if (questionsPerChunk <= 0) {
                throw new IllegalArgumentException("Questions per chunk must be positive");
            }
        }
    }

    /**
     * Result of movie processing
     */
    public record ProcessingResult(
        boolean success,
        int chunksCreated,
        int questionsGenerated,
        int embeddingsStored,
        String message
    ) {}
}
