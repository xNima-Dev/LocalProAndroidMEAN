package com.localpro.localproandroid.views;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.models.LocationRequest;
import com.localpro.localproandroid.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProviderDashboardActivity extends AppCompatActivity {

    private static final int LOCATION_PICK_CODE = 1002;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private SwitchMaterial switchOnlineStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        switchOnlineStatus = findViewById(R.id.switchOnlineStatus);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    sendLocationToServer(location.getLatitude(), location.getLongitude());
                }
            }
        };

        switchOnlineStatus.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                startLocationUpdate();
            } else {
                stopLocationUpdates();
            }
        }));

        Button btnLogoutProvider = findViewById(R.id.btnLogoutProvider);
        btnLogoutProvider.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProviderDashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PICK_CODE);
            return;
        }

        com.google.android.gms.location.LocationRequest locationRequest =
                new com.google.android.gms.location.LocationRequest.Builder(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 30000) // Interval 30s
                        .setMinUpdateIntervalMillis(15000) // Fastest Interval 15s
                        .build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Toast.makeText(this, "You are now ONLINE. Tracking started.", Toast.LENGTH_SHORT).show();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Toast.makeText(this, "You are now OFFLINE. Tracking stopped.", Toast.LENGTH_SHORT).show();
    }

    private void sendLocationToServer(double lat, double lon) {
        SharedPreferences sharedPreferences = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);

        if (token == null) {
            Log.e("ProviderLocation", "Token not found! Cannot update location.");
            return;
        }

        String bearerToken = "Bearer " + token;

        LocationRequest locationRequest = new LocationRequest(lat, lon);

        RetrofitClient.getApiService().updateLocation(bearerToken, locationRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ProviderLocation", "🎯 Location updated in MongoDB successfully!");
                } else {
                    Log.e("ProviderLocation", "❌ Server rejected location. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ProviderLocation", "💥 Network failure while updating location", t);
            }
        });
    }
}