package com.erzhan.mymarket.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.erzhan.mymarket.adapter.SoftwareAdapter;
import com.erzhan.mymarket.data.models.Software;
import com.erzhan.mymarket.databinding.FragmentMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private SoftwareAdapter softwareAdapter;
    private List<Software> softwareList;
    private FragmentMainBinding binding;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        softwareList = new ArrayList<>();
        softwareAdapter = new SoftwareAdapter(softwareList, this::onSoftwareItemClick);
        mViewModel.getSoftwareList();
    }

    private void initViews() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(softwareAdapter);
    }

    private void onSoftwareItemClick(String type) {
        Log.d("TAG", "onSoftwareItemClick: " + type);

        Navigation.findNavController(binding.getRoot()).navigate(MainFragmentDirections.actionMainFragmentToSoftwareDetailFragment(type));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initViewModels() {
        mViewModel.getSoftwareListLiveData().observe(getViewLifecycleOwner(), softwareList -> {
            if (softwareList != null) {
                this.softwareList.clear();
                this.softwareList.addAll(softwareList);
                softwareAdapter.notifyDataSetChanged();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (view == null) {
            binding = FragmentMainBinding.inflate(inflater, container, false);
            view = binding.getRoot();
            initViews();
        } else {
            binding = FragmentMainBinding.bind(view);
            mViewModel.getSoftwareList();
        }
        initViewModels();
        return binding.getRoot();
    }
}