package com.localpro.localproandroid.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.CategoryAdapter;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.CategoryModel;
import com.localpro.localproandroid.models.ProviderListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_REQ_CODE = 2001;
    private static final String TAG = "CustomerHomeFragment";
    
    private double currentLat = 0.0;
    private double currentLon = 0.0;

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<CategoryModel> categoryList;
    
    public CustomerHomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        
        // Greet user
        SharedPreferences prefs = requireActivity().getSharedPreferences("LocalProPrefs", Context.MODE_PRIVATE);
        String name = prefs.getString("customer_name", "Customer");
        TextView tvName = view.findViewById(R.id.tvCustomerName);
        TextView tvInitials = view.findViewById(R.id.tvCustomerInitials);
        
        if (tvName != null) tvName.setText(name);
        if (tvInitials != null && name != null && !name.isEmpty()) {
            tvInitials.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }

        // Setup Categories
        rvCategories = view.findViewById(R.id.rvCustomerCategories);
        setupCategoryList();
        
        adapter = new CategoryAdapter(categoryList, category -> {
            if (currentLat != 0.0 && currentLon != 0.0) {
                Intent intent = new Intent(requireContext(), ProviderListActivity.class);
                intent.putExtra("SELECTED_CATEGORY", category.getId());
                intent.putExtra("LAT", currentLat);
                intent.putExtra("LON", currentLon);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Waiting for GPS location...", Toast.LENGTH_SHORT).show();
            }
        });
        
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);

        // Setup Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.customer_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    
    private void setupCategoryList() {
        categoryList = new ArrayList<>();
        categoryList.add(new CategoryModel("ac-repair", "AC Repair", "වායුසමීකරණ", "❄️"));
        categoryList.add(new CategoryModel("appliance-repair", "Appliance", "විදුලි උපකරණ", "🔌"));
        categoryList.add(new CategoryModel("cleaning", "Cleaning", "පිරිසිදුකිරීම්", "🧹"));
        categoryList.add(new CategoryModel("electrician", "Electrician", "විදුලිවැඩ", "⚡"));
        categoryList.add(new CategoryModel("plumbing", "Plumbing", "නලපද්ධති", "🚰"));
        categoryList.add(new CategoryModel("carpentry", "Carpentry", "වඩුවැඩ", "🪚"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);
        getCurrentCustomerLocation();
    }

    private void getCurrentCustomerLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLon = location.getLongitude();

                        LatLng customerLatLng = new LatLng(currentLat, currentLon);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLatLng, 13.0f));

                        fetchNearProviders(currentLat, currentLon);
                    }
                }
            });
        }
    }

    private void fetchNearProviders(double lat, double lon) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("LocalProPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) return;
        String bearerToken = "Bearer " + token;

        // Fetch all providers regardless of category for home screen map
        RetrofitClient.getApiService().getNearProviders(bearerToken, lat, lon, null).enqueue(new Callback<ProviderListResponse>() {
            @Override
            public void onResponse(Call<ProviderListResponse> call, Response<ProviderListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProviderListResponse listResponse = response.body();
                    if (listResponse.getProviders() != null) {
                        mMap.clear();
                        for (ProviderListResponse.UserDoc provider : listResponse.getProviders()) {
                            double pLon = provider.getLocation().getCoordinates().get(0);
                            double pLat = provider.getLocation().getCoordinates().get(1);
                            LatLng providerPos = new LatLng(pLat, pLon);
                            mMap.addMarker(new MarkerOptions()
                                    .position(providerPos)
                                    .title(provider.getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ProviderListResponse> call, Throwable t) {}
        });
    }
}
