package com.arun.ebook.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.arun.ebook.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
