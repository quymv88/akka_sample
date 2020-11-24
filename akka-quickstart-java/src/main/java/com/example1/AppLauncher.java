package com.example1;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;

/**
 * There are two flavors of the Actor APIs.
 *
 * 1. The functional programming style where you pass a function to a factory which then constructs a behavior,
 * for stateful actors this means passing immutable state around as parameters and switching
 * to a new behavior whenever you need to act on a changed state.
 *
 * 2. The object-oriented style where a concrete class for the actor behavior is
 * defined and mutable state is kept inside of it as fields.
 *
 * This example is demo of the object-oriented style.
 * Refer: https://doc.akka.io/docs/akka/current/typed/style-guide.html
 */
public class AppLauncher {

    public static void main(String ... args) {
        //ActorSystem<SimpleBehavior.Command> system = ActorSystem.create(SimpleBehavior.create(), "SimpleBehavior");
        //system.tell(new SimpleBehavior.SayHello("QuyMV"));
        ActorSystem<SimpleBehavior.Command> system2 = ActorSystem.create(Behaviors.setup(context -> {
            ActorRef<SimpleBehavior.Command> actorRef = context.spawn(SimpleBehavior.create(), "test");
            actorRef.tell(new SimpleBehavior.SayHello("QuyMV"));
            return Behaviors.empty();
        }),"Root");

    }
}
