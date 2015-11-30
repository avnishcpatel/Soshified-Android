package com.soshified.soshified.ui.Adapters;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soshified.soshified.R;
import com.soshified.soshified.model.Article;
import com.soshified.soshified.model.ArticleList;
import com.soshified.soshified.util.DateUtils;
import com.soshified.soshified.util.TextUtils;
import com.soshified.soshified.view.activity.ArticleViewerActivity;
import com.squareup.picasso.Picasso;

import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * List adapter for the ArticleList. Handles all the layout stuff as well as
 * the Activity transitions
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>
        implements HeaderRecyclerViewAdapter.FooterRecyclerView{

    private Stack<Article> mDataset = new Stack<>();
    private AppCompatActivity mActivity;
    private int mCountTotal;

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.news_list_item_title)
        TextView mNewsTitle;
        @Bind(R.id.news_list_item_subtitle) TextView mNewsSubtitle;
        @Bind(R.id.news_list_item_image)
        ImageView mNewsImage;

        View itemView;

        public ViewHolder(View itemView) {
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

    public ArticleAdapter(AppCompatActivity activity, ArticleList posts) {
        super();
        this.mDataset = posts.posts;
        this.mActivity = activity;
        this.mCountTotal = posts.count_total;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item,
                parent, false);

        return new ViewHolder(v);
    }

    public void addPage(Stack<Article> mPage) {
        mDataset.addAll(mPage);
    }

    public void addItemToDatasetStart(Article article) {
        mDataset.add(0, article);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Article article = mDataset.get(position);

        holder.mNewsTitle.setText(Html.fromHtml(article.title));

        holder.mNewsSubtitle.setText(TextUtils.formatStringRes(mActivity,
                R.string.post_subtitle, new String[]{article.getAuthor(),
                        DateUtils.parseWordPressFormat(article.date)}));

        Picasso.with(mActivity)
                .load(TextUtils.validateImageUrl(article.getImageUrl()))
                .error(R.color.primary)
                .placeholder(R.color.primary_light)
                .into(holder.mNewsImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ArticleViewerActivity.class);
                intent.putExtra("article", article);
                Pair<View, String> p1 = Pair.create((View) holder.mNewsImage, "newsImage");
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(mActivity, p1);
                mActivity.startActivity(intent, options.toBundle());
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_progress_item, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {
        ((ProgressViewHolder)holder).mLoadingView.animate();
        ((ProgressViewHolder)holder).mLoadingView.setVisibility(View.VISIBLE);
        if(mDataset.size() >= mCountTotal) {
            ((ProgressViewHolder)holder).mLoadingView.setVisibility(View.INVISIBLE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}