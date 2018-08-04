package com.arun.ebook.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.activity.FontActivity;
import com.arun.ebook.bean.ConfigData;
import com.arun.ebook.common.Constant;
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.widget.StrokeTextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by wy on 2018/4/23.
 */

public class ReadBottomDialog extends DialogFragment {

    private SeekBar mSeekBar;
    private HorizontalScrollView color_group;
    private LinearLayout color_panel;
    private LinearLayout edit_group;
    private static final String[] BG_COLORS = new String[]{"#000000", "#363636", "#9C9C9C", "#F2F1ED", "#98FB98", "#FAFAD2", "#ffffff"};
    private static final String[] EDIT_NAMES = new String[]{"进度", "亮度", "背景", "字体", "字号", "字色", "行距", "边距", "段距"};
    private List<String> bg_colors = new ArrayList<>();
    private List<String> text_colors = new ArrayList<>();
    //记录seekbar当前的进度
    private Hashtable<Integer, Integer> seekProgresses = new Hashtable<>();
    private PageViewListener listener;
    private int currentShowTag;
    private Dialog dialog;
    //private int colorStyle = 5;
    private static final int MIN_TEXT_SP = 7;
    private static final int MIN_EDGE_SPACE = 10;
    private static final int MIN_LINE_SPACE = 1;
    public static final int MIN_PARA_SPACE = Constant.DEFAULT_PARA_SPACE_DP;
    private int progress;
    private float defaultTextSize;
    private float defaultLineSpace;
    private int defaultEdgeSpace;
    private float defaultParaSpace;
    private boolean isChangeTab;
    private View bottomView;

    //颜色区域距离
    private static final int itemWidthDp = 33;
    private static final int itemMarginDp = 10;
    private static final int itemRadiusDp = 8;
    private static final int itemStrokeWidthDp = 2;

    public void setListener(PageViewListener listener) {
        this.listener = listener;
    }

    public void setDefaultConfig(int progress, float defaultTextSize, float defaultLineSpace, int defaultEdgeSpace, float defaultParaSpace) {
        this.progress = progress;
        this.defaultTextSize = defaultTextSize;
        this.defaultLineSpace = defaultLineSpace;

        this.defaultParaSpace = defaultParaSpace;
        this.defaultEdgeSpace = defaultEdgeSpace;
    }

   /* public void setBgColor(int bgColor) {
        if (bottomView != null) {
            bottomView.setBackgroundColor(bgColor);
        }
    }*/

