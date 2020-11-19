package com.example9;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.IOResult;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

/**
 * Unsupported access to ActorContext operation from the outside of Actor
 */
public class AppLauncher {

    public static void main(String ... args) {

        ActorSystem.create(

                Behaviors.setup(context -> {

                    final Source<Integer, NotUsed> source = Source.range(1, 5);

                    final Source<BigInteger, NotUsed> factorials =
                            source.scan(BigInteger.ONE, (acc, next) -> acc.multiply(BigInteger.valueOf(next)));

                    final CompletionStage<IOResult> result =
                            factorials
                                    .map(num -> ByteString.fromString(num.toString() + "\n"))
                                    .runWith(FileIO.toPath(Paths.get("factorials.txt")), context.getSystem());

                    result.whenComplete((ioResult, throwable) -> {
                        System.out.println(ioResult);
                    });

                    return Behaviors.empty();
                }), "SimpleRootActor");
    }

}
