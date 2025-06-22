package com.techisthoughts.ia.movieclassification.domain.port;

import java.util.List;
import java.util.Optional;
import com.techisthoughts.ia.movieclassification.domain.model.Movie;

/**
 * Port interface for Movie repository operations
 */
public interface MovieRepositoryPort {

    /**
     * Save a movie
     */
    Movie save(Movie movie);

    /**
     * Save multiple movies
     */
    List<Movie> saveAll(List<Movie> movies);

    /**
     * Find a movie by title
     */
    Optional<Movie> findByTitle(String title);

    /**
     * Find all movies
     */
    List<Movie> findAll();

    /**
     * Find movies by genre
     */
    List<Movie> findByGenre(String genre);

    /**
     * Delete a movie by title
     */
    void deleteByTitle(String title);

    /**
     * Delete all movies
     */
    void deleteAll();

    /**
     * Count total movies
     */
    long count();

    /**
     * Check if movie exists by title
     */
    boolean existsByTitle(String title);
}
