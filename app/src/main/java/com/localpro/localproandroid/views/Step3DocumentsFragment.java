package com.localpro.localproandroid.views;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.viewmodels.OnboardingViewModel;

public class Step3DocumentsFragment extends Fragment {

    private Button btnUploadId, btnUploadCertificate, btnBack, btnSubmit;
    private ImageView ivIdPreview, ivCertificatePreview;

    private OnboardingViewModel viewModel;
    private String uploadType = "";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (uploadType.equals("ID")) {
                        viewModel.setIdImageUri(selectedImageUri);
                        ivIdPreview.setImageURI(selectedImageUri);
                        ivIdPreview.setVisibility(View.VISIBLE);
                    } else if (uploadType.equals("CERT")) {
                        viewModel.setCertificateImageUri(selectedImageUri);
                        ivCertificatePreview.setImageURI(selectedImageUri);
                        ivCertificatePreview.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step3_documents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Access shared ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        btnUploadId = view.findViewById(R.id.btnUploadId);
        btnUploadCertificate = view.findViewById(R.id.btnUploadCertificate);
        btnBack = view.findViewById(R.id.btnBackStep3);
        btnSubmit = view.findViewById(R.id.btnSubmitOnboarding);
        ivIdPreview = view.findViewById(R.id.ivIdPreview);
        ivCertificatePreview = view.findViewById(R.id.ivCertificatePreview);

        // Restore image previews if they already exist in ViewModel
        restoreState();

        // Upload ID Clicked
        btnUploadId.setOnClickListener(v -> {
            uploadType = "ID";
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Upload Certificate Clicked
        btnUploadCertificate.setOnClickListener(v -> {
            uploadType = "CERT";
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Back Clicked
        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof ProviderOnboardingActivity) {
                ((ProviderOnboardingActivity) getActivity()).goToStep(2);
            }
        });

        // Submit Clicked
        btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                if (getActivity() instanceof ProviderOnboardingActivity) {
                    ((ProviderOnboardingActivity) getActivity()).submitOnboardingForm();
                }
            }
        });
    }

    private void restoreState() {
        if (viewModel.getIdImageUri().getValue() != null) {
            ivIdPreview.setImageURI(viewModel.getIdImageUri().getValue());
            ivIdPreview.setVisibility(View.VISIBLE);
        }
        if (viewModel.getCertificateImageUri().getValue() != null) {
            ivCertificatePreview.setImageURI(viewModel.getCertificateImageUri().getValue());
            ivCertificatePreview.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInput() {
        if (viewModel.getIdImageUri().getValue() == null) {
            Toast.makeText(requireContext(), "Please upload your Government ID / Passport Photo", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
