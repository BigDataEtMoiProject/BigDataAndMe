package ca.uqac.bigdataetmoi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Prefs {
    public static void setBoolean(Context context, String sharedPrefs, String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setString(Context context, String sharedPrefs, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String sharedPrefs, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static String getString(Context context, String sharedPrefs, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}
