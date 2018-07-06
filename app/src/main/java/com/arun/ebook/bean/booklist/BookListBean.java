package com.arun.ebook.bean.booklist;

import java.io.Serializable;

public class BookListBean implements Serializable{
    public int id;
    public String name;
    public String desc;

    public String lastReadTime;
    public String readProgress;
}
