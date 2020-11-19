package com.example9;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Source;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

/**
 * Unsupported access to ActorContext operation from the outside of Actor
 */
public class AppLauncherWithException {

    public static void main(String ... args) {

        ActorSystem.create(
                Behaviors.setup(context -> {

                    final Source<Integer, NotUsed> source = Source.range(1, 10);
                    final CompletionStage<Done> done =  source.runForeach(System.out::println, context.getSystem());

                    done.whenComplete((done1, throwable) -> {
                        context.getLog().info(done1.getClass().getName());
                        //context.getSystem().terminate();
                    }).whenComplete((done1, throwable) -> {
                        if (throwable != null) {
                            throwable.printStackTrace();
                        }
                    });

                    context.getSystem().scheduler().scheduleWithFixedDelay(
                            Duration.ZERO,
                            Duration.ofSeconds(5),
                            () -> {
                                context.getLog().info("{}", System.currentTimeMillis());
                            },
                            context.getExecutionContext()
                    );

                    return Behaviors.empty();
                }), "SimpleRootActor");
    }

}
