package com.arun.ebook.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/4/13.
 */

public class Constant {
    public static final String GET_TRANSLATE_DATA = "https://tps.link365.cn/wx/article/translate";
    public static final String PATH_FONT = Environment.getExternalStorageDirectory() + File.separator + "enreader" + File.separator + "fonts";
}
