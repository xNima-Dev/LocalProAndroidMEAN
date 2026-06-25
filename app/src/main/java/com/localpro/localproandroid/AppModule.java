package com.localpro.localproandroid;

import android.content.Context;
import android.content.SharedPreferences;

import com.localpro.localproandroid.api.ApiService;
import com.localpro.localproandroid.api.RetrofitClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public ApiService provideApiService() {
        return RetrofitClient.getApiService();
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
        return context.getSharedPreferences("LocalProPrefs", Context.MODE_PRIVATE);
    }
}
