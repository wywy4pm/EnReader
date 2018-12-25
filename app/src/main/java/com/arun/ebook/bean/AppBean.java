package com.arun.ebook.bean;

import android.content.Context;

import com.arun.ebook.R;
import com.arun.ebook.utils.AppUtils;
import com.arun.ebook.utils.DeviceUtils;
import com.arun.ebook.utils.SharedPreferencesUtils;

/**
 * Created by wy on 2017/5/24.
 */

public class AppBean {

    public String uid;
    public String device_id;
    public String version;
    public String device_name;
    public String platform;
    //public String osVersion;
    //public int isRelease;
    //public String apiVersion;

    public AppBean(Context context) {
        uid = SharedPreferencesUtils.getUid(context);
        device_id = DeviceUtils.getMobileIMEI(context);
        version = AppUtils.getAppVersion(context);
        device_name = DeviceUtils.getMobileModel();
        platform = context.getString(R.string.android);
        //osVersion = String.valueOf(DeviceUtils.getMobileSDK());
        //isRelease = AppUtils.isApkDebug(context) ? 0 : 1;
        //apiVersion = "v2";
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
