package com.arun.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.common.Constant;
import com.arun.ebook.event.EditPageEvent;
import com.arun.ebook.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

public class EditPageActivity extends BaseActivity {
    private TextView content;
    private String pageContent;
    private int currentPage;
    private int paragraphId;
    private boolean isEditPage;

    public static void jumpToEditPage(Context context, int paragraphId, int currentPage, String content) {
        Intent intent = new Intent(context, EditPageActivity.class);
        intent.putExtra(Constant.INTENT_PAGE_PARAGRAPHID, paragraphId);
        intent.putExtra(Constant.INTENT_PAGE_INDEX, currentPage);
        intent.putExtra(Constant.INTENT_PAGE_EDIT, content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);
        initView();
        initData();
    }

    private void initView() {
        content = findViewById(R.id.content);
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(Constant.INTENT_PAGE_EDIT)) {
                pageContent = getIntent().getExtras().getString(Constant.INTENT_PAGE_EDIT);
                if (!TextUtils.isEmpty(pageContent)) {
                    isEditPage = true;
                    setTextTitleName(getString(R.string.edit) + "page");
                    StringUtils.setEnTextFont(this, content);
                    content.setText(pageContent);
                } else {
                    isEditPage = false;
                    setTextTitleName(getString(R.string.insert) + "page");
                }
            }
            if (getIntent().getExtras().containsKey(Constant.INTENT_PAGE_INDEX)) {
                currentPage = getIntent().getExtras().getInt(Constant.INTENT_PAGE_INDEX);
            }
            if (getIntent().getExtras().containsKey(Constant.INTENT_PAGE_PARAGRAPHID)) {
                paragraphId = getIntent().getExtras().getInt(Constant.INTENT_PAGE_PARAGRAPHID);
            }
        }
    }

    @Override
    public void onClickTextRight() {
        if (!TextUtils.isEmpty(content.getText())) {
            String text = content.getText().toString();
            EventBus.getDefault().post(new EditPageEvent(text, paragraphId, currentPage, isEditPage));
            finish();
        } else {
            showToast(getString(R.string.page_edit_tips));
        }
    }
}
