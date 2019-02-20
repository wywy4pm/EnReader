package com.arun.ebook.common;

import android.graphics.Color;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/4/13.
 */

public class Constant {
    public static final String API_BASE_URL = "http://xima.link365.cn/fd/";
    public static final String GET_TRANSLATE_DATA = "https://tps.link365.cn/wx/article/translate";
    public static final String PATH_FONT = Environment.getExternalStorageDirectory() + File.separator + "enreader" + File.separator + "fonts";
    public static final String PATH_FONT_NEW = Environment.getExternalStorageDirectory() + File.separator + "FD" + File.separator + "fonts";

    public static final int DEFAULT_PARA_SPACE_DP = 20;

    public static final int TAB_INDEX_MAIN = 0;
    public static final int TAB_INDEX_ANSWER = 1;
    public static final int TAB_INDEX_INTERACT = 2;
    public static final int TAB_INDEX_MESSAGE = 3;
    public static final int TAB_INDEX_MINE = 1;

    public static final String INTENT_UID = "uid";
    public static final String INTENT_BOOK_ITEM = "book_item";
    public static final String INTENT_BOOK_DETAIL = "book_detail";
    public static final String INTENT_BOOK_ID = "book_id";
    public static final String INTENT_PAGE_EDIT = "page_edit";
    public static final String INTENT_PAGE_INDEX = "page_index";
    public static final String INTENT_PAGE_PARAGRAPHID = "paragraphId";

    public static final String INTENT_WEB_URL = "web_url";
    /*public static final String INTENT_TOTAL_COUNT = "total_count";
    public static final String INTENT_CURRENT_PAGE = "current_page";*/

}
