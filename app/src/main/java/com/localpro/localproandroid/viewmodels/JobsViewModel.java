package com.localpro.localproandroid.viewmodels;

import android.util.Log;

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
public class JobsViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<List<BookingRequest>> activeJobs = new MutableLiveData<>();
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();

    @Inject
    public JobsViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<List<BookingRequest>> getActiveJobs() {
        return activeJobs;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public void loadActiveJobs() {
        userRepository.getActiveBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
               if (response.isSuccessful() && response.body() != null) {
                   activeJobs.setValue(response.body().data);
               } else {
                   errorMsg.setValue("Failed to load active jobs. Error: " + response.code() + " " + response.message());
               }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                errorMsg.setValue("Network Error: " + t.getMessage());
                Log.e("JobsViewModel", "Error fetching active jobs", t);
            }
        });
    }

    public void cancelActiveJob(BookingRequest request) {
        userRepository.declineBooking(request.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Remove from local list on success
                    List<BookingRequest> current = activeJobs.getValue();
                    if (current != null) {
                        current.remove(request);
                        activeJobs.setValue(current);
                    }
                } else {
                    errorMsg.setValue("Failed to cancel. Error: " + response.code());
                    Log.e("JobsViewModel", "Cancel error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMsg.setValue("Network Error: " + t.getMessage());
                Log.e("JobsViewModel", "Cancel failure", t);
            }
        });
    }
}
