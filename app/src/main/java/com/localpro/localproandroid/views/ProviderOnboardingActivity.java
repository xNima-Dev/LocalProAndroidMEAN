package com.localpro.localproandroid.views;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.localpro.localproandroid.R;

public class ProviderOnboardingActivity extends AppCompatActivity {

    private EditText etExperience, etHourlyRate, etBio;
    private Button btnUploadId, btnUploadCertificate, btnSubmitOnboarding;
    private ImageView ivIdPreview, ivCertificatePreview;

    private Uri idImageUri, certificateImageUri;
    private String uploadType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_onboarding);

        etExperience = findViewById(R.id.etExperience);
        etHourlyRate = findViewById(R.id.etHourlyRate);
        etBio = findViewById(R.id.etBio);
        btnUploadId = findViewById(R.id.btnUploadId);
        btnUploadCertificate = findViewById(R.id.btnUploadCertificate);
        btnSubmitOnboarding = findViewById(R.id.btnSubmitOnboarding);
        ivIdPreview = findViewById(R.id.ivIdPreview);
        ivCertificatePreview = findViewById(R.id.ivCertificatePreview);

        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (uploadType.equals("ID")) {
                            idImageUri = selectedImageUri;
                            ivIdPreview.setImageURI(selectedImageUri);
                            ivIdPreview.setVisibility(View.VISIBLE);
                        } else if (uploadType.equals("CERT")) {
                            certificateImageUri = selectedImageUri;
                            ivCertificatePreview.setImageURI(selectedImageUri);
                            ivCertificatePreview.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        // NIC/ID Photo Button Click
        btnUploadId.setOnClickListener(v -> {
            uploadType = "ID";
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Certificate Button Click
        btnSubmitOnboarding.setOnClickListener(v -> {

        });
    }

    private boolean validateInput() {
        if (etExperience.getText().toString().trim().isEmpty()) {
            etExperience.setError("Experience is required");
            return false;
        }
        if (etHourlyRate.getText().toString().trim().isEmpty()) {
            etHourlyRate.setError("Hourly rate is required");
            return false;
        }
        if (idImageUri == null) {
            Toast.makeText(this, "Please upload your Government ID Photo", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}