package com.tech.mynewsapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.suke.widget.SwitchButton;
import com.tech.mynewsapp.base.BaseActivity;
import com.tech.mynewsapp.service.AlarmReceiver;
import com.tech.mynewsapp.sharePreference.PreferenceKey;
import com.tech.mynewsapp.sharePreference.Prefs;
import com.tech.mynewsapp.utils.Tools;

import java.util.Calendar;

import butterknife.BindView;

public class ActivitySetting extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.img_back)
    ImageView img_back;

    @BindView(R.id.switch_on_off)
    SwitchButton switch_on_off;

    @BindView(R.id.ly_setreminder)
    LinearLayout ly_setreminder;

    @BindView(R.id.txt_setreminder)
    TextView txt_setreminder;

    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;

    Calendar mCalendar;


    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponent();
    }

    private void initComponent() {

        mCalendar = Calendar.getInstance();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ly_setreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        txt_setreminder.setText(Prefs.getString(PreferenceKey.DataTime));

        if (Prefs.getBoolean(PreferenceKey.DataOnLoad, false)) {
            switch_on_off.setChecked(true);
        } else {
            switch_on_off.setChecked(false);
        }

        switch_on_off.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {

                    Prefs.putBoolean(PreferenceKey.DataOnLoad, true);
                } else {
                    Prefs.putBoolean(PreferenceKey.DataOnLoad, false);
                }
            }
        });
    }

    private void showDatePickerDialog() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ActivitySetting.this, ActivitySetting.this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = dayOfMonth;
        myMonth = month;

        Log.e("Time", "onDateSet: " + myYear + " " + myMonth + " " + myday);

        //mCalendar.set(myYear, myday, myMonth);

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(ActivitySetting.this, ActivitySetting.this, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;

        Log.e("Time", "onTimeSet: " + myYear + " " + myMonth + " " + myday + " " + myHour + " " + myMinute);

        mCalendar.set(myYear, myMonth, myday, myHour, myMinute);

        Log.e("TAG", "onTimeSet: " + mCalendar.getTime());

        if (Prefs.getLong(PreferenceKey.DataTimeStep) != 0L) {

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ActivitySetting.this, 0, alarmIntent, 0);
            alarmManager.cancel(pendingIntent);
        }

        txt_setreminder.setText(Tools.getDate(mCalendar.getTime()));

        Prefs.putString(PreferenceKey.DataTime, txt_setreminder.getText().toString().trim());
        Prefs.putLong(PreferenceKey.DataTimeStep, mCalendar.getTimeInMillis());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent);
        }
    }
}