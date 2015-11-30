package com.soshified.soshified.view;

import com.soshified.soshified.model.ArticleList;

/**
 * View component for viewing the Article List
 */
public interface ArticleListView {

    void loadArticles(ArticleList articleList);

    void setupRecyclerView();

    void setupToolBar();

    void refreshCompleted(boolean success, ArticleList articleList);

    void addNewPage(boolean success, ArticleList articleList);
}
