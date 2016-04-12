package com.soshified.soshified.data.source;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;

import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * Implementation that loads articles from either Soshified Servers or a local database.
 * Loads Articles into a cache which is updated dynamically.
 */
public class ArticlesRepository implements ArticlesDataSource {

    private static ArticlesRepository INSTANCE;

    private ArticlesDataSource mRemoteDataSource;
    private ArticlesDataSource mLocalDataSource;

    private HashMap<Integer, Article> mCachedArticles;

    private ArticlesRepository(@NonNull RemoteArticlesDataSource remoteDataSource,
                              @NonNull ArticlesDataSource localDataSource) {
        this.mLocalDataSource = localDataSource;
        this.mRemoteDataSource = remoteDataSource;
        this.mCachedArticles = new HashMap<>();
    }

    public static ArticlesRepository getInstance(@NonNull RemoteArticlesDataSource remoteDataSource,
                                                 @NonNull ArticlesDataSource localDataSource) {
        if (INSTANCE == null)
            INSTANCE = new ArticlesRepository(remoteDataSource, localDataSource);
        return INSTANCE;
    }

    @Override
    public Observable<List<Article>> getPageObservable(int page) {
        Observable<List<Article>> remoteArticles = mRemoteDataSource.getPageObservable(page);

        Observable<List<Article>> remoteArticlesWithLocalUpdate = remoteArticles
                .flatMap(Observable::from)
                .doOnNext(article -> mCachedArticles.put(article.id, article))
                .toList();

        return remoteArticlesWithLocalUpdate;
    }
}
