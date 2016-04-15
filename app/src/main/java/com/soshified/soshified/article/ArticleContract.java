package com.soshified.soshified.article;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;

/**
 * Class that holds the interfaces for the Presenter and View
 */
public class ArticleContract {

    public interface Presenter {

        void init(Article mArticle);

        /**
         * Fires the ParseContent AsyncTask and waits for a response to be then passed back to the view
         * @param postContent Un-parsed post content
         */
        void parsePost(String postContent);

        void parseMeta();
    }

    public interface View {

        void setPresenter(@NonNull Presenter presenter);

        void loadHeaderImage(String imageUrl);

        void setupToolbar();

        void loadPostContent(String postContent);

        void loadPostMeta(String title, String author, String date);

    }

}
