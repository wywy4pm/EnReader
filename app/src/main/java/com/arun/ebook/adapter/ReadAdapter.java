package com.arun.ebook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.bean.PageParaBean;
import com.arun.ebook.bean.book.NewBookBean;
import com.arun.ebook.listener.PageViewListener;
import com.arun.ebook.listener.ParaEditListener;
import com.arun.ebook.listener.TranslateListener;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.utils.StringUtils;
import com.arun.ebook.widget.JustifyTextView;

import java.util.List;
import java.util.regex.Pattern;

public class ReadAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<NewBookBean> list;
    public static final String DATA_TYPE_EN = "english";
    public static final String DATA_TYPE_CN = "chinese";
    public static final int VIEW_TYPE_EN = 1;
    public static final int VIEW_TYPE_CN = 2;

    private PageViewListener pageViewListener;
    private ParaEditListener paraEditListener;
    private PageParaBean pageParaBean = new PageParaBean();

    public ReadAdapter(Context context, List<NewBookBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setPageParaBean(PageParaBean pageParaBean) {
        this.pageParaBean = pageParaBean;
        notifyDataSetChanged();
    }

    public void setParaEditListener(ParaEditListener paraEditListener) {
        this.paraEditListener = paraEditListener;
    }

    public void setPageViewListener(PageViewListener pageViewListener) {
        this.pageViewListener = pageViewListener;
    }

    public void setTextColor(int textColor) {
        if (pageParaBean != null) {
            pageParaBean.textColor = textColor;
            notifyDataSetChanged();
        }
    }

    public void setTextSize(int spSize) {
        if (pageParaBean != null) {
            pageParaBean.spSize = spSize;
            notifyDataSetChanged();
        }
    }

    public void setLineSpace(int lineSpace) {
        if (pageParaBean != null) {
            pageParaBean.lineSpace = lineSpace;
            notifyDataSetChanged();
        }
    }

    public void setParaSpace(int paraSpace) {
        if (pageParaBean != null) {
            pageParaBean.paraSpace = paraSpace;
            notifyDataSetChanged();
        }
    }

    public void setFont(Typeface typeface) {
        if (pageParaBean != null) {
            pageParaBean.typeface = typeface;
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EN) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.layout_item_english, parent, false);
            return new EnglishHolder(itemView, pageViewListener);
        } else if (viewType == VIEW_TYPE_CN) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.layout_item_chinese, parent, false);
            return new ChineseHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EnglishHolder) {
            EnglishHolder englishHolder = (EnglishHolder) holder;
            englishHolder.setData(list.get(position), pageParaBean);
        } else if (holder instanceof ChineseHolder) {
            ChineseHolder chineseHolder = (ChineseHolder) holder;
            if (position >= 1) {
                chineseHolder.setData(list.get(position), list.get(position - 1), paraEditListener, pageParaBean);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        if (DATA_TYPE_EN.equals(list.get(position).type)) {
            type = VIEW_TYPE_EN;
        } else if (DATA_TYPE_CN.equals(list.get(position).type)) {
            type = VIEW_TYPE_CN;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class EnglishHolder extends RecyclerView.ViewHolder implements TranslateListener {
        private JustifyTextView english_para;
        private PageViewListener pageViewListener;

        private EnglishHolder(View itemView, PageViewListener pageViewListener) {
            super(itemView);
            english_para = itemView.findViewById(R.id.english_para);
            this.pageViewListener = pageViewListener;
        }

        private void setData(NewBookBean bean, PageParaBean pageParaBean) {
            //english_para.reset();
            setParaCommon(english_para, pageParaBean);
            english_para.setTranslateListener(this);
            if (bean.separate_list != null && bean.separate_list.size() > 0) {
                english_para.setParaText(bean.separate_list);
                english_para.setParaSeq(bean.seq);
                StringBuilder stringBuilder = new StringBuilder();
                //long start = System.currentTimeMillis();
                for (int i = 0; i < bean.separate_list.size(); i++) {
                    String content = bean.separate_list.get(i).content;
                    int isQuote = bean.separate_list.get(i).only_quote;
                    if (!TextUtils.isEmpty(content) && isQuote != 1) {
                        stringBuilder.append(content);
                        if (i < bean.separate_list.size() - 1) {
                            String nextContent = bean.separate_list.get(i + 1).content;
                            int nextQuote = bean.separate_list.get(i + 1).only_quote;
                            if (!TextUtils.isEmpty(nextContent)) {
                                if (nextQuote == 1) {
                                    if (StringUtils.isAddSpacePun(nextContent)) {
                                        stringBuilder.append(nextContent).append(" ");
                                    } else {
                                        stringBuilder.append(nextContent);
                                    }
                                } else if (StringUtils.isEnNum(nextContent.charAt(nextContent.length() - 1))) {
                                    stringBuilder.append(" ");
                                } else {
                                    stringBuilder.append(nextContent);
                                }
                            }
                        }
                    }
                }
                Log.d("TAG", "stringBuilder text = " + stringBuilder.toString());
                english_para.setText(stringBuilder.toString());
            }
        }

        private void setParaCommon(TextView textView, PageParaBean pageParaBean) {
            textView.setTextColor(pageParaBean.textColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, pageParaBean.spSize);
            textView.setLineSpacing(DensityUtil.dp2px(pageParaBean.lineSpace + 10), 1);
            textView.setPadding(0, 0, 0, DensityUtil.dp2px(pageParaBean.paraSpace));
            textView.setTypeface(pageParaBean.typeface);
        }

        @Override
        public void showTransDialog(String word, String seq, String index) {
            if (pageViewListener != null) {
                pageViewListener.showTransDialog(word, seq, index);
            }
        }
    }

    private static class ChineseHolder extends RecyclerView.ViewHolder {
        private TextView chinese_para, front_insert_space, current_delete, front_merge, book_id, en_seq, cn_seq;

        private ChineseHolder(View itemView) {
            super(itemView);
            chinese_para = itemView.findViewById(R.id.chinese_para);
            front_insert_space = itemView.findViewById(R.id.front_insert_space);
            current_delete = itemView.findViewById(R.id.current_delete);
            front_merge = itemView.findViewById(R.id.front_merge);
            book_id = itemView.findViewById(R.id.book_id);
            en_seq = itemView.findViewById(R.id.en_seq);
            cn_seq = itemView.findViewById(R.id.cn_seq);
        }

        private void setParaCommon(PageParaBean pageParaBean, TextView... textViews) {
            for (int i = 0; i < textViews.length; i++) {
                //color int值转换为16进制rgb string值
                String hexColor = String.format("#%06X", (0xFFFFFF & pageParaBean.textColor));
                String newColor = "#80" + hexColor.substring(1, hexColor.length());
                textViews[i].setTextColor(Color.parseColor(newColor));
                textViews[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, pageParaBean.spSize);
                if (pageParaBean.lineSpace > 3) {
                    textViews[i].setLineSpacing(DensityUtil.dp2px(pageParaBean.lineSpace - 3), 1);
                } else {
                    textViews[i].setLineSpacing(0, 1);
                }
                if (i == 0) {
                    textViews[i].setPadding(DensityUtil.dp2px(25), 0, DensityUtil.dp2px(25), DensityUtil.dp2px(pageParaBean.paraSpace));
                } else if (i == 1) {
                    textViews[i].setPadding(DensityUtil.dp2px(25), 0, 0, DensityUtil.dp2px(pageParaBean.paraSpace));
                } else if (i == textViews.length - 1) {
                    textViews[i].setPadding(0, 0, DensityUtil.dp2px(25), DensityUtil.dp2px(pageParaBean.paraSpace));
                } else {
                    textViews[i].setPadding(0, 0, 0, DensityUtil.dp2px(pageParaBean.paraSpace));
                }
            }
        }

        private void setData(final NewBookBean bean, final NewBookBean preBean, final ParaEditListener paraEditListener, PageParaBean pageParaBean) {
            if (bean != null) {
                setParaCommon(pageParaBean, chinese_para, front_insert_space, current_delete, front_merge, book_id, en_seq, cn_seq);
                chinese_para.setText(bean.content);
                book_id.setText(String.valueOf(bean.book_id));
                cn_seq.setText(bean.seq);
                if (preBean != null) {
                    en_seq.setText(preBean.seq);
                }
                //1：前插空，2：删除，3：前合并
                front_insert_space.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (paraEditListener != null && preBean != null) {
                            paraEditListener.paraEdit(1, bean.book_id, bean.seq);
                        }
                    }
                });
                current_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (paraEditListener != null && preBean != null) {
                            paraEditListener.paraEdit(2, bean.book_id, bean.seq);
                        }
                    }
                });
                front_merge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (paraEditListener != null && preBean != null) {
                            paraEditListener.paraEdit(3, bean.book_id, bean.seq);
                        }
                    }
                });
            }
        }
    }
}
