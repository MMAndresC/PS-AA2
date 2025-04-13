package com.svalero.psaa2.task;

import com.svalero.psaa2.domain.ClashRoyaleClan;
import com.svalero.psaa2.service.ClashRoyaleService;
import com.svalero.psaa2.utils.ErrorLogger;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.concurrent.CountDownLatch;

public class GetClashRoyalRankingTask extends Task<Void> {

    private final ObservableList<ClashRoyaleClan> requestedData;
    private final int locationId;

    public GetClashRoyalRankingTask(ObservableList<ClashRoyaleClan> requestedData, int locationId) {
        this.requestedData = requestedData;
        this.locationId = locationId;
    }

    @Override
    protected Void call() throws Exception {
        ClashRoyaleService clashRoyaleService = new ClashRoyaleService(this.locationId);

        // Include it to synchronize Observer with Task, task end before observer
        CountDownLatch latch = new CountDownLatch(1);

        Consumer<ClashRoyaleClan> consumer = (clan ->{
            System.out.println("Data received: " + clan);
            //Space out sending of clans
            Thread.sleep(200);
            Platform.runLater(() -> this.requestedData.add(clan));
        });

        //Subscribe consumer to observable to get items
        clashRoyaleService.getClashRoyaleRankingClansByLocation().subscribe(consumer, throwable -> {
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
