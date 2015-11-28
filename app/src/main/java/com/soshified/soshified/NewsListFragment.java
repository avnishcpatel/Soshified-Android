package com.soshified.soshified;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.soshified.soshified.objects.Post;
import com.soshified.soshified.objects.Posts;
import com.soshified.soshified.util.HeaderRecyclerViewAdapter;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Fragment that handles displaying a list of news articles
 */
public class NewsListFragment extends Fragment {

    @Bind(R.id.news_list) RecyclerView mNewsList;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.news_list_swipe_refresh) SwipeRefreshLayout mRefreshLayout;

    private HeaderRecyclerViewAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private Context mContext;


    private int mItemsVisible, mItemsTotal, mItemsPast, mCountTotal;
    private int mLastRequestedPage = 1;
    private boolean mLoadingItems;

    private RestAdapter mRestAdapter;

    //Required empty constructor
    public NewsListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, group, false);
        ButterKnife.bind(this, view);


        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle(mContext.getResources().getString(R.string.news_title));

        layoutManager = new LinearLayoutManager(mContext);
        mNewsList.setItemAnimator(new DefaultItemAnimator());
        mNewsList.setLayoutManager(layoutManager);

        //Setup scroll listener so we can add new pages
        mNewsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {


                    mItemsVisible = layoutManager.getChildCount();
                    mItemsTotal = layoutManager.getItemCount();
                    mItemsPast = layoutManager.findFirstVisibleItemPosition();

                    if(!mLoadingItems && (mItemsVisible + mItemsPast) >= mItemsTotal - 10) {
                        mLoadingItems = true;
                        GetPagedRequest request = mRestAdapter.create(GetPagedRequest.class);
                        mLastRequestedPage += 1;
                        request.newsList(mLastRequestedPage, new Callback<Posts>() {
                            @Override
                            public void success(Posts posts, Response response) {
                                Stack<Post> mPage = posts.posts;
                                mAdapter.addPage(mPage);

                                mAdapter.notifyDataSetChanged();
                                mLoadingItems = false;

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                            }
                        });
                    }
                }
            }
        });

        //Set refresh icon colour
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.primary));

        //Setup refresh listener
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetRecentRequest request = mRestAdapter.create(GetRecentRequest.class);
                request.newsList(new Callback<Posts>() {
                    @Override
                    public void success(Posts posts, Response response) {

                        int topPostId = posts.posts.get(0).id;
                        Stack<Post> newPosts = posts.posts;
                        Collections.reverse(newPosts);

                        for(Post post : newPosts){
                            if(post.id != topPostId){
                                mAdapter.addItemToDatasetStart(post);
                                mAdapter.notifyItemInserted(0);
                            }
                        }
                        mRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        //Setting up Retrofit adapter
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint("http://soshified.com/json")
                .build();

        GetPagedRequest request = mRestAdapter.create(GetPagedRequest.class);
        request.newsList(mLastRequestedPage, new Callback<Posts>() {
            @Override
            public void success(Posts posts, Response response) {
                Stack<Post> mDataset = posts.posts;
                mCountTotal = posts.count_total;

                MainActivity.getInstance().toggleProgress();

                mAdapter = new HeaderRecyclerViewAdapter(new NewsAdapter(mDataset));
                mNewsList.setAdapter(mAdapter);

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * GET Request to fetch 'pages' of posts. Should return a page of 25 posts
     */
    private interface GetPagedRequest {
        @GET("/get_posts?count=25")
        void newsList(@Query("page") int page, Callback<Posts> callback);
    }

    /**
     * GET Request to fetch most recent posts. Used when refreshing.
     */
    private interface GetRecentRequest {
        @GET("/get_recent_posts")
        void newsList(Callback<Posts> callback);
    }

    /**
     * List adapter for the NewsList. Handles all the layout stuff as well as
     * the Activity transitions
     */
    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>
        implements HeaderRecyclerViewAdapter.FooterRecyclerView{

        private Stack<Post> mDataset = new Stack<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.news_list_item_title) TextView mNewsTitle;
            @Bind(R.id.news_list_item_subtitle) TextView mNewsSubtitle;
            @Bind(R.id.news_list_item_image) ImageView mNewsImage;

            View itemView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                ButterKnife.bind(this, itemView);
            }
        }

        public class ProgressViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.footer_loading_view) AVLoadingIndicatorView mLoadingView;

            View itemView;

            public ProgressViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                ButterKnife.bind(this, itemView);
            }
        }

        public NewsAdapter(Stack<Post> mDataset) {
            super();
            this.mDataset = mDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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

            if(post.getImageUrl().contains(" ")){
                 mImageUrl = post.getImageUrl().replaceAll(" ", "%20");
            } else {
                mImageUrl = post.getImageUrl();
            }

            Picasso.with(mContext)
                    .load(mImageUrl)
                    .error(R.color.primary_dark)
                    .placeholder(R.color.primary_light)
                    .into(holder.mNewsImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NewsViewerActivity.class);
                    intent.putExtra("post", post);
                    Pair<View, String> p1 = Pair.create((View) holder.mNewsImage, "newsImage");
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(getActivity(), p1);
                    getActivity().startActivity(intent, options.toBundle());
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

}
