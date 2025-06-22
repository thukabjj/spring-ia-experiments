package com.techisthoughts.ia.movieclassification.domain.port;

import java.util.List;

/**
 * Port interface for LLM operations
 */
public interface LLMServicePort {

    /**
     * Generate embeddings for text
     */
    List<Double> createEmbedding(String text);

    /**
     * Generate embeddings for multiple texts
     */
    List<List<Double>> createEmbeddings(List<String> texts);

    /**
     * Generate questions from content
     */
    List<String> generateQuestions(String content, int questionCount);

    /**
     * Generate a chat response
     */
    String chat(String prompt);

    /**
     * Generate a structured response
     */
    String generateStructuredResponse(String prompt, String format);

    /**
     * Check if the LLM service is available
     */
    boolean isAvailable();
}
