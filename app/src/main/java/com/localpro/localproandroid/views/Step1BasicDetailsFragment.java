package com.localpro.localproandroid.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.viewmodels.OnboardingViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Step1BasicDetailsFragment extends Fragment {

    private Spinner spinnerCategory;
    private EditText etExperience, etHourlyRate;
    private Button btnNext;

    private OnboardingViewModel viewModel;

    // Define Category display names and corresponding IDs
    private final List<String> displayCategories = Arrays.asList(
            "Select Service Type",
            "AC Repair ❄️",
            "Appliance Repair 🔌",
            "CCTV Camera 📷",
            "Carpentry 🪚",
            "Cleaning 🧹",
            "Electrician ⚡",
            "Electronic Repair 📺",
            "Gardening 🏡",
            "Masonry 🧱",
            "Painting 🎨",
            "Plumbing 🚰"
    );

    private final List<String> categoryIds = Arrays.asList(
            "",
            "ac-repair",
            "appliance-repair",
            "cctv-installation",
            "carpentry",
            "cleaning",
            "electrician",
            "electronic-repair",
            "gardening",
            "masonry",
            "painting",
            "plumber"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step1_basic_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Access shared ViewModel scoped to the parent Activity
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        etExperience = view.findViewById(R.id.etExperience);
        etHourlyRate = view.findViewById(R.id.etHourlyRate);
        btnNext = view.findViewById(R.id.btnNextStep1);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, displayCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Pre-fill existing data from ViewModel (if user returned to this step)
        restoreState();

        btnNext.setOnClickListener(v -> {
            if (validateInput()) {
                // Save to ViewModel
                int selectedPosition = spinnerCategory.getSelectedItemPosition();
                String selectedCategoryId = categoryIds.get(selectedPosition);
                String experienceVal = etExperience.getText().toString().trim();
                String hourlyRateVal = etHourlyRate.getText().toString().trim();

                viewModel.setCategory(selectedCategoryId);
                viewModel.setExperience(experienceVal);
                viewModel.setHourlyRate(hourlyRateVal);

                // Navigate to next step
                if (getActivity() instanceof ProviderOnboardingActivity) {
                    ((ProviderOnboardingActivity) getActivity()).goToStep(2);
                }
            }
        });
    }

    private void restoreState() {
        if (viewModel.getCategory().getValue() != null && !viewModel.getCategory().getValue().isEmpty()) {
            int index = categoryIds.indexOf(viewModel.getCategory().getValue());
            if (index >= 0) {
                spinnerCategory.setSelection(index);
            }
        }
        if (viewModel.getExperience().getValue() != null) {
            etExperience.setText(viewModel.getExperience().getValue());
        }
        if (viewModel.getHourlyRate().getValue() != null) {
            etHourlyRate.setText(viewModel.getHourlyRate().getValue());
        }
    }

    private boolean validateInput() {
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        if (selectedPosition <= 0) {
            Toast.makeText(requireContext(), "Please select a service type", Toast.LENGTH_SHORT).show();
            return false;
        }

        String exp = etExperience.getText().toString().trim();
        if (exp.isEmpty()) {
            etExperience.setError("Experience is required");
            return false;
        }

        String rate = etHourlyRate.getText().toString().trim();
        if (rate.isEmpty()) {
            etHourlyRate.setError("Hourly rate is required");
            return false;
        }

        return true;
    }
}
