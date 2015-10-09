package com.soshified.soshified;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soshified.soshified.objects.Post;
import com.soshified.soshified.objects.Posts;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/**
 * Fragment that handles displaying a list of news articles
 */
public class NewsListFragment extends Fragment {

    @Bind(R.id.news_list) RecyclerView mNewsList;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    private NewsAdapter mAdapter;
    private Context mContext;

    //Required empty constructor
    public NewsListFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, group, false);
        ButterKnife.bind(this, view);


        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setTitle(mContext.getResources().getString(R.string.news_title));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mNewsList.setLayoutManager(layoutManager);
        mNewsList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 200 && mAdapter != null){
                    mAdapter.canAnimate(false);
                } else if (mAdapter != null) {
                    mAdapter.canAnimate(true);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting up Retrofit adapter
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://www.soshified.com/api")
                .build();

        GetLatestRequest request = restAdapter.create(GetLatestRequest.class);
        request.newsList(new Callback<Posts>() {
            @Override
            public void success(Posts posts, Response response) {
                Stack<Post> mDataset = posts.posts;

                mAdapter = new NewsAdapter(mDataset);
                mNewsList.setAdapter(mAdapter);

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    /**
     * GET Request to fetch posts. The response should return the first 25 posts
     */
    private interface GetLatestRequest {
        @GET("/get_posts/?count=25")
        void newsList(Callback<Posts> callback);
    }

    /**
     * List adapter for the NewsList. Handles all the layout stuff as well as
     * the Activity transitions
     */
    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
        private Stack<Post> mDataset = new Stack<>();
        private boolean mCanAnimate = true;
        private int mLastAnimated = -1;

        public  class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.news_title) TextView mNewsTitle;
            @Bind(R.id.news_subtitle) TextView mNewsSubtitle;
            @Bind(R.id.news_image) ImageView mNewsImage;
            @Bind(R.id.news_info) RelativeLayout mNewsInfo;
            View itemView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                ButterKnife.bind(this, itemView);
            }
        }

        public NewsAdapter(Stack<Post> mDataset) {
            super();
            this.mDataset = mDataset;
        }

        public void canAnimate(boolean canAnim){
            mCanAnimate = canAnim;
        }


        // Create new views (invoked by the layout manager)
        @Override
        public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item,
                    parent, false);

            return new ViewHolder(v);
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

            Picasso.with(mContext)
                    .load(post.getImageUrl())
                    .error(R.color.error_color)
                    .placeholder(R.color.primary_light)
                    .into(holder.mNewsImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NewsViewerActivity.class);
                    intent.putExtra("post", post);
                    Pair<View, String> p1 = Pair.create((View) holder.mNewsImage, "newsImage");
                    Pair<View, String> p2 = Pair.create((View) holder.mNewsInfo, "newsInfo");
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(getActivity(), p1, p2);
                    getActivity().startActivity(intent, options.toBundle());
                }
            });


            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);

            if(position > mLastAnimated) {
                if(mCanAnimate) holder.itemView.startAnimation(animation);
                mLastAnimated = position;
            }


        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}
