package com.soshified.soshified.view.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soshified.soshified.R;
import com.soshified.soshified.model.Article;
import com.soshified.soshified.model.ArticleList;
import com.soshified.soshified.modules.ArticleListModule;
import com.soshified.soshified.presenter.ArticleListPresenter;
import com.soshified.soshified.presenter.ArticleListPresenterImpl;
import com.soshified.soshified.ui.Adapters.ArticleAdapter;
import com.soshified.soshified.ui.Adapters.HeaderRecyclerViewAdapter;
import com.soshified.soshified.view.ArticleListView;
import com.soshified.soshified.view.activity.MainActivity;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment that handles displaying a list of news articles
 */
public class ArticleListFragment extends BaseFragment implements ArticleListView {

    @Inject ArticleListPresenter mArticleListPresenter;

    @Bind(R.id.news_list) RecyclerView mNewsList;
    @Bind(R.id.news_list_swipe_refresh) SwipeRefreshLayout mRefreshLayout;

    private HeaderRecyclerViewAdapter mAdapter;
    private LinearLayoutManager layoutManager;


    private int mItemsVisible, mItemsTotal, mItemsPast;
    private boolean mLoadingItems;

    //Required empty constructor
    public ArticleListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, group, false);
        ButterKnife.bind(this, view);

        mArticleListPresenter.init(ArticleListPresenterImpl.ARTICLE_TYPE_NEWS);

        return view;
    }

    @Override
    public void loadArticles(ArticleList articleList) {

        MainActivity.getInstance().toggleProgress();

        mAdapter = new HeaderRecyclerViewAdapter(new ArticleAdapter((AppCompatActivity)
                getActivity(), articleList));
        mNewsList.setAdapter(mAdapter);
    }

    @Override
    protected List<ArticleListModule> getModules() {
        return Collections.singletonList(new ArticleListModule(this));
    }

    @Override
    public void setupRecyclerView() {

        layoutManager = new LinearLayoutManager(getContext());
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
                        mArticleListPresenter.fetchNewPage();
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
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mArticleListPresenter.fetchLatestArticles();
            }
        });
    }

    @Override
    public void refreshCompleted(boolean success, ArticleList articleList) {

        if(success){
            int topPostId = articleList.posts.get(0).id;
            Stack<Article> newArticles = articleList.posts;
            Collections.reverse(newArticles);

            for(Article article : newArticles){
                if(article.id != topPostId){
                    mAdapter.addItemToDatasetStart(article);
                    mAdapter.notifyItemInserted(0);
                }
            }
            mRefreshLayout.setRefreshing(false);

        } else {
            //TODO Notify user of failure
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void addNewPage(boolean success, ArticleList articleList) {

        if(success){
            Stack<Article> mPage = articleList.posts;
            mAdapter.addPage(mPage);

            mAdapter.notifyDataSetChanged();
            mLoadingItems = false;
        }

    }
}
