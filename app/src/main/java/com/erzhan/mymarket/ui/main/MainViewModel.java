package com.erzhan.mymarket.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.erzhan.mymarket.data.models.Software;

import java.util.List;

public class MainViewModel extends ViewModel {

    private MainRepository mainRepository;
    private LiveData<List<Software>> softwareListLiveData;
    private LiveData<Software> softwareDetailLiveData;

    public MainViewModel() {
        mainRepository = new MainRepository();
    }

    public void getSoftwareList() {
        softwareListLiveData = mainRepository.getSoftwareList();
    }

    public LiveData<List<Software>> getSoftwareListLiveData() {
        return softwareListLiveData;
    }

    public void getSoftwareDetail(String type) {
        softwareDetailLiveData = mainRepository.getSoftwareDetail(type);
    }
    public LiveData<Software> getSoftwareDetail() {
        return softwareDetailLiveData;
    }
}