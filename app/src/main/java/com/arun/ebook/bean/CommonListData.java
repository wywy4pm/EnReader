package com.arun.ebook.bean;

import java.util.List;

public class CommonListData<T> {
    public int current_page;
    public List<T> data;
    public int total;
}
