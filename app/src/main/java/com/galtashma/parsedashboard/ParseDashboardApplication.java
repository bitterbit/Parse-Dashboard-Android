package com.galtashma.parsedashboard;

import android.app.Application;

import com.appizona.yehiahd.fastsave.FastSave;

public class ParseDashboardApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FastSave.init(getApplicationContext());
    }
}
