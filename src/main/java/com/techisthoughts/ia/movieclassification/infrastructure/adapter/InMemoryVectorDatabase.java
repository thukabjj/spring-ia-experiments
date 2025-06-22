package com.techisthoughts.ia.movieclassification.infrastructure.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.techisthoughts.ia.movieclassification.domain.port.VectorDatabasePort;

/**
 * In-memory implementation of VectorDatabasePort with cosine similarity search
 */
@Component
public class InMemoryVectorDatabase implements VectorDatabasePort {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryVectorDatabase.class);

    private final Map<String, EmbeddingData> embeddings = new ConcurrentHashMap<>();

    @Override
    public void storeEmbedding(String id, List<Double> embedding, Map<String, Object> metadata) {
        embeddings.put(id, new EmbeddingData(id, embedding, metadata));
        logger.debug("Stored embedding with ID: {}", id);
    }

    @Override
    public void storeEmbeddings(Map<String, EmbeddingData> embeddingBatch) {
        embeddings.putAll(embeddingBatch);
        logger.info("Stored {} embeddings in batch", embeddingBatch.size());
    }

    @Override
    public List<SimilarityResult> findSimilar(List<Double> queryEmbedding, int limit) {
        return findSimilar(queryEmbedding, limit, Map.of());
    }

    @Override
    public List<SimilarityResult> findSimilar(List<Double> queryEmbedding, int limit, Map<String, Object> filter) {
        logger.debug("Finding similar embeddings, limit: {}, filter: {}", limit, filter);

        List<SimilarityResult> results = embeddings.values().stream()
                .filter(embeddingData -> matchesFilter(embeddingData.metadata(), filter))
                .map(embeddingData -> {
                    double similarity = calculateCosineSimilarity(queryEmbedding, embeddingData.embedding());
                    return new SimilarityResult(embeddingData.id(), similarity, embeddingData.metadata());
                })
                .sorted((r1, r2) -> Double.compare(r2.similarity(), r1.similarity())) // Descending order
                .limit(limit)
                .collect(Collectors.toList());

        logger.debug("Found {} similar embeddings", results.size());
        return results;
    }

    @Override
    public EmbeddingData getEmbedding(String id) {
        return embeddings.get(id);
    }

    @Override
    public void deleteEmbedding(String id) {
        embeddings.remove(id);
        logger.debug("Deleted embedding with ID: {}", id);
    }

    @Override
    public void deleteAll() {
        embeddings.clear();
        logger.info("Deleted all embeddings");
    }

    @Override
    public long count() {
        return embeddings.size();
    }

    /**
     * Calculate cosine similarity between two vectors
     */
    private double calculateCosineSimilarity(List<Double> vector1, List<Double> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }

        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (int i = 0; i < vector1.size(); i++) {
            double v1 = vector1.get(i);
            double v2 = vector2.get(i);

            dotProduct += v1 * v2;
            magnitude1 += v1 * v1;
            magnitude2 += v2 * v2;
        }

        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0; // Avoid division by zero
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    /**
     * Check if metadata matches the given filter
     */
    private boolean matchesFilter(Map<String, Object> metadata, Map<String, Object> filter) {
        if (filter.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, Object> filterEntry : filter.entrySet()) {
            String key = filterEntry.getKey();
            Object expectedValue = filterEntry.getValue();
            Object actualValue = metadata.get(key);

            if (!Objects.equals(expectedValue, actualValue)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get statistics about the vector database
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmbeddings", embeddings.size());

        // Group by type
        Map<String, Long> typeCount = embeddings.values().stream()
                .collect(Collectors.groupingBy(
                    data -> (String) data.metadata().getOrDefault("type", "unknown"),
                    Collectors.counting()
                ));
        stats.put("byType", typeCount);

        // Average vector dimension
        OptionalDouble avgDimension = embeddings.values().stream()
                .mapToInt(data -> data.embedding().size())
                .average();
        stats.put("averageDimension", avgDimension.orElse(0.0));

        return stats;
    }
}
