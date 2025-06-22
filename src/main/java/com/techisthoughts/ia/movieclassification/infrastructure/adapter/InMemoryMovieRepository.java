package com.techisthoughts.ia.movieclassification.infrastructure.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import com.techisthoughts.ia.movieclassification.domain.model.Movie;
import com.techisthoughts.ia.movieclassification.domain.port.MovieRepositoryPort;

/**
 * In-memory implementation of MovieRepositoryPort
 */
@Repository
public class InMemoryMovieRepository implements MovieRepositoryPort {

    private final Map<String, Movie> movies = new ConcurrentHashMap<>();

    @Override
    public Movie save(Movie movie) {
        movies.put(movie.getMovieTitle(), movie);
        return movie;
    }

    @Override
    public List<Movie> saveAll(List<Movie> movieList) {
        movieList.forEach(movie -> movies.put(movie.getMovieTitle(), movie));
        return new ArrayList<>(movieList);
    }

    @Override
    public Optional<Movie> findByTitle(String title) {
        return Optional.ofNullable(movies.get(title));
    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(movies.values());
    }

    @Override
    public List<Movie> findByGenre(String genre) {
        return movies.values().stream()
                .filter(movie -> Objects.equals(movie.getGenre(), genre))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByTitle(String title) {
        movies.remove(title);
    }

    @Override
    public void deleteAll() {
        movies.clear();
    }

    @Override
    public long count() {
        return movies.size();
    }

    @Override
    public boolean existsByTitle(String title) {
        return movies.containsKey(title);
    }
}
