package com.localpro.localproandroid.repositories;

import android.content.SharedPreferences;
import android.util.Log;


import com.localpro.localproandroid.api.ApiService;
import com.localpro.localproandroid.models.BookingResponse;

import javax.inject.Inject;

import retrofit2.Call;

public class UserRepository {
    private final ApiService apiService;
    private final SharedPreferences prefs;

    @Inject
    public UserRepository(ApiService apiService, SharedPreferences prefs) {
        this.apiService = apiService;
        this.prefs = prefs;
    }

    public String getProviderName() {
        return prefs.getString("provider_name", "N/A");
    }

    public String getProviderEmail() {
        return prefs.getString("provider_email", "N/A");
    }

    public void clearUserSession() {
        prefs.edit()
                .remove("auth_token")
                .remove("user_role")
                .remove("user_id")
                .remove("provider_email")
                .remove("provider_name")
                .apply();
    }

    public Call<BookingResponse> getPendingBookings() {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.getPendingBookings(token);
    }

    public Call<Void> acceptBooking(String bookingId) {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.acceptBooking(token, bookingId);
    }

    public Call<Void> declineBooking(String bookingId) {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.declineBooking(token, bookingId);
    }

    public Call<BookingResponse> getActiveBookings() {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.getActiveBookings(token);
    }

    public Call<BookingResponse> getCompletedBookings() {
        String token = "Bearer " + prefs.getString("auth_token","");
        return apiService.getCompletedBookings(token);
    }

    public Call<BookingResponse> getCancelledBookings() {
        String token = "Bearer " + prefs.getString("auth_token","");
        return apiService.getCancelledBookings(token);
    }
    public Call<Void> updateStatusRide(String bookingId) {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.updateStatusRide(token, bookingId);
    }

    public Call<Void> updateStatusArrived(String bookingId) {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.updateStatusArrived(token, bookingId);
    }

    public Call<Void> updateStatusCompleted(String bookingId) {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.updateStatusCompleted(token, bookingId);
    }

    public Call<Void> updateStatusPaid(String bookingId) {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.updateStatusPaid(token, bookingId);
    }

    public Call<Void> updateStatusUnpaid(String bookingId) {
        String token = "Bearer " + prefs.getString("auth_token", "");
        return apiService.updateStatusUnpaid(token, bookingId);
    }

    public Call<com.localpro.localproandroid.models.AuthResponse> updateProfile(String name, String phone, String bio, Double hourlyRate) {
        String token = "Bearer " + prefs.getString("auth_token", "");
        com.localpro.localproandroid.models.ProfileRequest request = new com.localpro.localproandroid.models.ProfileRequest(name, phone, bio, hourlyRate);
        return apiService.updateProfile(token, request);
    }

    public void saveUserProfile(String name, String phone, String bio) {
        prefs.edit()
                .putString("provider_name", name)
                .putString("provider_phone", phone)
                .putString("provider_bio", bio)
                .apply();
    }
}
