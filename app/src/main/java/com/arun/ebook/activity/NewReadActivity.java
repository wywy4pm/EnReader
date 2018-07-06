package com.arun.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arun.ebook.R;
import com.arun.ebook.adapter.ReadAdapter;
import com.arun.ebook.bean.CommonResponse;
import com.arun.ebook.bean.FontBean;
import com.arun.ebook.bean.PageParaBean;
import com.arun.ebook.bean.TranslateResponse;
import com.arun.ebook.bean.book.NewBookBean;
import com.arun.ebook.bean.book.NewBookResponse;
import com.arun.ebook.bean.booklist.BookListBean;
import com.arun.ebook.common.Constant;
import com.arun.ebook.dialog.ReadBottomDialog;
import com.arun.ebook.dialog.TranslateDialog;
import com.arun.ebook.listener.DialogListener;
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.listener.ParaEditListener;
import com.arun.ebook.retrofit.RetrofitUtils;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.utils.SharedPreferencesUtils;
import com.arun.ebook.utils.Utils;
import com.arun.ebook.widget.PageRecyclerView;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import rx.Subscriber;

public class NewReadActivity extends AppCompatActivity implements PageViewListener, View.OnClickListener, ParaEditListener {

    private TranslateDialog dialog;
    private ReadBottomDialog bottomDialog;
    private double currentProgress = 0;
    private View contentView;
    private TextView to_main;
    private TextView to_edit;
    private TextView tvCurrentTime;
    private TextView tvCurrentProgress;
    private TimeHandler mHandler = new TimeHandler(this);
    private OkHttpClient okHttpClient = new OkHttpClient();
    private int currentPageParaIndex = 0;
    private BookListBean bookListBean;
    private int bookId;
    private PageRecyclerView recyclerView;
    private ReadAdapter readAdapter;
    private List<NewBookBean> allParas = new ArrayList<>();
    private static final int MODE_CURRENT = 0;
    private static final int MODE_NEXT_PAGE = 1;
    private static final int MODE_PRE_PAGE = 2;
    private static final int MODE_ONE_PAGE = 3;
    private static final int MODE_PARA_EDIT = 4;

    private int currentTotalPages = 0;

    private int light = 0,
            bgColor = Color.parseColor("#F2F1ED"),
            textColor = Color.parseColor("#15140F"),
            spSize = 15,
            lineSp = 1,
            edgeSpace = 10,
            paraSpace = 10;
    private Typeface typeface = Typeface.DEFAULT;

    public static void jumpToRead(Context context, BookListBean bookListBean) {
        Intent intent = new Intent(context, NewReadActivity.class);
        intent.putExtra("BookListBean", bookListBean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        contentView = LayoutInflater.from(this).inflate(R.layout.activity_new_read, null);
        setContentView(contentView);
        initViewAndListener();
        initTime();
        initData();
    }

    private void initViewAndListener() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setPageViewListener(this);
        tvCurrentTime = findViewById(R.id.currentTime);
        tvCurrentProgress = findViewById(R.id.currentProgress);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        readAdapter = new ReadAdapter(this, allParas);
        readAdapter.setParaEditListener(this);
        readAdapter.setPageViewListener(this);
        recyclerView.setAdapter(readAdapter);

        to_main = findViewById(R.id.to_main);
        to_edit = findViewById(R.id.to_edit);
        to_main.setOnClickListener(this);
        to_edit.setOnClickListener(this);
    }

    private void initTime() {
        //显示时间
        tvCurrentTime.setText(getCurrentTime());
        new TimeThread().start();
    }

