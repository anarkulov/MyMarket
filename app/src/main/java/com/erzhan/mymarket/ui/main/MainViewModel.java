package com.erzhan.mymarket.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.erzhan.mymarket.data.models.Software;

import java.util.List;

public class MainViewModel extends ViewModel {

    private MainRepository mainRepository;
    private LiveData<List<Software>> softwareListLiveData;

    public MainViewModel() {
        mainRepository = new MainRepository();
    }

    public void getSoftwareList() {
        softwareListLiveData = mainRepository.getSoftwareList();
    }

    public LiveData<List<Software>> getSoftwareListLiveData() {
        return softwareListLiveData;
    }

    public LiveData<Software> getSoftwareDetail(String type) {
        return mainRepository.getSoftwareDetail(type);
    }
}