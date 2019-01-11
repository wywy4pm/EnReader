package com.arun.ebook.bean;

import java.io.File;
import java.io.Serializable;

public class BookDetailBean implements Serializable {
    public int paragraphId;
    public int bookId;
    public String content;
    public int totalPage;
    public int currentPage;
    public File file;
}
