package com.example3;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
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
 * This example is demo of the functional programming style.
 * Refer: https://doc.akka.io/docs/akka/current/typed/style-guide.html
 */
public class AppLauncher {

    public static void main(String ... args) {

        ActorSystem.create(
                Behaviors.setup(context -> {

                    ActorRef<BehaviorFactory.CMD> ref =
                            context.spawn(BehaviorFactory.create(), "AnonymousActor");

                    ref.tell(BehaviorFactory.CmdType.CREATE);
                    ref.tell(BehaviorFactory.CmdType.READ);
                    ref.tell(BehaviorFactory.CmdType.UPDATE);
                    ref.tell(BehaviorFactory.CmdType.DELETE);

                    return Behaviors.empty();
                }), "SimpleRootActor");
    }

    public static class BehaviorFactory {

        public interface CMD {}

        public enum CmdType implements CMD {
            CREATE,
            READ,
            UPDATE,
            DELETE
        }

        /**
         * We can create a behavior instance without defining a sub-class of AbstractBehavior
         * @return a behavior instance
         */
        public static Behavior<CMD> create() {

            return Behaviors.setup(context ->

                    Behaviors.receive(CMD.class)
                            .onMessageEquals(CmdType.CREATE, () -> {
                                context.getLog().info("It's a funny actor, message: {}", CmdType.CREATE);
                                return Behaviors.same();
                            })
                            .onMessageEquals(CmdType.READ, () -> {
                                context.getLog().info("It's a funny actor, message: {}", CmdType.READ);
                                return Behaviors.same();
                            })
                            .onMessageEquals(CmdType.UPDATE, () -> {
                                context.getLog().info("It's a funny actor, message: {}", CmdType.UPDATE);
                                return Behaviors.same();
                            })
                            .onMessageEquals(CmdType.DELETE, () -> {
                                context.getLog().info("It's a funny actor, message: {}", CmdType.DELETE);
                                return Behaviors.same();
                            })
                            .build()
            );
        }
    }

}
