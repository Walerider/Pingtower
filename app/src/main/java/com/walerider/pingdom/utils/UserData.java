package com.walerider.pingdom.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserData {
    private static final String PREFS_NAME = "userPrefs";
    private static SharedPreferences sharedPreferences;
    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public static void setString(String key,String value){
        sharedPreferences.edit().putString(key,value).apply();
    }
    public static String getString(String key){
        return sharedPreferences.getString(key,"");
    }
    public static void setBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key,value).apply();
    }

    public static Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key,false);
    }
    public static void setInteger(String key, int value) {
        sharedPreferences.edit().putInt(key,value).apply();
    }public static void setLong(String key, Long value) {
        sharedPreferences.edit().putLong(key,value).apply();
    }
    public static void clearAll() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }
    public static Integer getInteger(String key) {
        return sharedPreferences.getInt(key,-1);
    }
}
