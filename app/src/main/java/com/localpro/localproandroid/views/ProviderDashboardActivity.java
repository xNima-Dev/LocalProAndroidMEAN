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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.BookingRequestAdapter;
import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.models.BookingResponse;
import com.localpro.localproandroid.models.LocationRequest;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.views.providerDashboard.EarningsFragment;
import com.localpro.localproandroid.views.providerDashboard.HomeFragment;
import com.localpro.localproandroid.views.providerDashboard.JobsFragment;
import com.localpro.localproandroid.views.providerDashboard.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderDashboardActivity extends AppCompatActivity
        implements BookingRequestAdapter.OnBookingActionListener {

    private static final String TAG = "ProviderDashboard";
    private static final int LOCATION_PICK_CODE = 1002;

    // ---- Location ----
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    // ---- Views ----
    private SwitchMaterial switchOnlineStatus;
    private TextView tvProviderName;
    private TextView tvProviderEmail;
    private TextView tvAvatarInitials;
    private TextView tvOnlineTitle;
    private TextView tvOnlineSubtitle;
    private TextView tvStatusLabel;
    private TextView tvRequestCount;
    private LinearLayout layoutStatusBadge;
    private LinearLayout cardOnlineToggle;
    private View viewPulseOuter;
    private View viewStatusDot;
    private LinearLayout layoutEmptyState;
    private RecyclerView rvBookingRequests;
    private MaterialButton btnLogoutProvider;

    // ---- Adapter ----
    private BookingRequestAdapter bookingAdapter;

    // ---- State ----
    private boolean isOnline = false;
    private Animation pulseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        initViews();
        loadProviderInfo();
        setupOnlineToggle();
        setupQuickActions();
        setupRecyclerView();
        setupLogout();
        setupBottomNav();

        // Load pulse animation
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
    }

    private void initViews() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        switchOnlineStatus = findViewById(R.id.switchOnlineStatus);
        tvProviderName = findViewById(R.id.tvProviderName);
        tvProviderEmail = findViewById(R.id.tvProviderEmail);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        tvOnlineTitle = findViewById(R.id.tvOnlineTitle);
        tvOnlineSubtitle = findViewById(R.id.tvOnlineSubtitle);
        tvStatusLabel = findViewById(R.id.tvStatusLabel);
        tvRequestCount = findViewById(R.id.tvRequestCount);
        layoutStatusBadge = findViewById(R.id.layoutStatusBadge);
        cardOnlineToggle = findViewById(R.id.cardOnlineToggle);
        viewPulseOuter = findViewById(R.id.viewPulseOuter);
        viewStatusDot = findViewById(R.id.viewStatusDot);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        rvBookingRequests = findViewById(R.id.rvBookingRequests);
        btnLogoutProvider = findViewById(R.id.btnLogoutProvider);
    }
    private void loadProviderInfo() {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String name = prefs.getString("provider_name", "Provider");
        String email = prefs.getString("provider_email", "provider@example.com");

        // Try to get name from stored name, else fallback
        if (name.equals("Provider")) {
            if (!email.isEmpty() && !email.equals("provider@example.com")) {
                // Use email prefix as display name
                name = email.split("@")[0];
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            }
        }

        tvProviderName.setText(name);
        if (tvProviderEmail != null) {
            tvProviderEmail.setText(email);
        }

        // Set avatar initial
        String initial = name.isEmpty() ? "P" : String.valueOf(name.charAt(0)).toUpperCase();
        tvAvatarInitials.setText(initial);
    }

    private void setupOnlineToggle() {
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

        switchOnlineStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isOnline = isChecked;
            if (isChecked) {
                startLocationUpdate();
                setOnlineUI(true);
            } else {
                stopLocationUpdates();
                setOnlineUI(false);
            }
        });
    }

    private void setOnlineUI(boolean online) {
        if (online) {
            // Online state
            cardOnlineToggle.setBackground(getDrawable(R.drawable.bg_online_card));
            tvOnlineTitle.setText("You are ONLINE 🟢");
            tvOnlineSubtitle.setText("Receiving booking requests from nearby customers");
            tvStatusLabel.setText("ONLINE");
            tvStatusLabel.setTextColor(getColor(R.color.lp_green));
            layoutStatusBadge.setBackground(getDrawable(R.drawable.bg_status_online));
            switchOnlineStatus.setTrackTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.lp_green)));

            // Start pulse on the outer dot
            viewPulseOuter.startAnimation(pulseAnimation);
            viewStatusDot.setAlpha(1.0f);

            // Show mock requests for demo (replace with real WebSocket data)
            loadBookingRequests();

        } else {
            // Offline state
            cardOnlineToggle.setBackground(getDrawable(R.drawable.bg_offline_card));
            tvOnlineTitle.setText("Go Online");
            tvOnlineSubtitle.setText("Toggle to receive booking requests");
            tvStatusLabel.setText("OFFLINE");
            tvStatusLabel.setTextColor(getColor(R.color.lp_text_secondary));
            layoutStatusBadge.setBackground(getDrawable(R.drawable.bg_status_offline));
            switchOnlineStatus.setTrackTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.lp_offline)));

            // Stop pulse
            viewPulseOuter.clearAnimation();
            viewStatusDot.setAlpha(0.4f);

            // Clear requests
            clearBookingRequests();
        }
    }

    private void setupQuickActions() {
        LinearLayout actionProfile = findViewById(R.id.actionProfile);
        LinearLayout actionEarnings = findViewById(R.id.actionEarnings);
        LinearLayout actionReviews = findViewById(R.id.actionReviews);
        LinearLayout actionSchedule = findViewById(R.id.actionSchedule);

        actionProfile.setOnClickListener(v -> {
            Toast.makeText(this, "👤 Profile - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        actionEarnings.setOnClickListener(v -> {
            Toast.makeText(this, "💳 Earnings History - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        actionReviews.setOnClickListener(v -> {
            Toast.makeText(this, "⭐ My Reviews - Coming Soon!", Toast.LENGTH_SHORT).show();
        });

        actionSchedule.setOnClickListener(v -> {
            Toast.makeText(this, "📅 My Schedule - Coming Soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingRequestAdapter(this);
        rvBookingRequests.setLayoutManager(new LinearLayoutManager(this));
        rvBookingRequests.setAdapter(bookingAdapter);
        rvBookingRequests.setNestedScrollingEnabled(false);
        updateRequestCount(0);
    }

    private void loadBookingRequests() {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("auth_token","");

        RetrofitClient.getApiService().getBookings(token)
                .enqueue(new Callback<BookingResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BookingResponse> call,@NonNull Response<BookingResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<BookingRequest> list = response.body().data;
                            bookingAdapter.setRequests(list);
                            updateRequestCount(list.size());
                            showBookingRequests(!list.isEmpty());
                        }else {
                            Toast.makeText(ProviderDashboardActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BookingResponse> call, Throwable t) {
                        Log.e(TAG, "Network Error: ", t);
                        Toast.makeText(ProviderDashboardActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void loadMockBookingRequests() {
//        List<BookingRequest> mockRequests = new ArrayList<>();
//
//        mockRequests.add(new BookingRequest(
//                "cust_001",
//                "Nuwan Perera",
//                "Electrician",
//                "Needs help fixing faulty wiring in the living room. Also wants to install new light fixtures.",
//                "0.8 km away",
//                "LKR 3,500",
//                "Just now",
//                6.9271, 79.8612
//        ));
//
//        mockRequests.add(new BookingRequest(
//                "cust_002",
//                "Ayesha Fernando",
//                "Plumber",
//                "Leaking pipe under kitchen sink. Water damage visible. Urgent fix needed.",
//                "1.4 km away",
//                "LKR 2,200",
//                "2 min ago",
//                6.9310, 79.8590
//        ));
//
//        mockRequests.add(new BookingRequest(
//                "cust_003",
//                "Tharindu Silva",
//                "Electrician",
//                "AC installation in master bedroom. Unit already purchased, just need installation.",
//                "2.1 km away",
//                "LKR 5,000",
//                "5 min ago",
//                6.9250, 79.8650
//        ));
//
//        bookingAdapter.setRequests(mockRequests);
//        updateRequestCount(mockRequests.size());
//        showBookingRequests(true);
//    }

    private void clearBookingRequests() {
        bookingAdapter.setRequests(new ArrayList<>());
        updateRequestCount(0);
        showBookingRequests(false);
    }

    private void showBookingRequests(boolean show) {
        if (show) {
            layoutEmptyState.setVisibility(View.GONE);
            rvBookingRequests.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvBookingRequests.setVisibility(View.GONE);
        }
    }

    private void updateRequestCount(int count) {
        if (count == 0) {
            tvRequestCount.setText("0 New");
        } else {
            tvRequestCount.setText(count + " New");
        }
    }

    @Override
    public void onAccept(BookingRequest request, int position) {
        new AlertDialog.Builder(this, R.style.AlertDialogDark)
                .setTitle("✅ Accept Job?")
                .setMessage("Accept booking from " + request.getCustomerName() + "?\n\nEstimated: " + request.getEstimatedEarning())
                .setPositiveButton("Accept", (dialog, which) -> {
                    bookingAdapter.removeItem(position);
                    int remaining = bookingAdapter.getRequestCount();
                    updateRequestCount(remaining);
                    if (remaining == 0) showBookingRequests(false);
                    Toast.makeText(this,
                            "✅ Job accepted! Contact " + request.getCustomerName() + " now.",
                            Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDecline(BookingRequest request, int position) {
        new AlertDialog.Builder(this, R.style.AlertDialogDark)
                .setTitle("❌ Decline Job?")
                .setMessage("Decline booking from " + request.getCustomerName() + "?")
                .setPositiveButton("Decline", (dialog, which) -> {
                    bookingAdapter.removeItem(position);
                    int remaining = bookingAdapter.getRequestCount();
                    updateRequestCount(remaining);
                    if (remaining == 0) showBookingRequests(false);
                    Toast.makeText(this, "Job declined.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

//    private void setupBottomNav() {
//        LinearLayout tabHome = findViewById(R.id.tabHome);
//        LinearLayout tabJobs = findViewById(R.id.tabJobs);
//        LinearLayout tabEarnings = findViewById(R.id.tabEarnings);
//        LinearLayout tabProfile = findViewById(R.id.tabProfile);
//
//        tabHome.setOnClickListener(v -> {
//            // Already on dashboard
//        });
//
//        tabJobs.setOnClickListener(v -> {
//            Toast.makeText(this, "🛠️ My Jobs - Coming Soon!", Toast.LENGTH_SHORT).show();
//        });
//
//        tabEarnings.setOnClickListener(v -> {
//            Toast.makeText(this, "💰 Earnings - Coming Soon!", Toast.LENGTH_SHORT).show();
//        });
//
//        tabProfile.setOnClickListener(v -> {
//            Toast.makeText(this, "👤 Profile - Coming Soon!", Toast.LENGTH_SHORT).show();
//        });
//    }

    // ProviderDashboardActivity.java තුළ
    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment(); // පවතින Home UI එක මෙයට මාරු කරන්න
            } else if (id == R.id.nav_jobs) {
                selectedFragment = new JobsFragment();
            } else if (id == R.id.nav_earnings) {
                selectedFragment = new EarningsFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Default fragment එක ලෙස Home fragment එක load කරන්න
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void setupLogout() {
        btnLogoutProvider.setOnClickListener(v -> {
            new AlertDialog.Builder(this, R.style.AlertDialogDark)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Sign Out", (dialog, which) -> {
                        stopLocationUpdates();
                        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
                        prefs.edit()
                             .remove("auth_token")
                             .remove("user_role")
                             .remove("user_id")
                             .remove("provider_email")
                             .remove("provider_name")
                             .apply();
                        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProviderDashboardActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void startLocationUpdate() {
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

    private void stopLocationUpdates() {
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
                switchOnlineStatus.setChecked(false);
                setOnlineUI(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}