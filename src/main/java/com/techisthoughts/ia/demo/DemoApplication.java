package com.techisthoughts.ia.demo;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import com.techisthoughts.ia.demo.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableRedisDocumentRepositories(basePackages = "com.techisthoughts.ia.demo.repository")
public class DemoApplication {

    private final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner loadData(MovieService movieService) {
        return args -> {
            // Load movie data from CSV file
            String filePath = "src/main/resources/NLID.csv";
            movieService.loadMovieData(filePath);
            // Load data or perform any startup tasks here
            LOG.info("Application started successfully!");
        };
    }


}
