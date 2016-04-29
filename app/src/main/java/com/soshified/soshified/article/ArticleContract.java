package com.soshified.soshified.article;

import android.support.annotation.NonNull;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.Comment;

import java.util.ArrayList;

/**
 * Class that holds the interfaces for the Presenter and View
 */
public class ArticleContract {

    public interface Presenter {

        void showArticle(Article article);
    }

    public interface View {

        void setPresenter(@NonNull Presenter presenter);

        void loadHeaderImage(String imageUrl);

        void setupToolbar();

        void loadPostContent(String postContent);

        void loadPostMeta(String title, String author, String date);

        void loadComments(ArrayList<Comment> comments);

    }

}
