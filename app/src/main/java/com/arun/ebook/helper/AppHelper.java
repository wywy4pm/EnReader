package com.arun.ebook.helper;

import android.content.Context;

import com.arun.ebook.bean.AppBean;

/**
 * Created by wy on 2017/5/24.
 */

public class AppHelper {
    private volatile static AppHelper instance = null;
    private static AppBean appBean;

    public static AppHelper getInstance() {
        if (instance == null)
            synchronized (AppHelper.class) {
                if (instance == null) {
                    instance = new AppHelper();
                }
            }
        return instance;
    }

    public void setAppConfig(Context context) {
        appBean = new AppBean(context);
    }

    public AppBean getAppConfig() {
        return appBean;
    }

}
