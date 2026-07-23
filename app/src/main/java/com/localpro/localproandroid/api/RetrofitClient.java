package com.localpro.localproandroid.api;

import android.os.Build;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 10.0.2.2 = Android Emulator's alias for host machine localhost
    // 192.168.8.171 = Host machine LAN IP for physical device on same WiFi
    private static final String EMULATOR_URL  = "http://10.0.2.2:5000/";
    private static final String DEVICE_URL    = "http://192.168.8.171:5000/";

    private static String getBaseUrl() {
        // Detect if running inside the Android emulator
        boolean isEmulator = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
        return isEmulator ? EMULATOR_URL : DEVICE_URL;
    }

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
