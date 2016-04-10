package com.soshified.soshified.data.source;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;

import java.util.ArrayList;

/**
 * Interface for retrieving tasks
 */
public interface ArticlesDataSource {

    void getPage(int page, @NonNull PageLoadCallback callback);

    void getRecent(@NonNull PageLoadCallback callback);

    interface PageLoadCallback {

        void onPageLoaded(ArrayList<Article> articles);

        void onError();

    }
}
