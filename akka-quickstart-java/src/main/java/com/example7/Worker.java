package com.example7;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Worker extends AbstractBehavior<Worker.Task> {

    private int workCounter = 0;

    public interface Task {};

    private Worker(ActorContext<Task> context) {
        super(context);
    }

    public static Behavior<Task> create() {
        return Behaviors.setup(Worker::new);
    }

    @Override
    public Receive<Task> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(this::execute)
                .build();

    }

    private Behavior<Task> execute(Task task) {
        workCounter++;
        getContext().getLog().info("My name is {}. My effort: {}", this, workCounter);
        return this;
    }
}
