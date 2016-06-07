package com.soshified.soshified.articles;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;

import java.util.List;

/**
 * Contract between view and presenter
 */
class ArticlesContract {

    interface Presenter {

        void init();

        void subscribe();

        void unsubscribe();

        void fetchLatestArticles();

        void fetchNewPage(boolean forceReload);

        void setSource(int source);
    }

    interface View {

        void setPresenter(@NonNull Presenter presenter);

        void setupRecyclerView();

        void setupToolBar();

        void addNewArticle(Article article);

        Article getRecentArticle();

        void setRefreshing(boolean refreshing);

        void addNewPage(List<Article> articles);

    }

}
