package com.arun.ebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arun.ebook.R;
import com.arun.ebook.bean.BookDetailBean;
import com.arun.ebook.bean.BookDetailItemBean;
import com.arun.ebook.event.HidePopEvent;
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

    public BookDetailAdapter(Context context, List<BookDetailItemBean> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CONTENT) {
            View itemView = LayoutInflater.from(contexts.get()).inflate(R.layout.layout_book_content, parent, false);
            return new BookContentHolder(contexts.get(), itemView);
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

    private static class BookContentHolder extends RecyclerView.ViewHolder implements TranslateListener {
        private Context context;
        private JustifyTextView contentView;
        private TextView pageText;
        private SelectableTextHelper mSelectableTextHelper;

        private BookContentHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            EventBus.getDefault().register(this);
            contentView = itemView.findViewById(R.id.contentView);
            pageText = itemView.findViewById(R.id.pageText);
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
        }

        private void setData(BookDetailBean bean) {
            if (bean != null) {
                contentView.setText(bean.content);
                contentView.setTranslateListener(this);
                pageText.setText((bean.currentPage + 1) + "/" + bean.totalPage);
            }
        }

        private void recycler() {
            EventBus.getDefault().unregister(this);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onHidePop(HidePopEvent hidePopEvent) {
            if (mSelectableTextHelper != null) {
                mSelectableTextHelper.resetSelectionInfo();
                mSelectableTextHelper.hideSelectView();
            }
        }

        @Override
        public void showTransDialog(String word, String seq) {
            ToastUtils.getInstance(context).showToast(word);
        }
    }

}
