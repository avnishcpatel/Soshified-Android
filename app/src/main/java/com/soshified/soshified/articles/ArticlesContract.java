package com.soshified.soshified.articles;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;

import java.util.List;

/**
 * Contract between view and presenter
 */
public class ArticlesContract {

    public interface Presenter {

        void init(int type);

        void subscribe();

        void unsubscribe();

        void fetchLatestArticles();

        void fetchNewPage(boolean forceReload);

    }

    public interface View {

        void setPresenter(@NonNull Presenter presenter);

        void setupRecyclerView();

        void setupToolBar();

        void addNewArticle(Article article);

        Article getRecentArticle();

        void hideRefreshing();

        void addNewPage(List<Article> articles);

    }

}
