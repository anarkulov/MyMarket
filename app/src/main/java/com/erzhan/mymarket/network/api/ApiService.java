package com.erzhan.mymarket.network.api;

import com.erzhan.mymarket.data.models.Software;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("software/latest/all")
    Call<List<Software>> getLatestAllSoftware();

    @GET("software/latest/{type}")
    Call<Software> getSoftwareDetail(
            @Path("type") String type
    );
}
