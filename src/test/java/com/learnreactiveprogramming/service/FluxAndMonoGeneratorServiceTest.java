package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.tools.agent.ReactorDebugAgent;

import java.time.Duration;
import java.util.List;

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
        int stringLength = 3;

        var namesFlux= fluxAndMonoGeneratorService.namesFlux_map(stringLength);

        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        var namesFlux= fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter() {
        int stringLength = 3;
        var nameMono= fluxAndMonoGeneratorService.namesMono_map_filter(stringLength);
        StepVerifier.create(nameMono)
                .expectNext("4-ALEX")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_defaultIfEmpty() {
        int stringLength = 4;
        var nameMono= fluxAndMonoGeneratorService.namesMono_map_filter_defaultIfEmpty(stringLength);
        StepVerifier.create(nameMono)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_switchIfEmpty() {
        int stringLength = 4;
        var nameMono= fluxAndMonoGeneratorService.namesMono_map_filter_switchIfEmpty(stringLength);
        StepVerifier.create(nameMono)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_async() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);
        StepVerifier.create(namesFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatMap() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatMap(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesFlux_concatMap_virtualTimer() {
        VirtualTimeScheduler.getOrSet();
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatMap(stringLength);
        StepVerifier.withVirtualTime(()->namesFlux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesMono_flatMap() {
        int stringLength = 3;
        var namesMono = fluxAndMonoGeneratorService.namesMono_flatMap(stringLength);
        StepVerifier.create(namesMono)
                .expectNext(List.of("A","L","E","X"))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {
        int stringLength = 3;
        var namesMono = fluxAndMonoGeneratorService.namesMono_flatMapMany(stringLength);
        StepVerifier.create(namesMono)
                .expectNext("A","L","E","X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_defaultIfEmpty() {
        int stringLength = 6;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_defaultIfEmpty(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {
        int stringLength = 6;
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);
        StepVerifier.create(namesFlux)
                .expectNext("D","E","F","A","U","L","T")
                .verifyComplete();
    }

    @Test
    void concat_example() {
        var concatFlux = fluxAndMonoGeneratorService.concat_example();
        StepVerifier.create(concatFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void concatWith_example() {
        var concatWith = fluxAndMonoGeneratorService.concatWith_example();
        StepVerifier.create(concatWith)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void concatWith_mono_example() {
        var concatWithMono = fluxAndMonoGeneratorService.concatWith_mono_example();
        StepVerifier.create(concatWithMono)
                .expectNext("A","D")
                .verifyComplete();
    }

    @Test
    void merge_example() {
        var mergeFlux = fluxAndMonoGeneratorService.merge_example();
        StepVerifier.create(mergeFlux)
                .expectNext("A","D","B","E","C","F")
                .verifyComplete();
    }

    @Test
    void mergeWith_example() {
        var mergeWith = fluxAndMonoGeneratorService.mergeWith_example();
        StepVerifier.create(mergeWith)
                .expectNext("A","D","B","E","C","F")
                .verifyComplete();
    }

    @Test
    void mergeWith_mono_example() {
        var mergeWithMono = fluxAndMonoGeneratorService.mergeWith_mono_example();
        StepVerifier.create(mergeWithMono)
                .expectNext("A","D")
                .verifyComplete();
    }

    @Test
    void mergeSequential_example() {
        var mergeSequentialFlux = fluxAndMonoGeneratorService.mergeSequential_example();
        StepVerifier.create(mergeSequentialFlux)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void zip_example() {
        var zipFlux = fluxAndMonoGeneratorService.zip_example();
        StepVerifier.create(zipFlux)
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }

    @Test
    void zip_tuple4_example() {
        var zipFlux = fluxAndMonoGeneratorService.zip_tuple4_example();
        StepVerifier.create(zipFlux)
                .expectNext("AD14","BE25","CF36")
                .verifyComplete();
    }

    @Test
    void zipWith_example() {
        var zipWithFlux = fluxAndMonoGeneratorService.zipWith_example();
        StepVerifier.create(zipWithFlux)
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }

    @Test
    void zipWith_mono_example() {
        var zipWithMono = fluxAndMonoGeneratorService.zipWith_mono_example();
        StepVerifier.create(zipWithMono)
                .expectNext("AD")
                .verifyComplete();
    }

    @Test
    void exception_flux() {
        var value = fluxAndMonoGeneratorService.exception_flux();
        StepVerifier.create(value)
                .expectNext("A","B","C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exception_flux_1() {
        var value = fluxAndMonoGeneratorService.exception_flux();
        StepVerifier.create(value)
                .expectNext("A","B","C")
                .expectError()
                .verify();
    }

    @Test
    void exception_flux_2() {
        var value = fluxAndMonoGeneratorService.exception_flux();
        StepVerifier.create(value)
                .expectNext("A","B","C")
                .expectErrorMessage("exception occurred")
                .verify();
    }

    @Test
    void explore_onErrorReturn() {
        var value = fluxAndMonoGeneratorService.explore_onErrorReturn();
        StepVerifier.create(value)
                .expectNext("A","B","C","D")
                .verifyComplete();
    }

    @Test
    void explore_onErrorResume() {
        var e = new IllegalStateException("Not a valid state");

        var value = fluxAndMonoGeneratorService.explore_onErrorResume(e);
        StepVerifier.create(value)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    void explore_onErrorResume_1() {
        var e = new RuntimeException("Not a valid state");

        var value = fluxAndMonoGeneratorService.explore_onErrorResume(e);
        StepVerifier.create(value)
                .expectNext("A","B","C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void explore_onErrorContinue() {
        var value = fluxAndMonoGeneratorService.explore_onErrorContinue();
        StepVerifier.create(value)
                .expectNext("A","C","D")
                .verifyComplete();
    }

    @Test
    void explore_onErrorMap() {
        var value = fluxAndMonoGeneratorService.explore_onErrorMap();
        StepVerifier.create(value)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void explore_doOnError() {
        var value = fluxAndMonoGeneratorService.explore_doOnError();
        StepVerifier.create(value)
                .expectNext("A","B","C")
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void exploreMono_onErrorReturn() {
        var value = fluxAndMonoGeneratorService.exploreMono_onErrorReturn();
        StepVerifier.create(value)
                .expectNext("abc")
                .verifyComplete();
    }

    @Test
    void explore_Mono_onErrorMap() {
        var e = new RuntimeException("Not a valid state");
        var value = fluxAndMonoGeneratorService.exceptionMono_onErrorMap(e);
        StepVerifier.create(value)
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exploreMono_onErrorContinue_abc() {
        var input = "abc";
        var value = fluxAndMonoGeneratorService.exploreMono_onErrorContinue(input);
        StepVerifier.create(value)
                .verifyComplete();
    }

    @Test
    void exploreMono_onErrorContinue_reactor() {
        var input = "reactor";
        var value = fluxAndMonoGeneratorService.exploreMono_onErrorContinue(input);
        StepVerifier.create(value)
                .expectNext("reactor")
                .verifyComplete();
    }

    @Test
    void explore_generate() {

        var flux=fluxAndMonoGeneratorService.explore_generate().log();
        StepVerifier.create(flux)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void explore_create() {

        var flux=fluxAndMonoGeneratorService.explore_create().log();
        StepVerifier.create(flux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void explore_create_mono() {
        var mono=fluxAndMonoGeneratorService.explore_create_mono().log();
        StepVerifier.create(mono)
                .expectNext("alex")
                .verifyComplete();
    }

    @Test
    void explore_create_mono1() {

        var mono = fluxAndMonoGeneratorService.explore_create_mono1();
        //then
        StepVerifier.create(mono)
                .expectNext("alex")
                .verifyComplete();
    }

    @Test
    void explore_handle() {
        var handle= fluxAndMonoGeneratorService.explore_handle().log();
        StepVerifier.create(handle)
                .expectNext("ALEX","CHLOE")
                .verifyComplete();
    }

    @Test
    void explore_Mono_onErrorMap_onOperatorDebug() {
        Hooks.onOperatorDebug();
        var e = new RuntimeException("Not a valid state");
        var value = fluxAndMonoGeneratorService.explore_Mono_onErrorMap_onOperatorDebug(e);
        StepVerifier.create(value)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void explore_Mono_onErrorMap_checkpoint() {

        var e = new RuntimeException("Not a valid state");
        var value = fluxAndMonoGeneratorService.explore_Mono_onErrorMap_checkpoint(e);
        StepVerifier.create(value)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void explore_Mono_onErrorMap_ReactorAgentDebug() {
        ReactorDebugAgent.init();
        ReactorDebugAgent.processExistingClasses();
        var e = new RuntimeException("Not a valid state");
        var value = fluxAndMonoGeneratorService.explore_Mono_onErrorMap_ReactorAgentDebug(e);
        StepVerifier.create(value)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }
}
