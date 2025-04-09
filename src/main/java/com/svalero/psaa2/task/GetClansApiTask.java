package com.svalero.psaa2.task;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.service.ClanService;
import com.svalero.psaa2.utils.ErrorLogger;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GetClansApiTask extends Task<Void> {

    private final ObservableList<Clan> requestedData;
    private final String after;
    private List<String> attrAfter = new ArrayList<>();

    public GetClansApiTask(ObservableList<Clan> requestedData,  List<String> attrAfter) {
        this.requestedData = requestedData;
        this.attrAfter = attrAfter;
        if(this.attrAfter.isEmpty()) this.after = null;
        else this.after = attrAfter.getLast();
    }

    @Override
    protected Void call() throws Exception {
        ClanService clanService = new ClanService(Constants.LOCATION_ID_SPAIN, this.after);

        // Include it to synchronize Observer with Task, task end before observer
        CountDownLatch latch = new CountDownLatch(1);

        Consumer<Clan> consumer = (clan -> {
            System.out.println("Data received: " + clan);
            //Space out sending of clans
            Thread.sleep(200);
            Platform.runLater(() -> this.requestedData.add(clan));
        });

        // Subscribe consumer to observable to get after
        clanService.getAfterCursor().subscribe(after -> {
            if(!after.isEmpty()){
                // Stop saving in list when there is no more next
                this.attrAfter.add(after);
            }
        });

        //Subscribe consumer to observable to get items
        clanService.getClans().subscribe(consumer, throwable -> {
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
