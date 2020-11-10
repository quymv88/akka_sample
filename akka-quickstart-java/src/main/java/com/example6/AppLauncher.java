package com.example6;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;

import java.time.Duration;
import java.util.concurrent.ForkJoinPool;

/**
 * Calculate
 */
public class AppLauncher {

    public interface Command {}

    public static void main(String ... args) {

        ActorSystem.create(
                Behaviors.setup(context -> {

                    context.getLog().info("ForkJoinPool CommonPool Parallelism: " + ForkJoinPool.commonPool().getParallelism());

                    ActorRef<FibonacciFactory.RequestActor> factory =
                            context.spawn(FibonacciFactory.create(), "FibonacciFactory");

                    ActorRef<Fibonacci.FiMessage> fib = context.spawn(Fibonacci.create(90, factory), "Fib");

                    long start = System.currentTimeMillis();
                    context.ask(Fibonacci.FiMessage.class, fib, Duration.ofMinutes(1L), ref -> new Fibonacci.Ask(ref), (arg1, arg2) -> {
                        context.getLog().info("Final Result: {}", ((Fibonacci.Answer) arg1 ).getResolved());
                        context.getLog().info("Duration: {}", System.currentTimeMillis() - start);
                        return "";
                    });

                    return Behaviors.ignore();

                }), "Fibonacci");
    }

}
