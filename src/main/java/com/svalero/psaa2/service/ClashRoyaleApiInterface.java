package com.svalero.psaa2.service;

import com.svalero.psaa2.domain.ApiResponseClashRoyaleClan;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ClashRoyaleApiInterface {

    @GET("locations/{locationId}/rankings/clans")
    Single<ApiResponseClashRoyaleClan> getClashRoyaleRankingClansByLocation(
            @Path("locationId") int locationId,
            @Query("limit") int limit
    );
}
