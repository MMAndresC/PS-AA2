package com.svalero.psaa2.service;

import com.svalero.psaa2.domain.TopClanMembers;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface WebFluxInterface {
    @GET("{locationId}/members")
    Observable<TopClanMembers> getTopClanMembers(@Path("locationId") int locationId);
}
