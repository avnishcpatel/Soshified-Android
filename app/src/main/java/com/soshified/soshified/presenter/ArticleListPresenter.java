package com.soshified.soshified.presenter;


public interface ArticleListPresenter {

    void init(int type);

    void fetchArticles();

    void fetchLatestArticles();

    void fetchNewPage();

}
