package com.erzhan.mymarket.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erzhan.mymarket.R;
import com.erzhan.mymarket.data.models.Software;
import com.erzhan.mymarket.databinding.FragmentSoftwareDetailBinding;
import com.erzhan.mymarket.ext.ViewExt;
import com.erzhan.mymarket.ui.main.MainViewModel;

import java.util.ArrayList;

public class SoftwareDetailFragment extends Fragment {

    private MainViewModel mViewModel;
    private SoftwareDetailFragmentArgs navArgs;
    private FragmentSoftwareDetailBinding binding;
    private View view;

    public SoftwareDetailFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        navArgs = SoftwareDetailFragmentArgs.fromBundle(getArguments());
    }

    private void initViewModels() {
        mViewModel.getSoftwareDetail(navArgs.getType()).observe(getViewLifecycleOwner(), software -> {
            if (software != null) {
                setData(software);
            }
        });
    }

    private void setData(Software software) {
        binding.tvTitle.setText(software.getTitle());
        binding.tvDescription.setText(software.getDescription());
        binding.tvVersion.setText(software.getAppVersion());
        ViewExt.loadUrl(binding.ivLogo, software.getLogo50Link());
    }

    private void initView() {

    }

    private void initListeners() {
        binding.actionBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            binding = FragmentSoftwareDetailBinding.inflate(inflater, container, false);
            view = binding.getRoot();
        } else {
            binding = FragmentSoftwareDetailBinding.bind(view);
        }

        initView();
        initViewModels();
        initListeners();

        return binding.getRoot();
    }

    public static SoftwareDetailFragment newInstance() {
        SoftwareDetailFragment fragment = new SoftwareDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}