package com.localpro.localproandroid.views;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.models.LocationRequest;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.viewmodels.ProviderDashboardHomeViewModel;
import com.localpro.localproandroid.views.providerDashboard.EarningsFragment;
import com.localpro.localproandroid.views.providerDashboard.HomeFragment;
import com.localpro.localproandroid.views.providerDashboard.JobsFragment;
import com.localpro.localproandroid.views.providerDashboard.ProfileFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProviderDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ProviderDashboard";
    private static final int LOCATION_PICK_CODE = 1002;
    private ProviderDashboardHomeViewModel viewModel;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private boolean isOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        initViews();
        setupLocationCallback();
        setupBottomNav();
    }

    private void initViews() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupLocationCallback() {
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
    }

    public void setOnlineState(boolean online) {
        this.isOnline = online;
        if (online) {
            startLocationUpdate();
        } else {
            stopLocationUpdates();
        }
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) selectedFragment = new HomeFragment();
            else if (id == R.id.nav_jobs) selectedFragment = new JobsFragment();
            else if (id == R.id.nav_earnings) selectedFragment = new EarningsFragment();
            else if (id == R.id.nav_profile) selectedFragment = new ProfileFragment();

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
        bottomNav.setSelectedItemId(R.id.nav_home);
    }


    public void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PICK_CODE);
            return;
        }

        com.google.android.gms.location.LocationRequest locationRequest =
                new com.google.android.gms.location.LocationRequest.Builder(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 30000)
                        .setMinUpdateIntervalMillis(15000)
                        .build();

        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper());

        Log.d(TAG, "📡 Location tracking started.");
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "📴 Location tracking stopped.");
    }

    private void sendLocationToServer(double lat, double lon) {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Log.e(TAG, "Token not found! Cannot update location.");
            return;
        }

        String bearerToken = "Bearer " + token;
        LocationRequest locationRequest = new LocationRequest(lat, lon);

        RetrofitClient.getApiService().updateLocation(bearerToken, locationRequest)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "🎯 Location updated in MongoDB successfully!");
                        } else {
                            Log.e(TAG, "❌ Server rejected location update. Code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.e(TAG, "💥 Network failure while updating location", t);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PICK_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdate();
            } else {
                Toast.makeText(this,
                        "Location permission required to track your position.",
                        Toast.LENGTH_LONG).show();
                // If permission is denied, we can't be online
                this.isOnline = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}