package com.example1;

import akka.actor.typed.ActorSystem;

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
        ActorSystem<SimpleBehavior.Command> system = ActorSystem.create(SimpleBehavior.create(), "SimpleBehavior");
        system.tell(new SimpleBehavior.SayHello("QuyMV"));
    }
}
