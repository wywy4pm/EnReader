package com.arun.ebook.bean;

public class BookEditBean {
    public static final int TYPE_FRONT_MERGE = 1;
    public static final int TYPE_DELETE = 2;
    public static final int TYPE_INSERT = 3;
    public static final int TYPE_EDIT = 4;
    public static final int TYPE_STYLE = 5;

    public static final int STYLE_MAIN_BODY = 1;
    public static final int STYLE_TITLE = 2;
    public static final int STYLE_QUOTE = 3;

    //pageId是页的id
    public int pageId;
    //type是操作类型（1：往前合并；2：删除；3：插入空白页面；4：编辑；5：设置样式）
    public int type;
    //修改的内容；当type等于4的时候必传
    public String content;
    //样式id，当type等于5的时候传（//1:正文；2：标题；3：引用）
    public int styleId;

    public BookEditBean(int pageId, int type) {
        this.pageId = pageId;
        this.type = type;
    }

    public BookEditBean(int pageId, int type, String content) {
        this.pageId = pageId;
        this.type = type;
        this.content = content;
    }

    public BookEditBean(int pageId, int type, int styleId) {
        this.pageId = pageId;
        this.type = type;
        this.styleId = styleId;
    }

}
