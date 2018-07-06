package com.arun.ebook.listener;

public interface ParaEditListener {
    /*void frontInsertSpace(int position, NewBookBean bean);

    void currentDelete(int position);

    void frontMerge(int position, String currentContent);*/

    void paraEdit(int op_type, int bookId, String cnSeq);
}
