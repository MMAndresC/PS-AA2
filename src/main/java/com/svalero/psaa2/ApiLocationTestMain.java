package com.svalero.psaa2;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.Location;
import com.svalero.psaa2.service.ClanService;
import io.reactivex.rxjava3.functions.Consumer;

import java.util.concurrent.CountDownLatch;

public class ApiLocationTestMain {

    public static void main(String[] args) {

        int locationId = Constants.LOCATION_ID_SPAIN;
        ClanService clanService = new ClanService(locationId);

        //Evitar que se cierre el programa antes de que termine de recepcionar los datos de la aoi
        CountDownLatch latch = new CountDownLatch(1);

        Consumer<Location> onNext = location -> System.out.println("Received: " + location);
        Consumer<Throwable> onError = error -> {
            System.err.println("Error: " + error.getMessage());
            error.printStackTrace();
            latch.countDown(); // Let app end
        };

        clanService.getLocations()
                .doOnSubscribe(disposable -> System.out.println("Calling API..."))
                .doOnComplete(() -> {
                    System.out.println("Complete");
                    latch.countDown(); // Complete task, unlatch
                })
                .subscribe(onNext, onError);

        // Aqui es donde se queda hasta que termine o haya un error que le obligue a terminar
        try {
            latch.await(); // Stay here until a call to latch.countDown
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Force closing thread");
        }
    }
}
