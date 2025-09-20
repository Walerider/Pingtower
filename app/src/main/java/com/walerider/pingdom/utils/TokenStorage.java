package com.walerider.pingdom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class TokenStorage {
    private static SharedPreferences encryptedPrefs;
    private static final String TOKEN_KEY = "auth_token";
    public static void init(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encryptedPrefs = EncryptedSharedPreferences.create(
                    context,
                    "jwt_storage",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e("TokenStorage", "Failed to init encrypted prefs", e);
        }
    }

    public static void saveToken(String token) {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().putString(TOKEN_KEY, token).apply();
        }
    }

    // Метод для получения токена
    public static String getToken() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.getString(TOKEN_KEY, null);
        }
        return null;
    }

    // Метод для проверки наличия токена
    public static boolean hasToken() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.contains(TOKEN_KEY) &&
                    encryptedPrefs.getString(TOKEN_KEY, null) != null;
        }
        return false;
    }

    // Метод для удаления токена (logout)
    public static void removeToken() {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().remove(TOKEN_KEY).apply();
        }
    }

    // Метод для очистки всех данных
    public static void clear() {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().clear().apply();
        }
    }
}