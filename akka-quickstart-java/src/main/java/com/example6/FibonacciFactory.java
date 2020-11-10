package com.example6;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class FibonacciFactory extends AbstractBehavior<FibonacciFactory.RequestActor> {

    private Map<String, ActorRef<Fibonacci.FiMessage>> map = new HashMap<>();

    @Data
    @AllArgsConstructor
    public static class RequestActor {
        private int fiIdx;
        private ActorRef<Fibonacci.FiMessage> askedRef;
    }

    private FibonacciFactory(ActorContext context) {
        super(context);
    }

    public static Behavior<RequestActor> create() {
        return Behaviors.setup(context -> new FibonacciFactory(context));
    }

    @Override
    public Receive<RequestActor> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(message -> {
                    String name = "Fib" + message.fiIdx;
                    ActorRef<Fibonacci.FiMessage> actorRef = map.get(name);
                    if (actorRef == null) {
                        actorRef = getContext().spawn(Fibonacci.create(message.fiIdx, getContext().getSelf()), name);
                        map.put(name, actorRef);
                    }
                    message.askedRef.tell(new Fibonacci.ActorSupply(actorRef));
                    return this;
                })
                .build();
    }

}
