package com.techisthoughts.ia.movieclassification.domain.port;

import java.util.List;
import java.util.Map;

/**
 * Port interface for Vector database operations
 */
public interface VectorDatabasePort {

    /**
     * Store embedding with metadata
     */
    void storeEmbedding(String id, List<Double> embedding, Map<String, Object> metadata);

    /**
     * Store multiple embeddings
     */
    void storeEmbeddings(Map<String, EmbeddingData> embeddings);

    /**
     * Find similar embeddings
     */
    List<SimilarityResult> findSimilar(List<Double> queryEmbedding, int limit);

    /**
     * Find similar embeddings with filter
     */
    List<SimilarityResult> findSimilar(List<Double> queryEmbedding, int limit, Map<String, Object> filter);

    /**
     * Get embedding by ID
     */
    EmbeddingData getEmbedding(String id);

    /**
     * Delete embedding by ID
     */
    void deleteEmbedding(String id);

    /**
     * Delete all embeddings
     */
    void deleteAll();

    /**
     * Count total embeddings
     */
    long count();

    /**
     * Data class for embedding storage
     */
    record EmbeddingData(String id, List<Double> embedding, Map<String, Object> metadata) {}

    /**
     * Data class for similarity search results
     */
    record SimilarityResult(String id, double similarity, Map<String, Object> metadata) {}
}
