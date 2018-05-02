package com.arun.ebook.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.widget.StrokeTextView;

import java.util.Hashtable;

/**
 * Created by Administrator on 2018/4/23.
 */

public class ReadBottomDialog extends DialogFragment {

    private SeekBar mSeekBar;
    private LinearLayout color_panel;
    private LinearLayout edit_group;
    private static final String[] BG_COLORS = new String[]{"#000000", "#363636", "#9C9C9C", "#B0E2FF", "#98FB98", "#FAFAD2", "#ffffff"};
    private static final String[] EDIT_NAMES = new String[]{"进度", "亮度", "背景", "字体", "字号", "字色", "行距", "边距", "字距", "段距"};
    //记录seekbar当前的进度
    private Hashtable<Integer, Integer> seekProgresses = new Hashtable<>();
    private PageViewListener listener;
    private int currentShowTag;

    public void setListener(PageViewListener listener) {
        this.listener = listener;
    }

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
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogBottomStyle);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_read_bottom, null);
        dialog.setContentView(view);
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
        initView(view);
        return dialog;
    }

    private void initView(View itemView) {
        mSeekBar = itemView.findViewById(R.id.seekBar);
        edit_group = itemView.findViewById(R.id.edit_group);
        color_panel = itemView.findViewById(R.id.color_panel);
        edit_group.removeAllViews();
        for (int i = 0; i < EDIT_NAMES.length; i++) {
            final TextView textView = new TextView(getActivity());
            textView.setTag(i);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, DensityUtil.dp2px(10), 0);
            textView.setLayoutParams(params);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setTextColor(getResources().getColor(R.color.text_common));
            textView.setText(EDIT_NAMES[i]);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTextSelected(textView);
                }
            });
            edit_group.addView(textView);
        }
        initColorPanel();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekProgresses.put(currentShowTag, progress);
                if (currentShowTag == 0) {//设置进度
                    listener.setReadProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initColorPanel() {
        color_panel.removeAllViews();
        int itemWidthDp = 33;
        int itemMarginDp = 10;
        int itemRadiusDp = 8;
        int itemStrokeWidthDp = 2;
        for (int i = 0; i < BG_COLORS.length; i++) {
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(DensityUtil.dp2px(itemWidthDp), DensityUtil.dp2px(itemWidthDp));
            params.setMargins(0, 0, DensityUtil.dp2px(itemMarginDp), 0);
            StrokeTextView textView = new StrokeTextView(getActivity(), itemWidthDp, itemRadiusDp, itemStrokeWidthDp, BG_COLORS[i]);
            textView.setLayoutParams(params);
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.setTextColor(Color.parseColor(BG_COLORS[finalI]));
                    }
                }
            });
            color_panel.addView(textView);
        }
    }

    private void showColorPanel() {
        color_panel.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.INVISIBLE);
    }

    private void showSeekBar(int tag, int progress) {
        currentShowTag = tag;
        mSeekBar.setProgress(progress);
        if (listener != null) {

        }
        mSeekBar.setVisibility(View.VISIBLE);
        color_panel.setVisibility(View.INVISIBLE);
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
        int seekProgress = 0;
        if (seekProgresses.get(tag) != null) {
            seekProgress = seekProgresses.get(tag);
        }
        switch (tag) {
            case 0:
            case 1:
                showSeekBar(tag, seekProgress);
                break;
            case 2:
            case 5:
                showColorPanel();
                break;
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
