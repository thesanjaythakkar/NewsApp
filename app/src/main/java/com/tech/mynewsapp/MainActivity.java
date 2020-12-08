package com.tech.mynewsapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tech.mynewsapp.adapter.AdapterNews;
import com.tech.mynewsapp.base.BaseActivity;
import com.tech.mynewsapp.callback.APICallBack;
import com.tech.mynewsapp.model.NewsData;
import com.tech.mynewsapp.service.MyService;
import com.tech.mynewsapp.sharePreference.PreferenceKey;
import com.tech.mynewsapp.sharePreference.Prefs;
import com.tech.mynewsapp.utils.API_Manager;
import com.tech.mynewsapp.utils.Tools;
import com.tech.mynewsapp.utils.XMLParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

import static com.tech.mynewsapp.sharePreference.PreferenceKey.API_XML;
import static com.tech.mynewsapp.sharePreference.PreferenceKey.TAG;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    public static MainActivity instance = null;

    @BindView(R.id.txt_time)
    TextView txt_time;

    @BindView(R.id.img_setting)
    ImageView img_setting;

    @BindView(R.id.img_refresh)
    ImageView img_refresh;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    AdapterNews adapter;
    List<NewsData> NewsDataList;

    public MainActivity() {
        try {
            instance = this;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static MainActivity getInstance() {
        if (instance == null) { //if there is no instance available... create new one
            instance = new MainActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponent();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    private void initComponent() {

        NewsDataList = new ArrayList<>();
        txt_time.setText(Tools.getDateTimestep(Prefs.getLong(PreferenceKey.APITime)));

        img_refresh.setOnClickListener(this);
        img_setting.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Tools.isMyServiceRunning(this)) {
                startService(new Intent(this, MyService.class));
            }
        }

        if (!Prefs.getString(PreferenceKey.API_XML).equals("")) {
            XMLParser xmlParser = new XMLParser();
            NewsDataList = xmlParser.ResponceParse(Prefs.getString(PreferenceKey.API_XML));
            setAdapter();
        }

    }

    private void setAdapter() {
        SetRecycleviewVartical(this, recyclerview);
        adapter = new AdapterNews(NewsDataList);
        recyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterNews.OnItemClickListener() {

            @Override
            public void onItemClick(View view, NewsData data, int position) {

            }
        });
    }

    public void callMethod() {

        if (!Prefs.getString(PreferenceKey.API_XML).equals("")) {
            XMLParser xmlParser = new XMLParser();
            NewsDataList = xmlParser.ResponceParse(Prefs.getString(PreferenceKey.API_XML));
            setAdapter();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.img_refresh) {

            if (Tools.isNetworkAvailable(MainActivity.this)) {

                showProgressLoading("Doing something, please wait.");
                API_Manager.API_Manager(new APICallBack() {
                    @Override
                    public void ResponseWithSuccess(String success) {

                        hideProgressLoading();

                        Calendar cal = Calendar.getInstance();
                        Prefs.putLong(PreferenceKey.ConnectAPITime, cal.getTimeInMillis());

                        Log.e(TAG, "Apicall: " + success);
                        Prefs.putString(API_XML, success);

                        if (!success.equals("")) {
                            NewsDataList.clear();

                            XMLParser xmlParser = new XMLParser();
                            NewsDataList = xmlParser.ResponceParse(Prefs.getString(PreferenceKey.API_XML));

                            if (NewsDataList.size() > 0) {
                                NewsData newsData = NewsDataList.get(0);
                                Prefs.putLong(PreferenceKey.APITime, newsData.getTimestemp());
                            }
                            setAdapter();
                        }
                    }
                });
            } else {
                showSnackbar("Network is not available");
            }

        } else if (v.getId() == R.id.img_setting) {

            StartActivity(ActivitySetting.class);
        }
    }

}