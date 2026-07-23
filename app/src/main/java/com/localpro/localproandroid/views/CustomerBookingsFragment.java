package com.localpro.localproandroid.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.CustomerBookingAdapter;
import com.localpro.localproandroid.api.RetrofitClient;
import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.models.BookingResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerBookingsFragment extends Fragment {

    private RecyclerView rvBookings;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyMessage;
    
    private CustomerBookingAdapter adapter;
    private List<BookingRequest> allBookings = new ArrayList<>();
    private List<BookingRequest> activeBookings = new ArrayList<>();
    private List<BookingRequest> pastBookings = new ArrayList<>();

    public CustomerBookingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_bookings, container, false);

        rvBookings = view.findViewById(R.id.rvBookings);
        tabLayout = view.findViewById(R.id.tabLayout);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);

        rvBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CustomerBookingAdapter(new ArrayList<>(), booking -> {
            android.content.Intent intent = new android.content.Intent(getContext(), CustomerBookingTrackingActivity.class);
            intent.putExtra("BOOKING_ID", booking.getId());
            if (booking.getProvider() != null) {
                intent.putExtra("PROVIDER_NAME", booking.getProvider().getName());
                intent.putExtra("PROVIDER_PHONE", booking.getProvider().getPhoneNumber());
            }
            intent.putExtra("PROVIDER_CATEGORY", booking.getServiceCategory());
            intent.putExtra("CUSTOMER_LAT", booking.getCustomerLat());
            intent.putExtra("CUSTOMER_LON", booking.getCustomerLon());
            startActivity(intent);
        });
        rvBookings.setAdapter(adapter);

        setupTabs();
        loadBookings();

        return view;
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateListForTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadBookings() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("LocalProPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) return;

        progressBar.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        rvBookings.setVisibility(View.GONE);

        RetrofitClient.getApiService().getCustomerBookings("Bearer " + token).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allBookings = response.body().getBookings();
                    if (allBookings == null) allBookings = new ArrayList<>();
                    
                    filterBookings();
                    updateListForTab(tabLayout.getSelectedTabPosition());
                } else {
                    Toast.makeText(getContext(), "Failed to load bookings", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("CustomerBookings", "API Error", t);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterBookings() {
        activeBookings.clear();
        pastBookings.clear();

        for (BookingRequest booking : allBookings) {
            String status = booking.getStatus() != null ? booking.getStatus().toLowerCase() : "";
            if (status.equals("completed") || status.equals("cancelled") || status.equals("declined") || status.equals("paid")) {
                pastBookings.add(booking);
            } else {
                activeBookings.add(booking);
            }
        }
    }

    private void updateListForTab(int tabPosition) {
        List<BookingRequest> currentList = (tabPosition == 0) ? activeBookings : pastBookings;
        
        if (currentList.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText(tabPosition == 0 ? "No active bookings found" : "No past bookings found");
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvBookings.setVisibility(View.VISIBLE);
            adapter.setBookings(currentList);
        }
    }
}
