package com.localpro.localproandroid.views.providerDashboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.ActiveJobsAdapter;
import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.viewmodels.JobsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class JobsFragment extends Fragment implements ActiveJobsAdapter.OnActiveJobActionListener {

    private TextView tabActive, tabCompleted, tabCancelled;
    private LinearLayout sectionActive, sectionCompleted, sectionCancelled;

    private RecyclerView rvActiveJobs;
    private TextView tvEmptyActiveJobs;

    private JobsViewModel jobsViewModel;
    private ActiveJobsAdapter activeJobsAdapter;

    public JobsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jobs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel Initialize
        jobsViewModel = new ViewModelProvider(this).get(JobsViewModel.class);

        // UI Components
        tabActive = view.findViewById(R.id.tabActive);
        tabCompleted = view.findViewById(R.id.tabCompleted);
        tabCancelled = view.findViewById(R.id.tabCancelled);
        sectionActive = view.findViewById(R.id.sectionActive);
        sectionCompleted = view.findViewById(R.id.sectionCompleted);
        sectionCancelled = view.findViewById(R.id.sectionCancelled);
        rvActiveJobs = view.findViewById(R.id.rvActiveJobs);
        tvEmptyActiveJobs = view.findViewById(R.id.tvEmptyActiveJobs);

        // RecyclerView Setup
        rvActiveJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        activeJobsAdapter = new ActiveJobsAdapter(this);
        rvActiveJobs.setAdapter(activeJobsAdapter);

        // Observe Active Jobs from ViewModel
        jobsViewModel.getActiveJobs().observe(getViewLifecycleOwner(), jobsList -> {
            if (jobsList != null && !jobsList.isEmpty()) {
                activeJobsAdapter.setJobs(jobsList);
                rvActiveJobs.setVisibility(View.VISIBLE);
                tvEmptyActiveJobs.setVisibility(View.GONE);
            } else {
                rvActiveJobs.setVisibility(View.GONE);
                tvEmptyActiveJobs.setVisibility(View.VISIBLE);
            }
        });
        // Observe Errors
        jobsViewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });



        // Tab Click Listeners
        tabActive.setOnClickListener(v -> selectTab(0));
        tabCompleted.setOnClickListener(v -> selectTab(1));
        tabCancelled.setOnClickListener(v -> selectTab(2));

        // Default: Active tab
        selectTab(0);
    }

    // Called when "Cancel Booking" is pressed in the adapter
    @Override
    public void onCancelJob(com.localpro.localproandroid.models.BookingRequest request, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this job?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    jobsViewModel.cancelActiveJob(request);
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onStartJob(BookingRequest request) {
        Intent intent = new Intent(requireContext(), com.localpro.localproandroid.views.JobTrackingActivity.class);
        intent.putExtra("booking_id", request.getId());
        startActivity(intent);
    }

    private void selectTab(int index) {
        resetTab(tabActive);
        resetTab(tabCompleted);
        resetTab(tabCancelled);

        sectionActive.setVisibility(View.GONE);
        sectionCompleted.setVisibility(View.GONE);
        sectionCancelled.setVisibility(View.GONE);

        switch (index) {
            case 0:
                activeTab(tabActive, R.drawable.gradient_card_blue);
                sectionActive.setVisibility(View.VISIBLE);
                jobsViewModel.loadActiveJobs(); // Refresh data on tab open
                break;
            case 1:
                activeTab(tabCompleted, R.drawable.gradient_card_blue);
                sectionCompleted.setVisibility(View.VISIBLE);
                break;
            case 2:
                activeTab(tabCancelled, R.drawable.gradient_card_blue);
                sectionCancelled.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(0);
        if (getContext() != null) {
            tab.setTextColor(ContextCompat.getColor(getContext(), R.color.lp_text_muted));
        }
    }

    private void activeTab(TextView tab, int backgroundRes) {
        tab.setBackgroundResource(backgroundRes);
        tab.setTextColor(Color.WHITE);
    }
}