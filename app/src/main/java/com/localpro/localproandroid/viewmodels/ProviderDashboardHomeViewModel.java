package com.localpro.localproandroid.viewmodels;

import static androidx.fragment.app.FragmentManager.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.models.BookingResponse;
import com.localpro.localproandroid.repositories.UserRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ProviderDashboardHomeViewModel extends ViewModel {
    private final UserRepository userRepository;

    private final MutableLiveData<String> providerName = new MutableLiveData<>();
    private final MutableLiveData<String> providerEmail = new MutableLiveData<>();

    private final MutableLiveData<List<BookingRequest>> bookings = new MutableLiveData<>();
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();

    @Inject
    public ProviderDashboardHomeViewModel(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getProviderName() {
        return providerName;
    }

    public LiveData<String> getProviderEmail() {
        return providerEmail;
    }

    public void loadProviderInfo() {
        providerName.setValue(userRepository.getProviderName());
        providerEmail.setValue(userRepository.getProviderEmail());
    }

    public LiveData<List<BookingRequest>> getBookings() {
        return bookings;
    }

    public void loadBookingRequests() {
        userRepository.getBooking().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    System.out.println("booking response" + response);
                    bookings.setValue(response.body().data);
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                errorMsg.setValue("Network Error");
            }
        });
    }

    public void logout() {
        userRepository.clearUserSession();
    }

    public void acceptBooking(BookingRequest request) {
        userRepository.acceptBooking(request.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removeBookingFromList(request);
                } else {
                    Log.e("ProviderDashboardviewModel", "❌ Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ProviderDashboardviewModel", "💥 failure", t);
            }
        });
    }

    public void declineBooking(BookingRequest request) {
        userRepository.declineBooking(request.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removeBookingFromList(request);
                } else {
                    Log.e("ProviderDashboardviewModel", "❌ Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ProviderDashboardviewModel", "💥 failure", t);
            }
        });
    }

    private void removeBookingFromList(BookingRequest request) {
        List<BookingRequest> currentList = bookings.getValue();
        if (currentList != null) {
            currentList.remove(request);
            bookings.setValue(currentList);
        }
    }
}
