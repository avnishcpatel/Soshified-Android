package com.soshified.soshified.articles;

import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.util.DateUtils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleListView
 */
public class ArticlesPresenter implements ArticlesContract.Presenter {

    public static final int ARTICLE_TYPE_NEWS = 0;
    public static final int ARTICLE_TYPE_STYLE = 1;
    public static final int ARTICLE_TYPE_SUBS = 2;

    private int mLastRequestedPage;

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
        fetchNewPage();
    }

    @Override
    public void fetchLatestArticles() {
        long mostRecentDate = DateUtils.getUnixTimeStamp(mArticlesView.getRecentArticle().getDate());
        mArticlesRepository.getRecentObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .filter(article -> {
                    long articleDate = DateUtils.getUnixTimeStamp(article.getDate());
                    return articleDate > mostRecentDate;
                })
                .finallyDo(mArticlesView::hideRefreshing)
                .subscribe(mArticlesView::addNewArticle);
    }

    @Override
    public void fetchNewPage() {
        mLastRequestedPage += 1;

        mArticlesRepository.getPageObservable(mLastRequestedPage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mArticlesView::addNewPage);
    }
}
