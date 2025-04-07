package com.svalero.psaa2.service;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.ApiResponse;
import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.env.ApiKey;
import io.reactivex.rxjava3.core.Observable;

public class ClanService {

    private final ClashOfClansApiInterface api;
    private final int locationId;

    public ClanService(int locationId){
        this.locationId = locationId;
        this.api =ClashOfClansApiService.getInstance().create(ClashOfClansApiInterface.class);
    }

    public Observable<Clan> getClans(){
        return this.api.getClansByWarFrequency(this.locationId, Constants.LIMIT)
                .toObservable()
                .doOnNext(response -> {
                    if (response.getItems() == null || response.getItems().isEmpty())
                        System.out.println("No clans in response");
                    else
                        System.out.println("Clan numbers in response: " + response.getItems().size());
                })
                .doOnComplete(() -> {
                    System.out.println("End sending");
                })
                .flatMapIterable(ApiResponse::getItems);
    }
}
