package com.soshified.soshified.articles;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesDataSource;
import com.soshified.soshified.data.source.ArticlesRepository;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleListView
 */
public class ArticlesPresenter implements ArticlesContract.Presenter {

    public static final int ARTICLE_TYPE_NEWS = 0;
    public static final int ARTICLE_TYPE_STYLE = 1;
    public static final int ARTICLE_TYPE_SUBS = 2;

    private int mLastRequestedPage = 1;

    private final ArticlesContract.View mArticlesView;
    private final ArticlesRepository mArticlesRepository;

    public ArticlesPresenter(ArticlesRepository articlesRepository, ArticlesContract.View articleListView) {
        this.mArticlesRepository = checkNotNull(articlesRepository);
        this.mArticlesView = checkNotNull(articleListView);
        this.mArticlesView.setPresenter(this);
    }

    @Override
    public void init(int type) {
        mArticlesView.setupRecyclerView();
        mArticlesView.setupToolBar();
        fetchArticles();
    }

    @Override
    public void fetchArticles() {
        mArticlesRepository.getPage(mLastRequestedPage, new ArticlesDataSource.PageLoadCallback() {
            @Override
            public void onPageLoaded(ArrayList<Article> articles) {
                mArticlesView.addNewPage(articles);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void fetchLatestArticles() {
        mArticlesRepository.getRecent(new ArticlesDataSource.PageLoadCallback() {
            @Override
            public void onPageLoaded(ArrayList<Article> articles) {
                mArticlesView.refreshCompleted(articles);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void fetchNewPage() {
        mLastRequestedPage += 1;

        mArticlesRepository.getPage(mLastRequestedPage, new ArticlesDataSource.PageLoadCallback() {
            @Override
            public void onPageLoaded(ArrayList<Article> articles) {
                mArticlesView.addNewPage(articles);
            }

            @Override
            public void onError() {

            }
        });
    }
}
