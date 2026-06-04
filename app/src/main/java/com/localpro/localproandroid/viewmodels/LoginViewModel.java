package com.localpro.localproandroid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.AuthResponse;
import com.localpro.localproandroid.models.LoginRequest;

import retrofit2.Callback;

import retrofit2.Call;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<AuthResponse> loginResult = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<AuthResponse> getLoginResult() { return loginResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getApiService().loginUser(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(response.body());
                } else {
                    errorMessage.setValue("Login failed: Invalid email or password");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                errorMessage.setValue("Network Error: " + t.getMessage());
            }
        });
    }
}