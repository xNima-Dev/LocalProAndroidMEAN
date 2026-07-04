package com.localpro.localproandroid.views;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.viewmodels.JobTrackingViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class JobTrackingActivity extends AppCompatActivity {

    private JobTrackingViewModel viewModel;
    private String bookingId = "12345";
    private LinearLayout layoutPaymentPanel, layoutLoading, btnJobAction;
    private TextView tvStatusBadge, tvActionLabel, tvActionIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_tracking);

        viewModel = new ViewModelProvider(this).get(JobTrackingViewModel.class);
        initViews();
        setupObservers();
        setupClickListeners();
    }

    private void initViews() {
        layoutPaymentPanel = findViewById(R.id.layoutPaymentPanel);
        layoutLoading = findViewById(R.id.layoutLoading);
        btnJobAction = findViewById(R.id.btnJobAction);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvActionLabel = findViewById(R.id.tvActionLabel);
//        tvActionIcon = findViewById(R.id.tvActionIcon);
    }

    private void setupObservers() {
        viewModel.getJobStatus().observe(this, status -> {
            updateUI(status);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            layoutLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getSuccessMsg().observe(this, msg -> {
            if(msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI(String status) {
        switch (status) {
            case "ride":
                tvStatusBadge.setText("RIDING");
                tvActionLabel.setText("I HAVE ARRIVED");
                layoutPaymentPanel.setVisibility(View.GONE);
                break;
            case "arrived":
                tvStatusBadge.setText("📍 ARRIVED");
                tvActionLabel.setText("COMPLETE JOB");
                layoutPaymentPanel.setVisibility(View.GONE);
                break;
            case "completed":
                tvStatusBadge.setText("COMPLETED");
                tvActionLabel.setVisibility(View.GONE);
                layoutPaymentPanel.setVisibility(View.VISIBLE);
                break;
            case "paid":
            case "unpaid":
                finish();
                break;
        }
    }

    private void setupClickListeners() {
        btnJobAction.setOnClickListener(v -> {
            String currentStatus = viewModel.getJobStatus().getValue();
            if ("ride".equals(currentStatus)) {
                viewModel.markAsArrived(bookingId);
            } else if ("arrived".equals(currentStatus)) {
                viewModel.markAsCompleted(bookingId);
            }
        });

        findViewById(R.id.btnMarkPaid).setOnClickListener(v -> {
            viewModel.markAsPaid(bookingId);
        });

        findViewById(R.id.btnMarkUnpaid).setOnClickListener(v -> {
            viewModel.markAsUnpaid(bookingId);
        });
    }
}