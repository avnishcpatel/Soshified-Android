package com.soshified.soshified.data.source;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.local.RealmArticle;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Func1;

/**
 * Implementation that loads articles from either Soshified Servers or a local database.
 * Loads Articles into a cache which is updated dynamically.
 */
public class ArticlesRepository implements ArticlesDataSource {

    private static ArticlesRepository INSTANCE;

    private ArticlesDataSource mRemoteDataSource;
    private ArticlesDataSource mLocalDataSource;

    private HashMap<Integer, Article> mCachedArticles = new HashMap<>();

    private boolean mCacheIsValid = true;

    private ArticlesRepository(@NonNull ArticlesDataSource remoteDataSource,
                              @NonNull ArticlesDataSource localDataSource) {
        this.mLocalDataSource = localDataSource;
        this.mRemoteDataSource = remoteDataSource;

    }

    public static ArticlesRepository getInstance(@NonNull ArticlesDataSource remoteDataSource,
                                                 @NonNull ArticlesDataSource localDataSource) {
        if (INSTANCE == null)
            INSTANCE = new ArticlesRepository(remoteDataSource, localDataSource);
        return INSTANCE;
    }

    public void invalidateCache() {
        mCacheIsValid = false;
    }

    @Override
    public Observable<List<Article>> getPageObservable(int page) {
        Observable<List<Article>> cachedArticles = Observable.from(mCachedArticles.values()).toList();
        Observable<List<Article>> remoteArticles = mRemoteDataSource.getPageObservable(page);
        Observable<List<Article>> localArticles = mLocalDataSource.getPageObservable(page);

        Observable<List<Article>> remoteArticlesWithLocalUpdate = remoteArticles
                .flatMap(Observable::from)
                .doOnNext(this::saveArticle)
                .toList();

        if (!mCacheIsValid)
            return remoteArticlesWithLocalUpdate;

        return Observable
                .concat(cachedArticles, localArticles, remoteArticlesWithLocalUpdate)
                .filter(articles -> articles.size() != 0)
                .first();
    }

    @Override
    public void saveArticle(Article article) {
        mCachedArticles.put(article.getId(), article);
        mLocalDataSource.saveArticle(article);
    }
}
