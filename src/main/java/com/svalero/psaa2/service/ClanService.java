package com.svalero.psaa2.service;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.ApiResponseClans;
import com.svalero.psaa2.domain.ApiResponseLocations;
import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.domain.Location;
import io.reactivex.rxjava3.core.Observable;

public class ClanService {

    private final ClashOfClansApiInterface api;
    private final int locationId;

    public ClanService(int locationId){
        this.locationId = locationId;
        this.api =ClashOfClansApiService.getInstance().create(ClashOfClansApiInterface.class);
    }

    public Observable<Clan> getClans(){
        return this.api.getClansByLocationId(this.locationId, Constants.LIMIT)
                .toObservable()
                .doOnNext(response -> {
                    if (response.getItems() == null || response.getItems().isEmpty())
                        System.out.println("No clans in response");
                    else
                        System.out.println("Clan numbers in response: " + response.getItems().size());
                })
                .doOnComplete(() -> {
                    System.out.println("Observer complete event");
                })
                .flatMapIterable(ApiResponseClans::getItems);
    }

    public Observable<Location> getLocations(){
        return this.api.getLocations(Constants.LIMIT)
                .toObservable()
                .doOnNext(response -> {
                    if (response.getItems() == null || response.getItems().isEmpty())
                        System.out.println("No locations in response");
                    else
                        System.out.println("Location numbers in response: " + response.getItems().size());
                })
                .doOnComplete(() -> {
                    System.out.println("Observer complete event");
                })
                .flatMapIterable(ApiResponseLocations::getItems);
    }
}
