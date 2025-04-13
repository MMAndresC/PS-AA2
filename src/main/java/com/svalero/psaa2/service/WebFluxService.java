package com.svalero.psaa2.service;

import com.svalero.psaa2.constants.Constants;
import com.svalero.psaa2.domain.Member;
import com.svalero.psaa2.domain.TopClanMembers;
import io.reactivex.rxjava3.core.Observable;

public class WebFluxService {

    private final WebFluxInterface api;

    private Observable<TopClanMembers> observableTopClanMembers = null;

    public WebFluxService(int locationId) {
        this.api = ApiService.getInstance("", Constants.WEBFLUX_URL).create(WebFluxInterface.class);
        // Want to divide response and create two observers
        this.observableTopClanMembers = this.api.getTopClanMembers(locationId).cache();
    }


    public Observable<TopClanMembers> getTopClanObservable(){
        return observableTopClanMembers.map(topClan ->
                new TopClanMembers(
                        topClan.getClanName(),
                        topClan.getLocationName(),
                        topClan.getBadgeUrl(),
                        topClan.getRank(),
                        topClan.getPreviousRank(),
                        null
                )
        );
    }

    public Observable<Member> getMembersObservable() {
        return observableTopClanMembers
                .flatMapIterable(TopClanMembers::getMembers);
    }
}
