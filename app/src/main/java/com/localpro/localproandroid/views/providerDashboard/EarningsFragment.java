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

public class EarningsFragment extends Fragment {

    private TextView tabWeek, tabMonth, tabYear;
    private LinearLayout sectionWeek, sectionMonth, sectionYear;

    public EarningsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earnings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tabs
        tabWeek = view.findViewById(R.id.tabWeek);
        tabMonth = view.findViewById(R.id.tabMonth);
        tabYear = view.findViewById(R.id.tabYear);

        // Sections
        sectionWeek = view.findViewById(R.id.sectionWeek);
        sectionMonth = view.findViewById(R.id.sectionMonth);
        sectionYear = view.findViewById(R.id.sectionYear);

        // Click Listeners
        tabWeek.setOnClickListener(v -> selectTab(0));
        tabMonth.setOnClickListener(v -> selectTab(1));
        tabYear.setOnClickListener(v -> selectTab(2));
        
        // Default selection (Month is the default in the layout, so let's select it)
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