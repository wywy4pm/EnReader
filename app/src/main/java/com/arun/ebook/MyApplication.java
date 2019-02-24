package com.arun.ebook;

import android.app.Application;
import android.content.Context;

import com.arun.ebook.helper.AppHelper;
import com.umeng.commonsdk.UMConfigure;

/**
 * Created by Administrator on 2018/4/10.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
        AppHelper.getInstance().setAppConfig(getApplicationContext());
    }

    public static Context getGlobalContext() {
        return mContext;
    }
}