package com.localpro.localproandroid.viewmodels;


import android.util.Log;

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

    // Real-time stats
    private final MutableLiveData<Double> totalEarnings = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> activeJobs = new MutableLiveData<>(0);
    private final MutableLiveData<Double> rating = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> ratedJobsCount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> jobsDone = new MutableLiveData<>(0);

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

    public LiveData<Double> getTotalEarnings() { return totalEarnings; }
    public LiveData<Integer> getActiveJobs() { return activeJobs; }
    public LiveData<Double> getRating() { return rating; }
    public LiveData<Integer> getRatedJobsCount() { return ratedJobsCount; }
    public LiveData<Integer> getJobsDone() { return jobsDone; }

    public void loadProviderInfo() {
        providerName.setValue(userRepository.getProviderName());
        providerEmail.setValue(userRepository.getProviderEmail());
        fetchDashboardStats();
    }

    private void fetchDashboardStats() {
        // Fetch Active Jobs Count
        userRepository.getActiveBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBookings() != null) {
                    activeJobs.setValue(response.body().getBookings().size());
                }
            }
            @Override
            public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {}
        });

        // Fetch Completed Jobs for Earnings, Jobs Done, and Rating
        userRepository.getCompletedBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getBookings() != null) {
                    List<BookingRequest> completed = response.body().getBookings();
                    jobsDone.setValue(completed.size());
                    
                    double totalEarn = 0;
                    double totalRating = 0;
                    int ratedJobs = 0;

                    for (BookingRequest req : completed) {
                        totalEarn += req.getEarning();
                        if (req.getRating() > 0) {
                            totalRating += req.getRating();
                            ratedJobs++;
                        }
                    }
                    
                    totalEarnings.setValue(totalEarn);
                    ratedJobsCount.setValue(ratedJobs);
                    
                    if (ratedJobs > 0) {
                        rating.setValue(totalRating / ratedJobs);
                    } else if (completed.size() > 0) {
                        // Default good rating if no ratings given yet
                        rating.setValue(5.0);
                    } else {
                        rating.setValue(0.0);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {}
        });
    }

    public LiveData<List<BookingRequest>> getBookings() {
        return bookings;
    }

    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public void loadBookingRequests(String status) {
        Call<BookingResponse> call;

        if ("PENDING".equalsIgnoreCase(status)) {
            call = userRepository.getPendingBookings();
        } else if ("ACTIVE".equalsIgnoreCase(status)) {
            call = userRepository.getActiveBookings();
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            call = userRepository.getCompletedBookings();
        } else {
            call = userRepository.getCancelledBookings();
        }

        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                Log.d("ProviderDashboardVM", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    bookings.setValue(response.body().getBookings());
                } else {
                    errorMsg.setValue("Server Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                Log.e("ProviderDashboardVM", "Network Failure", t);
                errorMsg.setValue("Network Error. Please check your backend server!");
            }
        });
    }

    public void logout() {
        userRepository.clearUserSession();
    }

    public void acceptBooking(BookingRequest request) {
        userRepository.acceptBooking(request.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    removeBookingFromList(request);
                } else {
                    Log.e("ProviderDashboardviewModel", "❌ Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("ProviderDashboardviewModel", "💥 failure", t);
            }
        });
    }

    public void declineBooking(BookingRequest request) {
        userRepository.declineBooking(request.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    removeBookingFromList(request);
                } else {
                    Log.e("ProviderDashboardviewModel", "❌ Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
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