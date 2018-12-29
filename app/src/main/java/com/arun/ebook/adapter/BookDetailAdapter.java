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
import com.arun.ebook.selectable.OnSelectListener;
import com.arun.ebook.selectable.SelectableTextHelper;
import com.arun.ebook.widget.JustifyTextView;

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

    private static class BookContentHolder extends RecyclerView.ViewHolder {
        private JustifyTextView contentView;
        private TextView pageText;
        private SelectableTextHelper mSelectableTextHelper;

        private BookContentHolder(Context context,View itemView) {
            super(itemView);
            contentView = itemView.findViewById(R.id.contentView);
            pageText = itemView.findViewById(R.id.pageText);
            /*mSelectableTextHelper = new SelectableTextHelper.Builder(contentView)
                    .setSelectedColor(context.getResources().getColor(R.color.text_green))
                    .setCursorHandleSizeInDp(20)
                    .setCursorHandleColor(context.getResources().getColor(R.color.red))
                    .build();

            mSelectableTextHelper.setSelectListener(new OnSelectListener() {
                @Override
                public void onTextSelected(CharSequence content) {

                }
            });*/
        }

        private void setData(BookDetailBean bean) {
            if (bean != null) {
                contentView.setText(bean.content);
                pageText.setText((bean.currentPage + 1) + "/" + bean.totalPage);
            }
        }
    }

}
