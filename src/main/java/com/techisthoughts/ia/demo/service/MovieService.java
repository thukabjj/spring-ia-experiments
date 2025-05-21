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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<MovieRecord> records = fileService.readMoviesFromCsv(filePath);
        List<MovieEntity> entities = new ArrayList<>();
        records
                .forEach(record -> {
                    LOG.info("Generating summary movie title: {}", record.movieTitle());
                    // String summary = createSummary(record);
                    String summary = record.movieTitle();

                    LOG.info("Generating Embedding for movie title: {}", record.movieTitle());
                    EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(summary));
                    LOG.info("Embedding response for movie title - {}: {}",record.movieTitle(), embeddingResponse);

                    float[] vectorValues = embeddingResponse.getResult().getOutput();
                    LOG.info("Vector Size for movie title - {}: {}",record.movieTitle(), vectorValues.length);
                    byte[] embeddingBytes = convertToBytes(vectorValues);
                    LOG.info("Dimension (embedding) size for movie title - {}: {}",record.movieTitle(), embeddingBytes.length);

                    entities.add(new MovieEntity(
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
                            summary,
                            embeddingBytes
                    ));
                });


        LOG.info("Saving {} movies to the repository", entities.size());
        movieRepository.saveAll(entities);
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
                record.reviewHighlights(),
                record.minuteOfLifeChangingInsight(),
                record.howDiscovered(),
                record.meaningfulAdviceTaken(),
                record.isSuggestedToFriendsFamily(),
                record.percentageSuggestedToFriendsFamily()
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


    public List<MovieRecord> getAllMovies() {
        LOG.info("Fetching all movies");
        return movieRepository.findAll().stream().map(
                movieEntity -> new MovieRecord(
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
