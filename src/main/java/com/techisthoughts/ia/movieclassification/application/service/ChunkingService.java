package com.techisthoughts.ia.movieclassification.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.techisthoughts.ia.movieclassification.domain.model.Movie;
import com.techisthoughts.ia.movieclassification.domain.model.MovieChunk;

/**
 * Application service for chunking movies
 */
@Service
public class ChunkingService {

    private static final Logger logger = LoggerFactory.getLogger(ChunkingService.class);
    private static final int DEFAULT_CHUNK_SIZE = 5;
    private static final int MAX_CONTENT_LENGTH = 2000;

    /**
     * Create chunks from movies using the specified strategy
     */
    public List<MovieChunk> createChunks(List<Movie> movies, MovieChunk.ChunkType chunkType) {
        logger.info("Creating chunks using strategy: {} for {} movies", chunkType, movies.size());

        return switch (chunkType) {
            case SINGLE_MOVIE -> createSingleMovieChunks(movies);
            case BY_GENRE -> createGenreBasedChunks(movies);
            case FIXED_SIZE -> createFixedSizeChunks(movies);
        };
    }

    private List<MovieChunk> createSingleMovieChunks(List<Movie> movies) {
        return movies.stream()
                .map(this::createSingleMovieChunk)
                .collect(Collectors.toList());
    }

    private MovieChunk createSingleMovieChunk(Movie movie) {
        String chunkId = "single_" + sanitizeId(movie.getMovieTitle()) + "_" + UUID.randomUUID().toString().substring(0, 8);
        String content = buildMovieContent(movie);
        String metadata = createMetadata(Map.of(
            "strategy", "single_movie",
            "movieTitle", movie.getMovieTitle()
        ));

        return new MovieChunk(
            chunkId,
            MovieChunk.ChunkType.SINGLE_MOVIE,
            content,
            List.of(movie.getMovieTitle()),
            movie.getGenre(),
            getRatingRange(parseRating(movie.getAverageRating())),
            getYearRange(parseYear(movie.getReleaseYear())),
            1,
            metadata
        );
    }

