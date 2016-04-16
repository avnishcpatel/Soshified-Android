package com.soshified.soshified.article;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soshified.soshified.R;
import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.data.source.local.LocalArticlesDataSource;
import com.soshified.soshified.data.source.remote.RemoteArticlesDataSource;
import com.soshified.soshified.util.AnimUtils;
import com.soshified.soshified.util.TextUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticleViewerActivity extends AppCompatActivity implements ArticleContract.View {

    private boolean mIsTitleVisible = false;
    private boolean mEnterComplete = false;

    ArticleContract.Presenter articlePresenter;

    @Bind(R.id.news_view_toolbar)  Toolbar mToolbar;
    @Bind(R.id.news_view_appbar) AppBarLayout mAppBarLayout;
    @Bind(R.id.news_view_collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.news_view_backdrop) ImageView mBackdrop;
    @Bind(R.id.news_view_backdrop_blur) ImageView mBlurredBackdrop;
    @Bind(R.id.news_view_title) TextView mTitle;
    @Bind(R.id.news_view_toolbar_title) TextView mToolbarTitle;
    @Bind(R.id.news_view_subtitle) TextView mSubTitle;
    @Bind(R.id.news_view_webView) WebView mWebView;
    @Bind(R.id.news_view_scrollView) NestedScrollView mScrollView;
    @Bind(R.id.news_view_progress) ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_viewer);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition.TransitionListener returnHomeListener = new AnimUtils.TransitionListenerAdapter(){

                @Override
                public void onTransitionStart(Transition transition) {
                    super.onTransitionStart(transition);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mEnterComplete) {
                        mScrollView.animate()
                                .alpha(0f)
                                .setDuration(100)
                                .setInterpolator(AnimationUtils.loadInterpolator(ArticleViewerActivity.this,
                                        android.R.interpolator.linear_out_slow_in));
                    }
                }
            };

            getWindow().getSharedElementReturnTransition().addListener(returnHomeListener);
        }

        int type = getIntent().getIntExtra("type", ArticlesRepository.ARTICLE_TYPE_NEWS);
        int articleID = getIntent().getIntExtra("article_id", 0);

        ArticlesRepository articlesRepository =
                ArticlesRepository.getInstance(RemoteArticlesDataSource.getInstance(type),
                        LocalArticlesDataSource.getInstance(type));

        new ArticlePresenter(articlesRepository, articleID, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Animates the WebView by translating from the bottom.
     * Lollipop onwards only.
     */
    private void animate(){
        Interpolator interpolator;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            interpolator = AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.fast_out_slow_in);
        } else {
            interpolator = AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.decelerate_cubic);
        }

        int offset = mTitle.getHeight();
        AnimUtils.doSimpleYAnimation(mTitle, offset, interpolator);

        offset *= 1.5f;
        AnimUtils.doSimpleYAnimation(mSubTitle, offset, interpolator);

        offset *= 1.5f;
        AnimUtils.doSimpleYAnimation(mWebView, offset, interpolator);

        mEnterComplete = true;
    }

    @Override
    public void setPresenter(@NonNull ArticleContract.Presenter presenter) {
        this.articlePresenter = presenter;
    }

    @Override
    public void loadHeaderImage(String imageUrl) {

        Picasso.with(this)
                .load(TextUtils.validateImageUrl(imageUrl))
                .error(R.color.primary)
                .placeholder(R.color.primary_light)
                .into(mBackdrop, new Callback() {
                    @Override
                    public void onSuccess() {

                        Drawable drawable = mBackdrop.getDrawable();

                        if (drawable instanceof BitmapDrawable) {
                            BitmapDrawable mBlurredBitmap = AnimUtils.blur(ArticleViewerActivity.this,
                                    ((BitmapDrawable) drawable).getBitmap());
                            mBlurredBackdrop.setBackground(mBlurredBitmap);
                        }

                        //Alter the Status Bar if using Lollipop or higher
                        if (Build.VERSION.SDK_INT >= 21) {
                            getWindow().setStatusBarColor(Color.argb(75, 0, 0, 0));
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void setupToolbar() {
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            //Hides/Shows toolbar title depending on scroll amount
            float percentage = (float) Math.abs(verticalOffset) /
                    (float) appBarLayout.getTotalScrollRange();

            mBlurredBackdrop.setAlpha(percentage);
            if (percentage >= 0.9f){

                if(!mIsTitleVisible) {
                    AnimUtils.startAlphaAnimation(mToolbarTitle, 200, View.VISIBLE);
                    mIsTitleVisible = true;
                }
            } else {
                if(mIsTitleVisible) {
                    AnimUtils.startAlphaAnimation(mToolbarTitle, 200, View.INVISIBLE);
                    mIsTitleVisible = false;
                }
            }

        });

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout.setContentScrimColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setStatusBarScrimColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setTitleEnabled(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void loadPostContent(String postContent) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadDataWithBaseURL("http://soshified.com", postContent, "text/html", "UTF-8", null);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                animate();

            }
        });
    }

    @Override
    public void loadPostMeta(String title, String author, String date) {
        mTitle.setText(title);
        mSubTitle.setText(TextUtils.formatStringRes(ArticleViewerActivity.this,
                R.string.post_subtitle, new String[]{author, date}));


        String postTitle = TextUtils.fromHtml(title);
        mToolbarTitle.setText(postTitle);
    }
}
