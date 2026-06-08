package com.localpro.localproandroid.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

        // Intent eken category eka saha customerege lat/lon gannava
        selectedCategory = getIntent().getStringExtra("SELECTED_CATEGORY");
        customerLat = getIntent().getDoubleExtra("LAT", 0.0);
        customerLon = getIntent().getDoubleExtra("LON", 0.0);

        tvTitle.setText(selectedCategory.toUpperCase() + "S NEAR YOU 🛠️");


    }

    private void loadProviders() {
        SharedPreferences prefs = getSharedPreferences("LocalProPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth|_token", null);
        String bearerToken = "Bearer " + token;

        ///  Parse the category with API call...
        RetrofitClient.getApiService().getNearProviders(bearerToken, customerLat, customerLon, selectedCategory.toUpperCase()).enqueue(new Callback<ProviderListResponse>() {
            @Override
            public void onResponse(Call<ProviderListResponse> call, Response<ProviderListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new ProviderAdapter(response.body().getProviders(), provider -> {
                        Toast.makeText(ProviderListActivity.this, "Clicked: " + provider.getName(), Toast.LENGTH_SHORT).show();
                    });
                    rvProviders.setAdapter(adapter);
                } else {
                    Toast.makeText(ProviderListActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProviderListResponse> call, Throwable t) {
                Log.e("ListError", "Failed", t);
            }
        });
    }
}