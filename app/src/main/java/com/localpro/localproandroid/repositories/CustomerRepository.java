package com.localpro.localproandroid.repositories;

import android.content.SharedPreferences;

import com.localpro.localproandroid.api.ApiService;
import com.localpro.localproandroid.models.BookingResponse;
import com.localpro.localproandroid.models.CreateBookingRequest;
import com.localpro.localproandroid.models.CreateBookingResponse;
import com.localpro.localproandroid.models.ProviderListResponse;

import javax.inject.Inject;

import retrofit2.Call;

public class CustomerRepository {
    private final ApiService apiService;
    private final SharedPreferences prefs;

    @Inject
    public CustomerRepository(ApiService apiService, SharedPreferences prefs) {
        this.apiService = apiService;
        this.prefs = prefs;
    }

    private String getBearerToken() {
        return "Bearer " + prefs.getString("auth_token", "");
    }

    public Call<ProviderListResponse> getNearProviders(double lat, double lon, String category) {
        return apiService.getNearProviders(getBearerToken(), lat, lon, category);
    }

    public Call<CreateBookingResponse> createBooking(CreateBookingRequest request) {
        return apiService.createBooking(getBearerToken(), request);
    }

    public Call<BookingResponse> getActiveBookings() {
        return apiService.getActiveBookings(getBearerToken());
    }

    public Call<BookingResponse> getPendingBookings() {
        return apiService.getPendingBookings(getBearerToken());
    }

    public Call<BookingResponse> getCompletedBookings() {
        return apiService.getCompletedBookings(getBearerToken());
    }

    public Call<BookingResponse> getCancelledBookings() {
        return apiService.getCancelledBookings(getBearerToken());
    }
}
