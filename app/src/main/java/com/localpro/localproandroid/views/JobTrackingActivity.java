package com.localpro.localproandroid.views;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.LocationRequest;
import com.localpro.localproandroid.viewmodels.JobTrackingViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@AndroidEntryPoint
public class JobTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "JobTrackingActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 4001;

    private JobTrackingViewModel viewModel;
    private String bookingId = "12345";
    
    // Views
    private LinearLayout layoutPaymentPanel, layoutLoading, btnJobAction;
    private TextView tvStatusBadge, tvActionLabel;
    private TextView tvTripDistance, tvDistanceRemaining, tvTripDuration, tvEtaTime;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private OkHttpClient okHttpClient;
    private Polyline currentPolyline;
    private Marker customerMarker;
    
    private LatLng customerLatLng;
    private LatLng lastKnownProviderLatLng;
    private boolean isFirstZoom = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_tracking);

        okHttpClient = new OkHttpClient();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (getIntent() != null && getIntent().hasExtra("booking_id")) {
            bookingId = getIntent().getStringExtra("booking_id");
        }

        viewModel = new ViewModelProvider(this).get(JobTrackingViewModel.class);
        
        initViews();
        setupObservers();
        setupClickListeners();

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (bookingId != null) {
            viewModel.markAsRide(bookingId);
        }

        setupLocationUpdates();
    }

    private void initViews() {
        layoutPaymentPanel = findViewById(R.id.layoutPaymentPanel);
        layoutLoading = findViewById(R.id.layoutLoading);
        btnJobAction = findViewById(R.id.btnJobAction);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvActionLabel = findViewById(R.id.tvActionLabel);

        TextView tvCustomerInitial = findViewById(R.id.tvCustomerInitial);
        TextView tvCustomerName = findViewById(R.id.tvCustomerName);
        TextView tvServiceCategory = findViewById(R.id.tvServiceCategory);
        TextView tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        
        tvTripDistance = findViewById(R.id.tvTripDistance);
        tvDistanceRemaining = findViewById(R.id.tvDistanceRemaining);
        tvTripDuration = findViewById(R.id.tvTripDuration);
        tvEtaTime = findViewById(R.id.tvEtaTime);
        TextView tvEstEarning = findViewById(R.id.tvEstEarning);

        Intent intent = getIntent();
        if (intent != null) {
            String customerName = intent.getStringExtra("customer_name");
            String customerPhone = intent.getStringExtra("customer_phone");
            String customerInitial = intent.getStringExtra("customer_initial");
            String serviceCategory = intent.getStringExtra("service_category");
            String distanceText = intent.getStringExtra("distance_text");
            String estimatedEarning = intent.getStringExtra("estimated_earning");
            
            double lat = intent.getDoubleExtra("customer_lat", 0.0);
            double lon = intent.getDoubleExtra("customer_lon", 0.0);
            if (lat != 0.0 && lon != 0.0) {
                customerLatLng = new LatLng(lat, lon);
            }

            if (customerInitial != null) tvCustomerInitial.setText(customerInitial);
            if (customerName != null) tvCustomerName.setText(customerName);
            if (serviceCategory != null) tvServiceCategory.setText("🔧 " + serviceCategory);
            
            if (customerPhone != null && !customerPhone.trim().isEmpty()) {
                tvCustomerPhone.setText(customerPhone);
            } else {
                tvCustomerPhone.setText("N/A");
            }

            if (distanceText != null) {
                tvTripDistance.setText(distanceText);
                tvDistanceRemaining.setText(distanceText);
            }

            if (estimatedEarning != null) {
                tvEstEarning.setText("LKR " + estimatedEarning);
            }
        }
    }

    private void setupObservers() {
        viewModel.getJobStatus().observe(this, status -> {
            updateUI(status);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            layoutLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getSuccessMsg().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI(String status) {
        switch (status) {
            case "ride":
                tvStatusBadge.setText("RIDING");
                tvActionLabel.setText("I HAVE ARRIVED");
                btnJobAction.setVisibility(View.VISIBLE);
                layoutPaymentPanel.setVisibility(View.GONE);
                break;
            case "arrived":
                tvStatusBadge.setText("📍 ARRIVED");
                tvActionLabel.setText("COMPLETE JOB");
                btnJobAction.setVisibility(View.VISIBLE);
                layoutPaymentPanel.setVisibility(View.GONE);
                break;
            case "completed":
                tvStatusBadge.setText("COMPLETED");
                btnJobAction.setVisibility(View.GONE);
                layoutPaymentPanel.setVisibility(View.VISIBLE);
                break;
            case "paid":
            case "unpaid":
                finish();
                break;
        }
    }

    private void setupClickListeners() {
        btnJobAction.setOnClickListener(v -> {
            String currentStatus = viewModel.getJobStatus().getValue();
            if ("ride".equals(currentStatus)) {
                viewModel.markAsArrived(bookingId);
            } else if ("arrived".equals(currentStatus)) {
                viewModel.markAsCompleted(bookingId);
            }
        });

        findViewById(R.id.btnMarkPaid).setOnClickListener(v -> viewModel.markAsPaid(bookingId));
        findViewById(R.id.btnMarkUnpaid).setOnClickListener(v -> viewModel.markAsUnpaid(bookingId));

        findViewById(R.id.btnCallCustomer).setOnClickListener(v -> {
            String phone = getIntent().getStringExtra("customer_phone");
            if (phone != null && !phone.trim().isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Customer phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.fabNavigate).setOnClickListener(v -> {
            if (customerLatLng != null) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + customerLatLng.latitude + "," + customerLatLng.longitude));
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Intent genericMapIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("geo:" + customerLatLng.latitude + "," + customerLatLng.longitude + "?q=" + customerLatLng.latitude + "," + customerLatLng.longitude));
                    startActivity(genericMapIntent);
                }
            } else {
                Toast.makeText(this, "Customer coordinates not available", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupLocationUpdates() {
        com.google.android.gms.location.LocationRequest locationRequest = new com.google.android.gms.location.LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 8000) // update every 8 seconds
                .setMinUpdateIntervalMillis(4000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng providerLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    lastKnownProviderLatLng = providerLatLng;

                    // Push live location to backend so customer can track
                    pushLocationToBackend(location.getLatitude(), location.getLongitude());

                    if (mMap != null && customerLatLng != null) {
                        updateMapAndRoute(providerLatLng, customerLatLng);
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void pushLocationToBackend(double lat, double lon) {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("auth_token", "");
        LocationRequest locationRequest = new LocationRequest(lat, lon);
        RetrofitClient.getApiService().updateLocation(token, locationRequest).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                Log.d(TAG, "Location pushed: " + lat + "," + lon + " code=" + response.code());
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                Log.w(TAG, "Location push failed: " + t.getMessage());
            }
        });
    }

    private void updateMapAndRoute(LatLng providerLoc, LatLng customerLoc) {
        // Zoom to show both positions on first location acquisition
        if (isFirstZoom) {
            isFirstZoom = false;
            try {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(providerLoc)
                        .include(customerLoc)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 180));
            } catch (Exception e) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(providerLoc, 15.0f));
            }
        }

        // Call free OSRM Driving Road API for professional Sri Lankan road routes
        String url = "https://router.project-osrm.org/route/v1/driving/" 
                + providerLoc.longitude + "," + providerLoc.latitude + ";"
                + customerLoc.longitude + "," + customerLoc.latitude 
                + "?overview=full&geometries=geojson";

        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "OSRM API Route retrieval failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            
                            // Distance & Duration
                            double distanceMeters = route.getDouble("distance");
                            double durationSeconds = route.getDouble("duration");

                            double distanceKm = distanceMeters / 1000.0;
                            int durationMins = (int) Math.ceil(durationSeconds / 60.0);

                            // Decode route geometries
                            JSONObject geometry = route.getJSONObject("geometry");
                            JSONArray coordinates = geometry.getJSONArray("coordinates");
                            List<LatLng> polylinePoints = new ArrayList<>();
                            for (int i = 0; i < coordinates.length(); i++) {
                                JSONArray point = coordinates.getJSONArray(i);
                                double lon = point.getDouble(0);
                                double lat = point.getDouble(1);
                                polylinePoints.add(new LatLng(lat, lon));
                            }

                            // Update UI on Main UI Thread
                            runOnUiThread(() -> {
                                // Draw Polyline
                                if (currentPolyline != null) {
                                    currentPolyline.remove();
                                }
                                currentPolyline = mMap.addPolyline(new PolylineOptions()
                                        .addAll(polylinePoints)
                                        .width(12f)
                                        .color(getResources().getColor(R.color.lp_cyan))
                                        .geodesic(true));

                                // Set ETA Info & Remaining meters/km
                                tvDistanceRemaining.setText(String.format("%.2f km", distanceKm));
                                tvTripDistance.setText(String.format("%.2f km", distanceKm));
                                tvTripDuration.setText(durationMins + " mins");
                                tvEtaTime.setText(durationMins + "m");
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing OSRM response", e);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Setup Customer Pin with Info window
        if (customerLatLng != null) {
            String customerName = getIntent().getStringExtra("customer_name");
            String serviceCategory = getIntent().getStringExtra("service_category");
            
            customerMarker = mMap.addMarker(new MarkerOptions()
                    .position(customerLatLng)
                    .title(customerName != null ? customerName : "Customer")
                    .snippet(serviceCategory != null ? "Job: " + serviceCategory : "Service requested")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, 15.0f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationUpdates();
                if (mMap != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Location permission required to get live route updates", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}