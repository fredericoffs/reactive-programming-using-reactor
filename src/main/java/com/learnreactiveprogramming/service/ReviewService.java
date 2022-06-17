package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Review;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.List;

public class ReviewService {

    private WebClient webClient;

    public ReviewService(WebClient webClient) {
        this.webClient = webClient;
    }

    public ReviewService() {
    }

    public  Flux<Review> retrieveReviewsFlux_RestClient(long movieInfoId){

        var uri = UriComponentsBuilder
                .fromUriString("/v1/reviews")
                .queryParam("movieInfoId", movieInfoId)
                .buildAndExpand()
                .toUriString();

        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Review.class)
                .log();
    }

    public  List<Review> retrieveReviews(long MovieId){

        var reviewsList = List.of(new Review(MovieId, "Awesome Movie", 8.9),
                new Review(MovieId, "Excellent Movie", 9.0));
        return reviewsList;
    }

    public Flux<Review> retrieveReviewsFlux(long MovieId){

        var reviewsList = List.of(new Review(MovieId, "Awesome Movie", 8.9),
                new Review(MovieId, "Excellent Movie", 9.0));
        return Flux.fromIterable(reviewsList);
    }
}