    /*@Override
    public void show(FragmentManager manager, String tag) {
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager.beginTransaction().remove(this).commit();
            super.show(manager, tag);
        } catch (Exception e) {
            //同一实例使用不同的tag会异常,这里捕获一下
            e.printStackTrace();
        }
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (dialog == null) {
            dialog = new Dialog(getActivity(), R.style.DialogBottomStyle);
            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bottomView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_read_bottom, null);
            dialog.setContentView(bottomView);
            dialog.setCanceledOnTouchOutside(true);
            // 设置宽度为屏宽、位置靠近屏幕顶部
            Window window = dialog.getWindow();
            if (window != null) {
                window.setDimAmount(0f);
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(wlp);
            }
            if (ConfigData.bgColor != null) {
                bg_colors = ConfigData.bgColor;
            }
            if (ConfigData.textColor != null) {
                text_colors = ConfigData.textColor;
            }
            initView(bottomView);
        }
        return dialog;
    }

    private void initView(View itemView) {
        mSeekBar = itemView.findViewById(R.id.seekBar);
        edit_group = itemView.findViewById(R.id.edit_group);
        color_panel = itemView.findViewById(R.id.color_panel);
        color_group = itemView.findViewById(R.id.color_group);
        edit_group.removeAllViews();
        for (int i = 0; i < EDIT_NAMES.length; i++) {
            final TextView textView = new TextView(getActivity());
            textView.setTag(i);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, DensityUtil.dp2px(10), 0);
            textView.setLayoutParams(params);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setTextColor(getResources().getColor(i == 0 ? R.color.red : R.color.text_common));
            if (i == 0) {
                seekProgresses.put(i, progress);
                setTextSelected(textView);
            } else if (i == 1) {
                seekProgresses.put(i, DensityUtil.getSystemBrightness(getActivity()));
            } else if (i == 4) {
                seekProgresses.put(i, (int) defaultTextSize - MIN_TEXT_SP);
            } else if (i == 6) {
                seekProgresses.put(i, (int) defaultLineSpace - MIN_LINE_SPACE);
            } else if (i == 7) {
                seekProgresses.put(i, defaultEdgeSpace - MIN_EDGE_SPACE);
            } else if (i == 8) {
                seekProgresses.put(i, (int) defaultParaSpace - MIN_PARA_SPACE);
            }
            textView.setText(EDIT_NAMES[i]);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTextSelected(textView);
                }
            });
            edit_group.addView(textView);
        }
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!isChangeTab) {
                    Log.d("TAG", "onProgressChanged progress = " + progress);
                    seekProgresses.put(currentShowTag, progress);
                    if (currentShowTag == 0) {//设置进度
                        //listener.setReadProgress(progress);
                    } else if (currentShowTag == 1) {//设置亮度
                        //DensityUtil.changeAppBrightness(getActivity(), progress);
                        listener.setScreenLight(progress);
                    } else if (currentShowTag == 4) {//minSize设为5sp,设置字号
                        listener.setTextSize(MIN_TEXT_SP + progress);
                    } else if (currentShowTag == 6) {//设置行间距
                        listener.setLineSpace(MIN_LINE_SPACE + progress);
                    } else if (currentShowTag == 7) {//设置左右边距
                        listener.setEdgeSpace(MIN_EDGE_SPACE + progress);
                    } /*else if (currentShowTag == 8) {//设置字间距
                    listener.setWordSpace(progress);
                    } */ else if (currentShowTag == 8) {//设置段落间距
                        listener.setParaSpace(MIN_PARA_SPACE + progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (currentShowTag == 0) {//设置进度
                    listener.setReadProgress(seekBar.getProgress());
                }
            }
        });
    }

    private void showColorPanel(final int tag) {
        color_group.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.INVISIBLE);
        color_panel.removeAllViews();
        int size = 0;
        if (tag == 2) {//背景色
            size = bg_colors.size();
        } else if (tag == 5) {//字色
            size = text_colors.size();
        }
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                final String color = tag == 2 ? bg_colors.get(i) : text_colors.get(i);
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(DensityUtil.dp2px(itemWidthDp), DensityUtil.dp2px(itemWidthDp));
                params.setMargins(0, 0, DensityUtil.dp2px(itemMarginDp), 0);
                StrokeTextView textView = new StrokeTextView(getActivity(), itemWidthDp, itemRadiusDp, itemStrokeWidthDp, color);
                textView.setLayoutParams(params);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            if (tag == 2) {//修改背景色
                                listener.setReadBackground(Color.parseColor(color));
                            } else {//修改文字颜色
                                listener.setTextColor(Color.parseColor(color));
                            }
                        }
                    }
                });
                color_panel.addView(textView);
            }
        }
    }

    private void showSeekBar(int tag, int progress) {
        currentShowTag = tag;
        mSeekBar.setProgress(progress);
        mSeekBar.setVisibility(View.VISIBLE);
        color_group.setVisibility(View.INVISIBLE);
    }

    private void showFontView() {
        mSeekBar.setVisibility(View.INVISIBLE);
        color_group.setVisibility(View.INVISIBLE);
        FontActivity.jumpToFont(getActivity());
    }

    private void setTextSelected(TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.red));
        if (edit_group != null && edit_group.getChildCount() > 0) {
            for (int i = 0; i < edit_group.getChildCount(); i++) {
                if (edit_group.getChildAt(i) instanceof TextView) {
                    TextView tv = (TextView) edit_group.getChildAt(i);
                    if (!textView.getTag().equals(tv.getTag())) {
                        tv.setTextColor(getResources().getColor(R.color.text_common));
                    }
                }
            }
        }
        int tag = (int) textView.getTag();
        if (tag == 0 || tag == 1 || tag == 4 || tag == 6 || tag == 7 || tag == 8) {
            isChangeTab = true;
            if (tag == 0 || tag == 1) {//进度和亮度范围
                mSeekBar.setMax(100);
            } else if (tag == 4) {//字号范围
                mSeekBar.setMax(15);
            } else if (tag == 6) {
                mSeekBar.setMax(15);//行间距范围
            } else if (tag == 7) {
                mSeekBar.setMax(30);//左右边距范围
            } else {
                mSeekBar.setMax(30);//段落间距范围
            }
            int seekProgress = 0;
            if (seekProgresses.get(tag) != null) {
                seekProgress = seekProgresses.get(tag);
            }
            showSeekBar(tag, seekProgress);
            isChangeTab = false;
        } else if (tag == 2 || tag == 5) {
            showColorPanel(tag);
        } else if (tag == 3) {
            showFontView();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