    private List<MovieChunk> createGenreBasedChunks(List<Movie> movies) {
        Map<String, List<Movie>> genreGroups = movies.stream()
                .collect(Collectors.groupingBy(movie ->
                    movie.getGenre() != null ? movie.getGenre() : "Unknown"));

        return genreGroups.entrySet().stream()
                .map(entry -> createGenreChunk(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private MovieChunk createGenreChunk(String genre, List<Movie> movies) {
        String chunkId = "genre_" + sanitizeId(genre) + "_" + UUID.randomUUID().toString().substring(0, 8);
        String content = buildGenreContent(genre, movies);
        List<String> movieTitles = movies.stream()
                .map(Movie::getMovieTitle)
                .collect(Collectors.toList());
        String metadata = createMetadata(Map.of(
            "strategy", "by_genre",
            "genre", genre,
            "movieCount", movies.size()
        ));

        return new MovieChunk(
            chunkId,
            MovieChunk.ChunkType.BY_GENRE,
            content,
            movieTitles,
            genre,
            calculateRatingRange(movies),
            calculateYearRange(movies),
            movies.size(),
            metadata
        );
    }

    private List<MovieChunk> createFixedSizeChunks(List<Movie> movies) {
        List<MovieChunk> chunks = new ArrayList<>();

        for (int i = 0; i < movies.size(); i += DEFAULT_CHUNK_SIZE) {
            int end = Math.min(i + DEFAULT_CHUNK_SIZE, movies.size());
            List<Movie> chunkMovies = movies.subList(i, end);
            chunks.add(createFixedSizeChunk(chunkMovies, (i / DEFAULT_CHUNK_SIZE) + 1));
        }

        return chunks;
    }

    private MovieChunk createFixedSizeChunk(List<Movie> movies, int chunkNumber) {
        String chunkId = "fixed_" + chunkNumber + "_" + UUID.randomUUID().toString().substring(0, 8);
        String content = buildFixedSizeContent(movies, chunkNumber);
        List<String> movieTitles = movies.stream()
                .map(Movie::getMovieTitle)
                .collect(Collectors.toList());
        String metadata = createMetadata(Map.of(
            "strategy", "fixed_size",
            "chunkNumber", chunkNumber,
            "movieCount", movies.size()
        ));

        return new MovieChunk(
            chunkId,
            MovieChunk.ChunkType.FIXED_SIZE,
            content,
            movieTitles,
            findDominantGenre(movies),
            calculateRatingRange(movies),
            calculateYearRange(movies),
            movies.size(),
            metadata
        );
    }

    // Content building methods
    private String buildMovieContent(Movie movie) {
        StringBuilder content = new StringBuilder();
        content.append("Movie: ").append(movie.getMovieTitle()).append("\n");

        if (movie.getGenre() != null) content.append("Genre: ").append(movie.getGenre()).append("\n");
        if (movie.getAverageRating() != null) content.append("Rating: ").append(movie.getAverageRating()).append("\n");
        if (movie.getReleaseYear() != null) content.append("Year: ").append(movie.getReleaseYear()).append("\n");
        if (movie.getNumberOfReviews() != null) content.append("Reviews: ").append(movie.getNumberOfReviews()).append("\n");

        if (movie.getReviewHighlights() != null && !movie.getReviewHighlights().isEmpty()) {
            content.append("Highlights: ").append(String.join(", ", movie.getReviewHighlights())).append("\n");
        }

        if (movie.getMinuteOfLifeChangingInsight() != null) {
            content.append("Life Insight: ").append(movie.getMinuteOfLifeChangingInsight()).append("\n");
        }

        return truncateContent(content.toString());
    }

    private String buildGenreContent(String genre, List<Movie> movies) {
        StringBuilder content = new StringBuilder();
        content.append("Genre Collection: ").append(genre).append("\n");
        content.append("Total Movies: ").append(movies.size()).append("\n\n");

        for (Movie movie : movies) {
            content.append("- ").append(movie.getMovieTitle());
            if (movie.getAverageRating() != null) content.append(" (").append(movie.getAverageRating()).append(")");
            if (movie.getReleaseYear() != null) content.append(" [").append(movie.getReleaseYear()).append("]");
            content.append("\n");
        }

        return truncateContent(content.toString());
    }

    private String buildFixedSizeContent(List<Movie> movies, int chunkNumber) {
        StringBuilder content = new StringBuilder();
        content.append("Movie Collection - Chunk ").append(chunkNumber).append("\n");
        content.append("Movies: ").append(movies.size()).append("\n\n");

        for (Movie movie : movies) {
            content.append("- ").append(movie.getMovieTitle());
            if (movie.getGenre() != null) content.append(" (").append(movie.getGenre()).append(")");
            content.append("\n");
        }

        return truncateContent(content.toString());
    }

    // Utility methods
    private String getRatingRange(Double rating) {
        if (rating == null) return "Unknown";
        if (rating >= 9.0) return "9.0-10.0";
        if (rating >= 8.0) return "8.0-8.9";
        if (rating >= 7.0) return "7.0-7.9";
        if (rating >= 6.0) return "6.0-6.9";
        return "Below 6.0";
    }

    private String getYearRange(Integer year) {
        if (year == null) return "Unknown";
        int decade = (year / 10) * 10;
        return decade + "-" + (decade + 9);
    }

    private String calculateRatingRange(List<Movie> movies) {
        List<Double> ratings = movies.stream()
                .map(movie -> parseRating(movie.getAverageRating()))
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        if (ratings.isEmpty()) return "Unknown";

        double min = ratings.get(0);
        double max = ratings.get(ratings.size() - 1);
        return String.format("%.1f-%.1f", min, max);
    }

    private String calculateYearRange(List<Movie> movies) {
        List<Integer> years = movies.stream()
                .map(movie -> parseYear(movie.getReleaseYear()))
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        if (years.isEmpty()) return "Unknown";

        int min = years.get(0);
        int max = years.get(years.size() - 1);
        return min + "-" + max;
    }

    private String findDominantGenre(List<Movie> movies) {
        return movies.stream()
                .map(Movie::getGenre)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(genre -> genre, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Mixed");
    }

    private Double parseRating(String ratingStr) {
        if (ratingStr == null || ratingStr.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(ratingStr.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseYear(String yearStr) {
        if (yearStr == null || yearStr.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(yearStr.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String sanitizeId(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private String truncateContent(String content) {
        if (content.length() <= MAX_CONTENT_LENGTH) {
            return content;
        }
        return content.substring(0, MAX_CONTENT_LENGTH - 3) + "...";
    }

    private String createMetadata(Map<String, Object> metadata) {
        // Simple JSON-like string for metadata
        return metadata.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                .collect(Collectors.joining(",", "{", "}"));
    }
}
