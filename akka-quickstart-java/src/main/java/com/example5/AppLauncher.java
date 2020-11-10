package com.example5;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

import java.time.Duration;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

/**
 * The functional programming style where you pass a function to a factory which then constructs a behavior,
 * for stateful actors this means passing immutable state around as parameters and switching to a new behavior
 * whenever you need to act on a changed state.
 */
public class AppLauncher {

    public interface Command {}

    public static void main(String ... args) {

        ActorSystem.create(
                Behaviors.setup(context -> {

                    context.getLog().info(String.valueOf("ForkJoinPool.commonPool().getParallelism(): " + ForkJoinPool.commonPool().getParallelism()));

                    ActorRef<FibonacciFactory.RequestActor> factory =
                            context.spawn(FibonacciFactory.create(), "FibonacciFactory");

                    ActorRef<Fibonacci.FibMessage> fib = context.spawn(Fibonacci.create(5, factory), "Fib5");

                    context.ask(Fibonacci.FibMessage.class, fib, Duration.ofMinutes(1L), ref -> new Fibonacci.Ask(ref), (arg1, arg2) -> {
                        context.getLog().info("Final Result: {}", ((Fibonacci.Answer) arg1 ).getResolved());
                        return "";
                    });

                    return Behaviors.empty();

                }), "SimpleRootActor");
    }

}
