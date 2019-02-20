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

    public void getBookPageIds(int bookId, CommonRequestListener listener) {
        request(RetrofitInit.getApi().getBookPageIds(bookId), listener);
    }

    public void getBookDetail(int bookId, String page_ids, CommonRequestListener listener) {
        request(RetrofitInit.getApi().getBookDetail(bookId, page_ids), listener);
    }

    public void bookEdit(int pageId, int style, String content, int styleId, CommonRequestListener listener) {
        request(RetrofitInit.getApi().bookEdit(pageId, style, content, styleId), listener);
    }

    public void bookTranslate(int book_id, String keyword, String page_id, CommonRequestListener listener) {
        request(RetrofitInit.getApi().bookTranslate(book_id, keyword, page_id), listener);
    }
}
