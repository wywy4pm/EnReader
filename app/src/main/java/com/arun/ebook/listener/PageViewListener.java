package com.arun.ebook.listener;

/**
 * Created by Administrator on 2018/4/17.
 */

public interface PageViewListener {
    void showNextPage();

    void showPrePage();

    void showTransDialog(String word, String seq);

    //void showBottom(boolean isShowDialog);

    void setReadBackground(int bgColor);

    void setTextColor(int textColor);

    void setReadProgress(int progress);

    void setScreenLight(int light);

    void setTextSize(double scale);

    void setTextSize(int spSize);

    void setLineSpace(int lineSpace);

    void setEdgeSpace(int edgeSpace);

    //void setWordSpace(int wordSpace);

    void setParaSpace(int paraSpace);
}