    private static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    private void initData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("BookListBean")) {
            bookListBean = (BookListBean) getIntent().getExtras().getSerializable("BookListBean");
            if (bookListBean != null) {
                bookId = bookListBean.id;
                Log.d("TAG", "BOOK_ID = " + bookId);
            }
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String readConfig = SharedPreferencesUtils.getConfigString(NewReadActivity.this, SharedPreferencesUtils.KEY_READ_CONFIG);
                String fontName = SharedPreferencesUtils.getConfigString(NewReadActivity.this, SharedPreferencesUtils.KEY_READ_FONT);
                if (!TextUtils.isEmpty(readConfig)) {
                    String[] configs = readConfig.split("_");
                    if (configs.length == 7) {
                        light = Integer.parseInt(configs[0]);
                        bgColor = Integer.parseInt(configs[1]);
                        spSize = Integer.parseInt(configs[2]);
                        textColor = Integer.parseInt(configs[3]);
                        lineSp = Integer.parseInt(configs[4]);
                        edgeSpace = Integer.parseInt(configs[5]);
                        paraSpace = Integer.parseInt(configs[6]);
                    }
                }
                if (!TextUtils.isEmpty(SharedPreferencesUtils.getConfigString(NewReadActivity.this, String.valueOf(bookId)))) {
                    String pageAndCounts = SharedPreferencesUtils.getConfigString(NewReadActivity.this, String.valueOf(bookId));
                    String p_c[] = pageAndCounts.split("_", 2);
                    if (p_c.length == 2) {
                        currentPageParaIndex = Integer.parseInt(p_c[0]);
                        currentTotalPages = Integer.parseInt(p_c[1]);
                        currentProgress = (double) currentPageParaIndex / currentTotalPages * 100;
                    }
                }
                Typeface typeface = Typeface.DEFAULT;
                File[] files = Utils.readFontsFile(NewReadActivity.this);
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (!TextUtils.isEmpty(fontName) && fontName.equals(files[i].getName())) {
                            typeface = Typeface.createFromFile(files[i]);
                            break;
                        }
                    }
                }

                initConfig();
                readAdapter.setPageParaBean(new PageParaBean().setTextColor(textColor).setSpSize(spSize).seLineSpace(lineSp).setParaSpace(paraSpace).setTypeface(typeface).create());
            }
        });
    }

    private void initConfig() {
        if (light > 0) {
            setScreenLight(light);
        }
        setEdgeSpace(lineSp);
        setReadBackground(bgColor);
        if (currentProgress >= 0 && currentProgress <= 100) {
            if (currentPageParaIndex == 0) {
                showNextPage();
            } else {
                showPage(MODE_CURRENT);
            }
        }
        if (to_main != null && to_edit != null && tvCurrentTime != null && tvCurrentProgress != null) {
            to_main.setTextColor(textColor);
            to_edit.setTextColor(textColor);
            tvCurrentTime.setTextColor(textColor);
            tvCurrentProgress.setTextColor(textColor);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_main:
                finish();
                break;
            case R.id.to_edit:
                if (bottomDialog == null) {
                    bottomDialog = new ReadBottomDialog();
                    bottomDialog.setListener(this);
                    bottomDialog.setDefaultConfig((int) currentProgress, DensityUtil.dp2px(spSize), DensityUtil.dp2px(lineSp),
                            DensityUtil.dp2px(edgeSpace), DensityUtil.dp2px(paraSpace));
                    //bottomDialog.setBgColor(bgColor);
                }
                bottomDialog.show(getFragmentManager(), "read_bottom");
                break;
        }
    }

    private class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(60000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    //在主线程里面处理消息并更新UI界面
    private static class TimeHandler extends Handler {
        WeakReference<NewReadActivity> weakReference;

        private TimeHandler(NewReadActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (weakReference != null && weakReference.get() != null) {
                        weakReference.get().tvCurrentTime.setText(getCurrentTime()); //更新时间
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void showPage(final int mode) {
        switch (mode) {
            case MODE_NEXT_PAGE:
                if (currentPageParaIndex == 0 || currentPageParaIndex <= currentTotalPages) {
                    currentPageParaIndex++;
                } else {
                    Toast.makeText(NewReadActivity.this, "已经到最后一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case MODE_PRE_PAGE:
                if (currentPageParaIndex > 1) {
                    currentPageParaIndex--;
                } else {
                    Toast.makeText(NewReadActivity.this, "当前为第一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case MODE_ONE_PAGE:
                if (currentProgress >= 0 && currentProgress <= 100 && currentTotalPages >= 0) {
                    currentPageParaIndex = (int) (currentTotalPages * (currentProgress / 100));
                    if (currentPageParaIndex == 0) {
                        currentPageParaIndex = 1;
                    }
                }
                break;
        }
        RetrofitUtils.getInstance().getBookContent(new Subscriber<NewBookResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(NewBookResponse newBookResponse) {
                if (newBookResponse != null && newBookResponse.code == 200
                        && newBookResponse.data != null) {

                    currentTotalPages = newBookResponse.data.totalpage;
                    DecimalFormat format = new DecimalFormat("#0.00");
                    currentProgress = (float) (currentPageParaIndex - 1) / currentTotalPages * 100;
                    //String readingProgress = format.format(currentProgress) + "%";
                    tvCurrentProgress.setText(String.valueOf(currentPageParaIndex + "/" + currentTotalPages));

                    if (newBookResponse.data.enparalist != null && newBookResponse.data.enparalist.size() > 0) {
                        allParas.clear();
                        for (int i = 0; i < newBookResponse.data.enparalist.size(); i++) {
                            newBookResponse.data.enparalist.get(i).type = ReadAdapter.DATA_TYPE_EN;
                            allParas.add(newBookResponse.data.enparalist.get(i));
                            if (newBookResponse.data.cnparalist != null && i <= newBookResponse.data.cnparalist.size() - 1) {
                                newBookResponse.data.cnparalist.get(i).type = ReadAdapter.DATA_TYPE_CN;
                                allParas.add(newBookResponse.data.cnparalist.get(i));
                            }
                        }
                        readAdapter.notifyDataSetChanged();
                        if (mode != MODE_PARA_EDIT) {
                            recyclerView.scrollToPosition(0);
                        }
                    } else {
                        if (newBookResponse.data.cnparalist != null && newBookResponse.data.cnparalist.size() > 0) {
                            for (int i = 0; i < newBookResponse.data.cnparalist.size(); i++) {
                                newBookResponse.data.cnparalist.get(i).type = ReadAdapter.DATA_TYPE_CN;
                                allParas.add(newBookResponse.data.cnparalist.get(i));
                            }
                            readAdapter.notifyDataSetChanged();
                            if (mode != MODE_PARA_EDIT) {
                                recyclerView.scrollToPosition(0);
                            }
                        } else {
                            Toast.makeText(NewReadActivity.this, "已经到最后一页", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }, bookId, currentPageParaIndex);
    }

    public void showNextPage() {
        showPage(MODE_NEXT_PAGE);
    }

    public void showPrePage() {
        showPage(MODE_PRE_PAGE);
    }

    @Override
    public void paraEdit(int op_type, int bookId, final String cnSeq) {
        RetrofitUtils.getInstance().paraEdit(new Subscriber<CommonResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(CommonResponse commonResponse) {
                if (commonResponse != null && commonResponse.code == 200) {
                    showPage(MODE_PARA_EDIT);
                }
            }
        }, op_type, bookId, cnSeq);
    }

    @Override
    public void showTransDialog(String word, String seq, String index) {
        if (dialog != null
                && dialog.getDialog() != null
                && dialog.getDialog().isShowing()) {
        } else {
            getTranslateData(word, seq, index);
        }
    }

    @Override
    public void setReadBackground(int bgColor) {
        this.bgColor = bgColor;
        if (contentView != null) {
            contentView.setBackgroundColor(bgColor);
        }
        /*if (dialog != null) {
            dialog.setBgColor(bgColor);
        }*/
        /*if (bottomDialog != null) {
            bottomDialog.setBgColor(bgColor);
        }*/
    }

    @Override
    public void setReadProgress(int progress) {
        currentProgress = progress;
        showPage(MODE_ONE_PAGE);
    }

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (readAdapter != null) {
            readAdapter.setTextColor(textColor);
        }
        /*if (dialog != null) {
            dialog.setTransTextColor(textColor);
        }*/
        if (to_main != null && to_edit != null && tvCurrentTime != null && tvCurrentProgress != null) {
            to_main.setTextColor(textColor);
            to_edit.setTextColor(textColor);
            tvCurrentTime.setTextColor(textColor);
            tvCurrentProgress.setTextColor(textColor);
        }
    }

    @Override
    public void setTextSize(int spSize) {
        this.spSize = spSize;
        if (readAdapter != null) {
            readAdapter.setTextSize(spSize);
        }
    }

    @Override
    public void setLineSpace(int lineSpace) {
        this.lineSp = lineSpace;
        if (readAdapter != null) {
            readAdapter.setLineSpace(lineSpace);
        }
    }

    @Override
    public void setParaSpace(int paraSpace) {
        this.paraSpace = paraSpace;
        if (readAdapter != null) {
            readAdapter.setParaSpace(paraSpace);
        }
    }

    @Override
    public void setEdgeSpace(int edgeSpace) {
        this.edgeSpace = edgeSpace;
        if (recyclerView.getLayoutParams() != null && recyclerView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            ((RelativeLayout.LayoutParams) recyclerView.getLayoutParams()).setMargins(DensityUtil.dp2px(10 + edgeSpace), 0, DensityUtil.dp2px(10 + edgeSpace), 0);
        }
    }

    @Override
    public void setScreenLight(int light) {
        this.light = light;
        DensityUtil.changeAppBrightness(this, light);
    }

    private void setFont(Typeface typeface) {
        this.typeface = typeface;
        if (readAdapter != null) {
            readAdapter.setFont(typeface);
        }
    }

    private String fontName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FontActivity.REQUEST_CODE_FOR_FONT) {
            if (resultCode == FontActivity.RESULT_CODE_TO_READ) {
                /*if (bottomDialog != null) {
                    bottomDialog.getDialog().hide();
                }*/
                if (data != null && data.getExtras() != null && data.getExtras().containsKey("switchFont")) {
                    FontBean bean = (FontBean) data.getExtras().getSerializable("switchFont");
                    if (bean != null) {
                        fontName = bean.fontName;
                        Typeface typeface = getTypeFace(bean);
                        if (typeface != null) {
                            setFont(typeface);
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Typeface getTypeFace(FontBean bean) {
        if (bean != null && bean.file != null) {
            return Typeface.createFromFile(bean.file);
        }
        return Typeface.DEFAULT;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getTranslateData(final String word, String seq, final String index) {
        Request request = new Request.Builder()
                .url(Constant.GET_TRANSLATE_DATA + "?book_id=" + bookId + "&word=" + word + "&seq=" + seq + "&index=" + index)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("TAG", "TranslateResponse = " + json);
                try {
                    TranslateResponse translateResponse = new GsonBuilder().create().fromJson(json, new TypeToken<TranslateResponse>() {
                    }.getType());
                    if (translateResponse != null
                            && translateResponse.code == 200
                            && translateResponse.data != null) {
                        translateResponse.data.word = word;
                        dialog = new TranslateDialog();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("TranslateBean", translateResponse.data);
                        bundle.putInt("bgColor", bgColor);
                        bundle.putInt("textColor", textColor);
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "dialog");
                        dialog.setListener(new DialogListener() {
                            @Override
                            public void onDismiss() {
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d("TAG", "Get TranslateResponse Error = " + e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitUtils.getInstance().unSubscribe("getBookContent");
        RetrofitUtils.getInstance().unSubscribe("paraEdit");
        if (dialog != null) {
            dialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferencesUtils.setConfigString(this, String.valueOf(bookId), currentPageParaIndex + "_" + currentTotalPages);
        SharedPreferencesUtils.setConfigString(this, bookListBean.name + bookId, String.valueOf(System.currentTimeMillis()));
        SharedPreferencesUtils.setConfigString(this, SharedPreferencesUtils.KEY_READ_CONFIG,
                light + "_" + bgColor + "_" + spSize + "_" + textColor + "_" + lineSp + "_" + edgeSpace + "_" + paraSpace);
        SharedPreferencesUtils.setConfigString(this, SharedPreferencesUtils.KEY_READ_FONT, fontName);
    }
}