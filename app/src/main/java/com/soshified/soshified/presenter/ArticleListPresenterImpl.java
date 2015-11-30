package com.soshified.soshified.presenter;

import com.soshified.soshified.model.PostList;
import com.soshified.soshified.view.PostListView;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by David on 1/12/15.
 */
public class ArticleListPresenterImpl implements ArticleListPresenter {

    public static final int ARTICLE_TYPE_NEWS = 0;
    public static final int ARTICLE_TYPE_STYLE = 1;
    public static final int ARTICLE_TYPE_SUBS = 2;

    private static RestAdapter mRestAdapter;

    private int mLastRequestedPage = 1;

    private PostListView mPostListView;

    public ArticleListPresenterImpl(PostListView postListView) {
        mPostListView = postListView;
    }

    @Override
    public void init(int type) {

        String jsonEndpoint;
        switch (type) {
            case ARTICLE_TYPE_NEWS:
                jsonEndpoint = "http://soshified.com/json";
                break;
            case ARTICLE_TYPE_STYLE:
                jsonEndpoint = "whatever the style url is";
                break;
            case ARTICLE_TYPE_SUBS:
                jsonEndpoint = "whatever the subs endpoint is";
                break;
            default:
                jsonEndpoint = "http://soshified.com/json";
                break;
        }

        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(jsonEndpoint)
                .build();

        mPostListView.setupRecyclerView();
        mPostListView.setupToolBar();
        fetchArticles();
    }

    @Override
    public void fetchArticles() {

        GetPagedRequest request = mRestAdapter.create(GetPagedRequest.class);
        request.newsList(mLastRequestedPage, new Callback<PostList>() {
            @Override
            public void success(PostList postList, Response response) {
                mPostListView.loadArticles(postList);


            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void fetchLatestArticles() {

        GetRecentRequest request = mRestAdapter.create(GetRecentRequest.class);
        request.newsList(new Callback<PostList>() {
            @Override
            public void success(PostList postList, Response response) {
                mPostListView.refreshCompleted(true, postList);
            }

            @Override
            public void failure(RetrofitError error) {
                mPostListView.refreshCompleted(false, null);
            }
        });
    }

    @Override
    public void fetchNewPage() {

        GetPagedRequest request = mRestAdapter.create(GetPagedRequest.class);
        mLastRequestedPage += 1;
        request.newsList(mLastRequestedPage, new Callback<PostList>() {
            @Override
            public void success(PostList postList, Response response) {
                mPostListView.addNewPage(true, postList);
            }

            @Override
            public void failure(RetrofitError error) {
                mPostListView.addNewPage(false, null);
            }
        });
    }

    /**
     * GET Request to fetch 'pages' of posts. Should return a page of 25 posts
     */
    private interface GetPagedRequest {
        @GET("/get_posts?count=25")
        void newsList(@Query("page") int page, Callback<PostList> callback);
    }

    /**
     * GET Request to fetch most recent posts. Used when refreshing.
     */
    private interface GetRecentRequest {
        @GET("/get_recent_posts")
        void newsList(Callback<PostList> callback);
    }
}
