package com.localpro.localproandroid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.models.AuthResponse;
import com.localpro.localproandroid.models.ProviderProfile;
import com.localpro.localproandroid.repositories.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final UserRepository userRepository;

    private final MutableLiveData<ProviderProfile> providerProfile = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public ProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<ProviderProfile> getProviderProfile() { return providerProfile; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getUpdateSuccess() { return updateSuccess; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadProfile() {
        isLoading.setValue(true);
        userRepository.getProfile().enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    providerProfile.setValue(response.body().getUser().getProviderProfile());
                } else {
                    errorMessage.setValue("Failed to load profile details.");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void updateProfile(String name, String phone, String bio) {
        isLoading.setValue(true);
        userRepository.updateProfile(name, phone, bio, null).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    userRepository.saveUserProfile(name, phone, bio);
                    updateSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Error updating profile. Code: " + response.code());
                    updateSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                updateSuccess.setValue(false);
            }
        });
    }

    public void logout() {
        userRepository.clearUserSession();
    }
}
