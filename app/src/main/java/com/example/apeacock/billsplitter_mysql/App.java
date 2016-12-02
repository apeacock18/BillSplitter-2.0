package com.example.apeacock.billsplitter_mysql;

import android.app.Application;
import android.content.Context;

/**
 * Created by apeacock on 11/29/16.
 */
public class App extends Application {

    public static String SHARED_PREF_KEY = "com.example.apeacock.billsplitter_mysql.SHARED_PREFERENCE_KEY";
    private static Context context;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}
