package com.soshified.soshified.articles;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soshified.soshified.R;
import com.soshified.soshified.data.Article;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.TextUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * List adapter for the ArticleList. Handles all the layout stuff as well as
 * the Activity transitions
 */
public class ArticlesAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ARTICLE = 0;
    private static final int TYPE_FOOTER = 1;

    private List<Article> mArticles = new ArrayList<>(0);
    private Context mContext;
    private ArticleClickListener mListener;

    public ArticlesAdapter(Context context, ArticleClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View footer = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.articles_list_progress_item, parent, false);
            return new ProgressViewHolder(footer);
        }

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.articles_list_item, parent, false);
        return new ArticleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_ARTICLE) {

            final ArticleViewHolder holder = (ArticleViewHolder) viewHolder;
            final Article article = mArticles.get(position);

            holder.mArticleTitle.setText(Html.fromHtml(article.getTitle()));

            holder.mArticleSubtitle.setText(TextUtils.formatStringRes(mContext,
                    R.string.post_subtitle, new String[]{article.getAuthorName(),
                            DateUtils.parseWordPressFormat(article.getDate())}));

            Picasso.with(mContext)
                    .load(TextUtils.validateImageUrl(article.getThumbnail()))
                    .error(R.color.primary)
                    .placeholder(R.color.primary_light)
                    .into(holder.mArticleImage);

            holder.itemView.setOnClickListener(v -> {
                Pair<View, String> p1 = Pair.create((View) holder.mArticleImage, "articleImage");

                mListener.onClick(article, p1);
            });
        } else if (getItemViewType(position) == TYPE_FOOTER) {
            ProgressViewHolder holder = (ProgressViewHolder) viewHolder;
            if (position > 0) {
                holder.mLoadingView.setVisibility(View.VISIBLE);
            } else {
                holder.mLoadingView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void addPage(List<Article> articles) {
        checkNotNull(articles);
        int currentCount = mArticles.size();

        if (mArticles.size() == 0)
            mArticles = articles;
        else
            mArticles.addAll(articles);
        notifyItemRangeInserted(currentCount + 1, articles.size());
    }

    public void addItemToStart(Article article) {
        mArticles.add(0, article);
    }

    public Article getArticle(int position) {
        return mArticles.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ARTICLE;
    }

    @Override
    public int getItemCount() {
        return mArticles.size() + 1;
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.article_list_item_title) TextView mArticleTitle;
        @Bind(R.id.article_list_item_subtitle) TextView mArticleSubtitle;
        @Bind(R.id.article_list_item_image) ImageView mArticleImage;

        View itemView;

        public ArticleViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.footer_loading_view)
        ProgressBar mLoadingView;

        View itemView;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ArticleClickListener {
        void onClick(Article article, Pair<View, String> transitionPair);
    }

}