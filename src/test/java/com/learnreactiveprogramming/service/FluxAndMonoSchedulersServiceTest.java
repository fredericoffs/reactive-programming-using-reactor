package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoSchedulersService =
            new FluxAndMonoSchedulersService();

    @Test
    void explore_publishOn() {

        var flux = fluxAndMonoSchedulersService.explore_publishOn();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_subcribeOn() {

        var flux = fluxAndMonoSchedulersService.explore_subcribeOn();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }
}