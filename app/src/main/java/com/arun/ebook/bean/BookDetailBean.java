package com.arun.ebook.bean;

import java.io.Serializable;

public class BookDetailBean implements Serializable {
    public int paragraphId;
    public int bookId;
    public String content;
    public int totalPage;
    public int currentPage;
}
