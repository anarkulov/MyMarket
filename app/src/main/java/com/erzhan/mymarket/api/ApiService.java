package com.erzhan.mymarket.api;

import com.erzhan.mymarket.data.models.Software;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("software/latest/all")
    Call<List<Software>> getLatestAllSoftware();
}
