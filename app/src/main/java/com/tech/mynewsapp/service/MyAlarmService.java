package com.tech.mynewsapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tech.mynewsapp.MainActivity;
import com.tech.mynewsapp.R;
import com.tech.mynewsapp.callback.APICallBack;
import com.tech.mynewsapp.model.NewsData;
import com.tech.mynewsapp.sharePreference.PreferenceKey;
import com.tech.mynewsapp.sharePreference.Prefs;
import com.tech.mynewsapp.utils.API_Manager;
import com.tech.mynewsapp.utils.XMLParser;

import java.util.List;

import static com.tech.mynewsapp.sharePreference.PreferenceKey.API_XML;
import static com.tech.mynewsapp.utils.NotificationClass.sendNotification;

public class MyAlarmService extends Service {

    private Context context;
    private String TAG = this.getClass().getSimpleName();


    @Override
    public void onCreate() {
        Log.e(TAG, "MyAlarmService.onCreate(): ");
        //Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "MyAlarmService.onBind(): ");
        //Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "MyAlarmService.onDestroy(): ");
        //Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        context = this;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, AlarmManager.INTERVAL_DAY, pendingIntent);

        if (Prefs.getBoolean(PreferenceKey.DataOnLoad, false)) {
            API_Manager.API_Manager(new APICallBack() {
                @Override
                public void ResponseWithSuccess(String success) {

                    Log.e(TAG, "Apicall: " + success);
                    Prefs.putString(API_XML, success);

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
                                sendNotification(context, getResources().getString(R.string.reminder_message));
                            } else {
                                Log.e(TAG, "onPostExecute: No.... data Update");

                                //sendNotification("No news update");
                            }
                        }
                    }
                }
            });
        }

        Log.e(TAG, "MyAlarmService.onStart(): ");
        // Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();
        Log.e(TAG, "MyAlarmService.onUnbind(): ");
        return super.onUnbind(intent);
    }
}
