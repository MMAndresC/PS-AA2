package com.svalero.psaa2.service;

import com.svalero.psaa2.domain.ApiResponseClans;
import com.svalero.psaa2.domain.ApiResponseLocations;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClashOfClansApiInterface {

    @GET("clans")
    Single<ApiResponseClans> getClansByLocationId(
            @Query("locationId") int locationId,
            @Query("limit") int limit
    );

    @GET("locations")
    Single<ApiResponseLocations> getLocations(@Query("limit") int limit);
}
