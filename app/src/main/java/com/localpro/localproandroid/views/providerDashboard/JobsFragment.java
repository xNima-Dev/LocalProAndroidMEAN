package com.localpro.localproandroid.views.providerDashboard;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.localpro.localproandroid.R;

public class JobsFragment extends Fragment {

    private TextView tabActive, tabCompleted, tabCancelled;
    private LinearLayout sectionActive, sectionCompleted, sectionCancelled;

    public JobsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jobs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tabs
        tabActive = view.findViewById(R.id.tabActive);
        tabCompleted = view.findViewById(R.id.tabCompleted);
        tabCancelled = view.findViewById(R.id.tabCancelled);

        // Sections
        sectionActive = view.findViewById(R.id.sectionActive);
        sectionCompleted = view.findViewById(R.id.sectionCompleted);
        sectionCancelled = view.findViewById(R.id.sectionCancelled);

        // Click Listeners
        tabActive.setOnClickListener(v -> selectTab(0));
        tabCompleted.setOnClickListener(v -> selectTab(1));
        tabCancelled.setOnClickListener(v -> selectTab(2));
        
        // Default selection
        selectTab(0);
    }

    private void selectTab(int index) {
        // Reset all tabs
        resetTab(tabActive);
        resetTab(tabCompleted);
        resetTab(tabCancelled);

        // Hide all sections
        sectionActive.setVisibility(View.GONE);
        sectionCompleted.setVisibility(View.GONE);
        sectionCancelled.setVisibility(View.GONE);

        // Highlight selected tab and show section
        switch (index) {
            case 0:
                activeTab(tabActive, R.drawable.gradient_card_blue);
                sectionActive.setVisibility(View.VISIBLE);
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
        tab.setBackgroundResource(0); // clear background
        if (getContext() != null) {
            tab.setTextColor(ContextCompat.getColor(getContext(), R.color.lp_text_muted));
        }
    }

    private void activeTab(TextView tab, int backgroundRes) {
        tab.setBackgroundResource(backgroundRes);
        tab.setTextColor(Color.WHITE);
    }
}