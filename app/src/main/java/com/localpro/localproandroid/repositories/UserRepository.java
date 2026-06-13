package com.localpro.localproandroid.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

public class UserRepository {
    private static final  String PREF_NAME = "LocalProPrefs";
    private SharedPreferences prefs;

    @Inject
    public UserRepository(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getProviderName() {
        return prefs.getString("provider_name", "N/A");
    }

    public String getProviderEmail() {
        return prefs.getString("provider_email", "N/A");
    }
}
