package com.arun.ebook.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.arun.ebook.MyApplication;

/**
 * Created by WY on 2017/4/11.
 */
public class DensityUtil {

    /**
     * 判断全面屏是否启用虚拟键盘
     */

    private static final String NAVIGATION = "navigationBarBackground";

    public static boolean isNavigationBarExist(@NonNull Activity activity) {
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();

                if (vp.getChildAt(i).getId()!=-1&& NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是全面屏
     */
    private volatile static boolean mHasCheckAllScreen;
    private volatile static boolean mIsAllScreenDevice;

    public static boolean isAllScreenDevice(Context context) {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        }
        mHasCheckAllScreen = true;
        mIsAllScreenDevice = false;
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                mIsAllScreenDevice = true;
            }
        }
        return mIsAllScreenDevice;
    }

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
