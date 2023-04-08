package com.weather.openweather.helperclasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SPHelper {

    public static final String spName = "weather";
    public static String cityName = "cityName";

    public static void saveStringData(Activity act, String key, String value) {
        SharedPreferences pref = act.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringData(Activity act, String key, String value) {
        SharedPreferences pref = act.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return pref.getString(key, value);
    }
}
