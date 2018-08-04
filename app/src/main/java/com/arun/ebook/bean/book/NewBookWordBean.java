package com.arun.ebook.bean.book;

public class NewBookWordBean {
    public String content;
    //是否被搜索翻译过，0：没有，1：有
    public int translated;
    //是否是标点，0：否，1：是
    public int only_quote;
    public boolean isAdd;
}
