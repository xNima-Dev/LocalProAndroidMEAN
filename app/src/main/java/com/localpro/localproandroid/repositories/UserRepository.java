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

    //    Api Call
    public Call<BookingResponse> getBooking() {
        String token = "Bearer " + prefs.getString("auth_token", "");
        Log.d("Api check", "sending token: " + token);
        return apiService.getBookings(token);
    }

    //    Log Out = clear prefs
    public void clearUserSession() {
        prefs.edit()
                .remove("auth_token")
                .remove("user_role")
                .remove("user_id")
                .remove("provider_email")
                .remove("provider_name")
                .apply();
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
}
