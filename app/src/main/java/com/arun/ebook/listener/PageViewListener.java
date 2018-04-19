package com.arun.ebook.listener;

/**
 * Created by Administrator on 2018/4/17.
 */

public interface PageViewListener {
    void showNextPage();

    void showPrePage();

    void showTransDialog(String word);

    void showBottom();
}
