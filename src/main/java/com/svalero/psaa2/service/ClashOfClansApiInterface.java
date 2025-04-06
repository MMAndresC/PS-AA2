package com.svalero.psaa2.service;

import com.svalero.psaa2.domain.ApiResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ClashOfClansApiInterface {

    @GET("clans")
    Single<ApiResponse> getClansByWarFrequency(
            @Header("Authorization") String token,
            @Query("warFrequency") String warFrequency,
            @Query("limit") int limit
    );
}
