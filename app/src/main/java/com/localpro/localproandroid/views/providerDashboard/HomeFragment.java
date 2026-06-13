package com.localpro.localproandroid.views.providerDashboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.databinding.FragmentHomeBinding;
import com.localpro.localproandroid.viewmodels.ProviderDashboardHomeViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProviderDashboardHomeViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProviderDashboardHomeViewModel.class);

        viewModel.getProviderName().observe(getViewLifecycleOwner(), name -> {
            binding.tvProviderName.setText(name);
        });

        viewModel.getProviderEmail().observe(getViewLifecycleOwner(), email -> {
            binding.tvProviderEmail.setText(email);
        });

        viewModel.loadProviderInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}