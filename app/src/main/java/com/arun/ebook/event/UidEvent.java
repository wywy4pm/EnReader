package com.arun.ebook.event;

import com.arun.ebook.bean.ReadProgressBean;

import java.util.List;

public class UidEvent {
    public String uid;
    public List<ReadProgressBean> read_progress;

    public UidEvent(String uid, List<ReadProgressBean> read_progress) {
        this.uid = uid;
        this.read_progress = read_progress;
    }
}
