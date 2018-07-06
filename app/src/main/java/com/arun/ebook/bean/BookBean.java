package com.arun.ebook.bean;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/8.
 */

public class BookBean implements Serializable{
    public File txtFile;
    public String lastReadTime;
    public String readProgress;
    public int bgColor;
    public int light;
    public int textColor;
    public int spSize;
    public int lineSpace;
    public int edgeSpace;
    public int paraSpace;
}
