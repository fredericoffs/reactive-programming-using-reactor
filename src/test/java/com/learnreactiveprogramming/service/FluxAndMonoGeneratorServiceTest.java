package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {
    
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void nameFlux() {
        var namesFlux= fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
                //.expectNext("alex", "ben", "chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesMono() {
        var nameMono = fluxAndMonoGeneratorService.nameMono();

        StepVerifier.create(nameMono)
                .expectNext("Other name")
                .verifyComplete();
    }

    @Test
    void namesFlux_map() {
        var namesFlux= fluxAndMonoGeneratorService.namesFlux_map();

        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }
}
