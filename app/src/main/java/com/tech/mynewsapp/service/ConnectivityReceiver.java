package com.tech.mynewsapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class ConnectivityReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();

    public ConnectivityReceiver() {
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent) {

// call your method
        Log.e(TAG, " >>>>>>>>>>>>  >>>>>>>>>>>>>>> >>>>>>>>>");
        if (intent.getAction() == Intent.ACTION_POWER_CONNECTED) {
            Log.e(TAG, ">>>>>>>>>>>>  ACTION POWER CONNECTED");

            Toast.makeText(context, "ACTION POWER CONNECTED", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, ">>>>>>>>>>>>  ACTION POWER DISCONNECTED");
            Toast.makeText(context, "ACTION POWER CONNECTED", Toast.LENGTH_LONG).show();
        }
    }


}
