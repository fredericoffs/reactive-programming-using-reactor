package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.MILLIS;

@Slf4j
public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .log();
    }

    public Mono<String> nameMono() {
        return Mono.just("Other name")
                .log();
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .map(s->s.length() +"-"+s)
                .log();
    }

    public Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                //.map(s->s.length() +"-"+s)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                //.map(s->s.length() +"-"+s)
                .flatMapMany(this::splitString)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray); //ALEX -> A,L,E,X
        return Mono.just(charList);
    }

    public Flux<String> namesFlux_map(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .map(s->s.length() +"-"+s)
                .doOnNext(name ->{
                    name.toLowerCase();
                    System.out.println("Name is: " + name);
                })
                .doOnSubscribe(s->{
                    System.out.println("Subscription is: " + s);
                })
                .doOnComplete(()-> {
                    System.out.println("Inside the complete callback");
                })
                .doFinally(signalType -> {
                    System.out.println("Inside doFinally: " + signalType);
                })
                .log();
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                //.map(s->s.length() +"-"+s)
                .flatMap(s->splitString(s))
                .log();
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                //.map(s->s.length() +"-"+s)
                .flatMap(s->splitString_withDelay(s))
                .log();
    }

    public Flux<String> namesFlux_concatMap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                //.map(s->s.length() +"-"+s)
                .concatMap(s->splitString_withDelay(s))
                .log();
    }

    public Flux<String> namesFlux_transform(int stringLength) {

        Function<Flux<String>,Flux<String>> filterMap = name -> name                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(s->splitString(s))
                .log();
    }

    public Flux<String> namesFlux_transform_defaultIfEmpty(int stringLength) {

        Function<Flux<String>,Flux<String>> filterMap = name -> name                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(s->splitString(s))
                .defaultIfEmpty("default")
                .log();
    }

    public Mono<String> namesMono_map_filter_defaultIfEmpty(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .map(s->s.length() +"-"+s)
                .defaultIfEmpty("default")
                .log();
    }

    public Mono<String> namesMono_map_filter_switchIfEmpty(int stringLength) {
        var defaultSwitchIfEmpty = Mono.just("default");
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .map(s->s.length() +"-"+s)
                .switchIfEmpty(defaultSwitchIfEmpty)
                .log();
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {

        Function<Flux<String>,Flux<String>> filterMap = name -> name                .map(String::toUpperCase)
                .filter(s-> s.length() > stringLength)
                .flatMap(s->splitString(s));

        var defaultSwitchIfEmpty = Flux.just("default")
                .transform(filterMap);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultSwitchIfEmpty)
                .log();
    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> splitString_withDelay(String name){
        var charArray = name.split("");
        //var delay = new Random().nextInt(1000);
        var delay = 1000;
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    // .concat - static method in Flux
    public Flux<String> concat_example(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");

        return Flux.concat(abcFlux,defFlux).log();
    }

    // .concatWith - instance method in Flux and Mono
    public Flux<String> concatWith_example(){
        var abcFlux = Flux.just("A","B","C");
        var defFlux = Flux.just("D","E","F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> concatWith_mono_example(){
        var aMono = Mono.just("A");
        var bMono = Mono.just("D");

        return aMono.concatWith(bMono).log();
    }

    // .merge - static method in Flux
    public Flux<String> merge_example(){
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.of(100,MILLIS));
        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.of(125,MILLIS));

        return Flux.merge(abcFlux,defFlux).log();
    }

    // .mergeWith - instance method in Flux and Mono
    public Flux<String> mergeWith_example(){
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> mergeWith_mono_example(){
        var aMono = Mono.just("A");
        var bMono = Mono.just("D");

        return aMono.mergeWith(bMono).log();
    }

    // .mergeSequential - static method in Flux
    public Flux<String> mergeSequential_example(){
        var abcFlux = Flux.just("A","B","C")
                .delayElements(Duration.of(100,MILLIS));
        var defFlux = Flux.just("D","E","F")
                .delayElements(Duration.of(125,MILLIS));

        return Flux.mergeSequential(abcFlux,defFlux).log();
    }

    // .zip - Static method that’s part of the Flux
    public Flux<String> zip_example() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.zip(abcFlux, defFlux, (first, second) ->first + second )
                .log(); // AD, BE, CF
    }

    public Flux<String> zip_tuple4_example() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var flux3 = Flux.just("1", "2", "3");
        var flux4 = Flux.just("4", "5", "6");
        return Flux.zip(abcFlux, defFlux,flux3,flux4)
                .map(t4 -> t4.getT1()+t4.getT2()+t4.getT3()+t4.getT4())
                .log(); // AD14, BE25, CF36
    }

    // .zipWith - instance method that’s part of the Flux and Mono
    public Flux<String> zipWith_example() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return abcFlux.zipWith(defFlux,(first, second) -> first + second)
                .log(); // AD, BE, CF
    }

    public Mono<String> zipWith_mono_example(){
        var aMono = Mono.just("A");
        var bMono = Mono.just("D");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1()+t2.getT2())
                .log();
    }

    public Flux<String> exception_flux(){
        return Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("exception occurred")))
                .concatWith(Flux.just("D"))
                .log();
    }

    public Flux<String> explore_onErrorReturn(){
        return Flux.just("A","B","C")
                .concatWith(Flux.error(new IllegalStateException("exception occurred")))
                .onErrorReturn("D")
                .log();
    }

    public Flux<String> explore_onErrorResume(Exception e){

        var recoveryFlux = Flux.just("D","E","F");
        return Flux.just("A","B","C")
                .concatWith(Flux.error(e))
                .onErrorResume(ex -> {
                    log.error("Exception is: ", ex);
                    if (ex instanceof IllegalStateException)
                        return recoveryFlux;
                    else
                        return Flux.error(ex);
                })
                .log();
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> {
                    System.out.println("Name is: " + name);
                });

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> {
                    System.out.println("Mono name is: " + name);
                });
    }
}
