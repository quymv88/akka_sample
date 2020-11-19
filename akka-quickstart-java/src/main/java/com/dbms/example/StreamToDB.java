package com.dbms.example;

import akka.Done;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.Materializer;
import akka.stream.alpakka.slick.javadsl.Slick;
import akka.stream.alpakka.slick.javadsl.SlickSession;
import akka.stream.javadsl.Source;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;

public class StreamToDB {

    public static void main(String ... args) {
        ActorSystem.create(
                Behaviors.setup(context -> {

                    final SlickSession session = SlickSession.forConfig("slick-postgres");

                    List<Account> accounts = createAccounts();

                    Materializer materializer = Materializer.createMaterializer(context.getSystem());

                    long start = System.currentTimeMillis();
                    final CompletionStage<Done> done =
                            Source.from(accounts)
                                    .runWith(
                                            Slick.<Account>sink(
                                                    session,
                                                    5, //parallelism factor
                                                    (account, connection) -> {
                                                        PreparedStatement statement =
                                                                connection.prepareStatement(
                                                                        "INSERT INTO accounts (username, email, created_on) VALUES (?, ?, NOW())");
                                                        statement.setString(1, account.getUserName());
                                                        statement.setString(2, account.getEmail());
                                                        return statement;
                                                    }),
                                            materializer);

                    done.whenComplete(
                            (done1, throwable) -> {
                                if (throwable != null) throwable.printStackTrace();
                                System.out.println("Inserting 20K records to DB spends: " + (System.currentTimeMillis() - start)/1000 + " seconds.");
                            }
                    );
                    return Behaviors.empty();
                }),
                "SimpleRootActor"
        );
    }

    static List<Account> createAccounts() {
        List<Account> result = new ArrayList<>();
        IntStream.range(0, 20000).forEach(
                i -> {
                    result.add(new Account(2, "quymv" + i, "quymv" + i + "@gmail.com", LocalDateTime.now()));
                }
        );

        return result;
    }
}
