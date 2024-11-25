package com.example.myapplicationuq.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PreferenceManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_PERMISSIONS = "key_permissions";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save token
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // Get token
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Save permissions
    public void savePermissions(Set<String> permissions) {
        editor.putStringSet(KEY_PERMISSIONS, permissions);
        editor.apply();
    }

    // Get permissions
    public Set<String> getPermissions() {
        return sharedPreferences.getStringSet(KEY_PERMISSIONS, new HashSet<>());
    }

    // Clear all data
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
