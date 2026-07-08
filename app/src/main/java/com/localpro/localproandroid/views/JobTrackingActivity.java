package com.localpro.localproandroid.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.viewmodels.JobTrackingViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class JobTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private JobTrackingViewModel viewModel;
    private String bookingId = "12345";
    private LinearLayout layoutPaymentPanel, layoutLoading, btnJobAction;
    private TextView tvStatusBadge, tvActionLabel;
    
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_tracking);

        if (getIntent() != null && getIntent().hasExtra("booking_id")) {
            bookingId = getIntent().getStringExtra("booking_id");
        }

        viewModel = new ViewModelProvider(this).get(JobTrackingViewModel.class);
        initViews();
        setupObservers();
        setupClickListeners();

        // Initialize Google Maps SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (bookingId != null) {
            // Trigger markAsRide on start to set initial state to RIDING on server
            viewModel.markAsRide(bookingId);
        }
    }

    private void initViews() {
        layoutPaymentPanel = findViewById(R.id.layoutPaymentPanel);
        layoutLoading = findViewById(R.id.layoutLoading);
        btnJobAction = findViewById(R.id.btnJobAction);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvActionLabel = findViewById(R.id.tvActionLabel);

        // Bind other customer elements from layout
        TextView tvCustomerInitial = findViewById(R.id.tvCustomerInitial);
        TextView tvCustomerName = findViewById(R.id.tvCustomerName);
        TextView tvServiceCategory = findViewById(R.id.tvServiceCategory);
        TextView tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        TextView tvTripDistance = findViewById(R.id.tvTripDistance);
        TextView tvEstEarning = findViewById(R.id.tvEstEarning);
        TextView tvDistanceRemaining = findViewById(R.id.tvDistanceRemaining);
        TextView tvTripDuration = findViewById(R.id.tvTripDuration);
        TextView tvEtaTime = findViewById(R.id.tvEtaTime);

        Intent intent = getIntent();
        if (intent != null) {
            String customerName = intent.getStringExtra("customer_name");
            String customerPhone = intent.getStringExtra("customer_phone");
            String customerInitial = intent.getStringExtra("customer_initial");
            String serviceCategory = intent.getStringExtra("service_category");
            String distanceText = intent.getStringExtra("distance_text");
            String estimatedEarning = intent.getStringExtra("estimated_earning");

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

                // Estimate duration based on distance (approx 4 mins per km)
                try {
                    String cleanDist = distanceText.replaceAll("[^0-9.]", "");
                    if (!cleanDist.isEmpty()) {
                        double distVal = Double.parseDouble(cleanDist);
                        int mins = (int) Math.max(2, Math.round(distVal * 4));
                        tvTripDuration.setText(mins + " mins");
                        tvEtaTime.setText(mins + "m");
                    } else {
                        tvTripDuration.setText("10 mins");
                        tvEtaTime.setText("10m");
                    }
                } catch (Exception e) {
                    tvTripDuration.setText("10 mins");
                    tvEtaTime.setText("10m");
                }
            } else {
                tvTripDistance.setText("--");
                tvDistanceRemaining.setText("Calculating...");
                tvTripDuration.setText("--");
                tvEtaTime.setText("--");
            }

            if (estimatedEarning != null) {
                tvEstEarning.setText("LKR " + estimatedEarning);
            } else {
                tvEstEarning.setText("--");
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
            if(msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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

        findViewById(R.id.btnMarkPaid).setOnClickListener(v -> {
            viewModel.markAsPaid(bookingId);
        });

        findViewById(R.id.btnMarkUnpaid).setOnClickListener(v -> {
            viewModel.markAsUnpaid(bookingId);
        });

        findViewById(R.id.btnCallCustomer).setOnClickListener(v -> {
            String phone = getIntent().getStringExtra("customer_phone");
            if (phone != null && !phone.trim().isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Customer phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.fabNavigate).setOnClickListener(v -> {
            double customerLat = getIntent().getDoubleExtra("customer_lat", 0.0);
            double customerLon = getIntent().getDoubleExtra("customer_lon", 0.0);
            if (customerLat != 0.0 && customerLon != 0.0) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("google.navigation:q=" + customerLat + "," + customerLon));
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback to web browser or generic map viewer
                    Intent genericMapIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("geo:" + customerLat + "," + customerLon + "?q=" + customerLat + "," + customerLon));
                    startActivity(genericMapIntent);
                }
            } else {
                Toast.makeText(this, "Customer coordinates not available", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void drawRoute(LatLng providerLoc, LatLng customerLoc) {
        if (mMap == null || providerLoc == null || customerLoc == null) return;
        mMap.addPolyline(new com.google.android.gms.maps.model.PolylineOptions()
                .add(providerLoc, customerLoc)
                .width(8f)
                .color(androidx.core.content.ContextCompat.getColor(this, R.color.lp_cyan))
                .geodesic(true));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        double customerLat = getIntent().getDoubleExtra("customer_lat", 0.0);
        double customerLon = getIntent().getDoubleExtra("customer_lon", 0.0);
        String customerName = getIntent().getStringExtra("customer_name");

        if (customerLat != 0.0 && customerLon != 0.0) {
            LatLng customerLatLng = new LatLng(customerLat, customerLon);
            mMap.addMarker(new MarkerOptions()
                    .position(customerLatLng)
                    .title(customerName != null ? customerName : "Customer Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, 15.0f));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
                        .getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng providerLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                drawRoute(providerLatLng, customerLatLng);
                                try {
                                    com.google.android.gms.maps.model.LatLngBounds.Builder builder = new com.google.android.gms.maps.model.LatLngBounds.Builder();
                                    builder.include(providerLatLng);
                                    builder.include(customerLatLng);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
                                } catch (Exception e) {
                                    Log.e("JobTrackingActivity", "Error setting camera bounds", e);
                                }
                            }
                        });
            }
        }
    }
}