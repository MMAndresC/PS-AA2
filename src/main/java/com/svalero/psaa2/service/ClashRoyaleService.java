package com.svalero.psaa2.service;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.ApiResponseClashRoyaleClan;
import com.svalero.psaa2.domain.ClashRoyaleClan;
import com.svalero.psaa2.env.ApiKey;
import io.reactivex.rxjava3.core.Observable;

public class ClashRoyaleService {

    private final ClashRoyaleApiInterface api;

    private final int locationId;

    public ClashRoyaleService(int locationId) {
        this.locationId = locationId;
        this.api = ApiService.getInstance(ApiKey.CR_TOKEN, Constants.CR_URL).create(ClashRoyaleApiInterface.class);
    }

    public Observable<ClashRoyaleClan> getClashRoyaleRankingClansByLocation(){
        return this.api.getClashRoyaleRankingClansByLocation(this.locationId, Constants.LIMIT_RANKING)
                .toObservable()
                .doOnNext(response -> {
                    if (response.getItems() == null || response.getItems().isEmpty())
                        System.out.println("No ranking clans in response");
                    else{
                        System.out.println("Ranking clan numbers in response: " + response.getItems().size());
                    }
                })
                .doOnComplete(() -> {
                    System.out.println("Observer complete event");
                })
                .flatMapIterable(ApiResponseClashRoyaleClan::getItems);
    }
}
