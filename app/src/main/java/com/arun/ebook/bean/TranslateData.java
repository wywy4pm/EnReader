package com.arun.ebook.bean;

import java.io.Serializable;
import java.util.List;

public class TranslateData implements Serializable{
    public String keyword;
    public List<String> explains;
    public String speak_url;
    public VoiceBean voice;
}
