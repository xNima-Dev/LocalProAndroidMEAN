package com.localpro.localproandroid.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.localpro.localproandroid.repositories.UserRepository;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProviderDashboardHomeViewModel extends ViewModel {
    private final UserRepository userRepository;

    private final MutableLiveData<String> providerName = new MutableLiveData<>();
    private final MutableLiveData<String> providerEmail = new MutableLiveData<>();

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
}
