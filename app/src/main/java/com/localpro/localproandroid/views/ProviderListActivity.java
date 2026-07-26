package com.localpro.localproandroid.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.ProviderAdapter;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.ProviderListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProviderListActivity extends AppCompatActivity {

    private RecyclerView rvProviders;
    private ProviderAdapter adapter;
    private TextView tvTitle;
    private String selectedCategory;
    private double customerLat, customerLon;
    private com.localpro.localproandroid.viewmodels.ProviderListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_list);

        rvProviders = findViewById(R.id.rvProviders);
        tvTitle = findViewById(R.id.tvCategoryTitle);
        rvProviders.setLayoutManager(new LinearLayoutManager(this));

        selectedCategory = getIntent().getStringExtra("SELECTED_CATEGORY");
        customerLat = getIntent().getDoubleExtra("LAT", 0.0);
        customerLon = getIntent().getDoubleExtra("LON", 0.0);

        if (selectedCategory == null || selectedCategory.trim().isEmpty()) {
            selectedCategory = "All";
        }

        tvTitle.setText(selectedCategory.toUpperCase().replace("-", " "));
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(com.localpro.localproandroid.viewmodels.ProviderListViewModel.class);

        // Observe ViewModel data
        viewModel.getProviders().observe(this, providers -> {
            if (providers != null && !providers.isEmpty()) {
                adapter = new ProviderAdapter(providers, provider -> {
                    // Navigate to booking confirmation
                    Intent intent = new Intent(ProviderListActivity.this, BookingConfirmationActivity.class);
                    intent.putExtra("PROVIDER_ID", provider.getId());
                    intent.putExtra("PROVIDER_NAME", provider.getName());
                    intent.putExtra("PROVIDER_PHONE", provider.getPhoneNumber());
                    intent.putExtra("PROVIDER_CATEGORY", selectedCategory);
                    intent.putExtra("CUSTOMER_LAT", customerLat);
                    intent.putExtra("CUSTOMER_LON", customerLon);
                    startActivity(intent);
                });
                rvProviders.setAdapter(adapter);
                rvProviders.setVisibility(android.view.View.VISIBLE);
                findViewById(R.id.layoutEmptyState).setVisibility(android.view.View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                if (msg.contains("No providers found")) {
                    rvProviders.setVisibility(android.view.View.GONE);
                    findViewById(R.id.layoutEmptyState).setVisibility(android.view.View.VISIBLE);
                }
            }
        });

        loadProviders();
    }

    private void loadProviders() {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.loadProviders(customerLat, customerLon, selectedCategory);
    }
}