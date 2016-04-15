package com.soshified.soshified.articles;

import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.data.source.local.RealmArticle;
import com.soshified.soshified.util.DateUtils;

import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleListView
 */
public class ArticlesPresenter implements ArticlesContract.Presenter {

    public static final int ARTICLE_TYPE_NEWS = 0;
    public static final int ARTICLE_TYPE_STYLE = 1;
    public static final int ARTICLE_TYPE_SUBS = 2;

    private int mLastRequestedPage;
    private CompositeSubscription mSubscriptions;

    private final ArticlesContract.View mArticlesView;
    private final ArticlesRepository mArticlesRepository;

    public ArticlesPresenter(ArticlesRepository articlesRepository, ArticlesContract.View articleListView) {
        this.mArticlesRepository = checkNotNull(articlesRepository);
        this.mArticlesView = checkNotNull(articleListView);
        this.mArticlesView.setPresenter(this);
        this.mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void init(int type) {
        mArticlesView.setupRecyclerView();
        mArticlesView.setupToolBar();
    }

    @Override
    public void subscribe() {
        fetchNewPage(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void fetchLatestArticles() {
        long mostRecentDate = DateUtils.getUnixTimeStamp(mArticlesView.getRecentArticle().getDate());
        mArticlesRepository.getPageObservable(1)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .take(10)
                .filter(article -> {
                    long articleDate = DateUtils.getUnixTimeStamp(article.getDate());
                    return articleDate > mostRecentDate;
                })
                .finallyDo(mArticlesView::hideRefreshing)
                .subscribe(mArticlesView::addNewArticle);
    }

    @Override
    public void fetchNewPage(boolean forceReload) {
        mLastRequestedPage += 1;

        if (forceReload || Realm.getDefaultInstance().where(RealmArticle.class).count() == 0) {
            mArticlesRepository.invalidateCache();
        }

        Subscription articleSubscription = mArticlesRepository.getPageObservable(mLastRequestedPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mArticlesView::addNewPage);

        mSubscriptions.add(articleSubscription);

    }
}
