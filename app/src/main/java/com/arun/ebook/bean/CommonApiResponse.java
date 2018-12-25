package com.arun.ebook.bean;

/**
 * Created by wy on 2017/5/22.
 */

public class CommonApiResponse<T> {
    public int errno;
    public int statusCode;
    public T data;
    public String errmsg;
}
