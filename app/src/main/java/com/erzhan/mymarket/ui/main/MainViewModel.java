package com.erzhan.mymarket.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.erzhan.mymarket.data.models.Software;

import java.util.List;

public class MainViewModel extends ViewModel {

    private MainRepository mainRepository;

    public MainViewModel() {
        mainRepository = new MainRepository();
    }

    public LiveData<List<Software>> getSoftwareList() {
        return mainRepository.getSoftwareList();
    }

    public LiveData<Software> getSoftwareDetail(String type) {
        return mainRepository.getSoftwareDetail(type);
    }
}