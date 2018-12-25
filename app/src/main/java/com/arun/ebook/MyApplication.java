package com.arun.ebook;

import android.app.Application;
import android.content.Context;

import com.arun.ebook.helper.AppHelper;

/**
 * Created by Administrator on 2018/4/10.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        AppHelper.getInstance().setAppConfig(getApplicationContext());
    }

    public static Context getGlobalContext() {
        return mContext;
    }
}