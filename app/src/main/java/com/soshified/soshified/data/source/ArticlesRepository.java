package com.soshified.soshified.data.source;

import android.support.annotation.NonNull;

/**
 * Implementation that loads articles from either Soshified Servers or a local database.
 * Loads Articles into a cache which is updated dynamically.
 */
public class ArticlesRepository implements ArticlesDataSource {

    private static ArticlesRepository INSTANCE;

    ArticlesDataSource mRemoteDataSource;
    ArticlesDataSource mLocalDataSource;

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

    @Override
    public void getPage(int page, @NonNull PageLoadCallback callback) {
        mRemoteDataSource.getPage(page, callback);
    }

    @Override
    public void getRecent(@NonNull PageLoadCallback callback) {

    }
}
