package com.erzhan.mymarket.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.erzhan.mymarket.data.models.Software;
import com.erzhan.mymarket.databinding.FragmentSoftwareDetailBinding;
import com.erzhan.mymarket.utils.DownloadHelper;
import com.erzhan.mymarket.ext.ViewExt;
import com.erzhan.mymarket.utils.DownloadReceiver;
import com.erzhan.mymarket.ui.main.MainViewModel;

public class SoftwareDetailFragment extends Fragment {

    private MainViewModel mViewModel;
    private SoftwareDetailFragmentArgs navArgs;
    private FragmentSoftwareDetailBinding binding;
    private View view;
    private Software software;
    private DownloadReceiver downloadReceiver;

    public SoftwareDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        navArgs = SoftwareDetailFragmentArgs.fromBundle(getArguments());
        mViewModel.getSoftwareDetail(navArgs.getType());
    }

    private void initViewModels() {
        mViewModel.getSoftwareDetail().observe(getViewLifecycleOwner(), software -> {
            if (software != null) {
                setData(software);
            }
        });
    }

    private void setData(Software software) {
        this.software = software;
        binding.tvTitle.setText(software.getTitle());
        binding.tvDescription.setText(software.getDescription());
        binding.tvVersion.setText(software.getAppVersion());
        ViewExt.loadImageUrlToImageView(binding.ivLogo, software.getLogo50Link());

        checkForSoftwareDownloadStatus();

        initListeners();
    }

    private void checkForSoftwareDownloadStatus() {
        if (software == null) return;
        if (DownloadHelper.isPackageInstalled(software.getPackageName(), requireContext().getPackageManager())) {
            binding.btnUninstall.setVisibility(View.VISIBLE);
            if (DownloadHelper.isVersionHigher(software.getPackageName(), requireContext().getPackageManager(), software.getAppVersion())) {
                binding.btnDownloadInstallUpdate.setVisibility(View.VISIBLE);
                binding.btnDownloadInstallUpdate.setText("????????????????");
            } else {
                binding.btnDownloadInstallUpdate.setVisibility(View.VISIBLE);
                binding.btnDownloadInstallUpdate.setText("??????????????");
            }
        } else if (DownloadHelper.isFileDownloaded(software.getLink(), requireContext())) {
            binding.btnUninstall.setVisibility(View.GONE);
            binding.btnDownloadInstallUpdate.setVisibility(View.VISIBLE);
            binding.btnDownloadInstallUpdate.setText("????????????????????");
        } else {
            binding.btnUninstall.setVisibility(View.GONE);
            binding.btnDownloadInstallUpdate.setVisibility(View.VISIBLE);
            binding.btnDownloadInstallUpdate.setText("?????????????? ?? ????????????????????");
        }
    }

    private void initView() {

    }

    private void initListeners() {
        downloadReceiver = new DownloadReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String type = intent.getStringExtra("type");
                Log.d("TAG", "onReceive: " + type);
                if (type.equals(software.getPackageName())) {
                    int downloadProgress = intent.getIntExtra("progress", 0);
                    Log.d("TAG", "onReceive: " + downloadProgress);
                    binding.progressBar.setProgress(downloadProgress);
                    binding.btnDownloadInstallUpdate.setEnabled(false);
                    binding.btnDownloadInstallUpdate.setText("??????????????????????...");
                    if (downloadProgress == 100) {
                        binding.btnDownloadInstallUpdate.setEnabled(true);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        DownloadHelper.stopDownloadService(requireContext());
                        checkForSoftwareDownloadStatus();
                        mViewModel.getSoftwareList();
                    }
                }
            }
        };
        requireContext().registerReceiver(downloadReceiver, new IntentFilter("download_progress"));

        binding.actionBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnUninstall.setOnClickListener(v -> {
            DownloadHelper.uninstallApp(software.getPackageName(), requireContext());
            binding.btnUninstall.setVisibility(View.GONE);
            binding.btnDownloadInstallUpdate.setVisibility(View.VISIBLE);
            binding.btnDownloadInstallUpdate.setText("?????????????? ?? ????????????????????");
        });

        binding.btnDownloadInstallUpdate.setOnClickListener(v -> {
            if (DownloadHelper.isPackageInstalled(software.getPackageName(), requireContext().getPackageManager())) {
                if (DownloadHelper.isVersionHigher(software.getPackageName(), requireContext().getPackageManager(), software.getAppVersion())) {
                    DownloadHelper.downloadFile(software.getLink(), software.getPackageName(), requireContext());
                } else {
                    DownloadHelper.openApp(software.getPackageName(), requireContext());
                }
            } else if (DownloadHelper.isFileDownloaded(software.getLink(), requireContext())) {
                DownloadHelper.installApp(software.getLink(), requireContext());
            } else {
                DownloadHelper.downloadFile(software.getLink(), software.getPackageName(), requireContext());
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnDownloadInstallUpdate.setEnabled(false);
                binding.btnDownloadInstallUpdate.setText("??????????????????????...");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            binding = FragmentSoftwareDetailBinding.inflate(inflater, container, false);
            view = binding.getRoot();
            initView();
        } else {
            binding = FragmentSoftwareDetailBinding.bind(view);
        }
        initViewModels();
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (downloadReceiver != null) {
            requireContext().unregisterReceiver(downloadReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForSoftwareDownloadStatus();
        if (downloadReceiver != null) {
            requireContext().registerReceiver(downloadReceiver, new IntentFilter("download_progress"));
        }
    }
}