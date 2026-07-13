package com.localpro.localproandroid.views;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.android.material.button.MaterialButton;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.models.BookingResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class CustomerBookingTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CustomerTracking";
    private static final int POLL_INTERVAL_MS = 8000; // 8 seconds

    // Intent extras from BookingConfirmationActivity
    private String bookingId;
    private String providerName;
    private String providerPhone;
    private String providerCategory;
    private double myLat, myLon;

    private GoogleMap mMap;
    private Marker providerMarker;
    private Marker myMarker;
    private Polyline currentPolyline;
    private OkHttpClient okHttpClient;

    // Views
    private TextView tvTrackingStatusTitle, tvTrackingStatusSub, tvStatusChip;
    private TextView tvProviderInitialTracking, tvProviderNameTracking, tvProviderCategoryTracking;
    private TextView tvCustomerEta, tvCustomerDistance;
    private MaterialButton btnConfirmPayment;

    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private boolean isFirstZoom = true;
    private boolean isCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_booking_tracking);

        okHttpClient = new OkHttpClient();
        pollingHandler = new Handler(Looper.getMainLooper());

        // Get extras
        bookingId = getIntent().getStringExtra("BOOKING_ID");
        providerName = getIntent().getStringExtra("PROVIDER_NAME");
        providerPhone = getIntent().getStringExtra("PROVIDER_PHONE");
        providerCategory = getIntent().getStringExtra("PROVIDER_CATEGORY");
        myLat = getIntent().getDoubleExtra("CUSTOMER_LAT", 0.0);
        myLon = getIntent().getDoubleExtra("CUSTOMER_LON", 0.0);

        bindViews();
        populateStaticInfo();

        // Init Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.customerTrackingMap);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        startPolling();
    }

    private void bindViews() {
        tvTrackingStatusTitle = findViewById(R.id.tvTrackingStatusTitle);
        tvTrackingStatusSub = findViewById(R.id.tvTrackingStatusSub);
        tvStatusChip = findViewById(R.id.tvStatusChip);
        tvProviderInitialTracking = findViewById(R.id.tvProviderInitialTracking);
        tvProviderNameTracking = findViewById(R.id.tvProviderNameTracking);
        tvProviderCategoryTracking = findViewById(R.id.tvProviderCategoryTracking);
        tvCustomerEta = findViewById(R.id.tvCustomerEta);
        tvCustomerDistance = findViewById(R.id.tvCustomerDistance);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        ImageButton btnBack = findViewById(R.id.btnBackCustomerTracking);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnCall = findViewById(R.id.btnCallProvider);
        btnCall.setOnClickListener(v -> {
            if (providerPhone != null && !providerPhone.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + providerPhone)));
            } else {
                Toast.makeText(this, "Provider phone not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirmPayment.setOnClickListener(v -> {
            // Navigate back to customer dashboard after payment
            Toast.makeText(this, "Thank you! Payment confirmed.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, CustomerDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void populateStaticInfo() {
        if (providerName != null && !providerName.isEmpty()) {
            tvProviderNameTracking.setText(providerName);
            tvProviderInitialTracking.setText(String.valueOf(providerName.charAt(0)).toUpperCase());
        }
        if (providerCategory != null) {
            tvProviderCategoryTracking.setText("🔧 " + providerCategory);
        }
    }

    private void startPolling() {
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isCompleted) {
                    fetchBookingStatus();
                    pollingHandler.postDelayed(this, POLL_INTERVAL_MS);
                }
            }
        };
        pollingHandler.post(pollingRunnable);
    }

    private void fetchBookingStatus() {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("auth_token", "");

        RetrofitClient.getApiService().getCustomerBookings(token).enqueue(new retrofit2.Callback<BookingResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<BookingResponse> call, @NonNull retrofit2.Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookingRequest> bookings = response.body().getBookings();
                    if (bookings != null) {
                        for (BookingRequest booking : bookings) {
                            if (booking.getId() != null && booking.getId().equals(bookingId)) {
                                handleBookingUpdate(booking);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<BookingResponse> call, @NonNull Throwable t) {
                Log.w(TAG, "Polling failed: " + t.getMessage());
            }
        });
    }

    private void handleBookingUpdate(BookingRequest booking) {
        String status = booking.getStatus();

        // Update status chip & title
        switch (status) {
            case "pending":
                tvStatusChip.setText("PENDING");
                tvTrackingStatusTitle.setText("Waiting for provider...");
                tvTrackingStatusSub.setText("Provider hasn't started yet");
                break;
            case "accepted":
                tvStatusChip.setText("ACCEPTED");
                tvTrackingStatusTitle.setText("Provider accepted!");
                tvTrackingStatusSub.setText("Provider will start soon");
                break;
            case "riding":
                tvStatusChip.setText("ON THE WAY");
                tvTrackingStatusTitle.setText("Provider is on the way 🛵");
                tvTrackingStatusSub.setText("Live tracking active");
                break;
            case "arrived":
                tvStatusChip.setText("ARRIVED");
                tvTrackingStatusTitle.setText("Provider has arrived! 📍");
                tvTrackingStatusSub.setText("They are at your location");
                tvCustomerEta.setText("0");
                tvCustomerDistance.setText("0.0");
                break;
            case "completed":
                tvStatusChip.setText("COMPLETED");
                tvTrackingStatusTitle.setText("Job Completed ✅");
                tvTrackingStatusSub.setText("Please confirm payment");
                btnConfirmPayment.setVisibility(View.VISIBLE);
                isCompleted = true;
                break;
        }

        // If provider location is available via populated field (riding state), show on map
        // For now we show the customer's location only, provider marker updates when backend supports
        if (mMap != null && myLat != 0.0 && myLon != 0.0) {
            LatLng myLatLng = new LatLng(myLat, myLon);
            if (myMarker == null) {
                myMarker = mMap.addMarker(new MarkerOptions()
                        .position(myLatLng)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                if (isFirstZoom) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15f));
                    isFirstZoom = false;
                }
            }
        }
    }

    private void updateProviderOnMap(double providerLat, double providerLon) {
        if (mMap == null) return;
        LatLng providerLatLng = new LatLng(providerLat, providerLon);
        LatLng myLatLng = new LatLng(myLat, myLon);

        if (providerMarker == null) {
            providerMarker = mMap.addMarker(new MarkerOptions()
                    .position(providerLatLng)
                    .title(providerName != null ? providerName : "Provider")
                    .snippet("Provider")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } else {
            providerMarker.setPosition(providerLatLng);
        }

        // Zoom to show both
        try {
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(providerLatLng)
                    .include(myLatLng)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 160));
        } catch (Exception e) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(providerLatLng, 14f));
        }

        // Draw OSRM road route
        fetchOsrmRoute(providerLatLng, myLatLng);
    }

    private void fetchOsrmRoute(LatLng from, LatLng to) {
        String url = "https://router.project-osrm.org/route/v1/driving/"
                + from.longitude + "," + from.latitude + ";"
                + to.longitude + "," + to.latitude
                + "?overview=full&geometries=geojson";

        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "OSRM route failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String body = response.body().string();
                        JSONObject json = new JSONObject(body);
                        JSONArray routes = json.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            double distanceMeters = route.getDouble("distance");
                            double durationSecs = route.getDouble("duration");
                            double distanceKm = distanceMeters / 1000.0;
                            int durationMins = (int) Math.ceil(durationSecs / 60.0);

                            JSONArray coords = route.getJSONObject("geometry").getJSONArray("coordinates");
                            List<LatLng> points = new ArrayList<>();
                            for (int i = 0; i < coords.length(); i++) {
                                JSONArray pt = coords.getJSONArray(i);
                                points.add(new LatLng(pt.getDouble(1), pt.getDouble(0)));
                            }

                            runOnUiThread(() -> {
                                if (currentPolyline != null) currentPolyline.remove();
                                currentPolyline = mMap.addPolyline(new PolylineOptions()
                                        .addAll(points)
                                        .width(12f)
                                        .color(getResources().getColor(R.color.lp_cyan))
                                        .geodesic(true));
                                tvCustomerEta.setText(String.valueOf(durationMins));
                                tvCustomerDistance.setText(String.format("%.1f", distanceKm));
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

        // Show customer location immediately
        if (myLat != 0.0 && myLon != 0.0) {
            LatLng myLatLng = new LatLng(myLat, myLon);
            myMarker = mMap.addMarker(new MarkerOptions()
                    .position(myLatLng)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15f));
            isFirstZoom = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }
}
