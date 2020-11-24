package com.example;

import java.util.stream.LongStream;

public class Main {

    public static void main(String ... args) {
        System.out.println("Start");
        new Thread(() -> {
            LongStream.range(0, 100).forEach(System.out::println);
        }).start();
        System.out.println("Stop");
    }
}
