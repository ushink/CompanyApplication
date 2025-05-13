package com.publish.monitorsystem.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    private static final String PREF_NAME = "monitor_system_prefs";

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }
} 