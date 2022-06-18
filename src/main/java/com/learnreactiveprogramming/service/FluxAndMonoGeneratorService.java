package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.tools.shaded.net.bytebuddy.agent.builder.AgentBuilder;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.learnreactiveprogramming.util.CommonUtil.delay;
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

    public Flux<String> explore_onErrorContinue(){

        return Flux.just("A","B","C")
                .map(name -> {
                    if(name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((ex,name) -> {
                    log.error("Exception is: ", ex);
                    log.info("Name is {}", name);
                })
                .log();
    }

    public Flux<String> explore_onErrorMap(){

        return Flux.just("A","B","C")
                .map(name -> {
                    if(name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorMap((ex) -> {
                    log.error("Exception is: ", ex);
                    return new ReactorException(ex,ex.getMessage());
                })
                .log();
    }

    public Flux<String> explore_doOnError(){

        return Flux.just("A","B","C")
                .concatWith(Flux.error(new IllegalStateException("Exception occurred")))
                .doOnError(ex -> {
                    log.error("Exception is: " + ex);
                })
                .log();
    }

    public Mono<Object> exploreMono_onErrorReturn(){
        return Mono.just("A")
                .map( value -> {
                    throw new RuntimeException("Error Occurred.");
                })
                .onErrorReturn("abc")
                .log();
    }

    public Mono<Object> exceptionMono_onErrorMap(Exception e){
        return Mono.just("B")
                .map(value -> {
                        throw new RuntimeException("Exception occurred");
                })
                .onErrorMap((ex) -> {
                    log.error("Exception is: ", ex);
                    return new ReactorException(ex,ex.getMessage());
                })
                .log();
    }

    public Mono<String> exploreMono_onErrorContinue(String input){

        return Mono.just(input)
                .map(name -> {
                    if(name.equals("abc"))
                        throw new RuntimeException("Exception occurred");
                    return name;
                })
                .onErrorContinue((ex,name) -> {
                    log.error("Exception is: ", ex);
                    log.info("Name is {}", name);
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

    public Flux<Integer> explore_generate(){

        return Flux.generate(
                ()->1,(state,sink)->{
                    sink.next(state*2);

                    if(state==10){
                        sink.complete();
                    }
                    return state+1;
                }
        );
    }

    public Flux<String> explore_create(){
        return Flux.create(sink -> {
            /*names().forEach(sink::next);*/

            //CompletableFuture is modern functional style of of writting Asynchronous code.
            CompletableFuture
                    //introduce asynchronism - release a calling thread and
                    // have this whole execution happen in a different thread
                    .supplyAsync(()->names())
                    //gives the accept to the whole list
                    .thenAccept(names -> {
                        names.forEach((name) -> {
                            sink.next(name);
                            sink.next(name);
                        });
                    })
                    .thenRun(()->sendEvents(sink));
        });
    }

    public void sendEvents(FluxSink<String> sink){
            CompletableFuture
                    .supplyAsync(()->names())
                    .thenAccept(names -> names.forEach(sink::next))
                    .thenRun(sink::complete);
    }

    public static List<String> names(){
        delay(1000);
        return List.of("alex", "ben", "chloe");
    }

    public Mono<String> explore_create_mono(){

        return Mono.create(sink->{
            sink.success("alex");
        });
    }

    public Mono<String> explore_create_mono1(){
        return Mono.create(sink->{
            CompletableFuture
                    .supplyAsync( ()-> "alex")
                    .thenAccept( name ->{ sink.success(name);})
                    .thenRun(sink::success);
        });
    }

    public Flux<String> explore_handle(){

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .handle((name,sink)->{
                    if(name.length()>3)
                        sink.next(name.toUpperCase());
                });
    }

    public Flux<String> explore_Mono_onErrorMap_onOperatorDebug(Exception e){

        return Flux.just("A")
                .concatWith(Flux.error(e))
                .onErrorMap((ex) -> {
                    log.error("Exception is: ", ex);
                    return new ReactorException(ex,ex.getMessage());
                })
                .log();
    }
}
