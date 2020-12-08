package com.tech.mynewsapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.tech.mynewsapp.base.BaseActivity;
import com.tech.mynewsapp.callback.APICallBack;
import com.tech.mynewsapp.model.NewsData;
import com.tech.mynewsapp.service.TrackerAlarm;
import com.tech.mynewsapp.sharePreference.PreferenceKey;
import com.tech.mynewsapp.sharePreference.Prefs;
import com.tech.mynewsapp.utils.API_Manager;
import com.tech.mynewsapp.utils.Tools;
import com.tech.mynewsapp.utils.XMLParser;

import java.util.List;

import static com.tech.mynewsapp.sharePreference.PreferenceKey.API_XML;
import static com.tech.mynewsapp.sharePreference.PreferenceKey.TAG;

public class ActivitySplashScreen extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_splash_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponent();
    }

    private void initComponent() {

        startAlarmManager();

        if (Prefs.getBoolean(PreferenceKey.DataOnLoad, false)) {
            callhandler();
        } else {
            if (Tools.isNetworkAvailable(ActivitySplashScreen.this)) {
                API_Manager.API_Manager(new APICallBack() {
                    @Override
                    public void ResponseWithSuccess(String success) {

                        Log.e(TAG, "Splash Apicall: " + success);
                        Prefs.putString(API_XML, success);

                        if (!success.equals("")) {
                            XMLParser xmlParser = new XMLParser();
                            List<NewsData> NewsDataList = xmlParser.ResponceParse(Prefs.getString(PreferenceKey.API_XML));

                            if (NewsDataList.size() > 0) {
                                NewsData newsData = NewsDataList.get(0);
                                Prefs.putLong(PreferenceKey.APITime, newsData.getTimestemp());
                            }

                            callhandler();
                        }
                    }
                });

            } else {
                showSnackbar("Network is not available");
            }
        }
    }

    @SuppressLint("ShortAlarm")
    private void startAlarmManager() {

        Intent intent = new Intent(getApplicationContext(), TrackerAlarm.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, TrackerAlarm.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long firstMillis = System.currentTimeMillis(); // alarm is set right away

        AlarmManager mgrAlarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mgrAlarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 5000, pIntent);
    }

    private void callhandler() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                StartActivity(MainActivity.class);
                finish();
            }
        }, 1000);
    }

}

