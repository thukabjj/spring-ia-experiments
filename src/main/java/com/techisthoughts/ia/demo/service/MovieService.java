package com.techisthoughts.ia.demo.service;

import com.redis.om.spring.search.stream.EntityStream;
import com.techisthoughts.ia.demo.controller.MovieRecord;
import com.techisthoughts.ia.demo.repository.MovieRepository;
import com.techisthoughts.ia.demo.repository.entity.MovieEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.List;

@Service
public class MovieService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final EntityStream entityStream;
    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

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

    public void loadMovieData(String filePath) {
        LOG.info("Loading movie data from file: {}", filePath);
        fileService.readMoviesFromCsv(filePath)
                .forEach(record -> {
                    String summary = createSummary(record);

                    // Generate embedding using Spring AI's EmbeddingClient
                    EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(summary));
                    float[] vectorValues = embeddingResponse.getResult().getOutput();

                    // Convert to bytes if needed for your MovieEntity
                    byte[] embeddingBytes = convertToBytes(vectorValues);

                    MovieEntity movieEntity = new MovieEntity(
                            record.movieTitle(),
                            record.genre(),
                            record.releaseYear(),
                            record.averageRating(),
                            record.reviewHighlights(),
                            record.minuteOfLifeChangingInsight(),
                            record.howDiscovered(),
                            record.meaningfulAdviceTaken(),
                            record.suggestedToFriendsFamily(),
                            summary,
                            embeddingBytes
                    );
                    movieRepository.save(movieEntity);
                });
    }

    private byte[] convertToBytes(float[] vector) {
        ByteBuffer buffer = ByteBuffer.allocate(vector.length * Float.BYTES);
        for (float value : vector) {
            buffer.putFloat(value);
        }
        return buffer.array();
    }

    private String createSummary(MovieRecord record) {
        // Use the chat model to create a structured summary to be embedded

        String condensedContent = String.format(
                "Movie: %s, Genre: %s, Year: %s, Rating: %s, Reviews: %s, Highlights: %s, Insight Minute: %s, Discovered: %s, Advice: %s, Suggested: %s",
                record.movieTitle(),
                record.genre(),
                record.releaseYear(),
                record.averageRating(),
                record.numberOfReviews(),
                record.reviewHighlights(),
                record.minuteOfLifeChangingInsight(),
                record.howDiscovered(),
                record.meaningfulAdviceTaken(),
                record.suggestedToFriendsFamily()
        );

        String prompt = String.format(
                "Summarize the following movie data as a JSON object with fields: title, genre, year, rating, reviews, highlights, insightMinute, discovered, advice, suggested. Data: %s",
                condensedContent
        );
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }


}
