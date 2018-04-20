package com.arun.ebook.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arun.ebook.R;
import com.arun.ebook.dialog.TranslateDialog;
import com.arun.ebook.bean.TranslateResponse;
import com.arun.ebook.common.Constant;
import com.arun.ebook.listener.DialogListener;
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.widget.JustifyTextView;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
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

public class ReadActivity extends AppCompatActivity implements PageViewListener, View.OnClickListener {

    private JustifyTextView txtView;
    private Paint paint = new Paint();
    //private MappedByteBuffer mappedFile;//映射到内存中的文件
    private RandomAccessFile randomFile;//关闭Random流时使用
    private FileChannel fileChannel;
    private StringBuilder builder;
    private File file;
    private long fileLength;
    private int start = 0;
    private int end = 0;
    private int preAllPageCounts = 0;
    private static final int margin = DensityUtil.dp2px(5);//文字显示距离屏幕实际尺寸的偏移量
    private int pageHeight, pageWidth;//文字排版页面尺寸
    private int maxLineNumber;//行数
    private int lineMaxCount;//一行最多显示字数
    private int pageMaxCount;//一页最多显示字数
    private List<Integer> readCounts = new ArrayList<>();
    private int currentPageIndex = -1;
    private int lineSpace = DensityUtil.dp2px(5);
    private double currentProgress;
    private RelativeLayout readBottom;
    private LinearLayout readBottomEdit;
    private TextView to_main;
    private TextView tvCurrentTime;
    private TextView tvCurrentProgress;
    private OkHttpClient okHttpClient = new OkHttpClient();

    public static void jumpToRead(Context context, String filePath) {
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra("filePath", filePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_read);
        initViewAndListener();
        initFile();
        initTime();
    }

