package com.techisthoughts.ia.movieclassification.infrastructure.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import com.techisthoughts.ia.movieclassification.domain.port.LLMServicePort;

/**
 * Ollama implementation of LLMServicePort using Spring AI
 */
@Service
public class OllamaLLMService implements LLMServicePort {

    private static final Logger logger = LoggerFactory.getLogger(OllamaLLMService.class);

    private final EmbeddingModel embeddingModel;
    private final OllamaChatModel chatModel;

    public OllamaLLMService(EmbeddingModel embeddingModel, OllamaChatModel chatModel) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
    }

        @Override
    public List<Double> createEmbedding(String text) {
        try {
            logger.debug("Creating embedding for text of length: {}", text.length());

            EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));

            if (response.getResults().isEmpty()) {
                logger.warn("No embedding results returned for text");
                return List.of();
            }

            // Convert float[] to List<Double>
            float[] embedding = response.getResults().get(0).getOutput();
            List<Double> doubleEmbedding = new ArrayList<>();
            for (float f : embedding) {
                doubleEmbedding.add((double) f);
            }

            logger.debug("Created embedding with dimension: {}", doubleEmbedding.size());
            return doubleEmbedding;

        } catch (Exception e) {
            logger.error("Error creating embedding for text", e);
            return List.of();
        }
    }

        @Override
    public List<List<Double>> createEmbeddings(List<String> texts) {
        try {
            logger.debug("Creating embeddings for {} texts", texts.size());

            if (texts.isEmpty()) {
                return List.of();
            }

            // Process in smaller batches to avoid timeout
            final int BATCH_SIZE = 10;
            List<List<Double>> allEmbeddings = new ArrayList<>();

            for (int i = 0; i < texts.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, texts.size());
                List<String> batch = texts.subList(i, endIndex);

                logger.debug("Processing embedding batch {}/{} with {} texts",
                           (i / BATCH_SIZE) + 1, (texts.size() + BATCH_SIZE - 1) / BATCH_SIZE, batch.size());

                EmbeddingResponse response = embeddingModel.embedForResponse(batch);

                List<List<Double>> batchEmbeddings = response.getResults().stream()
                        .map(result -> {
                            float[] embedding = result.getOutput();
                            List<Double> doubleEmbedding = new ArrayList<>();
                            for (float f : embedding) {
                                doubleEmbedding.add((double) f);
                            }
                            return doubleEmbedding;
                        })
                        .collect(Collectors.toList());

                allEmbeddings.addAll(batchEmbeddings);
            }

            logger.debug("Successfully created {} embeddings", allEmbeddings.size());
            return allEmbeddings;

        } catch (Exception e) {
            logger.error("Error creating embeddings for multiple texts", e);
            return texts.stream().map(text -> List.<Double>of()).collect(Collectors.toList());
        }
    }

    @Override
    public List<String> generateQuestions(String content, int questionCount) {
        try {
            String prompt = String.format("""
                Based on the following content, generate exactly %d questions that could be answered using this information.

                Content:
                %s

                Please provide only the questions, one per line, ending with a question mark.
                """, questionCount, content);

            String response = chat(prompt);

            return Arrays.stream(response.split("\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && line.endsWith("?"))
                    .limit(questionCount)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error generating questions", e);
            return List.of();
        }
    }

    @Override
    public String chat(String prompt) {
        try {
            logger.debug("Sending chat request, prompt length: {}", prompt.length());

            String response = chatModel.call(prompt);

            logger.debug("Received chat response, length: {}", response.length());
            return response;

        } catch (Exception e) {
            logger.error("Error in chat request", e);
            return "Error: Unable to process request";
        }
    }

    @Override
    public String generateStructuredResponse(String prompt, String format) {
        try {
            String enhancedPrompt = String.format("""
                %s

                Please format your response as %s. Be precise and follow the requested format exactly.
                """, prompt, format);

            return chat(enhancedPrompt);

        } catch (Exception e) {
            logger.error("Error generating structured response", e);
            return "{}";
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            // Simple test to check if the service is available
            String testResponse = chat("Hello");
            return testResponse != null && !testResponse.trim().isEmpty() && !testResponse.startsWith("Error:");
        } catch (Exception e) {
            logger.warn("LLM service is not available", e);
            return false;
        }
    }
}
