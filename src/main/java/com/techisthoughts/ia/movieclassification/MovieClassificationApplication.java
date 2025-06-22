package com.techisthoughts.ia.movieclassification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.techisthoughts.ia.movieclassification.infrastructure.adapter.CsvMovieLoader;

@SpringBootApplication
public class MovieClassificationApplication {

    private final Logger LOG = LoggerFactory.getLogger(MovieClassificationApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MovieClassificationApplication.class, args);
    }

    @Bean
    CommandLineRunner loadData(CsvMovieLoader csvMovieLoader) {
        return args -> {
            // Load movie data from CSV file on startup
            LOG.info("Loading movie data from CSV...");
            int loadedCount = csvMovieLoader.loadMoviesFromCsv();
            LOG.info("Application started successfully! Loaded {} movies", loadedCount);
        };
    }
}
