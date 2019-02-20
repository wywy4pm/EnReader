package com.arun.ebook.listener;

import com.arun.ebook.bean.BookEditBean;

public interface BookEditListener {
    void onBookEdit(BookEditBean bean);

    void translateWord(int book_id,String keyword, int page_id);
}
