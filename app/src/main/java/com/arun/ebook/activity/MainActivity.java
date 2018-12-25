package com.arun.ebook.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arun.ebook.bean.ConfigData;
import com.arun.ebook.bean.ConfigResponse;
import com.arun.ebook.bean.ConfigBean;
import com.arun.ebook.bean.booklist.BookListBean;
import com.arun.ebook.bean.booklist.BookListResponse;
import com.arun.ebook.helper.PermissionsChecker;
import com.arun.ebook.R;
import com.arun.ebook.adapter.FileListAdapter;
import com.arun.ebook.utils.SharedPreferencesUtils;
import com.arun.ebook.utils.Utils;

import java.text.DecimalFormat;
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
    private List<BookListBean> books = new ArrayList<>();
    //public static String TXT_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "tencent" + File.separator;

    private TextView text;
    private ListView listView;
    private FileListAdapter adapter;
    //private int pos;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPermissionsChecker = new PermissionsChecker(this);
        listView = findViewById(R.id.listView);
        text = findViewById(R.id.text);
        adapter = new FileListAdapter(this, books);
        //adapter.setOnClickItem(this);
        listView.setAdapter(adapter);
        getConfig();
    }

    private void getConfig() {
        /*RetrofitUtils.getInstance().getConfig(new Subscriber<ConfigResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.d("TAG", "getConfig error" + e.getMessage());
            }

            @Override
            public void onNext(ConfigResponse configResponse) {
                if (configResponse != null && configResponse.code == 200) {
                    if (configResponse.data != null) {
                        ConfigBean bean = configResponse.data;
                        if (bean.canrun == 1) {//可用
                            ConfigData.canRun = bean.canrun;
                            ConfigData.bgColor = bean.bgcolor;
                            ConfigData.textColor = bean.textcolor;
                            ConfigData.highlightcolor = bean.highlightcolor;
                            ConfigData.lightColor = Color.parseColor(bean.highlightcolor);
                            requestData();
                        } else {
                            cannotUse();
                        }
                    }
                }
            }
        });*/
    }

    private void cannotUse() {
        Toast.makeText(this, "暂无使用权限", Toast.LENGTH_LONG).show();
    }

    private void requestData() {
        /*RetrofitUtils.getInstance().getBookList(new Subscriber<BookListResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BookListResponse bookListResponse) {
                if (bookListResponse != null && bookListResponse.code == 200
                        && bookListResponse.data != null && bookListResponse.data.booklist != null
                        && bookListResponse.data.booklist.size() > 0) {
                    for (int i = 0; i < bookListResponse.data.booklist.size(); i++) {
                        BookListBean bean = bookListResponse.data.booklist.get(i);
                        if (!TextUtils.isEmpty(getReadTime(bean.id, bean.name))) {
                            bean.lastReadTime = Utils.longToDate(Long.valueOf(getReadTime(bean.id, bean.name)));
                        }
                        if (getProgress(bean.id) >= 0) {
                            DecimalFormat format = new DecimalFormat("#0.00");
                            bean.readProgress = format.format(getProgress(bean.id));
                        }
                    }
                    books.clear();
                    books.addAll(bookListResponse.data.booklist);
                    adapter.notifyDataSetChanged();
                }
            }
        }, page);*/
    }

    private double getProgress(int bookId) {
        if (!TextUtils.isEmpty(SharedPreferencesUtils.getConfigString(this, String.valueOf(bookId)))) {
            String pageAndCounts = SharedPreferencesUtils.getConfigString(this, String.valueOf(bookId));
            String p_c[] = pageAndCounts.split("_", 2);
            if (p_c.length == 2) {
                int currentPageParaIndex = Integer.parseInt(p_c[0]);
                int currentTotalPages = Integer.parseInt(p_c[1]);
                return (double) (currentPageParaIndex - 1) / currentTotalPages * 100;
            }
        }
        return 0;
    }

    private String getReadTime(int bookId, String bookName) {
        return SharedPreferencesUtils.getConfigString(this, bookName + bookId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
        if (ConfigData.canRun == 1) {
            requestData();
        }
    }

    /*private void setViewData() {
        text.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    books.clear();
                    Utils.getAllTxtFile(MainActivity.this, books, new File(TXT_FILE_PATH));
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
    }*/

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
