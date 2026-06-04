package com.localpro.localproandroid.api;

import com.localpro.localproandroid.models.AuthResponse;
import com.localpro.localproandroid.models.LoginRequest;
import com.localpro.localproandroid.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest registerRequest);

}
