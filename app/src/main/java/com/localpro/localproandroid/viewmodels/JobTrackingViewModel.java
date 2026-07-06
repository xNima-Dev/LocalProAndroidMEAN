package com.localpro.localproandroid.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.repositories.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class JobTrackingViewModel extends ViewModel {

    private static final String TAG = "JobTrackingViewModel";

    private final UserRepository userRepository;

    // Current booking status: "ride" | "arrived" | "completed" | "paid" | "unpaid"
    private final MutableLiveData<String> jobStatus = new MutableLiveData<>("ride");

    // Loading state
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // Error / Success messages
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private final MutableLiveData<String> successMsg = new MutableLiveData<>();

    @Inject
    public JobTrackingViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getJobStatus() { return jobStatus; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<String> getSuccessMsg() { return successMsg; }

    public void markAsRide(String bookingId) {
        isLoading.setValue(true);
        userRepository.updateStatusRide(bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    jobStatus.setValue("ride");
                    successMsg.setValue("Status updated: On the way!");
                } else {
                    // Even if API fails, update local status for smooth UX
                    jobStatus.setValue("ride");
                    Log.w(TAG, "markAsRide: server returned " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                // Update local status anyway
                jobStatus.setValue("ride");
                Log.e(TAG, "markAsRide failed: " + t.getMessage());
            }
        });
    }

    public void markAsArrived(String bookingId) {
        isLoading.setValue(true);
        userRepository.updateStatusArrived(bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    jobStatus.setValue("arrived");
                    successMsg.setValue("Great! You've arrived at the location.");
                } else {
                    jobStatus.setValue("arrived");
                    Log.w(TAG, "markAsArrived: server returned " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                jobStatus.setValue("arrived");
                Log.e(TAG, "markAsArrived failed: " + t.getMessage());
            }
        });
    }

    public void markAsCompleted(String bookingId) {
        isLoading.setValue(true);
        userRepository.updateStatusCompleted(bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    jobStatus.setValue("completed");
                    successMsg.setValue("Job completed! Please mark payment status.");
                } else {
                    jobStatus.setValue("completed");
                    Log.w(TAG, "markAsCompleted: server returned " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                jobStatus.setValue("completed");
                Log.e(TAG, "markAsCompleted failed: " + t.getMessage());
            }
        });
    }

    public void markAsPaid(String bookingId) {
        isLoading.setValue(true);
        userRepository.updateStatusPaid(bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    jobStatus.setValue("paid");
                    successMsg.setValue("Payment received! Job closed. 🎉");
                } else {
                    jobStatus.setValue("paid");
                    Log.w(TAG, "markAsPaid: server returned " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                jobStatus.setValue("paid");
                Log.e(TAG, "markAsPaid failed: " + t.getMessage());
            }
        });
    }

    public void markAsUnpaid(String bookingId) {
        isLoading.setValue(true);
        userRepository.updateStatusUnpaid(bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    jobStatus.setValue("unpaid");
                    successMsg.setValue("Marked as unpaid. Follow up with customer.");
                } else {
                    jobStatus.setValue("unpaid");
                    Log.w(TAG, "markAsUnpaid: server returned " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading.setValue(false);
                jobStatus.setValue("unpaid");
                Log.e(TAG, "markAsUnpaid failed: " + t.getMessage());
            }
        });
    }
}
