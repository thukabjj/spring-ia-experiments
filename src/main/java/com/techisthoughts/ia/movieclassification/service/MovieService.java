package com.techisthoughts.ia.movieclassification.service;

import com.redis.om.spring.search.stream.EntityStream;
import com.techisthoughts.ia.movieclassification.controller.MovieResponse;
import com.techisthoughts.ia.movieclassification.repository.MovieRepository;
import com.techisthoughts.ia.movieclassification.repository.entity.MovieEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final EntityStream entityStream; // Not used in the provided methods, but kept
    private final ChatClient chatClient;     // Kept, createSummary method is retained
    private final EmbeddingModel embeddingModel;

    // Define a batch size for embedding requests
    private static final int EMBEDDING_BATCH_SIZE = 50; // Adjust as needed

    public MovieService(
            MovieRepository movieRepository,
            FileService fileService,
            EntityStream entityStream,
            ChatClient chatClient,
            EmbeddingModel embeddingModel) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
        this.entityStream = entityStream;
        this.chatClient = chatClient;
        this.embeddingModel = embeddingModel;
    }

    // Helper method to create the text to be embedded from a Movie
    private String createTextForEmbedding(Movie record) {
        // Concatenate relevant fields to create a rich text for embedding.
        // You can adjust the format and fields included.
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

    public void loadMovieData(String filePath) {
        LOG.info("Loading movie data from file: {}", filePath);
        List<Movie> records = fileService.readMoviesFromCsv(filePath);
        if (records.isEmpty()) {
            LOG.info("No records found in the file: {}", filePath);
            return;
        }

        LOG.info("Preparing to process {} movie records for embedding and saving.", records.size());
        List<MovieEntity> allEntities = new ArrayList<>();

        for (int i = 0; i < records.size(); i += EMBEDDING_BATCH_SIZE) {
            List<Movie> batchRecords = records.subList(i, Math.min(i + EMBEDDING_BATCH_SIZE, records.size()));
            LOG.info("Processing batch of {} records (from index {} to {})", batchRecords.size(), i, Math.min(i + EMBEDDING_BATCH_SIZE, records.size()) -1);

            List<String> textsToEmbed = batchRecords.stream()
                    .map(this::createTextForEmbedding)
                    .collect(Collectors.toList());

            LOG.info("Generating embeddings for batch of {} texts.", textsToEmbed.size());

            OllamaOptions ollamaOptions = OllamaOptions.builder()
                    .model("nomic-embed-text")
                    .build();
            // Using null for EmbeddingOptions to use defaults
            EmbeddingRequest embeddingRequest = new EmbeddingRequest(textsToEmbed, ollamaOptions);
            EmbeddingResponse embeddingResponse = this.embeddingModel.call(embeddingRequest);

            List<Embedding> embeddings = embeddingResponse.getResults();
            if (embeddings.size() != batchRecords.size()) {
                LOG.warn("Mismatch in batch size and embeddings received. Expected: {}, Got: {}", batchRecords.size(), embeddings.size());
                // Handle error or skip this batch if necessary
                continue;
            }

            List<MovieEntity> batchEntities = new ArrayList<>();
            for (int j = 0; j < batchRecords.size(); j++) {
                Movie record = batchRecords.get(j);
                String textEmbedded = textsToEmbed.get(j); // The actual text that was embedded
                float[] vectorValues = embeddings.get(j).getOutput(); // Assuming getOutput() returns float[]
                byte[] embeddingBytes = convertToBytes(vectorValues);

                LOG.debug("Generated embedding for movie: '{}'. Vector length: {}. Byte array length: {}", record.movieTitle(), vectorValues.length, embeddingBytes.length);

                batchEntities.add(new MovieEntity(
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
                ));
            }
            allEntities.addAll(batchEntities);
        }

        if (!allEntities.isEmpty()) {
            LOG.info("Saving {} movie entities to the repository.", allEntities.size());
            movieRepository.saveAll(allEntities);
            LOG.info("Successfully saved {} movie entities.", allEntities.size());
        } else {
            LOG.info("No entities were processed to be saved.");
        }
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


    public List<MovieResponse> getAllMovies() {
        LOG.info("Fetching all movies");
        return movieRepository.findAll().stream().map(
                movieEntity -> new MovieResponse(
                        movieEntity.movieTitle(),
                        movieEntity.genre(),
                        movieEntity.releaseYear(),
                        movieEntity.averageRating(),
                        movieEntity.numberOfReviews(),
                        movieEntity.reviewHighlights(),
                        movieEntity.minuteOfLifeChangingInsight(),
                        movieEntity.howDiscovered(),
                        movieEntity.meaningfulAdviceTaken(),
                        movieEntity.isSuggestedToFriendsFamily(),
                        movieEntity.percentageSuggestedToFriendsFamily()
                )
        ).collect(Collectors.toList());
    }
}