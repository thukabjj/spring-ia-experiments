package com.techisthoughts.ia.movieclassification.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Domain entity representing a chunk of movies for processing
 */
public class MovieChunk {

    private final String chunkId;
    private final ChunkType chunkType;
    private final String content;
    private final List<String> movieTitles;
    private final String genre;
    private final String ratingRange;
    private final String yearRange;
    private final int movieCount;
    private final String metadata;

    public MovieChunk(String chunkId, ChunkType chunkType, String content, List<String> movieTitles,
                      String genre, String ratingRange, String yearRange, int movieCount, String metadata) {
        this.chunkId = Objects.requireNonNull(chunkId, "Chunk ID cannot be null");
        this.chunkType = Objects.requireNonNull(chunkType, "Chunk type cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.movieTitles = movieTitles != null ? List.copyOf(movieTitles) : List.of();
        this.genre = genre;
        this.ratingRange = ratingRange;
        this.yearRange = yearRange;
        this.movieCount = Math.max(0, movieCount);
        this.metadata = metadata;
    }

    // Getters
    public String getChunkId() { return chunkId; }
    public ChunkType getChunkType() { return chunkType; }
    public String getContent() { return content; }
    public List<String> getMovieTitles() { return movieTitles; }
    public String getGenre() { return genre; }
    public String getRatingRange() { return ratingRange; }
    public String getYearRange() { return yearRange; }
    public int getMovieCount() { return movieCount; }
    public String getMetadata() { return metadata; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieChunk chunk = (MovieChunk) o;
        return Objects.equals(chunkId, chunk.chunkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkId);
    }

    @Override
    public String toString() {
        return "MovieChunk{" +
                "chunkId='" + chunkId + '\'' +
                ", chunkType=" + chunkType +
                ", movieCount=" + movieCount +
                '}';
    }

    /**
     * Chunk types for different chunking strategies
     */
    public enum ChunkType {
        SINGLE_MOVIE("single_movie"),
        BY_GENRE("genre_group"),
        FIXED_SIZE("fixed_size");

        private final String value;

        ChunkType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
