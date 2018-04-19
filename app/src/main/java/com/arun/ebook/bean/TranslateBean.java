package com.arun.ebook.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/4/13.
 */

public class TranslateBean implements Serializable{
    public String word;
    public List<String> explains;
    public String speak_url;
    public VoiceBean voice;
}
