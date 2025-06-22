package com.techisthoughts.ia.movieclassification.infrastructure.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.techisthoughts.ia.movieclassification.domain.port.LLMServicePort;
import com.techisthoughts.ia.movieclassification.infrastructure.adapter.OptimizedOllamaLLMService;

/**
 * Infrastructure configuration for high-performance setup
 */
@Configuration
public class InfrastructureConfiguration {

    /**
     * High-performance LLM service with parallel processing
     */
    @Bean
    @Primary
    public LLMServicePort optimizedLLMService(EmbeddingModel embeddingModel, OllamaChatModel chatModel) {
        return new OptimizedOllamaLLMService(embeddingModel, chatModel);
    }


}
