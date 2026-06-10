package com.localpro.localproandroid.viewmodels;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.api.ApiService;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.OnboardingResponse;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingViewModel extends ViewModel {

    // Form data fields
    private final MutableLiveData<String> category = new MutableLiveData<>("");
    private final MutableLiveData<String> experience = new MutableLiveData<>("");
    private final MutableLiveData<String> hourlyRate = new MutableLiveData<>("");
    private final MutableLiveData<String> bio = new MutableLiveData<>("");
    private final MutableLiveData<Uri> idImageUri = new MutableLiveData<>();
    private final MutableLiveData<Uri> certificateImageUri = new MutableLiveData<>();

    // UI State fields
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<OnboardingResponse> onboardingResult = new MutableLiveData<>();

    // Getters and Setters for Form Data
    public MutableLiveData<String> getCategory() { return category; }
    public MutableLiveData<String> getExperience() { return experience; }
    public MutableLiveData<String> getHourlyRate() { return hourlyRate; }
    public MutableLiveData<String> getBio() { return bio; }
    public MutableLiveData<Uri> getIdImageUri() { return idImageUri; }
    public MutableLiveData<Uri> getCertificateImageUri() { return certificateImageUri; }

    // Getters for UI State
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<OnboardingResponse> getOnboardingResult() { return onboardingResult; }

    // Set value helpers
    public void setCategory(String value) { category.setValue(value); }
    public void setExperience(String value) { experience.setValue(value); }
    public void setHourlyRate(String value) { hourlyRate.setValue(value); }
    public void setBio(String value) { bio.setValue(value); }
    public void setIdImageUri(Uri value) { idImageUri.setValue(value); }
    public void setCertificateImageUri(Uri value) { certificateImageUri.setValue(value); }

    public void submitOnboarding(String userId, ContentResolver contentResolver, File cacheDir) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Prepare RequestBodies for text fields
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody expBody = RequestBody.create(MediaType.parse("text/plain"), experience.getValue() != null ? experience.getValue().trim() : "");
        RequestBody rateBody = RequestBody.create(MediaType.parse("text/plain"), hourlyRate.getValue() != null ? hourlyRate.getValue().trim() : "");
        RequestBody bioBody = RequestBody.create(MediaType.parse("text/plain"), bio.getValue() != null ? bio.getValue().trim() : "");
        RequestBody categoryBody = RequestBody.create(MediaType.parse("text/plain"), category.getValue() != null ? category.getValue().trim() : "");

        // Prepare Multipart parts for images
        MultipartBody.Part idFilePart = prepareFilePart(contentResolver, cacheDir, "idImage", idImageUri.getValue());
        if (idFilePart == null) {
            isLoading.setValue(false);
            errorMessage.setValue("Failed to process Government ID Image.");
            return;
        }

        MultipartBody.Part certFilePart = null;
        if (certificateImageUri.getValue() != null) {
            certFilePart = prepareFilePart(contentResolver, cacheDir, "certificateImage", certificateImageUri.getValue());
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.completeOnboarding(idFilePart, certFilePart, userIdBody, expBody, rateBody, bioBody, categoryBody)
                .enqueue(new Callback<OnboardingResponse>() {
                    @Override
                    public void onResponse(Call<OnboardingResponse> call, Response<OnboardingResponse> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            onboardingResult.setValue(response.body());
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    errorMessage.setValue(jObjError.getString("message"));
                                } else {
                                    errorMessage.setValue("Submission failed. Please try again.");
                                }
                            } catch (Exception e) {
                                errorMessage.setValue(e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OnboardingResponse> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Network Error: " + t.getMessage());
                    }
                });
    }

    private MultipartBody.Part prepareFilePart(ContentResolver contentResolver, File cacheDir, String partName, Uri fileUri) {
        if (fileUri == null) return null;
        try {
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

            File file = new File(cacheDir, fileName);
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
            Log.e("OnboardingVM", "Error preparing file: " + e.getMessage(), e);
            return null;
        }
    }
}
