package com.localpro.localproandroid.views.providerDashboard;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.RecentTransactionsAdapter;
import com.localpro.localproandroid.viewmodels.EarningsViewModel;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EarningsFragment extends Fragment {

    private TextView tabWeek, tabMonth, tabYear;
    private LinearLayout sectionWeek, sectionMonth, sectionYear;
    private EarningsViewModel earningsViewModel;

    private TextView tvTotalBalance, tvMonthEarning, tvMonthJobs, tvAvgPerJob;
    private TextView tvWeekEarning, tvWeekJobs;
    private RecyclerView rvTransactions;
    private RecentTransactionsAdapter transactionsAdapter;

    public EarningsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earnings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        earningsViewModel = new ViewModelProvider(this).get(EarningsViewModel.class);

        // Tabs
        tabWeek = view.findViewById(R.id.tabWeek);
        tabMonth = view.findViewById(R.id.tabMonth);
        tabYear = view.findViewById(R.id.tabYear);

        // Sections
        sectionWeek = view.findViewById(R.id.sectionWeek);
        sectionMonth = view.findViewById(R.id.sectionMonth);
        sectionYear = view.findViewById(R.id.sectionYear);

        // Balance headline
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);

        // Month section views
        tvMonthEarning = view.findViewById(R.id.tvMonthEarning);
        tvMonthJobs = view.findViewById(R.id.tvMonthJobs);
        tvAvgPerJob = view.findViewById(R.id.tvAvgPerJob);

        // Week section views
        tvWeekEarning = view.findViewById(R.id.tvWeekEarning);
        tvWeekJobs = view.findViewById(R.id.tvWeekJobs);

        // Click Listeners
        tabWeek.setOnClickListener(v -> selectTab(0));
        tabMonth.setOnClickListener(v -> selectTab(1));
        tabYear.setOnClickListener(v -> selectTab(2));

        // Setup Transactions Recycler
        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        transactionsAdapter = new RecentTransactionsAdapter();
        rvTransactions.setAdapter(transactionsAdapter);
        rvTransactions.setVisibility(View.VISIBLE);

        // Observe
        earningsViewModel.getTotalBalance().observe(getViewLifecycleOwner(), bal -> {
            tvTotalBalance.setText(String.format("LKR %.2f", bal));
        });

        earningsViewModel.getWeekEarnings().observe(getViewLifecycleOwner(), earn -> {
            tvWeekEarning.setText(String.format("LKR %.0f", earn));
        });

        earningsViewModel.getWeekJobs().observe(getViewLifecycleOwner(), count -> {
            tvWeekJobs.setText(count + " Jobs");
        });

        earningsViewModel.getMonthEarnings().observe(getViewLifecycleOwner(), earn -> {
            tvMonthEarning.setText(String.format("LKR %.0f", earn));
        });

        earningsViewModel.getMonthJobs().observe(getViewLifecycleOwner(), count -> {
            tvMonthJobs.setText(count + " Jobs");
        });

        earningsViewModel.getAvgPerJob().observe(getViewLifecycleOwner(), avg -> {
            tvAvgPerJob.setText(String.format("LKR %.0f", avg));
        });

        earningsViewModel.getErrorMsg().observe(getViewLifecycleOwner(), err -> {
            if (err != null && !err.isEmpty()) {
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
            }
        });

        earningsViewModel.getRecentTransactions().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                transactionsAdapter.setTransactions(list);
            }
        });

        // Load real data
        earningsViewModel.loadEarnings();

        // Default selection
        selectTab(1);
    }

    private void selectTab(int index) {
        // Reset all tabs
        resetTab(tabWeek);
        resetTab(tabMonth);
        resetTab(tabYear);

        // Hide all sections
        sectionWeek.setVisibility(View.GONE);
        sectionMonth.setVisibility(View.GONE);
        sectionYear.setVisibility(View.GONE);

        // Highlight selected tab and show section
        switch (index) {
            case 0:
                activeTab(tabWeek, R.drawable.gradient_card_green);
                sectionWeek.setVisibility(View.VISIBLE);
                break;
            case 1:
                activeTab(tabMonth, R.drawable.gradient_card_green);
                sectionMonth.setVisibility(View.VISIBLE);
                break;
            case 2:
                activeTab(tabYear, R.drawable.gradient_card_green);
                sectionYear.setVisibility(View.VISIBLE);
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