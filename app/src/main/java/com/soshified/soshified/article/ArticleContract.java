package com.soshified.soshified.article;

import com.soshified.soshified.data.Article;

/**
 * Created by david on 4/15/16.
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

        void loadHeaderImage(String imageUrl);

        void initToolbar();

        void loadPostContent(String postContent);

        void loadPostMeta(String title, String author, String date);

    }

}
