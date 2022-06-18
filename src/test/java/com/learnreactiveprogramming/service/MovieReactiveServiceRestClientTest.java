package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceRestClientTest {

    WebClient webClient = WebClient
            .builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    private MovieInfoService movieInfoService = new MovieInfoService(webClient);
    private ReviewService reviewService = new ReviewService(webClient);

    MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService,reviewService);

    @Test
    void getAllMovies_RestClient() {

        var moviesFlux = movieReactiveService.getAllMovies_RestClient();

        StepVerifier.create(moviesFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void getMovieById_restClient(){
        var movieId = 1L;
        var moviesMono = movieReactiveService.getMovieById_restClient(movieId);

        StepVerifier.create(moviesMono)
                //.expectNextCount(1)
                .assertNext( movieInfo ->
                        assertEquals("Batman Begins", movieInfo.getMovie().getName())
                )
                .verifyComplete();
    }
}