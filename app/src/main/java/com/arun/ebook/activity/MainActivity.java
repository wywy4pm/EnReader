package com.arun.ebook.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.arun.ebook.helper.PermissionsChecker;
import com.arun.ebook.R;
import com.arun.ebook.adapter.FileListAdapter;
import com.arun.ebook.bean.BookBean;
import com.arun.ebook.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // 所需的全部权限
    public static final String[] PERMISSIONS = new String[]{
            //Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_CODE = 0; // 请求码
    private List<BookBean> books = new ArrayList<>();
    public static String TXT_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "tencent" + File.separator;

    private TextView text;
    private ListView listView;
    private FileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPermissionsChecker = new PermissionsChecker(this);
        listView = findViewById(R.id.listView);
        text = findViewById(R.id.text);
        adapter = new FileListAdapter(this, books);
        listView.setAdapter(adapter);

        setViewData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void setViewData() {
        text.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    books.clear();
                    Utils.getAllTxtFile(books, new File(TXT_FILE_PATH));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }
}
