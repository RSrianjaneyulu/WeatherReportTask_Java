package com.weather.openweather.helperclasses;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.weather.openweather.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Helper {
    private Context mContext;
    private static SimpleDateFormat df = null;

    public Helper(Context context) {
        this.mContext = context;
    }

    public static void showShortToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
    //Convert Capital Letter
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    //Recyclerview - Divider Decoration Common Code
    public static void recyclerViewDivider(RecyclerView rv, Activity act) {
        DividerItemDecoration divider = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(act.getBaseContext(), R.drawable.recyclerview_divider));
        rv.addItemDecoration(divider);
    }
    //Convert Date Format
    public static String convertUnixTimeToDateMonth(String unixDateTime) {
        String amPm = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,  hh:mm");
        long millis = Long.valueOf(unixDateTime) * 1000;
        Date date = new Date();
        date.setTime(millis);
        SimpleDateFormat sdf1 = new SimpleDateFormat("aa");
        //converting date format for US
        TimeZone timezoneInAmerica = TimeZone.getTimeZone("America/New_York");
        sdf1.setTimeZone(timezoneInAmerica);
        if (sdf1.format(date).equalsIgnoreCase("pm")) {
            amPm = "pm";
        } else {
            amPm = "am";
        }
        return sdf.format(date) + amPm;
    }
    //Chcek internet connection
    public static boolean checkInternetConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) { // connected to the internet
            Helper.showShortToast(context, activeNetworkInfo.getTypeName());
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            } else {
                return false;
            }
        }else {
            Helper.showShortToast(context, context.getResources().getString(R.string.internetconnection_required));
        }
        return false;
    }
}

