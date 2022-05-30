package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;

import java.util.List;

public class FluxAndMonoSchedulersService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chole"));
    }

    public static void main(String[] args) {
        FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();
        fluxAndMonoSchedulersService.namesFlux()
                .subscribe(name -> {
                    System.out.println("Name is: " + name);
                });
    }
}
