package com.arun.ebook;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2018/4/10.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getGlobalContext() {
        return mContext;
    }
}