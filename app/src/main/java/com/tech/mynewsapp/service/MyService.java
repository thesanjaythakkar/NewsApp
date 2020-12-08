package com.tech.mynewsapp.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.tech.mynewsapp.MainActivity;
import com.tech.mynewsapp.R;
import com.tech.mynewsapp.callback.APICallBack;
import com.tech.mynewsapp.model.NewsData;
import com.tech.mynewsapp.sharePreference.PreferenceKey;
import com.tech.mynewsapp.sharePreference.Prefs;
import com.tech.mynewsapp.utils.API_Manager;
import com.tech.mynewsapp.utils.XMLParser;

import java.util.Calendar;
import java.util.List;

import static com.tech.mynewsapp.sharePreference.PreferenceKey.API_XML;
import static com.tech.mynewsapp.utils.NotificationClass.sendNotification;
import static com.tech.mynewsapp.utils.Tools.getMinute;

public class MyService extends Service {
    private final String ADMIN_CHANNEL_ID = "MyNewsApp";
    private Context context = this;
    private String TAG = this.getClass().getSimpleName();
    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call your method
            if (intent.getAction() == Intent.ACTION_POWER_CONNECTED) {
                Log.e(TAG, " Service>>>>>>>>>>>>  ACTION POWER CONNECTED");
                Toast.makeText(context, "ACTION POWER CONNECTED", Toast.LENGTH_SHORT).show();
                if (Prefs.getLong(PreferenceKey.ConnectAPITime) == 0) {
                    if (Prefs.getBoolean(PreferenceKey.DataOnLoad, false) && Prefs.getString(PreferenceKey.IsBackForgroung).equals("1")) {
                        callmaneger();
                    }

                } else {
                    if (getMinute() > 10) {
                        if (Prefs.getBoolean(PreferenceKey.DataOnLoad, false) && Prefs.getString(PreferenceKey.IsBackForgroung).equals("1")) {
                            callmaneger();
                        }
                    }
                }
            } else {
                Log.d(TAG, "Service >>>>>>>>>>>>  ACTION POWER DISCONNECTED");
                Toast.makeText(context, "ACTION POWER DISCONNECTED", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void callmaneger() {

        API_Manager.API_Manager(new APICallBack() {
            @Override
            public void ResponseWithSuccess(String success) {

                Log.e(TAG, "On Connect Apicall: " + success);
                Prefs.putString(API_XML, success);

                Calendar cal = Calendar.getInstance();
                Prefs.putLong(PreferenceKey.ConnectAPITime, cal.getTimeInMillis());

                if (!success.equals("")) {
                    XMLParser xmlParser = new XMLParser();
                    List<NewsData> NewsDataList = xmlParser.ResponceParse(Prefs.getString(PreferenceKey.API_XML));
                    if (NewsDataList.size() > 0) {
                        NewsData newsData = NewsDataList.get(0);

                        if (Prefs.getLong(PreferenceKey.APITime) > newsData.getTimestemp()) {

                            Log.e(TAG, "onPostExecute:  data Update..............");
                            Prefs.putLong(PreferenceKey.APITime, newsData.getTimestemp());

                            if (MainActivity.getInstance() != null) {
                                MainActivity.getInstance().callMethod();
                            }

                            sendNotification(context, getResources().getString(R.string.news_message));
                        } else {
                            Log.e(TAG, "onPostExecute: No.... data Update");
                            //sendNotification("No news update");
                        }
                    }

                }
            }
        });
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Inside onCreate() API");

        buildNotification();
        if (Build.VERSION.SDK_INT >= 26) {
            IntentFilter filter1 = new IntentFilter();
            filter1.addAction(Intent.ACTION_POWER_CONNECTED);
            filter1.addAction(Intent.ACTION_POWER_DISCONNECTED);
            registerReceiver(myBroadcastReceiver, filter1);
        }
    }

    private void buildNotification() {

        NotificationManager notifManager = null;
        String id = getString(R.string.app_name); // default_channel_id
        String title = getString(R.string.app_name); // Default Channel

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder;
        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                //mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);


            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            builder.setContentTitle(getString(R.string.app_name))                            // required
                    .setSmallIcon(R.mipmap.ic_launcher)   // required
                    .setContentText("SmartTracker is Running...") // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            Notification notification = builder.build();
            notifManager.notify(1, notification);
            startForeground(1, notification);

        } else {
            builder = new NotificationCompat.Builder(this, id);
            builder.setPriority(Notification.PRIORITY_MIN);


            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            builder.setContentTitle(getString(R.string.app_name))                            // required
                    .setSmallIcon(R.mipmap.ic_launcher)   // required
                    .setContentText("SmartTracker is Running...") // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            Notification notification = builder.build();
            notifManager.notify(1, notification);
            startForeground(1, notification);

        }


    }

    @Override
    public int onStartCommand(Intent resultIntent, int resultCode, int startId) {
        Log.d(TAG, "inside onStartCommand() API");
        return startId;
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "inside onDestroy() API");
        startAlarmManager();
        this.unregisterReceiver(myBroadcastReceiver); // implement on onDestroy().
        // super.onDestroy();

    }

    private void startAlarmManager() {

        Intent intent = new Intent(getApplicationContext(), TrackerAlarm.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, TrackerAlarm.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager Alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
