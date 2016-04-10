package com.soshified.soshified.data.source;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;

import java.util.ArrayList;

/**
 * Implementation of ArticlesDataSource that retrieves articles from the a local DB.
 * //TODO Implement
 */
public class LocalArticlesDataSource implements ArticlesDataSource {

    private static LocalArticlesDataSource INSTANCE;


    private LocalArticlesDataSource(int type){

    }

    public static LocalArticlesDataSource getInstance(int type) {
        if (INSTANCE == null)
            INSTANCE = new LocalArticlesDataSource(type);
        return INSTANCE;
    }

    @Override
    public void getPage(int page, @NonNull final PageLoadCallback callback) {
    }

    @Override
    public void getRecent(@NonNull final PageLoadCallback callback) {

    }

    private class Articles {
        public ArrayList<Article> posts;
    }
}
