package com.arun.ebook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arun.ebook.R;
import com.arun.ebook.adapter.FontAdapter;
import com.arun.ebook.bean.FontBean;
import com.arun.ebook.common.Constant;
import com.arun.ebook.listener.SwitchFontListener;
import com.arun.ebook.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FontActivity extends AppCompatActivity implements SwitchFontListener {

    private RecyclerView recyclerView;
    private FontAdapter fontAdapter;
    private List<FontBean> fonts;
    public static final int REQUEST_CODE_FOR_FONT = 1001;
    public static final int RESULT_CODE_TO_READ = 2001;

    public static void jumpToFont(Context context) {
        Intent intent = new Intent(context, FontActivity.class);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_FOR_FONT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);
        recyclerView = findViewById(R.id.recyclerView);
        fonts = new ArrayList<>();
        fonts.addAll(getFonts());
        fontAdapter = new FontAdapter(this, fonts);
        fontAdapter.setSwitchFontListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(fontAdapter);
    }

    private List<FontBean> getFonts() {
        List<FontBean> list = new ArrayList<>();
        FontBean top = new FontBean();
        top.fontName = "默认";
        list.add(top);
        File[] files = Utils.readFontsFile(this, "fonts",Constant.PATH_FONT);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                FontBean bean = new FontBean();
                bean.fontName = files[i].getName();
                bean.file = files[i];
                list.add(bean);
            }
        }
        FontBean bottom = new FontBean();
        bottom.fontName = getResources().getString(R.string.font_use);
        list.add(bottom);
        return list;
    }

    @Override
    public void switchFont(FontBean switchFont) {
        Intent intent = new Intent();
        intent.putExtra("switchFont", switchFont);
        setResult(RESULT_CODE_TO_READ, intent);
        finish();
    }
}
