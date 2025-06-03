package com.techisthoughts.ia.movieclassification.service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;
import com.techisthoughts.ia.movieclassification.repository.entity.MovieEntity;

@Service
public class EmbeddingService {

    private static final int EMBEDDING_BATCH_SIZE = 50;
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddingService.class);

    private final OllamaEmbeddingModel embeddingModel;

    public EmbeddingService(OllamaEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<MovieEntity> createEmbedding(List<Movie> movies) {
        List<MovieEntity> allEntities = new ArrayList<>();
        for (int i = 0; i < movies.size(); i += EMBEDDING_BATCH_SIZE) {
            List<Movie> batchRecords = getBatch(movies, i);
            LOG.info("Processing batch of {} records (from index {} to {})", batchRecords.size(), i, Math.min(i + EMBEDDING_BATCH_SIZE, movies.size()) - 1);

            List<String> textsToEmbed = createTextsForEmbedding(batchRecords);
            LOG.info("Generating embeddings for batch of {} texts.", textsToEmbed.size());

            List<Embedding> embeddings = generateEmbeddings(textsToEmbed);
            if (embeddings.size() != batchRecords.size()) {
                LOG.warn("Mismatch in batch size and embeddings received. Expected: {}, Got: {}", batchRecords.size(), embeddings.size());
                continue;
            }

            List<MovieEntity> batchEntities = createMovieEntities(batchRecords, textsToEmbed, embeddings);
            allEntities.addAll(batchEntities);
        }
        return allEntities;
    }

    private List<Movie> getBatch(List<Movie> movies, int startIndex) {
        return movies.subList(startIndex, Math.min(startIndex + EMBEDDING_BATCH_SIZE, movies.size()));
    }

    private List<String> createTextsForEmbedding(List<Movie> batchRecords) {
        return batchRecords.stream()
                .map(this::createTextForEmbedding)
                .collect(Collectors.toList());
    }

    private List<Embedding> generateEmbeddings(List<String> textsToEmbed) {
        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model("nomic-embed-text")
                .build();
        EmbeddingRequest embeddingRequest = new EmbeddingRequest(textsToEmbed, ollamaOptions);
        EmbeddingResponse embeddingResponse = this.embeddingModel.call(embeddingRequest);
        return embeddingResponse.getResults();
    }

    private List<MovieEntity> createMovieEntities(List<Movie> batchRecords, List<String> textsToEmbed, List<Embedding> embeddings) {
        List<MovieEntity> batchEntities = new ArrayList<>();
        for (int j = 0; j < batchRecords.size(); j++) {
            Movie record = batchRecords.get(j);
            String textEmbedded = textsToEmbed.get(j);
            float[] vectorValues = embeddings.get(j).getOutput();
            byte[] embeddingBytes = convertToBytes(vectorValues);

            LOG.debug("Generated embedding for movie: '{}'. Vector length: {}. Byte array length: {}", record.movieTitle(), vectorValues.length, embeddingBytes.length);

            batchEntities.add(createMovieEntity(record, textEmbedded, embeddingBytes));
        }
        return batchEntities;
    }

    private MovieEntity createMovieEntity(Movie record, String textEmbedded, byte[] embeddingBytes) {
        return new MovieEntity(
                record.movieTitle(),
                record.genre(),
                record.releaseYear(),
                record.averageRating(),
                record.numberOfReviews(),
                record.reviewHighlights(),
                record.minuteOfLifeChangingInsight(),
                record.howDiscovered(),
                record.meaningfulAdviceTaken(),
                record.isSuggestedToFriendsFamily(),
                record.percentageSuggestedToFriendsFamily(),
                textEmbedded,
                embeddingBytes
        );
    }

    private byte[] convertToBytes(float[] vector) {
        if (vector == null) {
            return new byte[0];
        }
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        for (float value : vector) {
            buffer.putFloat(value);
        }
        return buffer.array();
    }

    private String createTextForEmbedding(Movie record) {
        return String.format(
                "Title: %s. Genre: %s. Release Year: %s. Average Rating: %s. Number of Reviews: %s. Highlights: %s. Insight Minute: %s. Discovered Via: %s. Advice Taken: %s. Suggested to Others: %s (%s%%).",
                record.movieTitle(),
                record.genre(),
                record.releaseYear(),
                record.averageRating(),
                record.numberOfReviews(),
                record.reviewHighlights(),
                record.minuteOfLifeChangingInsight(),
                record.howDiscovered(),
                record.meaningfulAdviceTaken(),
                record.isSuggestedToFriendsFamily(),
                record.percentageSuggestedToFriendsFamily()
        );
    }
}
