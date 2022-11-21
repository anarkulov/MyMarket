package com.erzhan.mymarket.network.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClient {
    private static final String BASE_URL = "http://update.paymob.ru:9996/";
    private static RetrofitApiClient mInstance;
    private final Retrofit retrofit;

    private RetrofitApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitApiClient getInstance() {
        if (mInstance == null) {
            mInstance = new RetrofitApiClient();
        }
        return mInstance;
    }

    public ApiService getApi() {
        return retrofit.create(ApiService.class);
    }
}