    private void initViewAndListener() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(android.R.color.black));
        paint.setTextSize(DensityUtil.dp2px(18));

        txtView = findViewById(R.id.text);
        tvCurrentTime = findViewById(R.id.currentTime);
        tvCurrentProgress = findViewById(R.id.currentProgress);
        readBottom = findViewById(R.id.read_bottom);
        readBottomEdit = findViewById(R.id.read_bottom_edit);
        to_main = findViewById(R.id.to_main);
        to_main.setOnClickListener(this);
        txtView.setPageViewListener(this);
    }

    private void initFile() {
        String filePath = "";
        //filePath = Environment.getExternalStorageDirectory() + "/tencent/Lord of the Flies.txt";
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("filePath")) {
            filePath = getIntent().getExtras().getString("filePath");
        }
        if (!TextUtils.isEmpty(filePath)) {
            file = new File(filePath);
            fileLength = file.length();
            try {
                randomFile = new RandomAccessFile(file, "r");
                nioCharset();
                //mappedFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
            } catch (Exception e) {
                e.printStackTrace();
                //Util.makeToast("打开失败！");
            } finally {
                try {
                    randomFile.close();
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            pageHeight = DensityUtil.getScreenHeight(this) - margin * 2 - DensityUtil.dp2px(16);
            pageWidth = DensityUtil.getScreenWidth(this) - margin * 2;
            maxLineNumber = pageHeight / (DensityUtil.dp2px(16) + lineSpace);
            lineMaxCount = (int) (pageWidth / paint.measureText("l"));
            pageMaxCount = maxLineNumber * lineMaxCount;
            loadFromFile();
        }
    }

    private void initTime(){
        //显示时间
        tvCurrentTime.setText(getCurrentTime());
        new TimeThread().start();
    }

    private String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_main:
                finish();
                break;
        }
    }

    private class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
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
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tvCurrentTime.setText(getCurrentTime()); //更新时间
                    break;
                default:
                    break;
            }
        }
    };

    private void nioCharset() {
        try {
            Charset charset = Charset.forName("UTF-8");//Java.nio.charset.Charset处理了字符转换问题。它通过构造CharsetEncoder和CharsetDecoder将字符序列转换成字节和逆转换。
            CharsetDecoder decoder = charset.newDecoder();
            fileChannel = randomFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int) fileLength);
            CharBuffer charBuffer = CharBuffer.allocate((int) fileLength);
            int count = fileChannel.read(buffer);
            while (count != -1) {
                buffer.flip();
                decoder.decode(buffer, charBuffer, false);
                charBuffer.flip();
                buffer.clear();
                charBuffer.clear();
                count = fileChannel.read(buffer);
            }
            builder = new StringBuilder();
            for (int i = 0; i < charBuffer.length(); i++) {
                if (charBuffer.get(i) != '\u0000') {
                    builder.append(charBuffer.get(i));
                }
            }
            fileLength = builder.length();
            Log.d("TAG", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        showNextPage();
        //showPrePage(currentPageCounts);
    }

    private float downX = 0;
    private float downY = 0;

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = eventX;
                downY = eventY;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float moveX = eventX - downX;
                //float moveY = Math.abs(eventY - downY);
                if (moveX > 10) {//上一页
                    showPrePage();
                } else if (moveX < -10) {
                    showNextPage();
                }
                break;
        }
        return super.onTouchEvent(event);
    }*/

    public void showNextPage() {
        if (end >= fileLength) {//当前处于最后一页
            tvCurrentProgress.setText("100%");
            Toast.makeText(this, "已到最后", Toast.LENGTH_SHORT).show();
        } else {
            try {
                final StringBuilder onePage = new StringBuilder();
                while (onePage.length() <= pageMaxCount && end < fileLength) {
                    char[] byteTemp = readParagraphForward(end);
                    String onePara = new String(byteTemp);
                    end += byteTemp.length;
                    onePage.append(onePara);
                }
                txtView.setText(onePage.toString());
                /*StringUtils.setEachWord(txtView, onePage.toString(), new ClickWordListener() {
                    @Override
                    public void onClickWord(String word) {
                        getTranslateData(word);
                    }
                });*/
                txtView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int showNum = txtView.getCharNum();
                        currentPageIndex++;
                        readCounts.add(currentPageIndex, showNum);
                        preAllPageCounts += showNum;
                        end = preAllPageCounts;//end会多读，这边改为当前所有显示的最后一位
                        start = end - showNum;
                        currentProgress = (float) start / fileLength * 100;
                        DecimalFormat format = new DecimalFormat("#0.00");
                        String readingProgress = format.format(currentProgress) + "%";
                        tvCurrentProgress.setText(readingProgress);
                    }
                }, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showPrePage() {
        try {
            final int currentCount = readCounts.get(currentPageIndex);
            end -= currentCount + readCounts.get(currentPageIndex - 1);
            final StringBuilder onePage = new StringBuilder();
            while (onePage.length() <= pageMaxCount && end < fileLength) {
                char[] byteTemp = readParagraphForward(end);
                String onePara = new String(byteTemp);
                end += byteTemp.length;
                onePage.append(onePara);
            }
            txtView.setText(onePage.toString());
            txtView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int showNum = txtView.getCharNum();
                    readCounts.remove(currentPageIndex);
                    currentPageIndex--;
                    preAllPageCounts -= currentCount;
                    end = preAllPageCounts;
                    start = end - showNum;
                    currentProgress = (float) start / fileLength * 100;
                    DecimalFormat format = new DecimalFormat("#0.00");
                    String readingProgress = format.format(currentProgress) + "%";
                    tvCurrentProgress.setText(readingProgress);
                }
            }, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showTransDialog(String word) {
        if (dialog != null
                && dialog.getDialog() != null
                && dialog.getDialog().isShowing()) {
        } else {
            getTranslateData(word);
        }
    }

    private boolean isShowBottom;

    @Override
    public void showBottom() {
        if (!isShowBottom) {
            readBottomEdit.setVisibility(View.VISIBLE);
            readBottom.setVisibility(View.GONE);
            isShowBottom = true;
        } else {
            readBottomEdit.setVisibility(View.GONE);
            readBottom.setVisibility(View.VISIBLE);
            isShowBottom = false;
        }
    }

    private char[] readParagraphForward(int end) {
        char b0;
        int i = end;
        while (i < fileLength) {
            b0 = builder.charAt(i);
            if (b0 == 10) {
                break;
            }
            i++;
        }

        i = (int) Math.min(fileLength - 1, i);
        int nParaSize = i - end + 1;

        char[] buf = new char[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = builder.charAt(end + i);
        }
        return buf;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private TranslateDialog dialog;

    private void getTranslateData(final String keyword) {
        Request request = new Request.Builder()
                .url(Constant.GET_TRANSLATE_DATA + "keyword=" + keyword)
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
                        translateResponse.data.word = keyword;
                        dialog = new TranslateDialog();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("TranslateBean", translateResponse.data);
                        dialog.setArguments(bundle);
                        dialog.show(getFragmentManager(), "dialog");
                        dialog.setListener(new DialogListener() {
                            @Override
                            public void onDismiss() {
                                if (txtView != null) {
                                    txtView.reset();
                                    txtView.invalidate();
                                }
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
}
