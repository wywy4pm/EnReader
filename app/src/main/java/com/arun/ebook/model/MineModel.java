package com.arun.ebook.model;

import com.arun.ebook.listener.CommonRequestListener;
import com.arun.ebook.retrofit.RetrofitInit;

public class MineModel extends BaseModel {
    private volatile static MineModel instance;

    public static MineModel getInstance() {
        if (instance == null) {
            synchronized (MineModel.class) {
                if (instance == null) {
                    instance = new MineModel();
                }
            }
        }
        return instance;
    }

    public void getMineData(CommonRequestListener listener) {
        request(RetrofitInit.getApi().getMineData(), listener);
    }
}
