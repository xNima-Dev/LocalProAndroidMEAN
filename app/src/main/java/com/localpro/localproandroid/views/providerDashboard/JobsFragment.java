package com.localpro.localproandroid.views.providerDashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.ActiveJobsAdapter;
import com.localpro.localproandroid.adapter.CompletedJobsAdapter;
import com.localpro.localproandroid.adapter.CancelledJobsAdapter;
import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.viewmodels.JobsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class JobsFragment extends Fragment implements ActiveJobsAdapter.OnActiveJobActionListener {

    // Views
    private RecyclerView rvActiveJobs, rvCompletedJobs, rvCancelledJobs;
    private TextView tvEmptyJobs;
    private TextView tabActive, tabCompleted, tabCancelled;

    private JobsViewModel jobsViewModel;
    private ActiveJobsAdapter activeJobsAdapter;
    private CompletedJobsAdapter completedJobsAdapter;
    private CancelledJobsAdapter cancelledJobsAdapter;
    private int currentTab = 0;

    public JobsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jobs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jobsViewModel = new ViewModelProvider(this).get(JobsViewModel.class);

        // 1. View Initialization
        rvActiveJobs = view.findViewById(R.id.rvActiveJobs);
        rvCompletedJobs = view.findViewById(R.id.rvCompletedJobs);
        rvCancelledJobs = view.findViewById(R.id.rvCancelledJobs);
        tvEmptyJobs = view.findViewById(R.id.tvEmptyJobs);

        // Header Tabs (මේ IDs ඔයාගේ XML එකේ තියෙන්න ඕනේ)
        tabActive = view.findViewById(R.id.tabActive);
        tabCompleted = view.findViewById(R.id.tabCompleted);
        tabCancelled = view.findViewById(R.id.tabCancelled);

        // 2. Adapters Setup
        activeJobsAdapter = new ActiveJobsAdapter(this);
        completedJobsAdapter = new CompletedJobsAdapter();
        cancelledJobsAdapter = new CancelledJobsAdapter();

        rvActiveJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCompletedJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCancelledJobs.setLayoutManager(new LinearLayoutManager(requireContext()));

        rvActiveJobs.setAdapter(activeJobsAdapter);
        rvCompletedJobs.setAdapter(completedJobsAdapter);
        rvCancelledJobs.setAdapter(cancelledJobsAdapter);

        // 3. Tab Click Listeners
        tabActive.setOnClickListener(v -> selectTab(0));
        tabCompleted.setOnClickListener(v -> selectTab(1));
        tabCancelled.setOnClickListener(v -> selectTab(2));

        // 4. Data Observation (ViewModel)
        jobsViewModel.getActiveJobs().observe(getViewLifecycleOwner(), list -> {
            activeJobsAdapter.setJobs(list);
            if (currentTab == 0) {
                updateEmptyState(list == null || list.isEmpty());
            }
        });

        jobsViewModel.getCompletedJobs().observe(getViewLifecycleOwner(), list -> {
            completedJobsAdapter.setJobsList(list);
            if (currentTab == 1) {
                updateEmptyState(list == null || list.isEmpty());
            }
        });

        jobsViewModel.getCancelledJobs().observe(getViewLifecycleOwner(), list -> {
            cancelledJobsAdapter.setJobsList(list);
            if (currentTab == 2) {
                updateEmptyState(list == null || list.isEmpty());
            }
        });

        // Default: Active tab එක පෙන්වන්න
        selectTab(0);
    }

    private void selectTab(int index) {
        currentTab = index;
        // Tab UI එක වෙනස් කරන්න
        resetTabStyles();
        if (index == 0) {
            tabActive.setTextColor(android.graphics.Color.WHITE);
            tabActive.setBackgroundResource(R.drawable.bg_chip);
            tabActive.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#00D2FF")));
            jobsViewModel.loadActiveJobs();
        } else if (index == 1) {
            tabCompleted.setTextColor(android.graphics.Color.WHITE);
            tabCompleted.setBackgroundResource(R.drawable.bg_chip);
            tabCompleted.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#00D2FF")));
            jobsViewModel.loadCompletedJobs();
        } else if (index == 2) {
            tabCancelled.setTextColor(android.graphics.Color.WHITE);
            tabCancelled.setBackgroundResource(R.drawable.bg_chip);
            tabCancelled.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#00D2FF")));
            jobsViewModel.loadCancelledJobs();
        }

        // RecyclerViews පෙන්වන්න/සඟවන්න
        rvActiveJobs.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        rvCompletedJobs.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        rvCancelledJobs.setVisibility(index == 2 ? View.VISIBLE : View.GONE);
    }

    private void resetTabStyles() {
        tabActive.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
        tabActive.setBackgroundResource(android.R.color.transparent);
        tabActive.setBackgroundTintList(null);
        
        tabCompleted.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
        tabCompleted.setBackgroundResource(android.R.color.transparent);
        tabCompleted.setBackgroundTintList(null);
        
        tabCancelled.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
        tabCancelled.setBackgroundResource(android.R.color.transparent);
        tabCancelled.setBackgroundTintList(null);
    }

    private void updateEmptyState(boolean isEmpty) {
        tvEmptyJobs.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCancelJob(BookingRequest request, int position) {
        jobsViewModel.cancelActiveJob(request);
    }

    @Override
    public void onStartJob(BookingRequest request) {
        android.content.Intent intent = new android.content.Intent(requireContext(), com.localpro.localproandroid.views.JobTrackingActivity.class);
        intent.putExtra("booking_id", request.getId());
        intent.putExtra("booking_status", request.getStatus());
        intent.putExtra("customer_name", request.getCustomerName());
        intent.putExtra("customer_phone", request.getCustomerPhone());
        intent.putExtra("customer_initial", request.getInitial());
        intent.putExtra("service_category", request.getServiceCategory());
        intent.putExtra("distance_text", request.getDistanceText());
        intent.putExtra("estimated_earning", request.getEstimatedEarning());
        intent.putExtra("customer_lat", request.getCustomerLat());
        intent.putExtra("customer_lon", request.getCustomerLon());
        startActivity(intent);
    }
}