package com.arun.ebook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.activity.EditPageActivity;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookDetailItemBean;
import com.arun.ebook.bean.BookEditBean;
import com.arun.ebook.bean.FontBean;
import com.arun.ebook.event.HidePopEvent;
import com.arun.ebook.event.RemoveLongPressEvent;
import com.arun.ebook.listener.BookEditListener;
import com.arun.ebook.listener.TranslateListener;
import com.arun.ebook.selectable.OnSelectListener;
import com.arun.ebook.selectable.SelectableTextHelper;
import com.arun.ebook.utils.DensityUtil;
import com.arun.ebook.utils.ToastUtils;
import com.arun.ebook.widget.JustifyTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BookDetailAdapter extends BaseRecyclerAdapter<BookDetailItemBean> {
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int VIEW_TYPE_TRANSLATE = 2;
    public static final String DATA_TYPE_CONTENT = "content";
    public static final String DATA_TYPE_TRANSLATE = "translate";

    private BookEditListener bookEditListener;
    private int book_id;

    public BookDetailAdapter(Context context, List<BookDetailItemBean> list) {
        super(context, list);
    }

    public void setBookEditListener(BookEditListener bookEditListener) {
        this.bookEditListener = bookEditListener;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CONTENT) {
            View itemView = LayoutInflater.from(contexts.get()).inflate(R.layout.layout_book_content, parent, false);
            return new BookContentHolder(contexts.get(), itemView, bookEditListener);
        } else if (viewType == VIEW_TYPE_TRANSLATE) {
            View itemView = LayoutInflater.from(contexts.get()).inflate(R.layout.layout_book_translate, parent, false);
            return new BookTranslateHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookContentHolder) {
            ((BookContentHolder) holder).setData((BookDetailBean) getItem(position).content, book_id);
        } else if (holder instanceof BookTranslateHolder) {
            ((BookTranslateHolder) holder).setData((String) getItem(position).content);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        BookDetailItemBean bean = getItem(position);
        if (bean != null) {
            if (DATA_TYPE_CONTENT.equals(bean.type)) {
                type = VIEW_TYPE_CONTENT;
            } else if (DATA_TYPE_TRANSLATE.equals(bean.type)) {
                type = VIEW_TYPE_TRANSLATE;
            }
        }
        return type;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof BookContentHolder) {
            ((BookContentHolder) holder).recycler();
        }
    }

    private static class BookContentHolder extends RecyclerView.ViewHolder implements TranslateListener, View.OnClickListener {
        private Context context;
        private JustifyTextView contentView;
        private TextView pageText;
        private SelectableTextHelper mSelectableTextHelper;
        private TextView front_merge, insert, delete, edit, style_title, style_quote, style_main_body;
        private BookEditListener bookEditListener;
        private BookDetailBean detailBean;

        private BookContentHolder(Context context, View itemView, BookEditListener bookEditListener) {
            super(itemView);
            this.context = context;
            EventBus.getDefault().register(this);
            contentView = itemView.findViewById(R.id.contentView);
            pageText = itemView.findViewById(R.id.pageText);
            front_merge = itemView.findViewById(R.id.front_merge);
            insert = itemView.findViewById(R.id.insert);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
            style_title = itemView.findViewById(R.id.style_title);
            style_quote = itemView.findViewById(R.id.style_quote);
            style_main_body = itemView.findViewById(R.id.style_main_body);

            mSelectableTextHelper = new SelectableTextHelper.Builder(contentView)
                    .setSelectedColor(context.getResources().getColor(R.color.white))
                    .setCursorHandleSizeInDp(20)
                    .setCursorHandleColor(context.getResources().getColor(R.color.more_yellow))
                    .build();

            mSelectableTextHelper.setSelectListener(new OnSelectListener() {
                @Override
                public void onTextSelected(CharSequence content) {

                }
            });

            front_merge.setOnClickListener(this);
            insert.setOnClickListener(this);
            delete.setOnClickListener(this);
            edit.setOnClickListener(this);
            style_title.setOnClickListener(this);
            style_quote.setOnClickListener(this);
            style_main_body.setOnClickListener(this);
            this.bookEditListener = bookEditListener;
        }

        private void setData(BookDetailBean bean, int book_id) {
            if (bean != null) {
                this.detailBean = bean;
                initFont(contentView, bean);
                setTextStyle(bean.style);
                contentView.setBookId(book_id);
                contentView.setPageId(bean.page_id);
                if (bean.queryed_word_list == null) {
                    bean.queryed_word_list = new ArrayList<>();
                }
                contentView.setTrans_words(bean.queryed_word_list);
                contentView.setText(bean.content);
                contentView.setTranslateListener(this);
                pageText.setText(bean.seq + "/" + bean.totalPage);
            }
        }

        public void setTextStyle(int style) {
            setAlignStyle(style);
            switch (style) {
                case BookEditBean.STYLE_TITLE:
                    contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    contentView.setLineSpacing(DensityUtil.dp2px(12), 1);
                    contentView.setTypeface(contentView.getTypeface(), Typeface.NORMAL);
                    break;
                case BookEditBean.STYLE_QUOTE:
                    contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    contentView.setLineSpacing(DensityUtil.dp2px(12), 1);
                    contentView.setTypeface(contentView.getTypeface(), Typeface.ITALIC);
                    break;
                case BookEditBean.STYLE_MAIN_BODY:
                    contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    contentView.setLineSpacing(DensityUtil.dp2px(12), 1);
                    contentView.setTypeface(contentView.getTypeface(), Typeface.NORMAL);
                    break;
            }
        }

        private void setAlignStyle(int style) {
            int alignStyle = 0;
            if (style == BookEditBean.STYLE_TITLE) {
                alignStyle = RelativeLayout.CENTER_IN_PARENT;
            } else if (style == BookEditBean.STYLE_QUOTE) {
                alignStyle = RelativeLayout.CENTER_VERTICAL;
            } else if (style == BookEditBean.STYLE_MAIN_BODY) {
                alignStyle = RelativeLayout.ALIGN_PARENT_TOP;
            }
            if (contentView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) contentView.getLayoutParams()).addRule(alignStyle);
            }
        }

        private void initFont(TextView textView, BookDetailBean bean) {
            if (bean != null && bean.file != null) {
                Typeface typeface = Typeface.createFromFile(bean.file);
                if (typeface != null) {
                    textView.setTypeface(typeface);
                }
            } else {
                textView.setTypeface(Typeface.DEFAULT);
            }
        }

        private void recycler() {
            if (mSelectableTextHelper != null) {
                mSelectableTextHelper.destroy();
            }
            EventBus.getDefault().unregister(this);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onHidePop(HidePopEvent hidePopEvent) {
            if (mSelectableTextHelper != null) {
                mSelectableTextHelper.resetSelectionInfo();
                mSelectableTextHelper.hideSelectView();
                contentView.invalidate();
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void removeHandlerCallBack(RemoveLongPressEvent event) {
            if (contentView != null) {
                contentView.removeHandlerCallBack();
            }
        }

        @Override
        public void showTransDialog(int book_id, String word, int page_id) {
            //ToastUtils.getInstance(context).showToast(word);
            if (bookEditListener != null) {
                bookEditListener.translateWord(book_id, word, page_id);
            }
        }

        @Override
        public void onClick(View v) {
            BookEditBean bean = null;
            switch (v.getId()) {
                case R.id.front_merge:
                    if (detailBean.seq > 0) {
                        bean = new BookEditBean(detailBean.page_id, BookEditBean.TYPE_FRONT_MERGE);
                    } else {
                        ToastUtils.getInstance(context).showToast("当前处于第一页，不能向前合并");
                    }
                    break;
                case R.id.insert:
                    EditPageActivity.jumpToEditPage(context, detailBean.page_id, detailBean.seq, "");
                    break;
                case R.id.delete:
                    bean = new BookEditBean(detailBean.page_id, BookEditBean.TYPE_DELETE);
                    break;
                case R.id.edit:
                    EditPageActivity.jumpToEditPage(context, detailBean.page_id, detailBean.seq, contentView.getText().toString());
                    break;
                case R.id.style_title:
                    bean = new BookEditBean(detailBean.page_id, BookEditBean.TYPE_STYLE, BookEditBean.STYLE_TITLE);
                    setTextStyle(BookEditBean.STYLE_TITLE);
                    break;
                case R.id.style_quote:
                    bean = new BookEditBean(detailBean.page_id, BookEditBean.TYPE_STYLE, BookEditBean.STYLE_QUOTE);
                    setTextStyle(BookEditBean.STYLE_QUOTE);
                    break;
                case R.id.style_main_body:
                    bean = new BookEditBean(detailBean.page_id, BookEditBean.TYPE_STYLE, BookEditBean.STYLE_MAIN_BODY);
                    setTextStyle(BookEditBean.STYLE_MAIN_BODY);
                    break;
            }
            if (bookEditListener != null && bean != null) {
                bookEditListener.onBookEdit(bean);
            }
        }
    }

    private static class BookTranslateHolder extends RecyclerView.ViewHolder {
        private TextView translate_content;

        private BookTranslateHolder(View itemView) {
            super(itemView);
            translate_content = itemView.findViewById(R.id.translate_content);
        }

        private void setData(String cnContent) {
            if (!TextUtils.isEmpty(cnContent)) {
                translate_content.setText(cnContent);
            }
        }

    }

}
