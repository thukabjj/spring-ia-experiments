package com.techisthoughts.ia.movieclassification.presentation.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.techisthoughts.ia.movieclassification.application.usecase.ProcessMoviesUseCase;
import com.techisthoughts.ia.movieclassification.domain.model.Movie;
import com.techisthoughts.ia.movieclassification.domain.model.MovieChunk;
import com.techisthoughts.ia.movieclassification.domain.port.LLMServicePort;
import com.techisthoughts.ia.movieclassification.domain.port.MovieRepositoryPort;
import com.techisthoughts.ia.movieclassification.domain.port.VectorDatabasePort;
import com.techisthoughts.ia.movieclassification.infrastructure.adapter.CsvMovieLoader;
import com.techisthoughts.ia.movieclassification.presentation.dto.MovieDto;

/**
 * REST Controller for movie-related operations
 */
@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private final ProcessMoviesUseCase processMoviesUseCase;
    private final MovieRepositoryPort movieRepository;
    private final VectorDatabasePort vectorDatabase;
    private final LLMServicePort llmService;
    private final CsvMovieLoader csvMovieLoader;

    public MovieController(ProcessMoviesUseCase processMoviesUseCase,
                          MovieRepositoryPort movieRepository,
                          VectorDatabasePort vectorDatabase,
                          LLMServicePort llmService,
                          CsvMovieLoader csvMovieLoader) {
        this.processMoviesUseCase = processMoviesUseCase;
        this.movieRepository = movieRepository;
        this.vectorDatabase = vectorDatabase;
        this.llmService = llmService;
        this.csvMovieLoader = csvMovieLoader;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis(),
            "movieCount", movieRepository.count(),
            "embeddingCount", vectorDatabase.count(),
            "llmAvailable", llmService.isAvailable()
        );
        return ResponseEntity.ok(status);
    }

        /**
     * Load movies from CSV file
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, Object>> loadMovies() {
        logger.info("Loading movies from CSV");

        try {
            int loadedCount = csvMovieLoader.loadMoviesFromCsv();

            Map<String, Object> response = Map.of(
                "success", true,
                "moviesLoaded", loadedCount,
                "message", "Successfully loaded " + loadedCount + " movies"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error loading movies from CSV", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to load movies: " + e.getMessage()
            ));
        }
    }

    /**
     * Get all movies
     */
    @GetMapping
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        logger.info("Getting all movies");

        List<Movie> movies = movieRepository.findAll();
        List<MovieDto> movieDtos = movies.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(movieDtos);
    }

    /**
     * Get movie by title
     */
    @GetMapping("/{title}")
    public ResponseEntity<MovieDto> getMovieByTitle(@PathVariable String title) {
        logger.info("Getting movie by title: {}", title);

        return movieRepository.findByTitle(title)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Process movies using chunking and question generation
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processMovies(
            @RequestParam(defaultValue = "FIXED_SIZE") String strategy,
            @RequestParam(defaultValue = "20") int questionsPerChunk) {

        logger.info("Processing movies with strategy: {}, questions per chunk: {}", strategy, questionsPerChunk);

        try {
            MovieChunk.ChunkType chunkType = MovieChunk.ChunkType.valueOf(strategy.toUpperCase());
            ProcessMoviesUseCase.ProcessingRequest request =
                new ProcessMoviesUseCase.ProcessingRequest(chunkType, questionsPerChunk);

            ProcessMoviesUseCase.ProcessingResult result = processMoviesUseCase.execute(request);

            Map<String, Object> response = Map.of(
                "success", result.success(),
                "chunksCreated", result.chunksCreated(),
                "questionsGenerated", result.questionsGenerated(),
                "embeddingsStored", result.embeddingsStored(),
                "message", result.message()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid strategy: {}", strategy, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Invalid strategy: " + strategy
            ));
        } catch (Exception e) {
            logger.error("Error processing movies", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Processing failed: " + e.getMessage()
            ));
        }
    }

    /**
     * Search movies using vector similarity
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {

        logger.info("Searching movies with query: '{}', limit: {}", query, limit);

        try {
            List<Double> queryEmbedding = llmService.createEmbedding(query);

            if (queryEmbedding.isEmpty()) {
                return ResponseEntity.badRequest().body(List.of());
            }

            List<VectorDatabasePort.SimilarityResult> results =
                vectorDatabase.findSimilar(queryEmbedding, limit);

            List<Map<String, Object>> response = results.stream()
                    .map(result -> Map.of(
                        "id", result.id(),
                        "similarity", result.similarity(),
                        "metadata", result.metadata()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error searching movies", e);
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    /**
     * Get statistics about the system
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.info("Getting system statistics");

        Map<String, Object> stats = Map.of(
            "movieCount", movieRepository.count(),
            "embeddingCount", vectorDatabase.count(),
            "llmAvailable", llmService.isAvailable(),
            "timestamp", System.currentTimeMillis()
        );

        return ResponseEntity.ok(stats);
    }

    /**
     * Clear all data
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearAllData() {
        logger.info("Clearing all data");

        try {
            movieRepository.deleteAll();
            vectorDatabase.deleteAll();

            Map<String, Object> response = Map.of(
                "success", true,
                "message", "All data cleared successfully"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error clearing data", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to clear data: " + e.getMessage()
            ));
        }
    }

    /**
     * Convert Movie domain object to DTO
     */
    private MovieDto toDto(Movie movie) {
        return new MovieDto(
            movie.getMovieTitle(),
            movie.getGenre(),
            movie.getReleaseYear(),
            movie.getAverageRating(),
            movie.getNumberOfReviews(),
            movie.getReviewHighlights(),
            movie.getMinuteOfLifeChangingInsight(),
            movie.getHowDiscovered(),
            movie.getMeaningfulAdviceTaken(),
            movie.getIsSuggestedToFriendsFamily(),
            movie.getPercentageSuggestedToFriendsFamily()
        );
    }
}
