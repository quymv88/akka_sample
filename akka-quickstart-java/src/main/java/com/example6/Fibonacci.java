package com.example6;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class Fibonacci extends AbstractBehavior<Fibonacci.FiMessage> {

    private final int fibIdx;
    private long value = -1;
    private int countAnswer = 0;
    private long tempValue = 0;
    private boolean asked = false;

    private List<ActorRef<FiMessage>> waitingActors = new ArrayList<>();

    private ActorRef<FibonacciFactory.RequestActor> factory;

    private Fibonacci(ActorContext context, int fiIndex,
                      ActorRef<FibonacciFactory.RequestActor> factory) {

        super(context);
        this.fibIdx = fiIndex;
        this.factory = factory;
    }

    public static Behavior<FiMessage> create(int fiIdx,
                                             ActorRef<FibonacciFactory.RequestActor> factory) {
        return Behaviors.setup(context -> new Fibonacci(context, fiIdx, factory));
    }

    public interface FiMessage {}

    @Data
    @AllArgsConstructor
    public static class Ask implements FiMessage {
        private ActorRef<FiMessage> askedRef;
    }

    @Data
    @AllArgsConstructor
    public static class Answer implements FiMessage {
        private long resolved;
    }

    @Data
    @AllArgsConstructor
    public static class ActorSupply implements FiMessage {
        private ActorRef<FiMessage> actorRef;
    }

    @Override
    public Receive<FiMessage> createReceive() {

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
                        getContext().getLog().info("F({}) = {}", fibIdx, value);
                        waitingActors.forEach(actorRef -> actorRef.tell(new Answer(value)));
                        waitingActors.clear();
                    }
                    return this;
                })
                .build();
    }

    private Behavior<FiMessage> calculate(Ask msg) {

        if (value != -1) {
            msg.askedRef.tell(new Answer(value));
            return this;
        }

        if (fibIdx == 0) {
            value = 0;
            getContext().getLog().info("F({}) = {}", fibIdx, value);
            msg.askedRef.tell(new Answer(value));
            return this;
        }

        if (fibIdx == 1) {
            value = 1;
            getContext().getLog().info("F({}) = {}", fibIdx, value);
            msg.askedRef.tell(new Answer(value));
            return this;
        }

        if (!asked) {
            asked = true;
            factory.tell(new FibonacciFactory.RequestActor(fibIdx - 1, getContext().getSelf()));
            factory.tell(new FibonacciFactory.RequestActor(fibIdx - 2, getContext().getSelf()));
        }

        waitingActors.add(msg.getAskedRef());

        return this;
    }

}
