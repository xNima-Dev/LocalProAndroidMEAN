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

    // මීට කලින් රතු වෙලා තිබුණු කොටස හරි ගැස්සුවා (MutableLiveData -> LiveData)
    public LiveData<String> getErrorMsg() {
        return errorMsg;
    }

    public void loadBookingRequests(String status) {
        Call<BookingResponse> call;

        // 🔍 මෙතනට PENDING කියන තත්ත්වය (Status) එකතු කළා
        if ("PENDING".equalsIgnoreCase(status)) {
            call = userRepository.getPendingBookings(); // අපි අලුතින් UserRepository එකට දාපු මෙතඩ් එක
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
                // සර්වර් රෙස්පොන්ස් කෝඩ් එක ලොග් එකේ බලාගන්න (උදා: 200, 404, 500)
                Log.d("ProviderDashboardVM", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    // සාමාන්‍ยයෙන් ඔයාගේ backend එකෙන් එන්නේ getBookings() නම් එය යොදන්න, නැතහොත් response.body().data ම තියන්න
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