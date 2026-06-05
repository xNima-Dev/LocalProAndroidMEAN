package com.localpro.localproandroid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.models.AuthResponse;
import com.localpro.localproandroid.models.RegisterRequest;
import com.localpro.localproandroid.api.ApiService;
import com.localpro.localproandroid.api.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<AuthResponse> registerResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<AuthResponse> getRegisterResult() {
        return registerResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void register(String name, String email, String password, String role) {
        ApiService apiService = RetrofitClient.getApiService();

        RegisterRequest registerRequest = new RegisterRequest(name, email, password, role);

        apiService.registerUser(registerRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registerResult.setValue(response.body());
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMessage.setValue(jObjError.getString("message"));
                        } else {
                            errorMessage.setValue("Registration failed. Please try again.");
                        }
                    } catch (Exception e) {
                        errorMessage.setValue(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                errorMessage.setValue("Network Error: " + t.getMessage());
            }
        });
    }
}