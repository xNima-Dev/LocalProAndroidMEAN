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

public class ProviderListActivity extends AppCompatActivity {

    private RecyclerView rvProviders;
    private ProviderAdapter adapter;
    private TextView tvTitle;
    private String selectedCategory;
    private double customerLat, customerLon;

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

        tvTitle.setText(selectedCategory.toUpperCase().replace("-", " ") + "S NEAR YOU 🛠️");

        loadProviders();
    }

    private void loadProviders() {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String bearerToken = "Bearer " + token;

        RetrofitClient.getApiService().getNearProviders(bearerToken, customerLat, customerLon, selectedCategory).enqueue(new Callback<ProviderListResponse>() {
            @Override
            public void onResponse(Call<ProviderListResponse> call, Response<ProviderListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getProviders() != null && !response.body().getProviders().isEmpty()) {
                        adapter = new ProviderAdapter(response.body().getProviders(), provider -> {
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
                    } else {
                        Toast.makeText(ProviderListActivity.this, "No providers found for this category.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("ProviderList", "Server error: " + response.code());
                    Toast.makeText(ProviderListActivity.this, "Error fetching providers. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProviderListResponse> call, Throwable t) {
                Log.e("ProviderList", "Network failure", t);
                Toast.makeText(ProviderListActivity.this, "Network error. Check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}