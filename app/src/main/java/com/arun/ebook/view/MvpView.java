package com.arun.ebook.view;

import android.support.annotation.StringRes;

public interface MvpView {
    void onLoadStart();

    void onError(int errorType, @StringRes int errorMsg);

    void onError(int errorType, String errorMsg);

    void onRefreshComplete();
}
