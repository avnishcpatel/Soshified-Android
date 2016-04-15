package com.soshified.soshified.articles;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.soshified.soshified.R;
import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesRepository;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fragment that handles displaying a list of articles
 */
public class ArticlesFragment extends Fragment implements ArticlesContract.View {

    private ArticlesContract.Presenter mPresenter;

    @Bind(R.id.articles_list) RecyclerView mArticlesList;
    @Bind(R.id.articles_list_swipe_refresh) SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.articles_progress) ProgressBar mProgressBar;

    private ArticlesAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;


    private int mItemsVisible, mItemsTotal, mItemsPast;
    private boolean mLoadingItems;

    //Required empty constructor
    public ArticlesFragment() {}

    public static ArticlesFragment newInstance() {
        return new ArticlesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ArticlesAdapter(getContext(), (article, transitionPair) -> {
            //TODO Open Article
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.articles_fragment, group, false);
        ButterKnife.bind(this, view);

        mArticlesList.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(getActivity(), R.color.primary)));
        }

        mPresenter.init(ArticlesRepository.ARTICLE_TYPE_NEWS);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        mArticlesList.setItemAnimator(new DefaultItemAnimator());
        mArticlesList.setLayoutManager(mLayoutManager);

        //Setup scroll listener so we can add new pages
        mArticlesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) {

                    mItemsVisible = mLayoutManager.getChildCount();
                    mItemsTotal = mLayoutManager.getItemCount();
                    mItemsPast = mLayoutManager.findFirstVisibleItemPosition();

                    if(!mLoadingItems && (mItemsVisible + mItemsPast) >= mItemsTotal - 10) {
                        mLoadingItems = true;
                        mPresenter.fetchNewPage(false);
                    }
                }
            }
        });
    }

    @Override
    public void setupToolBar() {
        //Set refresh icon colour
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.primary));

        //Setup refresh listener
        mRefreshLayout.setOnRefreshListener(() -> mPresenter.fetchLatestArticles());
    }

    @Override
    public void addNewArticle(Article article) {
        mAdapter.addItemToStart(article);
        mAdapter.notifyItemInserted(0);
    }

    @Override
    public Article getRecentArticle() {
        return mAdapter.getArticle(0);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mRefreshLayout.post(() -> mRefreshLayout.setRefreshing(refreshing));
    }

    @Override
    public void addNewPage(List<Article> articles) {
        checkNotNull(articles);

        mAdapter.addPage(articles);
        mLoadingItems = false;

        // Hides initial loading indicator
        if (mProgressBar.getVisibility() == View.VISIBLE)
            mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(@NonNull ArticlesContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
