package com.techisthoughts.ia.movieclassification.service;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.redis.om.spring.search.stream.EntityStream;
import com.redis.om.spring.search.stream.SearchStream;
import com.redis.om.spring.tuple.Fields;
import com.redis.om.spring.tuple.Pair;
import com.redis.om.spring.vectorize.Embedder;
import com.techisthoughts.ia.movieclassification.controller.MovieResponse;
import com.techisthoughts.ia.movieclassification.controller.MovieSearchResponse;
import com.techisthoughts.ia.movieclassification.repository.MovieRepository;
import com.techisthoughts.ia.movieclassification.repository.entity.MovieEntity;

@Service
public class MovieService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final EmbeddingService embeddingService;
    private final Embedder embedder;
    private final EntityStream entityStream;

    public MovieService(MovieRepository movieRepository, FileService fileService,
            EmbeddingService embeddingService, Embedder embedder, EntityStream entityStream) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
        this.embeddingService = embeddingService;
        this.embedder = embedder;
        this.entityStream = entityStream;
    }

    public void loadMovieData(String filePath) {
        LOG.info("Loading movie data from file: {}", filePath);
        List<Movie> records = fileService.readMoviesFromCsv(filePath);

        if (records.isEmpty()) {
            LOG.info("No records found in the file: {}", filePath);
            return;
        }

        LOG.info("Preparing to process {} movie records for embedding and saving.", records.size());
        List<MovieEntity> allEntities = embeddingService.createEmbedding(records);

        saveEntities(allEntities);
    }

    private void saveEntities(List<MovieEntity> entities) {
        if (entities.isEmpty()) {
            LOG.info("No entities were processed to be saved.");
            return;
        }
        LOG.info("Saving {} movie entities to the repository.", entities.size());
        movieRepository.saveAll(entities);
        LOG.info("Successfully saved {} movie entities.", entities.size());
    }

    public List<MovieResponse> getAllMovies() {
        LOG.info("Fetching all movies");
        return movieRepository.findAll().stream().map(this::toMovieResponse)
                .collect(Collectors.toList());
    }

    private MovieResponse toMovieResponse(MovieEntity movieEntity) {
        return new MovieResponse(movieEntity.movieTitle(), movieEntity.genre(),
                movieEntity.releaseYear(), movieEntity.averageRating(),
                movieEntity.numberOfReviews(), movieEntity.reviewHighlights(),
                movieEntity.minuteOfLifeChangingInsight(), movieEntity.howDiscovered(),
                movieEntity.meaningfulAdviceTaken(), movieEntity.isSuggestedToFriendsFamily(),
                movieEntity.percentageSuggestedToFriendsFamily());
    }

    public List<MovieSearchResponse> search(byte[] embedding) {
        LOG.info("Received utterance: {}", embedding);
        SearchStream<MovieEntity> stream = entityStream.of(MovieEntity.class);
        List<Pair<MovieEntity, Double>> textsAndScores = stream
                .filter(MovieEntity$.EMBEDDED_TEXT.knn(3, embedding))
                .sorted(MovieEntity$._EMBEDDED_TEXT_SCORE)
                .map(Fields.of(MovieEntity$._THIS, MovieEntity$._EMBEDDED_TEXT_SCORE))
                .collect(Collectors.toList());

        return textsAndScores.stream()
                .map(pair -> new MovieSearchResponse(pair.getFirst().text(), pair.getSecond()))
                .toList();
    }
}

