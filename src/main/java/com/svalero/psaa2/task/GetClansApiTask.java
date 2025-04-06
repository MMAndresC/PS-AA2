package com.svalero.psaa2.task;

import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.service.ClanService;
import io.reactivex.rxjava3.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class GetClansApiTask extends Task<Void> {

    private final String warFrequency;
    private final ObservableList<Clan> requestedData;

    public GetClansApiTask(String warFrequency, ObservableList<Clan> requestedData){
        this.warFrequency = warFrequency;
        this.requestedData = requestedData;
    }
    @Override
    protected Void call() throws Exception {
        ClanService clanService = new ClanService(this.warFrequency);
        Consumer<Clan> consumer = (clan -> {
            System.out.println("Data received: " + clan);
            //Simular retardo para testear concurrencia
            Thread.sleep(100);
            Platform.runLater(() -> this.requestedData.add(clan));
        });

        //Subscribir el consumer al observable para que le lleguen los datos
        clanService.getClans().subscribe(consumer, throwable -> {
            System.err.println("Error receiving data: " + throwable.getMessage());
            //TODO ver como poner esto para que le llegue al controller cuando controle los estados de la task
            throwable.printStackTrace();
        });
        return null;
    }
}
