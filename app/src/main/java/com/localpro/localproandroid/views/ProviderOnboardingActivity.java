package com.localpro.localproandroid.views;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
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
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.OnboardingResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderOnboardingActivity extends AppCompatActivity {

    private EditText etExperience, etHourlyRate, etBio;
    private Button btnUploadId, btnUploadCertificate, btnSubmitOnboarding;
    private ImageView ivIdPreview, ivCertificatePreview;

    private Uri idImageUri, certificateImageUri;
    private String uploadType = "";
    private String userId;

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
        btnUploadCertificate.setOnClickListener(v -> {
            uploadType = "CERT";
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSubmitOnboarding.setOnClickListener(v -> {
            if (validateInput()) {
                uploadOnboardingData();
            }
        });
    }

    private void uploadOnboardingData() {
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody expBody = RequestBody.create(MediaType.parse("text/plain"), etExperience.getText().toString().trim());
        RequestBody rateBody = RequestBody.create(MediaType.parse("text/plain"), etHourlyRate.getText().toString().trim());
        RequestBody bioBody = RequestBody.create(MediaType.parse("text/plain"), etBio.getText().toString().trim());

        MultipartBody.Part idFilePart = prepareFilePart("idImage", idImageUri);
        if (idFilePart == null) {
            Toast.makeText(this, "Failed to process Government ID Image. Please select a valid image.", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Part certFilePart = null;
        if (certificateImageUri != null) {
            certFilePart = prepareFilePart("certificateImage", certificateImageUri);
        }

        RetrofitClient.getApiService().completeOnboarding(idFilePart, certFilePart, userIdBody, expBody, rateBody, bioBody)
                .enqueue(new Callback<OnboardingResponse>() {
                    @Override
                    public void onResponse(Call<OnboardingResponse> call, Response<OnboardingResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ProviderOnboardingActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                            SharedPreferences sharedPrefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            editor.putBoolean("is_onboarded_" + userId, true);
                            editor.apply();

                            Intent intent = new Intent(ProviderOnboardingActivity.this, ProviderDashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e("API_ERROR", "Code: " + response.code() + " Message: " + response.message());
                            Toast.makeText(ProviderOnboardingActivity.this, "Submission Failed! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OnboardingResponse> call, Throwable t) {
                        Log.e("API_ERROR", "Error: " + t.getMessage());
                        Toast.makeText(ProviderOnboardingActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        if (fileUri == null) return null;
        try {
            ContentResolver contentResolver = getContentResolver();
            String fileName = null;

            Cursor returnCursor = contentResolver.query(fileUri, null, null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1 && returnCursor.moveToFirst()) {
                    fileName = returnCursor.getString(nameIndex);
                }
                returnCursor.close();
            }

            if (fileName == null) {
                fileName = fileUri.getLastPathSegment();
            }
            if (fileName == null) {
                fileName = "upload_image.jpg";
            }

            File file = new File(getCacheDir(), fileName);
            InputStream inputStream = contentResolver.openInputStream(fileUri);
            if (inputStream == null) return null;

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            String fileType = contentResolver.getType(fileUri);
            if (fileType == null) {
                fileType = "image/*";
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(fileType), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (Exception e) {
            Log.e("Onboarding", "Error preparing file: " + e.getMessage(), e);
            return null;
        }
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