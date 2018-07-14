package com.arun.ebook.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.arun.ebook.MyApplication;

/**
 * Created by WY on 2017/4/11.
 */
public class DensityUtil {

    public static int dp2px(float dpValue) {
        float scale = MyApplication.getGlobalContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public static int px2dp(float pxValue) {
        float scale = MyApplication.getGlobalContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale);
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusHeight(Context context) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static int getScreenWidth(Context context) {
        int screenWidth = 0;
        if (context != null && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        }
        return screenWidth;
    }

    public static int getScreenHeight(Context context) {
        int screenHeight = 0;
        if (context != null && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        }
        return screenHeight;
    }

    public static float getScreenDensity(Context context) {
        float density = 0;
        if (context != null && context.getResources() != null && context.getResources().getDisplayMetrics() != null) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return density;
    }


    /**
     * 根据亮度值修改当前window亮度
     */
    public static void changeAppBrightness(Context context, int brightness) {
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 100f;
        }
        window.setAttributes(lp);
    }

    /**
     * 获得系统亮度
     */
    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            systemBrightness = (int) (systemBrightness / 2.55);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }
}
