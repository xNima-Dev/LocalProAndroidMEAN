package com.localpro.localproandroid.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.CreateBookingRequest;
import com.localpro.localproandroid.models.CreateBookingResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingConfirmationActivity extends AppCompatActivity {

    private String providerId, providerName, providerPhone, providerCategory;
    private double customerLat, customerLon;
    private EditText etJobDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        // Get intent extras
        providerId = getIntent().getStringExtra("PROVIDER_ID");
        providerName = getIntent().getStringExtra("PROVIDER_NAME");
        providerPhone = getIntent().getStringExtra("PROVIDER_PHONE");
        providerCategory = getIntent().getStringExtra("PROVIDER_CATEGORY");
        customerLat = getIntent().getDoubleExtra("CUSTOMER_LAT", 0.0);
        customerLon = getIntent().getDoubleExtra("CUSTOMER_LON", 0.0);

        // Bind views
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvProviderName = findViewById(R.id.tvProviderName);
        TextView tvProviderInitial = findViewById(R.id.tvProviderInitial);
        TextView tvProviderCategory = findViewById(R.id.tvProviderCategory);
        TextView tvProviderPhone = findViewById(R.id.tvProviderPhone);
        TextView tvProviderRate = findViewById(R.id.tvProviderRate);
        etJobDescription = findViewById(R.id.etJobDescription);
        MaterialButton btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        // Setup views
        if (providerName != null && !providerName.isEmpty()) {
            tvProviderName.setText(providerName);
            tvProviderInitial.setText(String.valueOf(providerName.charAt(0)).toUpperCase());
        }
        if (providerCategory != null) {
            tvProviderCategory.setText(providerCategory);
        }
        if (providerPhone != null && !providerPhone.isEmpty()) {
            tvProviderPhone.setText(providerPhone);
        }

        btnBack.setOnClickListener(v -> finish());

        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void confirmBooking() {
        String jobDescription = etJobDescription.getText().toString().trim();
        if (jobDescription.isEmpty()) {
            Toast.makeText(this, "Please describe your job", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String bearerToken = "Bearer " + token;

        // Mock distance and estimated earning for now. In a real app, calculate these based on coordinates and provider's hourly rate.
        String distanceText = "5.0 km";
        double estimatedEarning = 1500.0;

        CreateBookingRequest request = new CreateBookingRequest(
                providerId,
                providerCategory,
                jobDescription,
                distanceText,
                estimatedEarning,
                customerLat,
                customerLon
        );

        RetrofitClient.getApiService().createBooking(bearerToken, request).enqueue(new Callback<CreateBookingResponse>() {
            @Override
            public void onResponse(Call<CreateBookingResponse> call, Response<CreateBookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String newBookingId = response.body().getBooking() != null
                            ? response.body().getBooking().getId() : null;

                    Toast.makeText(BookingConfirmationActivity.this,
                            "Booking confirmed! Tracking provider...", Toast.LENGTH_SHORT).show();

                    // Navigate to live tracking screen
                    Intent intent = new Intent(BookingConfirmationActivity.this,
                            CustomerBookingTrackingActivity.class);
                    intent.putExtra("BOOKING_ID", newBookingId);
                    intent.putExtra("PROVIDER_NAME", providerName);
                    intent.putExtra("PROVIDER_PHONE", providerPhone);
                    intent.putExtra("PROVIDER_CATEGORY", providerCategory);
                    intent.putExtra("CUSTOMER_LAT", customerLat);
                    intent.putExtra("CUSTOMER_LON", customerLon);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("BookingConfirm", "Server error: " + response.code());
                    Toast.makeText(BookingConfirmationActivity.this,
                            "Error creating booking", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateBookingResponse> call, Throwable t) {
                Log.e("BookingConfirm", "Network failure", t);
                Toast.makeText(BookingConfirmationActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}