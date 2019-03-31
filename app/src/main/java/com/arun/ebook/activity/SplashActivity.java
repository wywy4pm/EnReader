package com.arun.ebook.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.arun.ebook.R;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.utils.StatusBarUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
        setFullScreen();
        setContentView(R.layout.activity_splash);
        delay();
    }

    private void delay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TestBackHomeIn","-----------------SplashActivity jumpToMain-----------------");
                NewMainActivity.jumpToMain(SplashActivity.this);
                finish();
            }
        }, 2000);
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
        if (getWindow() != null) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
