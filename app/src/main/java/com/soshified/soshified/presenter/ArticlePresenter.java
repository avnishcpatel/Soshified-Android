package com.soshified.soshified.presenter;


import com.soshified.soshified.model.Article;

public interface ArticlePresenter {

    void init(Article mArticle);

    /**
     * Fires the ParseContent AsyncTask and waits for a response to be then passed back to the view
     * @param postContent Un-parsed post content
     */
    void parsePost(String postContent);

    void parseMeta();

}
