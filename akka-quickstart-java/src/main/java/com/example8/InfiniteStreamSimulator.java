package com.example8;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InfiniteStreamSimulator {

    public static void main(String ... args) throws InterruptedException {

        Stream stream = IntStream.iterate(0, i -> i).mapToObj(i -> {
            // simulate waiting for a new event emitted
            while (true) {
                try {
                    return CompletableFuture.supplyAsync(() -> {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {}

                        return UUID.randomUUID();

                    }).get();
                } catch (Exception e) {}
            }
        });

        stream.flatMap(o -> Stream.of(o, o.hashCode())).forEach(System.out::println);

    }

}
