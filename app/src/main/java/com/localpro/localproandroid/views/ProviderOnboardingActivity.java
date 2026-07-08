package com.localpro.localproandroid.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.viewmodels.OnboardingViewModel;

public class ProviderOnboardingActivity extends AppCompatActivity {

    private TextView tvStepIndicator;
    private ProgressBar pbSteps;
    private View layoutLoading;

    private OnboardingViewModel viewModel;
    private String userId;
    private int currentStep = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_onboarding);

        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null) {
            SharedPreferences sharedPrefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
            userId = sharedPrefs.getString("user_id", null);
        }

        if (userId == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvStepIndicator = findViewById(R.id.tvStepIndicator);
        pbSteps = findViewById(R.id.pbSteps);
        layoutLoading = findViewById(R.id.layoutLoading);

        // Initialize Shared ViewModel scoped to this Activity
        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);

        setupObservers();

        // Load the first step fragment
        goToStep(1);
    }

    private void setupObservers() {
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                layoutLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe errors
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        // Observe API success
        viewModel.getOnboardingResult().observe(this, response -> {
            if (response != null) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();

                // Save onboarding state in SharedPreferences
                SharedPreferences sharedPrefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
                String userId = sharedPrefs.getString("user_id", "");
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("is_onboarded_" + userId, true);

                // Cache category and bio so Profile screen can display them
                String cat = viewModel.getCategory().getValue();
                String bio = viewModel.getBio().getValue();
                if (cat != null && !cat.trim().isEmpty()) {
                    editor.putString("provider_category", cat.trim());
                }
                if (bio != null && !bio.trim().isEmpty()) {
                    editor.putString("provider_bio", bio.trim());
                }
                editor.apply();

                // Navigate to dashboard
                Intent intent = new Intent(this, ProviderDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void goToStep(int step) {
        currentStep = step;
        Fragment fragment;
        String indicatorText;
        int progress;

        switch (step) {
            case 1:
                fragment = new Step1BasicDetailsFragment();
                indicatorText = "Step 1 of 3";
                progress = 33;
                break;
            case 2:
                fragment = new Step2BioFragment();
                indicatorText = "Step 2 of 3";
                progress = 66;
                break;
            case 3:
                fragment = new Step3DocumentsFragment();
                indicatorText = "Step 3 of 3";
                progress = 100;
                break;
            default:
                return;
        }

        tvStepIndicator.setText(indicatorText);
        pbSteps.setProgress(progress);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void submitOnboardingForm() {
        viewModel.submitOnboarding(userId, getContentResolver(), getCacheDir());
    }

    @android.annotation.SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (currentStep > 1) {
            goToStep(currentStep - 1);
        } else {
            super.onBackPressed();
        }
    }
}