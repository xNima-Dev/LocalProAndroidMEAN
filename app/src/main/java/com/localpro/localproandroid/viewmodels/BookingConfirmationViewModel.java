package com.localpro.localproandroid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.models.CreateBookingRequest;
import com.localpro.localproandroid.models.CreateBookingResponse;
import com.localpro.localproandroid.repositories.CustomerRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class BookingConfirmationViewModel extends ViewModel {
    private final CustomerRepository customerRepository;

    private final MutableLiveData<String> bookingSuccessId = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public BookingConfirmationViewModel(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public LiveData<String> getBookingSuccessId() { return bookingSuccessId; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void createBooking(String providerId, String category, String jobDescription, 
                              String distanceText, double estimatedEarning, double customerLat, double customerLon) {
        
        isLoading.setValue(true);
        
        CreateBookingRequest request = new CreateBookingRequest(
                providerId, category, jobDescription, distanceText, estimatedEarning, customerLat, customerLon
        );

        customerRepository.createBooking(request).enqueue(new Callback<CreateBookingResponse>() {
            @Override
            public void onResponse(Call<CreateBookingResponse> call, Response<CreateBookingResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    String newBookingId = response.body().getBooking() != null
                            ? response.body().getBooking().getId() : null;
                    bookingSuccessId.setValue(newBookingId);
                } else {
                    errorMessage.setValue("Error creating booking. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CreateBookingResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
