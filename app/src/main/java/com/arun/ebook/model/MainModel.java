package com.arun.ebook.model;

import com.arun.ebook.listener.CommonRequestListener;
import com.arun.ebook.retrofit.RetrofitInit;

public class MainModel extends BaseModel {
    private volatile static MainModel instance;

    public static MainModel getInstance() {
        if (instance == null) {
            synchronized (MainModel.class) {
                if (instance == null) {
                    instance = new MainModel();
                }
            }
        }
        return instance;
    }

    public void userRegister(CommonRequestListener listener) {
        request(RetrofitInit.getApi().userRegister(), listener);
    }

    public void getBookList(int page,CommonRequestListener listener) {
        request(RetrofitInit.getApi().getBookList(page),listener);
    }
}
