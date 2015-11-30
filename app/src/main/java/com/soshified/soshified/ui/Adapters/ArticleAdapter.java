package com.soshified.soshified.ui.Adapters;

import android.content.Context;
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
import com.soshified.soshified.model.Post;
import com.soshified.soshified.model.PostList;
import com.soshified.soshified.view.activity.NewsViewerActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * List adapter for the NewsList. Handles all the layout stuff as well as
 * the Activity transitions
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>
        implements HeaderRecyclerViewAdapter.FooterRecyclerView{

    private Stack<Post> mDataset = new Stack<>();
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

    public ArticleAdapter(AppCompatActivity activity, PostList posts) {
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

    public void addPage(Stack<Post> mPage) {
        mDataset.addAll(mPage);
    }

    public void addItemToDatasetStart(Post post) {
        mDataset.add(0, post);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Post post = mDataset.get(position);

        holder.mNewsTitle.setText(Html.fromHtml(post.title));

        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.ENGLISH);
        SimpleDateFormat toFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        try {
            Date date = fromFormat.parse(post.date);
            String subtitle = "Posted by " + post.getAuthor() + " on " + toFormat.format(date);
            holder.mNewsSubtitle.setText(subtitle);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String mImageUrl;

        if(post.getImageUrl() != null && post.getImageUrl().contains(" ")){
            mImageUrl = post.getImageUrl().replaceAll(" ", "%20");
        } else {
            mImageUrl = post.getImageUrl();
        }

        Picasso.with(mActivity)
                .load(mImageUrl)
                .error(R.color.primary)
                .placeholder(R.color.primary_light)
                .into(holder.mNewsImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, NewsViewerActivity.class);
                intent.putExtra("post", post);
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