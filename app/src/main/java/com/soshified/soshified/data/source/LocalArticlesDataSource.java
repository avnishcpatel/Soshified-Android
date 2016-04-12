package com.soshified.soshified.data.source;

import com.soshified.soshified.data.Article;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

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
    public Observable<List<Article>> getPageObservable(int page) {
        return null;
    }

    @Override
    public Observable<List<Article>> getRecentObservable() {
        return null;
    }

    private class Articles {
        public ArrayList<Article> posts;
    }
}
