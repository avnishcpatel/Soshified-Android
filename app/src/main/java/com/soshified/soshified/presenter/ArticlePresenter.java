package com.soshified.soshified.presenter;


import com.soshified.soshified.model.Article;

/**
 * Presenter for all the post based sub sites (News, Style, etc)
 */
public interface ArticlePresenter {

    void init(Article mArticle);

    /**
     * Fires the ParseContent AsyncTask and waits for a response to be then passed back to the view
     * @param postContent Un-parsed post content
     */
    void parsePost(String postContent);

    void parseMeta();

}
