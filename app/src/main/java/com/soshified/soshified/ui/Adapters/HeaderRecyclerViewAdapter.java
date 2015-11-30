package com.soshified.soshified.ui.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.soshified.soshified.model.Article;

import java.util.Stack;

/**
 * Wrapper adapter that adds the ability to add headers and footers to RecyclerView
 */
public class HeaderRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int TYPE_HEADER = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;
    private static final int TYPE_ADAPTEE_OFFSET = 2;

    private final RecyclerView.Adapter mAdaptee;


    public HeaderRecyclerViewAdapter(RecyclerView.Adapter adaptee) {
        mAdaptee = adaptee;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mAdaptee instanceof HeaderRecyclerView) {
            return ((HeaderRecyclerView) mAdaptee).onCreateHeaderViewHolder(parent, viewType);
        } else if (viewType == TYPE_FOOTER && mAdaptee instanceof FooterRecyclerView) {
            return ((FooterRecyclerView) mAdaptee).onCreateFooterViewHolder(parent, viewType);
        }
        return mAdaptee.onCreateViewHolder(parent, viewType - TYPE_ADAPTEE_OFFSET);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0 && holder.getItemViewType() == TYPE_HEADER && useHeader()) {
            ((HeaderRecyclerView) mAdaptee).onBindHeaderView(holder, position);
        } else if (position == mAdaptee.getItemCount() && holder.getItemViewType() == TYPE_FOOTER && useFooter()) {
            ((FooterRecyclerView) mAdaptee).onBindFooterView(holder, position);
        } else {
            mAdaptee.onBindViewHolder(holder, position - (useHeader() ? 1 : 0));
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = mAdaptee.getItemCount();
        if (useHeader()) {
            itemCount += 1;
        }
        if (useFooter()) {
            itemCount += 1;
        }
        return itemCount;
    }

    private boolean useHeader() {
        return mAdaptee instanceof HeaderRecyclerView;
    }

    private boolean useFooter() {
        return mAdaptee instanceof FooterRecyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && useHeader()) {
            return TYPE_HEADER;
        }
        if (position == mAdaptee.getItemCount() && useFooter()) {
            return TYPE_FOOTER;
        }
        return mAdaptee.getItemViewType(position) + TYPE_ADAPTEE_OFFSET;
    }

    public void addPage(Stack<Article> mPage) {
        ((ArticleAdapter)mAdaptee).addPage(mPage);
    }

    public void addItemToDatasetStart(Article article) {
        ((ArticleAdapter)mAdaptee).addItemToDatasetStart(article);
    }


    public interface HeaderRecyclerView {
        RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType);

        void onBindHeaderView(RecyclerView.ViewHolder holder, int position);
    }

    public interface FooterRecyclerView {
        RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType);

        void onBindFooterView(RecyclerView.ViewHolder holder, int position);
    }

}