package com.techisthoughts.ia.demo.controller;


import com.techisthoughts.ia.demo.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
public class MovieController {

    private final Logger LOG = LoggerFactory.getLogger(MovieController.class);

    private final ChatClient chatClient;

    private final MovieService movieService;

    public MovieController(ChatClient chatClient, MovieService movieService) {
        this.chatClient = chatClient;
        this.movieService = movieService;
    }

    @GetMapping("/movies")
    public ResponseEntity<Map<String, List<MovieRecord>>> getMovies() {
        LOG.info("Fetching movies");
        var movies =  movieService.getAllMovies().stream().map(
                movie -> new MovieRecord(
                        movie.movieTitle(),
                        movie.genre(),
                        movie.releaseYear(),
                        movie.averageRating(),
                        movie.numberOfReviews(),
                        movie.reviewHighlights(),
                        movie.minuteOfLifeChangingInsight(),
                        movie.howDiscovered(),
                        movie.meaningfulAdviceTaken(),
                        movie.isSuggestedToFriendsFamily(),
                        movie.percentageSuggestedToFriendsFamily()
                )
        ).collect(Collectors.toList());
        Map<String, List<MovieRecord>> response = new HashMap<>();
        response.put("data", movies);
        LOG.info("Fetched {} movies", movies.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .system("You are a virtual girlfriend. You are very smart and funny. You are also very beautiful. You are very kind and you love to help people. You are also very good at giving advice. You are also very good at making people feel better.")
                .user(message)
                .call()
                .content();
    }

}
