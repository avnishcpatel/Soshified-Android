package com.soshified.soshified.articles;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;

import java.util.ArrayList;

/**
 * Contract between view and presenter
 */
public class ArticlesContract {

    public interface Presenter {

        void init(int type);

        void fetchArticles();

        void fetchLatestArticles();

        void fetchNewPage();

    }

    public interface View {

        void setPresenter(@NonNull Presenter presenter);

        void showArticles(ArrayList<Article> articles);

        void setupRecyclerView();

        void setupToolBar();

        void refreshCompleted(boolean success, ArrayList<Article> articles);

        void addNewPage(boolean success, ArrayList<Article> articles);

    }

}
