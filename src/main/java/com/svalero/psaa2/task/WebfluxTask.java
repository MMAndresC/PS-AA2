package com.svalero.psaa2.task;

import com.svalero.psaa2.domain.Member;
import com.svalero.psaa2.domain.TopClanMembers;
import com.svalero.psaa2.service.WebFluxService;
import com.svalero.psaa2.utils.ErrorLogger;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.concurrent.CountDownLatch;

public class WebfluxTask extends Task<TopClanMembers> {

    private final ObservableList<Member> requestedData;
    private final int locationId;

    public WebfluxTask(ObservableList<Member> requestedData, int locationId) {
        this.requestedData = requestedData;
        this.locationId = locationId;
    }

    @Override
    protected TopClanMembers call() throws Exception {

        WebFluxService webFluxService = new WebFluxService(this.locationId);

        // Include it to synchronize Observer with Task, task end before observer
        CountDownLatch latch = new CountDownLatch(1);

        Consumer<Member> consumer = (member -> {
            System.out.println("Data received: " + member);
            //Space out sending of clans
            Thread.sleep(200);
            Platform.runLater(() -> this.requestedData.add(member));
        });

        // Return topClan
        webFluxService.getTopClanObservable().subscribe(this::updateValue);

        //Subscribe consumer to observable to get members
        webFluxService.getMembersObservable().subscribe(consumer, throwable -> {
            System.err.println("Error receiving data: " + throwable.getMessage());
            ErrorLogger.log(throwable.getMessage());
            latch.countDown(); //Error, end task, unlatch
        }, () -> {
            System.out.println("End sending");
            latch.countDown(); // End sending, end task, unlatch
        });

        //Stand by until resolve or error
        latch.await();

        return null;
    }
}
