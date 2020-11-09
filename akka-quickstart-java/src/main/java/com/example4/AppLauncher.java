package com.example4;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

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

                    ActorRef<Command> ref =
                            context.spawn(createCounter(0), "AnonymousActor");

                    IntStream.range(0, 1000000).forEach(i -> ref.tell(new Command() {}));

                    return Behaviors.empty();
                }), "SimpleRootActor");
    }

    /**
     * Counter behavior factory
     * @param value: current counter value
     * @return an instance of counter behavior
     */
    public static Behavior<Command> createCounter(int value) {

        return Behaviors.setup(context ->
                Behaviors.receiveMessage(message -> {
                    int newValue = value + 1;
                    context.getLog().info("A message is processed. Counter: {}", newValue);
                    return createCounter(newValue);
                })
        );
    }

}
