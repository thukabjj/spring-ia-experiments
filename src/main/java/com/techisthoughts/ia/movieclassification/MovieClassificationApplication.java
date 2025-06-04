package com.techisthoughts.ia.movieclassification;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import com.techisthoughts.ia.movieclassification.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
@EnableRedisDocumentRepositories(basePackages = "com.techisthoughts.ia.movieclassification.repository")
public class MovieClassificationApplication {

    private final Logger LOG = LoggerFactory.getLogger(MovieClassificationApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MovieClassificationApplication.class, args);
    }

    @Bean
    CommandLineRunner loadData(MovieService movieService) {
        return args -> {
            // Load movie data from CSV file using ClassPathResource
            ClassPathResource classPathResource = new ClassPathResource("NLID.csv");
            movieService.loadMovieData(classPathResource);
            // Load data or perform any startup tasks here
            LOG.info("Application started successfully!");
        };
    }
}
