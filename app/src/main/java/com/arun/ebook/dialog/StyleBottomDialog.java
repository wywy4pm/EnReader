package com.arun.ebook.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.activity.FontActivity;
import com.arun.ebook.bean.ConfigData;
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.widget.StrokeTextView;

import java.util.ArrayList;
import java.util.List;

public class StyleBottomDialog extends DialogFragment {
    private Window window;
    private Dialog dialog;
    private View bottomView;
    private SeekBar seekChangeSize;
    private TextView font_text, font_size, font_color, read_bg, font_name, change_font;
    private LinearLayout text_color_panel, bg_color_panel;
    private static final int itemWidthDp = 24;
    private static final int itemMarginDp = 10;
    private static final int itemRadiusDp = 12;
    private static final int itemStrokeWidthDp = 2;
    private List<String> bg_colors = new ArrayList<>();
    private List<String> text_colors = new ArrayList<>();
    private PageViewListener listener;

    public void setListener(PageViewListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (dialog == null) {
            dialog = new Dialog(getActivity());
            //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            bottomView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_style_bottom, null);
            dialog.setContentView(bottomView);
            dialog.setCanceledOnTouchOutside(true);
            // 设置宽度为屏宽、位置靠近屏幕顶部
            window = dialog.getWindow();
            if (window != null) {
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.getDecorView().setBackgroundColor(getResources().getColor(R.color.black));
                window.setGravity(Gravity.BOTTOM);
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
        font_text = itemView.findViewById(R.id.font_text);
        font_size = itemView.findViewById(R.id.font_size);
        font_color = itemView.findViewById(R.id.font_color);
        read_bg = itemView.findViewById(R.id.read_bg);

        font_name = itemView.findViewById(R.id.font_name);
        change_font = itemView.findViewById(R.id.change_font);
        seekChangeSize = itemView.findViewById(R.id.seekChangeSize);
        text_color_panel = itemView.findViewById(R.id.text_color_panel);
        bg_color_panel = itemView.findViewById(R.id.bg_color_panel);
        change_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontActivity.jumpToFont(getActivity());
            }
        });

        seekChangeSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("TAG", "onProgressChanged progress = " + progress);
                double scale = (double) progress / 100 + 0.5;
                Log.d("TAG", "onProgressChanged scale = " + scale);
                if (listener != null) {
                    listener.setTextSize(scale);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener != null) {
                    listener.setReadProgress(seekBar.getProgress());
                }
            }
        });

        setColorPanel(1);
        setColorPanel(2);
    }

    private void setColorPanel(final int tag) {
        int size = 0;
        if (tag == 1) {//背景色
            bg_color_panel.removeAllViews();
            size = bg_colors.size();
        } else {//字色
            text_color_panel.removeAllViews();
            size = text_colors.size();
        }
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                final String color = tag == 1 ? bg_colors.get(i) : text_colors.get(i);
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(DensityUtil.dp2px(itemWidthDp), DensityUtil.dp2px(itemWidthDp));
                params.setMargins(0, 0, DensityUtil.dp2px(itemMarginDp), 0);
                StrokeTextView textView = new StrokeTextView(getActivity(), itemWidthDp, itemRadiusDp, itemStrokeWidthDp, color);
                textView.setLayoutParams(params);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            if (tag == 1) {//修改背景色
                                listener.setReadBackground(Color.parseColor(color));
                                if (window != null) {
                                    window.getDecorView().setBackgroundColor(Color.parseColor(color));
                                }
                            } else {//修改文字颜色
                                listener.setTextColor(Color.parseColor(color));
                                font_text.setTextColor(Color.parseColor(color));
                                font_size.setTextColor(Color.parseColor(color));
                                font_color.setTextColor(Color.parseColor(color));
                                read_bg.setTextColor(Color.parseColor(color));
                                font_name.setTextColor(Color.parseColor(color));
                            }
                        }
                    }
                });
                if (tag == 1) {
                    bg_color_panel.addView(textView);
                } else {
                    text_color_panel.addView(textView);
                }
            }
        }
    }

    public void setFontName(String fontName) {
        if (font_name != null) {
            int lastIndex = fontName.indexOf(".");
            if (lastIndex <= 0) {
                font_name.setText(fontName);
            } else {
                font_name.setText(fontName.substring(0, lastIndex));
            }
        }
    }
}
