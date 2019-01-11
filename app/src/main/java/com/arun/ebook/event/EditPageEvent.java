package com.arun.ebook.event;

public class EditPageEvent {
    public String content;
    public int paragraphId;
    public int currentPage;
    public boolean isEdit;

    public EditPageEvent(String content, int paragraphId, int currentPage, boolean isEdit) {
        this.content = content;
        this.paragraphId = paragraphId;
        this.currentPage = currentPage;
        this.isEdit = isEdit;
    }
}
