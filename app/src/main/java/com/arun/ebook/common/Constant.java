package com.arun.ebook.common;

import android.graphics.Color;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/4/13.
 */

public class Constant {
    public static final String API_BASE_URL = "http://fd.link365.cn/";
    public static final String GET_TRANSLATE_DATA = "https://tps.link365.cn/wx/article/translate";
    public static final String PATH_FONT = Environment.getExternalStorageDirectory() + File.separator + "enreader" + File.separator + "fonts";

    public static final int DEFAULT_PARA_SPACE_DP = 20;

    public static final int TAB_INDEX_MAIN = 0;
    public static final int TAB_INDEX_ANSWER = 1;
    public static final int TAB_INDEX_INTERACT = 2;
    public static final int TAB_INDEX_MESSAGE = 3;
    public static final int TAB_INDEX_MINE = 4;

    public static final String INTENT_UID = "uid";

}
