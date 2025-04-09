package com.svalero.psaa2.service;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.*;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;


public class ClanService {

    private final ClashOfClansApiInterface api;

    private final int locationId;

    private final String after;

    // Send the last saved value to the new subscribers
    private final BehaviorSubject<String> afterCursorSubject = BehaviorSubject.create();

    public ClanService(int locationId, String after){
        this.locationId = locationId;
        this.api =ClashOfClansApiService.getInstance().create(ClashOfClansApiInterface.class);
        this.after = after;
    }

    public Observable<String> getAfterCursor(){
        return afterCursorSubject.hide();
    }

    public Observable<Clan> getClans(){
        return this.api.getClansByLocationId(this.locationId, Constants.LIMIT, this.after)
                .toObservable()
                .doOnNext(response -> {
                    if (response.getItems() == null || response.getItems().isEmpty())
                        System.out.println("No clans in response");
                    else{
                        System.out.println("Clan numbers in response: " + response.getItems().size());
                        String after = response.getPaging().getCursors().getAfter();
                        if(after == null) after = "";
                        // Publish value to consumers, only once
                        afterCursorSubject.onNext(after);
                    }
                })
                .doOnComplete(() -> {
                    System.out.println("Observer complete event");
                })
                .flatMapIterable(ApiResponseClans::getItems);
    }

    public Observable<Location> getLocations(){
        return this.api.getLocations(Constants.LIMIT, this.after)
                .toObservable()
                .doOnNext(response -> {
                    if (response.getItems() == null || response.getItems().isEmpty())
                        System.out.println("No locations in response");
                    else{
                        System.out.println("Location numbers in response: " + response.getItems().size());
                        String after = response.getPaging().getCursors().getAfter();
                        if(after == null) after = "";
                        // Publish value to consumers, only once
                        afterCursorSubject.onNext(after);
                    }
                })
                .doOnComplete(() -> {
                    System.out.println("Observer complete event");
                })
                .flatMapIterable(ApiResponseLocations::getItems);
    }
}
