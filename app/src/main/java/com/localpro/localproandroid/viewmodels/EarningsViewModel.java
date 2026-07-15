package com.localpro.localproandroid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.models.BookingRequest;
import com.localpro.localproandroid.models.BookingResponse;
import com.localpro.localproandroid.repositories.UserRepository;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class EarningsViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<Double> totalBalance = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> weekEarnings = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> weekJobs = new MutableLiveData<>(0);
    private final MutableLiveData<Double> monthEarnings = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> monthJobs = new MutableLiveData<>(0);
    private final MutableLiveData<Double> avgPerJob = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public EarningsViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Double> getTotalBalance() { return totalBalance; }
    public LiveData<Double> getWeekEarnings() { return weekEarnings; }
    public LiveData<Integer> getWeekJobs() { return weekJobs; }
    public LiveData<Double> getMonthEarnings() { return monthEarnings; }
    public LiveData<Integer> getMonthJobs() { return monthJobs; }
    public LiveData<Double> getAvgPerJob() { return avgPerJob; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    
    private final MutableLiveData<List<BookingRequest>> recentTransactions = new MutableLiveData<>();
    public LiveData<List<BookingRequest>> getRecentTransactions() { return recentTransactions; }

    public void loadEarnings() {
        isLoading.setValue(true);
        userRepository.getCompletedBookings().enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<BookingRequest> bookings = response.body().getBookings();
                    computeStats(bookings);
                } else {
                    errorMsg.setValue("Failed to load earnings. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMsg.setValue("Network error: " + t.getMessage());
            }
        });
    }

    private void computeStats(List<BookingRequest> bookings) {
        if (bookings == null) return;

        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH);

        // Week: last 7 days in milliseconds
        long oneWeekAgo = now.getTimeInMillis() - (7L * 24 * 60 * 60 * 1000);

        double total = 0;
        double weekTotal = 0;
        int weekCount = 0;
        double monthTotal = 0;
        int monthCount = 0;

        for (BookingRequest booking : bookings) {
            double earning = booking.getEarning();
            total += earning;

            // Parse request time to determine week/month bucket
            String requestTime = booking.getRequestTime();
            if (requestTime != null) {
                try {
                    String cleanTime = requestTime;
                    if (cleanTime.endsWith("+00:00")) {
                        cleanTime = cleanTime.substring(0, cleanTime.length() - 6) + "Z";
                    }
                    java.text.DateFormat sdf;
                    if (cleanTime.contains(".")) {
                        sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                    } else {
                        sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault());
                    }
                    java.util.Date date = sdf.parse(cleanTime);
                    if (date != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);

                        // Month check
                        if (cal.get(Calendar.YEAR) == currentYear && cal.get(Calendar.MONTH) == currentMonth) {
                            monthTotal += earning;
                            monthCount++;
                        }

                        // Week check
                        if (date.getTime() >= oneWeekAgo) {
                            weekTotal += earning;
                            weekCount++;
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        totalBalance.setValue(total);
        weekEarnings.setValue(weekTotal);
        weekJobs.setValue(weekCount);
        monthEarnings.setValue(monthTotal);
        monthJobs.setValue(monthCount);
        avgPerJob.setValue(bookings.isEmpty() ? 0.0 : total / bookings.size());

        // Sort by most recent (assuming they might not be sorted)
        // For simplicity, we just reverse the list assuming backend returns oldest first
        List<BookingRequest> recentList = new java.util.ArrayList<>(bookings);
        java.util.Collections.reverse(recentList);
        // Keep top 10
        if (recentList.size() > 10) {
            recentList = recentList.subList(0, 10);
        }
        recentTransactions.setValue(recentList);
    }
}
