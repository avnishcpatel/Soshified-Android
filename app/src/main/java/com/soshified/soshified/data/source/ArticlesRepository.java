package com.soshified.soshified.data.source;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.soshified.soshified.data.Article;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;

import static com.soshified.soshified.util.ArrayUtils.asList;

/**
 * Implementation that loads articles from either Soshified Servers or a local database.
 * Loads Articles into a cache which is updated dynamically.
 */
public class ArticlesRepository implements ArticlesDataSource {

    private static ArticlesRepository INSTANCE;

    private ArticlesDataSource mRemoteDataSource;
    private ArticlesDataSource mLocalDataSource;


    private static Article_Type mCurrentType;

    private static List<SparseArray<Article>> mCachedArticles = new LinkedList<>();

    private boolean mCacheIsValid = true;

    private ArticlesRepository(@NonNull ArticlesDataSource remoteDataSource,
                              @NonNull ArticlesDataSource localDataSource) {
        this.mLocalDataSource = localDataSource;
        this.mRemoteDataSource = remoteDataSource;

        for (Article_Type t : Article_Type.values()) {
            mCachedArticles.add(new SparseArray<>());
        }

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
        Observable<List<Article>> cachedArticles = Observable.from(asList(mCachedArticles.get(mCurrentType.ordinal()))).toList();
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
        mCachedArticles.get(mCurrentType.ordinal()).put(article.getId(), article);
        mLocalDataSource.saveArticle(article);
    }

    @Override
    public void setSource(Article_Type sourceType) {
        invalidateCache();
        mCurrentType = sourceType;
        mRemoteDataSource.setSource(sourceType);
        mLocalDataSource.setSource(sourceType);
    }

    @Override
    public Observable<Article> getArticleObservable(int id) {
        Observable<Article> cachedArticle = Observable.just(mCachedArticles.get(mCurrentType.ordinal()).get(id));
        Observable<Article> remoteArticle = mRemoteDataSource.getArticleObservable(id);
        Observable<Article> localArticle = mLocalDataSource.getArticleObservable(id);

        return Observable
                .concat(cachedArticle, localArticle, remoteArticle)
                .filter(article -> article != null && !article.getPostContent().equalsIgnoreCase(""))
                .first();

    }
}
