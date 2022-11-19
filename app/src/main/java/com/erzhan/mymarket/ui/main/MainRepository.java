package com.erzhan.mymarket.ui.main;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.erzhan.mymarket.api.ApiService;
import com.erzhan.mymarket.api.RetrofitApiClient;
import com.erzhan.mymarket.data.models.Software;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRepository {
    private ApiService apiService;

    public MainRepository() {
        apiService = RetrofitApiClient.getInstance().getApi();
    }

    public LiveData<List<Software>> getSoftwareList() {
        final MutableLiveData<List<Software>> data = new MutableLiveData<>();

        apiService.getLatestAllSoftware().enqueue(new Callback<List<Software>>() {
            @Override
            public void onResponse(@NonNull Call<List<Software>> call, @NonNull Response<List<Software>> response) {
                data.setValue(response.body());
                Log.d("TAG", "onResponse: " + response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Software>> call, @NonNull Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                data.setValue(null);
            }
        });

        return data;
    }

}
