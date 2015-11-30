package com.soshified.soshified.view;

/**
 * View component for the post based sub sites (News, Style, etc)
 */
public interface ArticleView {

    void loadHeaderImage(String imageUrl);

    void initToolbar();

    void loadPostContent(String postContent);

    void loadPostMeta(String title, String author, String date);

}
