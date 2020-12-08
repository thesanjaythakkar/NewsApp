package com.tech.mynewsapp.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class TrackerAlarm extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("BroadCast", "Oooops...................Alarm start");
        this.context = context;

        if (!isMyServiceRunning(MyService.class)) {

            Log.e("Service", "Oooops >>>>>>>>>>>>>>>  start again");
            /*final Intent locationService = new Intent(context, LocationService.class);
            context.startService(locationService);*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final Intent locationService = new Intent(context, MyService.class);
                context.startForegroundService(locationService);
                //context.bindService(locationService, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                final Intent locationService = new Intent(context, MyService.class);
                context.startService(locationService);
                //context.bindService(locationService, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }


    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
