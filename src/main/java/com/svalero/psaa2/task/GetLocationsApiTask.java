package com.svalero.psaa2.task;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.Location;
import com.svalero.psaa2.service.ClanService;
import com.svalero.psaa2.utils.ErrorLogger;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class GetLocationsApiTask extends Task<Void> {

    private final ObservableList<Location> locationData;

    public GetLocationsApiTask(ObservableList<Location> locationData){
        this.locationData = locationData;
    }
    @Override
    protected Void call() throws Exception {
        // No need locationId but constructor need it
        ClanService clanService = new ClanService(Constants.LOCATION_ID_SPAIN);
        Consumer<Location> consumer = (location -> {
            System.out.println("Data received: " + location);
            //Space out sending of locations
            Thread.sleep(200);
            Platform.runLater(() -> this.locationData.add(location));
        });

        //Subscribe consumer to observable
        clanService.getLocations().subscribe(consumer, throwable -> {
            System.err.println("Error receiving data: " + throwable.getMessage());
            ErrorLogger.log(throwable.getMessage());
        });

        return null;
    }
}
