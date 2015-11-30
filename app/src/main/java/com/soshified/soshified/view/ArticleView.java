package com.soshified.soshified.view;

/**
 * View component for the viewing an Article
 */
public interface ArticleView {

    void loadHeaderImage(String imageUrl);

    void initToolbar();

    void loadPostContent(String postContent);

    void loadPostMeta(String title, String author, String date);

}
