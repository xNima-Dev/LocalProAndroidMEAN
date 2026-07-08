package com.localpro.localproandroid.views.providerDashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.localpro.localproandroid.R;
import com.localpro.localproandroid.adapter.BookingRequestAdapter;
import com.localpro.localproandroid.databinding.FragmentHomeBinding;
import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.viewmodels.ProviderDashboardHomeViewModel;
import com.localpro.localproandroid.views.MainActivity;
import com.localpro.localproandroid.views.ProviderDashboardActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements BookingRequestAdapter.OnBookingActionListener {

    private FragmentHomeBinding binding;
    private ProviderDashboardHomeViewModel viewModel;
    private BookingRequestAdapter bookingAdapter;
    private Animation pulseAnimation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProviderDashboardHomeViewModel.class);

        viewModel.getProviderName().observe(getViewLifecycleOwner(), name -> {
            binding.tvProviderName.setText(name);
            if (name != null && !name.trim().isEmpty()) {
                binding.tvAvatarInitials.setText(String.valueOf(name.trim().charAt(0)).toUpperCase());
            } else {
                binding.tvAvatarInitials.setText("P");
            }
        });
        viewModel.getProviderEmail().observe(getViewLifecycleOwner(), email -> {
            binding.tvProviderEmail.setText(email);
        });
        viewModel.loadProviderInfo();

        // RecyclerView Setup
        bookingAdapter = new BookingRequestAdapter(this);
        binding.rvBookingRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBookingRequests.setAdapter(bookingAdapter);

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Bookings LiveData Observe
        viewModel.getBookings().observe(getViewLifecycleOwner(), list -> {
            Log.d("LocalPro_Debug", "Bookings Observer triggered!");
            Log.d("LocalPro_Debug", "List size: " + (list != null ? list.size() : "null"));
            bookingAdapter.setRequests(list);
            binding.tvRequestCount.setText(list != null ? list.size() + " New" : "0 New");

            if (list == null || list.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                binding.rvBookingRequests.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.rvBookingRequests.setVisibility(View.VISIBLE);
            }
        });

        viewModel.loadBookingRequests("pending");

        pulseAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse_animation);
        setupOnlineToggle();
        setupLogout();
    }

    @Override
    public void onAccept(BookingRequest request, int position) {
        viewModel.acceptBooking(request);
    }

    @Override
    public void onDecline(BookingRequest request, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Decline Booking")
                .setMessage("Are you sure you want to decline this?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModel.declineBooking(request);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void setupLogout() {
        binding.btnLogoutProvider.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext(), R.style.AlertDialogDark)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Sign Out", (dialog, which) -> {
                        viewModel.logout();
                        if (getActivity() instanceof ProviderDashboardActivity) {
                            ((ProviderDashboardActivity) getActivity()).stopLocationUpdates();
                        }
                        Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupOnlineToggle() {
        binding.switchOnlineStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (getActivity() instanceof ProviderDashboardActivity) {
                ((ProviderDashboardActivity) getActivity()).setOnlineState(isChecked);
            }
            setOnlineUI(isChecked);
        });
    }

    private void setOnlineUI(boolean online) {
        if (online) {
            binding.cardOnlineToggle.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_online_card));
            binding.tvOnlineTitle.setText("You are ONLINE 🟢");
            binding.tvOnlineSubtitle.setText("Receiving booking requests from nearby customers");
            binding.tvStatusLabel.setText("ONLINE");
            binding.tvStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.lp_green));
            binding.layoutStatusBadge.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_status_online));
            binding.switchOnlineStatus.setTrackTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.lp_green)));
            binding.viewPulseOuter.startAnimation(pulseAnimation);
            binding.viewStatusDot.setAlpha(1.0f);
        } else {
            binding.cardOnlineToggle.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_offline_card));
            binding.tvOnlineTitle.setText("Go Online");
            binding.tvOnlineSubtitle.setText("Toggle to receive booking requests");
            binding.tvStatusLabel.setText("OFFLINE");
            binding.tvStatusLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.lp_text_secondary));
            binding.layoutStatusBadge.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.bg_status_offline));
            binding.switchOnlineStatus.setTrackTintList(
                    android.content.res.ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.lp_offline)));
            binding.viewPulseOuter.clearAnimation();
            binding.viewStatusDot.setAlpha(0.4f);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}