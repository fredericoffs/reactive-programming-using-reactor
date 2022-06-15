package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest(){
        var flux = Flux.range(1,10);

        flux.subscribe(i-> System.out.println("Subscriber 1: " + i));
        flux.subscribe(i-> System.out.println("Subscriber 2: " + i));
    }
}
