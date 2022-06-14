package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;
    private RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies(){

        var moviesInfoFlux = movieInfoService.movieInfoFlux();
        return moviesInfoFlux
                //reason for using flatmap is because the review service is again going to give you another reactive type.
                // flatmap - any time in your transformation if you have a function that's returning another reactive type.
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo,reviewsList));
                })
                .doOnError((ex)->{
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                })
                .log();
    }

    public Flux<Movie> getAllMovies_retry(){

        var moviesInfoFlux = movieInfoService.movieInfoFlux();
        return moviesInfoFlux
                //reason for using flatmap is because the review service is again going to give you another reactive type.
                // flatmap - any time in your transformation if you have a function that's returning another reactive type.
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo,reviewsList));
                })
                .doOnError((ex)->{
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                })
                .retry(3)
                .log();
    }

    public Flux<Movie> getAllMovies_retryWhen(){

        var moviesInfoFlux = movieInfoService.movieInfoFlux();
        return moviesInfoFlux
                //reason for using flatmap is because the review service is again going to give you another reactive type.
                // flatmap - any time in your transformation if you have a function that's returning another reactive type.
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo,reviewsList));
                })
                .doOnError((ex)->{
                    log.error("Exception is : ", ex);
                    if(ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .log();
    }

    public Flux<Movie> getAllMovies_repeat(){

        var moviesInfoFlux = movieInfoService.movieInfoFlux();
        return moviesInfoFlux
                //reason for using flatmap is because the review service is again going to give you another reactive type.
                // flatmap - any time in your transformation if you have a function that's returning another reactive type.
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo,reviewsList));
                })
                .doOnError((ex)->{
                    log.error("Exception is : ", ex);
                    if(ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .repeat()
                .log();
    }

    public Flux<Movie> getAllMovies_repeat_n(long n){

        var moviesInfoFlux = movieInfoService.movieInfoFlux();
        return moviesInfoFlux
                //reason for using flatmap is because the review service is again going to give you another reactive type.
                // flatmap - any time in your transformation if you have a function that's returning another reactive type.
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo,reviewsList));
                })
                .doOnError((ex)->{
                    log.error("Exception is : ", ex);
                    if(ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .repeat(n)
                .log();
    }

    private RetryBackoffSpec getRetryBackoffSpec() {
        return Retry.fixedDelay(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure())));
    }

    public Mono<Movie> getMovieById(long movieId){
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsList = reviewService.retrieveReviewsFlux(movieId)
                .collectList();
        return movieInfoMono.zipWith(reviewsList,(movieInfo, reviews) -> new Movie(movieInfo,reviews)).log();
    }

    public Mono<Movie> getMovieById_withRevenue(long movieId){
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsList = reviewService.retrieveReviewsFlux(movieId)
                .collectList();
        var revenueMono = Mono.fromCallable(()->revenueService.getRevenue(movieId));

        return movieInfoMono
                .zipWith(reviewsList,(movieInfo, reviews) -> new Movie(movieInfo,reviews))
                .zipWith(revenueMono,(movie,revenue)->{
                    movie.setRevenue(revenue);
                    return movie;
                })
                .log();
    }

    public Mono<Movie> getMovieByIdMono (long movieId){
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        return movieInfoMono
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo,reviewsList));
                }).log();
    }
}
