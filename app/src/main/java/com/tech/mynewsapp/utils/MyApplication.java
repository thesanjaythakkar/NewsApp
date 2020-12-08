package com.tech.mynewsapp.utils;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.jenzz.appstate.AppStateListener;
import com.jenzz.appstate.AppStateMonitor;
import com.tech.mynewsapp.sharePreference.PreferenceKey;
import com.tech.mynewsapp.sharePreference.Prefs;


public class MyApplication extends Application {

    public static AppStateMonitor appStateMonitor;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        AppState();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void AppState() {

        appStateMonitor = AppStateMonitor.create(this);
        AppStateListener appStateListener = new AppStateListener() {
            @Override
            public void onAppDidEnterForeground() {
                Log.e("AppStateListener", "onApp Did Enter Foreground");
                Prefs.putString(PreferenceKey.IsBackForgroung, "0");
            }

            @Override
            public void onAppDidEnterBackground() {
                Log.e("AppStateListener", "onAppDid Enter Background");

                Prefs.putString(PreferenceKey.IsBackForgroung, "1");


            }
        };
        appStateMonitor.addListener(appStateListener);
        appStateMonitor.start();
    }

}
