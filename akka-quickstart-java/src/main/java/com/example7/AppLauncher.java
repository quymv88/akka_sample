package com.example7;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.PoolRouter;
import akka.actor.typed.javadsl.Routers;

import java.util.stream.IntStream;

/**
 * In some cases it is useful to distribute messages of the same type over a set of actors,
 * so that messages can be processed in parallel - a single actor will only process one message at a time.
 * The router itself is a behavior that is spawned into a running actor that will then forward any message
 * sent to it to one final recipient out of the set of routees.
 */
public class AppLauncher {

    public interface Command {}

    public static void main(String ... args) {

        ActorSystem.create(
                Behaviors.setup(context -> {

                    int poolSize = 5;

                    PoolRouter<Worker.Task> pool =
                            Routers.pool(
                                    poolSize,
                                    Behaviors.supervise(Worker.create())
                                            .onFailure(SupervisorStrategy.restart()));

                    ActorRef<Worker.Task> router = context.spawn(pool, "worker-pool");

                    IntStream.range(0, 10).forEach(i -> router.tell(new Worker.Task() {
                        @Override
                        public int hashCode() {
                            return super.hashCode();
                        }
                    }));

                    return Behaviors.ignore();

                }), "WorkerPool");
    }

}
