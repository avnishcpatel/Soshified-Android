package com.soshified.soshified.view;

import com.soshified.soshified.model.ArticleList;

/**
 * Created by David on 1/12/15.
 */
public interface ArticleListView {

    void loadArticles(ArticleList articleList);

    void setupRecyclerView();

    void setupToolBar();

    void refreshCompleted(boolean success, ArticleList articleList);

    void addNewPage(boolean success, ArticleList articleList);
}
