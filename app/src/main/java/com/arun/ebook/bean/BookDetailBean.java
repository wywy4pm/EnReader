package com.arun.ebook.bean;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class BookDetailBean implements Serializable {
    public int page_id;
    public String content;
    public int totalPage;
    public File file;
    public int style;
    public int seq;
    public List<String> queryed_word_list;
}
