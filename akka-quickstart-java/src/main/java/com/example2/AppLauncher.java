package com.example2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import com.example1.SimpleBehavior;

public class AppLauncher {

    public static void main(String ... args) {
        ActorSystem.create(
                Behaviors.setup(context -> {

                    ActorRef<SimpleBehavior.Command> ref = context.spawn(SimpleBehavior.create(), "SimpleBehavior");
                    ref.tell(new SimpleBehavior.SayHello("Mr. QuyMV"));

                    return Behaviors.empty();
                }),
                "SimpleRootActor"
        );
    }
}
