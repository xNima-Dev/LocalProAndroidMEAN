package com.localpro.localproandroid.api;

import com.localpro.localproandroid.models.AuthResponse;
import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.models.BookingResponse;
import com.localpro.localproandroid.models.LocationRequest;
import com.localpro.localproandroid.models.LoginRequest;
import com.localpro.localproandroid.models.OnboardingResponse;
import com.localpro.localproandroid.models.ProviderListResponse;
import com.localpro.localproandroid.models.RegisterRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
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

    @Multipart
    @POST("api/auth/onboarding")
    Call<OnboardingResponse> completeOnboarding(
            @Part MultipartBody.Part idImage,
            @Part MultipartBody.Part certificateImage,
            @Part("userId") RequestBody userId,
            @Part("experience") RequestBody experience,
            @Part("hourlyRate") RequestBody hourlyRate,
            @Part("bio") RequestBody bio,
            @Part("serviceCategory") RequestBody category
    );

    @GET("api/auth/bookings")
    Call<BookingResponse> getBookings(@Header("Authorization") String token);

    // Accept Booking
    @POST("api/auth/bookings/{bookingId}/accept")
    Call<Void> acceptBooking(
            @Header("Authorization") String token,
            @Path("bookingId") String bookingId
    );

    // Decline Booking
    @POST("api/auth/bookings/{bookingId}/decline")
    Call<Void> declineBooking(
            @Header("Authorization") String token,
            @Path("bookingId") String bookingId
    );

    @GET("api/auth/bookings/pending")
    Call<BookingResponse> getPendingBookings(@Header("Authorization") String token);
    @GET("api/auth/bookings/active")
    Call<BookingResponse> getActiveBookings(@Header("Authorization") String token);

    @GET("api/auth/bookings/completed")
    Call<BookingResponse> getCompletedBookings(@Header("Authorization") String token);

    @GET("api/auth/bookings/cancelled")
    Call<BookingResponse> getCancelledBookings(@Header("Authorization") String token);

    // Update booking status to 'ride' (provider is on the way)
    @POST("api/auth/bookings/{bookingId}/ride")
    Call<Void> updateStatusRide(
            @Header("Authorization") String token,
            @Path("bookingId") String bookingId
    );

    // Update booking status to 'arrived' (provider reached customer location)
    @POST("api/auth/bookings/{bookingId}/arrived")
    Call<Void> updateStatusArrived(
            @Header("Authorization") String token,
            @Path("bookingId") String bookingId
    );

    // Update booking status to 'completed' (job done)
    @POST("api/auth/bookings/{bookingId}/complete")
    Call<Void> updateStatusCompleted(
            @Header("Authorization") String token,
            @Path("bookingId") String bookingId
    );

    // Update payment status to 'paid'
    @POST("api/auth/bookings/{bookingId}/paid")
    Call<Void> updateStatusPaid(
            @Header("Authorization") String token,
            @Path("bookingId") String bookingId
    );

    // Update payment status to 'unpaid'
    @POST("api/auth/bookings/{bookingId}/unpaid")
    Call<Void> updateStatusUnpaid(
            @Header("Authorization") String token,
            @Path("bookingId") String bookingId
    );
}
