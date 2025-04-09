package com.svalero.psaa2.task;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.Location;
import com.svalero.psaa2.service.ClanService;
import com.svalero.psaa2.utils.ErrorLogger;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GetLocationsApiTask extends Task<Void> {

    private final ObservableList<Location> locationData;

    private final String after;

    private List<String> attrAfter = new ArrayList<>();

    public GetLocationsApiTask(ObservableList<Location> locationData, List<String> attrAfter){
        this.locationData = locationData;
        this.attrAfter = attrAfter;
        if(this.attrAfter.isEmpty()) this.after = null;
        else this.after = attrAfter.getLast();
    }

    @Override
    protected Void call() throws Exception {
        // No need locationId but constructor need it
        ClanService clanService = new ClanService(Constants.LOCATION_ID_SPAIN, after);

        // Include it to synchronize Observer with Task, task end before observer
        CountDownLatch latch = new CountDownLatch(1);

        Consumer<Location> consumer = (location -> {
            System.out.println("Data received: " + location);
            //Space out sending of locations
            Thread.sleep(200);
            Platform.runLater(() -> this.locationData.add(location));
        });

        // Subscribe consumer to observable to get after
        clanService.getAfterCursor().subscribe(after -> {
            if(!after.isEmpty()){
                // Stop saving in list when there is no more next
                this.attrAfter.add(after);
            }
        });

        //Subscribe consumer to observable
        clanService.getLocations().subscribe(consumer, throwable -> {
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
