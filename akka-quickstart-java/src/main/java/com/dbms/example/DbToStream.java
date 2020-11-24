package com.dbms.example;

import akka.Done;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.Materializer;
import akka.stream.alpakka.slick.javadsl.Slick;
import akka.stream.alpakka.slick.javadsl.SlickRow;
import akka.stream.alpakka.slick.javadsl.SlickSession;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;

public class DbToStream {

    public static void main(String ... args) {
        ActorSystem.create(
                Behaviors.setup(context -> {

                    final SlickSession session = SlickSession.forConfig("slick-postgres");

                    List<Account> accounts = new ArrayList<>();

                    Materializer materializer = Materializer.createMaterializer(context.getSystem());

                    final CompletionStage<Done> done =
                            Slick.source(
                                    session,
                                    "SELECT * from accounts limit 10",
                                    (SlickRow row) -> accounts.add(new Account(row.nextInt(),
                                            row.nextString(),
                                            row.nextString(),
                                            row.nextTimestamp().toLocalDateTime())))
                                    .log("account")
                                    .runWith(Sink.ignore(), materializer);

                    done.whenComplete((done1, throwable) -> {
                       if (throwable != null) {

                       } else {
                           accounts.stream().forEach(account -> {
                               System.out.println(account.getUserId() + "|" + account.getUserName());
                           });
                       }
                    });

                    return Behaviors.empty();
                }),
                "SimpleRootActor"
        );
    }

}
