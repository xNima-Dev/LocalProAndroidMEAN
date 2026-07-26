package com.localpro.localproandroid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.models.ProviderListResponse;
import com.localpro.localproandroid.repositories.CustomerRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ProviderListViewModel extends ViewModel {
    private final CustomerRepository customerRepository;

    private final MutableLiveData<List<ProviderListResponse.UserDoc>> providers = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public ProviderListViewModel(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public LiveData<List<ProviderListResponse.UserDoc>> getProviders() { return providers; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadProviders(double lat, double lon, String category) {
        isLoading.setValue(true);
        customerRepository.getNearProviders(lat, lon, category).enqueue(new Callback<ProviderListResponse>() {
            @Override
            public void onResponse(Call<ProviderListResponse> call, Response<ProviderListResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getProviders() != null && !response.body().getProviders().isEmpty()) {
                        providers.setValue(response.body().getProviders());
                    } else {
                        errorMessage.setValue("No providers found for this category.");
                    }
                } else {
                    errorMessage.setValue("Error fetching providers. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProviderListResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error. Check your connection.");
            }
        });
    }
}
