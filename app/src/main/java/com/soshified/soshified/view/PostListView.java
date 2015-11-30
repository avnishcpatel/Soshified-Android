package com.soshified.soshified.view;

import com.soshified.soshified.model.PostList;

/**
 * Created by David on 1/12/15.
 */
public interface PostListView {

    void loadArticles(PostList postList);

    void setupRecyclerView();

    void setupToolBar();

    void refreshCompleted(boolean success, PostList postList);

    void addNewPage(boolean success, PostList postList);
}
