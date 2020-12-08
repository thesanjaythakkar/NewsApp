package com.tech.mynewsapp.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tech.mynewsapp.callback.APICallBack;

import static com.tech.mynewsapp.sharePreference.PreferenceKey.TAG;

public class API_Manager {

    static APICallBack callBack;

    public static void API_Manager(APICallBack mycallBack) {
        callBack = mycallBack;
        new ApiCall().execute();
    }


    @SuppressLint("StaticFieldLeak")
    private static class ApiCall extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            XMLParser parser = new XMLParser();
            return parser.getXmlFromUrl(XMLParser.URL);

        }

        @Override
        protected void onPostExecute(String result) {



            Log.e(TAG, "API_Manager Apicall: " + result);
            callBack.ResponseWithSuccess(result);

        }
    }

}
