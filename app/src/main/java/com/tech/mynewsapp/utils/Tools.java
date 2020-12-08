package com.tech.mynewsapp.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.format.DateFormat;

import androidx.annotation.RequiresApi;

import com.tech.mynewsapp.service.MyService;
import com.tech.mynewsapp.sharePreference.PreferenceKey;
import com.tech.mynewsapp.sharePreference.Prefs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Tools {

    public static long convertDateToTimestap(String date) {
        long result = 0;
        try {
           SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
           Date parsed = sourceFormat.parse(date);

           assert parsed != null;
           result = parsed.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getDateTimestep(long time) {
        String date = "";
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(time * 1000);
            date = DateFormat.format("dd-MM-yyyy HH:mm", cal.getTime()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDate(Date time) {
        String date = "";
        try {
            date = DateFormat.format("dd-MM-yyyy HH:mm", time).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isMyServiceRunning(Context activity) {
        ActivityManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        }
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Activity act) {

        ConnectivityManager connMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static int getMinute() {

        Calendar cal = Calendar.getInstance();
        Date date1 = cal.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(Prefs.getLong(PreferenceKey.ConnectAPITime));
        Date date2 = cal2.getTime();

        long milliseconds = date1.getTime() - date2.getTime();
        int seconds = (int) milliseconds / 1000;

        return (seconds % 3600) / 60;
    }

}
