package com.arun.ebook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.bean.FontBean;
import com.arun.ebook.listener.SwitchFontListener;

import java.lang.ref.WeakReference;
import java.util.List;

public class FontAdapter extends RecyclerView.Adapter {
    private WeakReference<Context> weakReference;
    private List<FontBean> fonts;
    private SwitchFontListener switchFontListener;

    public FontAdapter(Context context, List<FontBean> fonts) {
        this.weakReference = new WeakReference<>(context);
        this.fonts = fonts;
    }

    public void setSwitchFontListener(SwitchFontListener switchFontListener) {
        this.switchFontListener = switchFontListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(weakReference.get()).inflate(R.layout.layout_font_item, parent, false);
        return new FontHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FontHolder) {
            FontHolder fontHolder = (FontHolder) holder;
            fontHolder.setData(weakReference.get(), fonts.get(position), position, fonts.size(), switchFontListener);
        }
    }

    @Override
    public int getItemCount() {
        return fonts.size();
    }

    private static class FontHolder extends RecyclerView.ViewHolder {
        private TextView font_text;

        private FontHolder(View itemView) {
            super(itemView);
            font_text = itemView.findViewById(R.id.font_text);
        }

        private void setData(final Context context, final FontBean bean, final int position, final int size, final SwitchFontListener switchFontListener) {
            if (bean != null) {
                initFont(font_text, bean);
                if (position == 0 || position == size - 1) {
                    font_text.setText(bean.fontName);
                    if (position == size - 1) {
                        font_text.setClickable(false);
                    }else {
                        font_text.setClickable(true);
                    }
                } else {
                    font_text.setClickable(true);
                    font_text.setText(String.valueOf(bean.fontName + context.getResources().getString(R.string.font_example)));
                }
                font_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (switchFontListener != null) {
                            if(position != size - 1){
                                switchFontListener.switchFont(bean);
                            }
                        }
                    }
                });
            }
        }

        private void initFont(TextView textView, FontBean bean) {
            if (bean != null && bean.file != null) {
                Typeface typeface = Typeface.createFromFile(bean.file);
                if (typeface != null) {
                    textView.setTypeface(typeface);
                }
            } else {
                textView.setTypeface(Typeface.DEFAULT);
            }
        }
    }
}
