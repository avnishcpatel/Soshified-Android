package com.soshified.soshified.articles;

import com.soshified.soshified.data.source.ArticlesDataSource;
import com.soshified.soshified.data.source.ArticlesRepository;
import com.soshified.soshified.data.source.local.RealmArticle;

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
class ArticlesPresenter implements ArticlesContract.Presenter {

    private int mLastRequestedPage = 1;
    private CompositeSubscription mSubscriptions;
    private boolean mFirstLaunch = true;

    private ArticlesContract.View mArticlesView;
    private final ArticlesRepository mArticlesRepository;

    ArticlesPresenter(ArticlesRepository articlesRepository, ArticlesContract.View articleListView) {
        this.mArticlesRepository = checkNotNull(articlesRepository);
        this.mSubscriptions = new CompositeSubscription();
        setView(articleListView);
    }

    @Override
    public void init() {
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
        long mostRecentDate = mArticlesView.getRecentArticle().getDate();
        mArticlesView.setRefreshing(true);

        mArticlesRepository.invalidateCache();
        mArticlesRepository.getPageObservable(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .take(10)
                .filter( article -> (article.getDate() > mostRecentDate) )
                .doOnTerminate(() -> mArticlesView.setRefreshing(false))
                .doOnError(throwable -> mArticlesView.setRefreshing(false))
                .subscribe(mArticlesView::addNewArticle);
    }

    @Override
    public void fetchNewPage(boolean forceReload) {

        long localArticles = Realm.getDefaultInstance().where(RealmArticle.class).count();

        if (forceReload || localArticles < 25) {
            mArticlesRepository.invalidateCache();
        }

        Subscription articleSubscription = mArticlesRepository.getPageObservable(mLastRequestedPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articles -> {
                    mLastRequestedPage += 1;
                    mArticlesView.addNewPage(articles);
                    if (localArticles != 0 && mFirstLaunch) {
                        fetchLatestArticles();
                        mFirstLaunch = false;
                    }
                });

        mSubscriptions.add(articleSubscription);

    }

    @Override
    public void setSource(ArticlesDataSource.Article_Type source) {
        mArticlesRepository.setSource(source);
        mLastRequestedPage = 1;
    }

    @Override
    public void setView(ArticlesContract.View view) {
        mArticlesView = checkNotNull(view);
        mArticlesView.setPresenter(this);
    }
}
