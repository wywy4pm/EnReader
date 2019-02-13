package com.arun.ebook.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.utils.SharedPreferencesUtils;
import com.arun.ebook.utils.StatusBarUtils;
import com.arun.ebook.utils.ToastUtils;
import com.arun.ebook.view.MvpView;

public abstract class BaseActivity extends AppCompatActivity implements MvpView {
    public TextView text_back, text_name, text_right;
    public String userId;
    public String deviceId;
    public int screenWidth;
    public ProgressBar progressBar;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenWidth = DensityUtil.getScreenWidth(this);
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
    }

    public void setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!DensityUtil.isNavigationBarExist(this)) {
                if (getWindow() != null) {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                    getWindow().setAttributes(lp);
                    StatusBarUtils.setStatusBarVisible(this, false);
                }
            }
        }
    }


    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        return super.moveTaskToBack(true);
    }

    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initCommonView();
    }

    public void initCommonView() {
        initProgressBar();
        initTextTitle();
    }

    private void initTextTitle() {
        text_back = findViewById(R.id.text_back);
        text_name = findViewById(R.id.text_name);
        text_right = findViewById(R.id.text_right);
        setTextBack();
        setTextRight();
    }

    public void setTextTitleName(String titleName) {
        if (text_name != null && !TextUtils.isEmpty(titleName)) {
            text_name.setText(titleName);
        }
    }

    public void setTextBack() {
        if (text_back != null) {
            text_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public void setTextRight() {
        if (text_right != null) {
            text_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickTextRight();
                }
            });
        }
    }

    public void onClickTextRight() {

    }

    private void initProgressBar() {
        progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void onLoadStart() {
        setLoading(true);
        showProgressBar();
    }

    @Override
    public void onError(int errorType, int errorMsg) {
        showToast(errorMsg);
    }

    @Override
    public void onError(int errorType, String errorMsg) {
        showToast(errorMsg);
    }

    @Override
    public void onRefreshComplete() {
        setLoading(false);
        hideProgressBar();
    }

    public void showToast(int msgRes) {
        ToastUtils.getInstance(this).showToast(msgRes);
    }

    public void showToast(String msg) {
        ToastUtils.getInstance(this).showToast(msg);
    }

    public String getUserId() {
        if (TextUtils.isEmpty(userId)) {
            userId = SharedPreferencesUtils.getUid(this);
        }
        return userId;
    }

    public void setBack(View backView) {
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setBack() {
        /*image_back = findViewById(R.id.image_back);
        if (image_back != null) {
            image_back.setVisibility(View.VISIBLE);
            setBack(image_back);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtils.getInstance(this).destory();
    }
}
