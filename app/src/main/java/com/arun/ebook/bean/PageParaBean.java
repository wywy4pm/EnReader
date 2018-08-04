package com.arun.ebook.bean;

import android.graphics.Color;
import android.graphics.Typeface;

import com.arun.ebook.common.Constant;

public class PageParaBean {
    public int textColor = Color.parseColor("#15140F");
    public int spSize = 15;
    public int lineSpace = 1;
    public int paraSpace = Constant.DEFAULT_PARA_SPACE_DP;
    public Typeface typeface = Typeface.DEFAULT;

    public PageParaBean create() {
        PageParaBean bean = new PageParaBean();
        bean.textColor = textColor;
        bean.spSize = spSize;
        bean.lineSpace = lineSpace;
        bean.paraSpace = paraSpace;
        bean.typeface = typeface;
        return bean;
    }

    public PageParaBean setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public PageParaBean setSpSize(int spSize) {
        this.spSize = spSize;
        return this;
    }

    public PageParaBean seLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
        return this;
    }

    public PageParaBean setParaSpace(int paraSpace) {
        this.paraSpace = paraSpace;
        return this;
    }

    public PageParaBean setTypeface(Typeface typeface) {
        this.typeface = typeface;
        return this;
    }
}
