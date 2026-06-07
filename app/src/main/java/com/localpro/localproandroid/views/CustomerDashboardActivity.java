package com.localpro.localproandroid.views;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.ProviderListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDashboardActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private MaterialButton btnLogoutCustomer;
    private static final int LOCATION_REQ_CODE = 2001;
    private static final String TAG = "CustomerDashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnLogoutCustomer = findViewById(R.id.btnLogoutCustomer);

        btnLogoutCustomer.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("auth_token");
            editor.apply();

            Intent intent = new Intent(CustomerDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.customer_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);
        getCurrentCustomerLocation();
    }

    private void getCurrentCustomerLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        LatLng customerLatLng = new LatLng(lat, lon);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, 14.0f));

                        Log.d(TAG, "Customer Location: Lat: " + lat + ", Lng: " + lon);

                        fetchNearProviders(lat, lon);
                    } else {
                        Toast.makeText(CustomerDashboardActivity.this, "Unable to get current location. Make sure GPS is ON.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void fetchNearProviders(double lat, double lon) {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String bearerToken = "Bearer " + token;

        RetrofitClient.getApiService().getNearProviders(bearerToken, lat, lon).enqueue(new Callback<ProviderListResponse>() {
            @Override
            public void onResponse(Call<ProviderListResponse> call, Response<ProviderListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProviderListResponse listResponse = response.body();

                    if (listResponse.getProviders() != null && !listResponse.getProviders().isEmpty()) {

                        mMap.clear();

                        for (ProviderListResponse.UserDoc provider : listResponse.getProviders()) {

                            // MongoDB GeoJSON Array  0=Longitude, 1=Latitude [Lng, Lat]
                            double pLon = provider.getLocation().getCoordinates().get(0);
                            double pLat = provider.getLocation().getCoordinates().get(1);
                            LatLng providerPos = new LatLng(pLat, pLon);

                            mMap.addMarker(new MarkerOptions()
                                    .position(providerPos)
                                    .title(provider.getName())
                                    .snippet("Phone: " + provider.getPhoneNumber())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }

                        Toast.makeText(CustomerDashboardActivity.this, "🎯 Found " + listResponse.getResults() + " providers near you!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CustomerDashboardActivity.this, "No service providers found within 5km.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Server Error Code: " + response.code());
                    Toast.makeText(CustomerDashboardActivity.this, "Server error fetching providers.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProviderListResponse> call, Throwable t) {
                Log.e(TAG, "Network Failure!", t);
                Toast.makeText(CustomerDashboardActivity.this, "Network error. Please check connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getCurrentCustomerLocation();
                }
            } else {
                Toast.makeText(this, "Location permission is required to find providers near you.", Toast.LENGTH_LONG).show();
            }
        }
    }
}