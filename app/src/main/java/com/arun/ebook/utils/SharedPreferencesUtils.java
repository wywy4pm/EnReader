package com.arun.ebook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by WY on 2017/5/7.
 */
public class SharedPreferencesUtils {
    private static final String PATH_USER = "user";
    private static final String PATH_CONFIG = "config";
    private static final String PATH_PAGE_IDS = "page_ids";
    private static final String KEY_USER_UID = "uid";
    public static final String KEY_READ_CONFIG = "read_config";
    public static final String KEY_READ_FONT = "read_font";
    public static final String KEY_READ_POS = "read_pos";
    public static final String KEY_READ_BG = "read_bg";
    public static final String KEY_READ_TEXT_COLOR = "read_text_color";
    public static final String KEY_READ_EN_SIZE = "read_en_size";
    public static final String KEY_READ_EN_FONT = "read_en_font";

    public static void saveUid(Context context, String uid) {
        if (!TextUtils.isEmpty(uid)) {
            SharedPreferencesUtils.setUid(context, uid);
        }
    }

    public static void setUid(Context context, String uid) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_USER, Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putString(KEY_USER_UID, uid);
                editor.apply();
            }
        }
    }

    public static String getUid(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_USER, Context.MODE_PRIVATE);
        String uid = "";
        if (preferences != null) {
            uid = preferences.getString(KEY_USER_UID, "");
        }
        return uid;
    }

    public static void setPageIds(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_PAGE_IDS, Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putString(key, value);
                editor.apply();
            }
        }
    }

    public static String getPageIds(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_PAGE_IDS, Context.MODE_PRIVATE);
        String value = "";
        if (preferences != null) {
            value = preferences.getString(key, "");
        }
        return value;
    }

    public static void setConfigString(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_CONFIG, Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putString(key, value);
                editor.apply();
            }
        }
    }

    public static String getConfigString(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_CONFIG, Context.MODE_PRIVATE);
        String value = "";
        if (preferences != null) {
            value = preferences.getString(key, "");
        }
        return value;
    }

    public static void setConfigLong(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_CONFIG, Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putLong(key, value);
                editor.apply();
            }
        }
    }

    public static long getConfigLong(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_CONFIG, Context.MODE_PRIVATE);
        long value = 0;
        if (preferences != null) {
            value = preferences.getLong(key, 0);
        }
        return value;
    }

    public static void setConfigInt(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_CONFIG, Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putInt(key, value);
                editor.apply();
            }
        }
    }

    public static int getConfigInt(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_CONFIG, Context.MODE_PRIVATE);
        int value = 0;
        if (preferences != null) {
            value = preferences.getInt(key, 0);
        }
        return value;
    }

    /*public static void setConfigFloat(Context context, String key, float value) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_CONFIG, Context.MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            if (editor != null) {
                editor.putFloat(key, value);
                editor.apply();
            }
        }
    }

    public static float getConfigFloat(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PATH_CONFIG, Context.MODE_PRIVATE);
        float value = 0;
        if (preferences != null) {
            value = preferences.getFloat(key, 0);
        }
        return value;
    }*/

}
