package com.arun.ebook.listener;

import com.arun.ebook.bean.BookEditBean;

public interface BookEditListener {
    void onBookEdit(BookEditBean bean);

    void translateWord(String keyword, int page_id);
}
