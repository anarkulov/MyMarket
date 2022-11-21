package com.erzhan.mymarket.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewbinding.ViewBinding;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erzhan.mymarket.R;
import com.erzhan.mymarket.adapter.SoftwareAdapter;
import com.erzhan.mymarket.data.models.Software;
import com.erzhan.mymarket.databinding.FragmentMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
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
    }

    private void initViews() {
        softwareAdapter = new SoftwareAdapter(softwareList, this::onItemClick);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(softwareAdapter);
    }

    private void onItemClick(String type) {
        Log.d("TAG", "onItemClick: " + type);

        Navigation.findNavController(binding.getRoot()).navigate(MainFragmentDirections.actionMainFragmentToSoftwareDetailFragment(type));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initViewModels() {
        mViewModel.getSoftwareList().observe(getViewLifecycleOwner(), softwareList -> {
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
            initViewModels();
        } else {
            binding = FragmentMainBinding.bind(view);
        }
        return binding.getRoot();
    }
}