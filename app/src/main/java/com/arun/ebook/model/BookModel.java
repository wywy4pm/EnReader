package com.arun.ebook.model;

import com.arun.ebook.listener.CommonRequestListener;
import com.arun.ebook.retrofit.RetrofitInit;

public class BookModel extends BaseModel {
    private volatile static BookModel instance;

    public static BookModel getInstance() {
        if (instance == null) {
            synchronized (BookModel.class) {
                if (instance == null) {
                    instance = new BookModel();
                }
            }
        }
        return instance;
    }

    public void getBookDetail(String bookId, int pageSize, int currentPage, CommonRequestListener listener) {
        request(RetrofitInit.getApi().getBookDetail(bookId, pageSize, currentPage), listener);
    }

    public void bookEdit(int pageId, int style, String content, int styleId, CommonRequestListener listener) {
        request(RetrofitInit.getApi().bookEdit(pageId, style, content, styleId), listener);
    }

    public void bookTranslate(String keyword, String page_id, CommonRequestListener listener) {
        request(RetrofitInit.getApi().bookTranslate(keyword, page_id), listener);
    }
}
