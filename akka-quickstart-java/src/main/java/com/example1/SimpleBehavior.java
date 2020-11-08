package com.example1;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.AllArgsConstructor;
import lombok.Data;

public class SimpleBehavior extends AbstractBehavior<SimpleBehavior.Command> {

    /**
     * Define protocol
     */
    public interface Command {}

    @Data
    @AllArgsConstructor
    public static class SayHello implements Command {
        private String name;
    }

    /**
     * Private constructor
     */
    private SimpleBehavior(ActorContext<Command> context) {
        super(context);
        // setup something here ...
    }

    /**
     * Create a Behavior instance
     * @return a Behavior instance
     */
    public static Behavior<Command> create() {
        return Behaviors.setup(SimpleBehavior::new);
        // equivalent of
        // return Behaviors.setup(context -> new SimpleBehavior(context));
    }

    /**
     * Register message handler
     * @return
     */
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(SayHello.class, this::sayHello)
                .build();
    }

    private Behavior<Command> sayHello(SayHello message) {
        getContext().getLog().info("Welcome to Akka, {}!", message.getName());
        return this;
    }

}
