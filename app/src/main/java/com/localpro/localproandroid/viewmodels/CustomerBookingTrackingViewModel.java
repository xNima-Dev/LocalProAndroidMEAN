package com.localpro.localproandroid.viewmodels;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.models.BookingResponse;
import com.localpro.localproandroid.repositories.CustomerRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class CustomerBookingTrackingViewModel extends ViewModel {
    private final CustomerRepository customerRepository;

    private final MutableLiveData<BookingRequest> currentBooking = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private Runnable pollRunnable;
    private String trackingBookingId;
    private boolean isPolling = false;

    @Inject
    public CustomerBookingTrackingViewModel(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public LiveData<BookingRequest> getCurrentBooking() { return currentBooking; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void startTracking(String bookingId) {
        this.trackingBookingId = bookingId;
        if (!isPolling) {
            isPolling = true;
            startPolling();
        }
    }
    
    public void stopTracking() {
        isPolling = false;
        if (pollRunnable != null) {
            pollHandler.removeCallbacks(pollRunnable);
        }
    }

    private void startPolling() {
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPolling) return;
                
                fetchLatestBookingStatus();
                
                // Poll every 5 seconds
                pollHandler.postDelayed(this, 5000);
            }
        };
        pollHandler.post(pollRunnable);
    }

    private void fetchLatestBookingStatus() {
        customerRepository.getActiveBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBookings() != null) {
                    List<BookingRequest> bookings = response.body().getBookings();
                    boolean found = false;
                    for (BookingRequest b : bookings) {
                        if (b.getId().equals(trackingBookingId)) {
                            currentBooking.setValue(b);
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        // Check pending
                        checkPendingBookings();
                    }
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                // Ignore network errors during polling to avoid spamming the user
            }
        });
    }
    
    private void checkPendingBookings() {
        customerRepository.getPendingBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBookings() != null) {
                    List<BookingRequest> bookings = response.body().getBookings();
                    for (BookingRequest b : bookings) {
                        if (b.getId().equals(trackingBookingId)) {
                            currentBooking.setValue(b);
                            return;
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {}
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopTracking();
    }
}
