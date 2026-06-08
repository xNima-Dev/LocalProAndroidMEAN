package com.localpro.localproandroid.api;

import com.localpro.localproandroid.models.AuthResponse;
import com.localpro.localproandroid.models.LocationRequest;
import com.localpro.localproandroid.models.LoginRequest;
import com.localpro.localproandroid.models.ProviderListResponse;
import com.localpro.localproandroid.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest registerRequest);

    @PUT("api/auth/update-location")
    Call<Void> updateLocation(
            @Header("Authorization") String token,
            @Body LocationRequest locationRequest
    );

    @GET("api/auth/near-providers")
    Call<ProviderListResponse> getNearProviders(
            @Header("Authorization") String token,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("category") String category
    );

}
