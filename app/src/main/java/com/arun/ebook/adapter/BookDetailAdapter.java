package com.arun.ebook.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
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
import com.arun.ebook.utils.ToastUtils;
import com.arun.ebook.widget.JustifyTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class BookDetailAdapter extends BaseRecyclerAdapter<BookDetailItemBean> {
    private static final int VIEW_TYPE_CONTENT = 1;
    public static final String DATA_TYPE_CONTENT = "content";
    private BookEditListener bookEditListener;

    public BookDetailAdapter(Context context, List<BookDetailItemBean> list) {
        super(context, list);
    }

    public void setBookEditListener(BookEditListener bookEditListener) {
        this.bookEditListener = bookEditListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CONTENT) {
            View itemView = LayoutInflater.from(contexts.get()).inflate(R.layout.layout_book_content, parent, false);
            return new BookContentHolder(contexts.get(), itemView, bookEditListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookContentHolder) {
            ((BookContentHolder) holder).setData((BookDetailBean) getItem(position).content);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        BookDetailItemBean bean = getItem(position);
        if (bean != null) {
            if (DATA_TYPE_CONTENT.equals(bean.type)) {
                type = VIEW_TYPE_CONTENT;
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
                    .setSelectedColor(context.getResources().getColor(R.color.text_green))
                    .setCursorHandleSizeInDp(20)
                    .setCursorHandleColor(context.getResources().getColor(R.color.red))
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

        private void setData(BookDetailBean bean) {
            if (bean != null) {
                this.detailBean = bean;
                initFont(contentView, bean);
                contentView.setText(bean.content);
                contentView.setTranslateListener(this);
                pageText.setText((bean.currentPage + 1) + "/" + bean.totalPage);
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
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void removeHandlerCallBack(RemoveLongPressEvent event) {
            if (contentView != null) {
                contentView.removeHandlerCallBack();
            }
        }

        @Override
        public void showTransDialog(String word, String seq) {
            ToastUtils.getInstance(context).showToast(word);
        }

        @Override
        public void onClick(View v) {
            BookEditBean bean = null;
            switch (v.getId()) {
                case R.id.front_merge:
                    bean = new BookEditBean(detailBean.paragraphId, BookEditBean.TYPE_FRONT_MERGE);
                    break;
                case R.id.insert:
                    EditPageActivity.jumpToEditPage(context, detailBean.paragraphId, detailBean.currentPage, "");
                    break;
                case R.id.delete:
                    bean = new BookEditBean(detailBean.paragraphId, BookEditBean.TYPE_DELETE);
                    break;
                case R.id.edit:
                    EditPageActivity.jumpToEditPage(context, detailBean.paragraphId, detailBean.currentPage, contentView.getText().toString());
                    break;
            }
            if (bookEditListener != null && bean != null) {
                bookEditListener.onBookEdit(bean);
            }
        }
    }

}
