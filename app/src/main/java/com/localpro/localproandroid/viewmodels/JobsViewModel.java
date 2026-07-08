package com.localpro.localproandroid.viewmodels;

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
    private final MutableLiveData<List<BookingRequest>> completedJobs = new MutableLiveData<>();
    private final MutableLiveData<List<BookingRequest>> cancelledJobs = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();

    @Inject
    public JobsViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<List<BookingRequest>> getActiveJobs() {
        return activeJobs;
    }
    public LiveData<List<BookingRequest>> getCompletedJobs() { return completedJobs; }
    public LiveData<List<BookingRequest>> getCancelledJobs() { return cancelledJobs; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public void loadActiveJobs() {
        isLoading.setValue(true);
        userRepository.getActiveBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    activeJobs.setValue(response.body().getBookings());
                } else {
                    errorMsg.setValue("Failed to load active jobs.");
                }
            }
            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMsg.setValue("Network Error: " + t.getMessage());
            }
        });
    }

    public void loadCompletedJobs() {
        isLoading.setValue(true);
        userRepository.getCompletedBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    completedJobs.postValue(response.body().getBookings());
                } else {
                    errorMsg.postValue("Failed to load completed jobs.");
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                isLoading.postValue(false);
                errorMsg.postValue("Network Error: " + t.getMessage());
            }
        });
    }

    public void loadCancelledJobs() {
        isLoading.setValue(true);
        userRepository.getCancelledBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    cancelledJobs.setValue(response.body().getBookings());
                } else {
                    errorMsg.setValue("Failed to load cancelled jobs.");
                }
            }
            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMsg.setValue("Network Error: " + t.getMessage());
            }
        });
    }

    public void cancelActiveJob(BookingRequest request) {
        userRepository.declineBooking(request.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<BookingRequest> current = activeJobs.getValue();
                    if (current != null) {
                        current.remove(request);
                        activeJobs.setValue(current);
                    }
                    loadCancelledJobs();
                } else {
                    errorMsg.setValue("Failed to cancel job.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMsg.setValue("Network Error: " + t.getMessage());
            }
        });
    }
}
