package com.example5;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

public class Fibonacci extends AbstractBehavior<Fibonacci.FibMessage> {

    private final int fibIndex;
    private int value = -1;
    private int countAnswer = 0;
    private int tempValue = 0;
    private boolean asked = false;
    private ActorRef<FibonacciFactory.RequestActor> factory;

    private Fibonacci(ActorContext context, int fibIndex,
                      ActorRef<FibonacciFactory.RequestActor> factory) {

        super(context);
        this.fibIndex = fibIndex;
        this.factory = factory;
    }

    public static Behavior<FibMessage> create(int fibIndex,
                                              ActorRef<FibonacciFactory.RequestActor> factory) {
        return Behaviors.setup(context -> new Fibonacci(context, fibIndex, factory));
    }

    public interface FibMessage {}

    @Data
    @AllArgsConstructor
    public static class Ask implements FibMessage {
        private ActorRef<FibMessage> askedRef;
    }

    @Data
    @AllArgsConstructor
    public static class Answer implements FibMessage {
        private int resolved;
    }

    @Data
    @AllArgsConstructor
    public static class ActorSupply implements FibMessage {
        private ActorRef<FibMessage> actorRef;
    }

    @Override
    public Receive<FibMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(ActorSupply.class, msg -> {
                    msg.actorRef.tell(new Ask(getContext().getSelf()));
                    return this;
                })

                .onMessage(Ask.class, this::calculate)

                .onMessage(Answer.class, msg -> {
                    countAnswer++;
                    tempValue += msg.getResolved();
                    if (countAnswer == 2) {
                        value = tempValue;
                        getContext().getLog().info("F({}) = {}", fibIndex, value);
                    }
                    return this;
                })
                .build();
    }

    private Behavior<FibMessage> calculate(Ask calMsg) {

        if (value != -1) {
            calMsg.askedRef.tell(new Answer(value));
            return this;
        }

        if (fibIndex == 0) {
            value = 0;
            getContext().getLog().info("F({}) = {}", fibIndex, value);
            calMsg.askedRef.tell(new Answer(value));
            return this;
        }

        if (fibIndex == 1) {
            value = 1;
            getContext().getLog().info("F({}) = {}", fibIndex, value);
            calMsg.askedRef.tell(new Answer(value));
            return this;
        }

        if (!asked) {
            asked = true;
            getContext().getLog().info("F{} = F{} + F{}", fibIndex, fibIndex - 1 , fibIndex - 2);
            factory.tell(new FibonacciFactory.RequestActor(fibIndex - 1, getContext().getSelf()));
            factory.tell(new FibonacciFactory.RequestActor(fibIndex - 2, getContext().getSelf()));
        }

        CompletableFuture.runAsync(() -> {
            if (fibIndex == 2) {
                getContext().getLog().info("F{} = F{} + F{}", fibIndex, fibIndex - 1 , fibIndex - 2);
            }
            while (this.value == -1) {
            }
            calMsg.askedRef.tell(new Answer(value));
        });

        return this;
    }

}
