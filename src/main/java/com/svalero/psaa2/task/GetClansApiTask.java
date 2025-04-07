package com.svalero.psaa2.task;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.service.ClanService;
import com.svalero.psaa2.utils.ErrorLogger;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class GetClansApiTask extends Task<Void> {

    private final ObservableList<Clan> requestedData;

    public GetClansApiTask(ObservableList<Clan> requestedData){
        this.requestedData = requestedData;
    }
    @Override
    protected Void call() throws Exception {
        ClanService clanService = new ClanService(Constants.LOCATION_ID_SPAIN);
        Consumer<Clan> consumer = (clan -> {
            System.out.println("Data received: " + clan);
            //Space out sending of clans
            Thread.sleep(100);
            Platform.runLater(() -> this.requestedData.add(clan));
        });

        //Subscribe consumer to observable
        clanService.getClans().subscribe(consumer, throwable -> {
            System.err.println("Error receiving data: " + throwable.getMessage());
            ErrorLogger.log(throwable.getMessage());
        });
        return null;
    }
}
