package com.localpro.localproandroid.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.viewmodels.OnboardingViewModel;

public class Step2BioFragment extends Fragment {

    private EditText etBio;
    private Button btnBack, btnNext;

    private OnboardingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step2_bio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Access shared ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        etBio = view.findViewById(R.id.etBio);
        btnBack = view.findViewById(R.id.btnBackStep2);
        btnNext = view.findViewById(R.id.btnNextStep2);

        // Restore state if user previously typed a bio
        restoreState();

        btnBack.setOnClickListener(v -> {
            // Save state first, in case they typed something but wanted to go back
            viewModel.setBio(etBio.getText().toString().trim());

            if (getActivity() instanceof ProviderOnboardingActivity) {
                ((ProviderOnboardingActivity) getActivity()).goToStep(1);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (validateInput()) {
                viewModel.setBio(etBio.getText().toString().trim());

                if (getActivity() instanceof ProviderOnboardingActivity) {
                    ((ProviderOnboardingActivity) getActivity()).goToStep(3);
                }
            }
        });
    }

    private void restoreState() {
        if (viewModel.getBio().getValue() != null) {
            etBio.setText(viewModel.getBio().getValue());
        }
    }

    private boolean validateInput() {
        String bio = etBio.getText().toString().trim();
        if (bio.isEmpty()) {
            etBio.setError("Bio is required");
            return false;
        }
        if (bio.length() < 10) {
            etBio.setError("Bio must be at least 10 characters long");
            return false;
        }
        return true;
    }
}
