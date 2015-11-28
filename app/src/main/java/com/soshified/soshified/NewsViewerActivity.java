package com.soshified.soshified;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.Html;
import android.transition.Transition;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soshified.soshified.objects.Post;
import com.soshified.soshified.util.AnimUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsViewerActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener{

    //TODO Fix videos playing after close

    private Post mPost;
    private boolean mIsTitleVisible = false;
    private boolean mEnterComplete = false;

    private Bitmap headerImageBitmap;

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
        mPost = getIntent().getParcelableExtra("post");

        ButterKnife.bind(this);

        mWebView.setAlpha(0f);
        mTitle.setAlpha(0f);
        mSubTitle.setAlpha(0f);

        mAppBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String postTitle = Html.fromHtml(mPost.title).toString();
        mToolbarTitle.setText(postTitle);
        mCollapsingToolbarLayout.setContentScrimColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setStatusBarScrimColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setTitleEnabled(false);

        mTitle.setText(postTitle);

        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat toFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        try {
            Date date = fromFormat.parse(mPost.date);
            String subtitle = "Posted by " + mPost.mAuthor + " on " + toFormat.format(date);
            mSubTitle.setText(subtitle);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String mImageUrl;

        if(mPost.mThumbnail.contains(" ")){
            mImageUrl = mPost.mThumbnail.replaceAll(" ", "%20");
        } else {
            mImageUrl = mPost.mThumbnail;
        }

        Picasso.with(this)
                .load(mImageUrl)
                .error(R.color.primary)
                .placeholder(R.color.primary_light)
                .into(mBackdrop, new Callback() {
                    @Override
                    public void onSuccess() {

                        Drawable drawable = mBackdrop.getDrawable();

                        if (drawable instanceof BitmapDrawable) {

                            headerImageBitmap = ((BitmapDrawable) drawable).getBitmap();
                            blur();
                        }

                        //Alter the statusbar if using Lollipop or higher
                        if (Build.VERSION.SDK_INT >= 21) {
                            getWindow().setStatusBarColor(Color.argb(75, 0, 0, 0));
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });

        new ParseNews().execute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition.TransitionListener returnHomeListener = new AnimUtils.TransitionListenerAdapter(){

                @Override
                public void onTransitionStart(Transition transition) {
                    super.onTransitionStart(transition);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mEnterComplete) {
                        mScrollView.animate()
                                .alpha(0f)
                                .setDuration(100)
                                .setInterpolator(AnimationUtils.loadInterpolator(NewsViewerActivity.this,
                                        android.R.interpolator.linear_out_slow_in));
                    }
                }
            };

            getWindow().getSharedElementReturnTransition().addListener(returnHomeListener);
        }
    }



    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

        //Hides/Shows toolbar title depending on scroll amount
        float percentage = (float) Math.abs(i) / (float) appBarLayout.getTotalScrollRange();

        mBlurredBackdrop.setAlpha(percentage);
        if (percentage >= 0.9f){

            if(!mIsTitleVisible) {
                startAlphaAnimation(mToolbarTitle, 200, View.VISIBLE);
                mIsTitleVisible = true;
            }
        } else {
            if(mIsTitleVisible) {
                startAlphaAnimation(mToolbarTitle, 200, View.INVISIBLE);
                mIsTitleVisible = false;
            }
        }

    }

    /**
     * Animates the WebView by rotating it 15 degrees and translating from the bottom.
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
        doAnimation(mTitle, offset, interpolator);

        offset *= 1.5f;
        doAnimation(mSubTitle, offset, interpolator);

        offset *= 1.5f;
        doAnimation(mWebView, offset, interpolator);

        mEnterComplete = true;
    }

    private void doAnimation(View view, float offset, Interpolator interpolator) {
        view.setTranslationY(offset);
        view.setAlpha(0.8f);
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(interpolator)
                .start();

    }

    /**
     * Blurs the header image for when the AppBar collapses
     */
    private void blur() {
        int width = Math.round(headerImageBitmap.getWidth() * 0.1f);
        int height = Math.round(headerImageBitmap.getHeight() * 0.1f);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(headerImageBitmap, width, height, false);
        Bitmap finalBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript renderScript = RenderScript.create(this);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, finalBitmap);
        intrinsicBlur.setRadius(15.5f);
        intrinsicBlur.setInput(tmpIn);
        intrinsicBlur.forEach(tmpOut);
        tmpOut.copyTo(finalBitmap);

        mBlurredBackdrop.setBackground(new BitmapDrawable(getResources(), finalBitmap));

    }

    /**
     * Animates the provided view from invisible to visible
     *
     * @param v View to be animated
     * @param duration Duration of animation
     * @param visibility Whether to be Invisible or Visible
     */
    private static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    /**
     * Parses the mPosts content to remove things and fix some things
     */
    class ParseNews extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Document html = Jsoup.parse(mPost.content);

            html.body().attr("style", "color: #444444");

            if(html.select("img").first() != null)
                html.select("img").first().remove();

            if(html.select("br").first() != null)
                html.select("br").first().remove();

            if(html.select("p").last() != null)
                html.select("p").last().remove();

            Elements images = html.select("img");
            images.attr("style", "max-width:100%; margin: 10px 0px");
            images.attr("height", "auto");

            Elements iframes = html.select("iframe");
            iframes.attr("style", "max-width:100%; max-height: auto; margin: 10px 0px");

            return html.html();
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadDataWithBaseURL("http://soshified.com", s, "text/html", "UTF-8", null);
            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mProgressBar.setVisibility(View.GONE);
                    animate();

                }
            });
        }
    }

}
