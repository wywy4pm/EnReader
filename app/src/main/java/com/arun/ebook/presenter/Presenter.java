package com.arun.ebook.presenter;

import com.arun.ebook.view.MvpView;

public interface Presenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();

}
