package com.soshified.soshified.data.source;

import android.support.annotation.NonNull;

import com.soshified.soshified.articles.ArticlesPresenter;
import com.soshified.soshified.data.Article;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Implementation of ArticlesDataSource that retrieves articles from the servers.
 */
public class RemoteArticlesDataSource implements ArticlesDataSource {

    private static RemoteArticlesDataSource INSTANCE;

    private static ArticlesRequest request;

    private RemoteArticlesDataSource(int type){
        String jsonEndpoint;
        switch (type) {
            case ArticlesPresenter.ARTICLE_TYPE_NEWS:
                jsonEndpoint = "http://soshified.com/json";
                break;
            case ArticlesPresenter.ARTICLE_TYPE_STYLE:
                jsonEndpoint = "whatever the style url is";
                break;
            case ArticlesPresenter.ARTICLE_TYPE_SUBS:
                jsonEndpoint = "whatever the subs endpoint is";
                break;
            default:
                jsonEndpoint = "http://soshified.com/json";
                break;
        }

        RestAdapter mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(jsonEndpoint)
                .build();

        request = mRestAdapter.create(ArticlesRequest.class);

    }

    public static RemoteArticlesDataSource getInstance(int type) {
        if (INSTANCE == null)
            INSTANCE = new RemoteArticlesDataSource(type);
        return INSTANCE;
    }

    @Override
    public void getPage(int page, @NonNull final PageLoadCallback callback) {
        request.getPage(page, new Callback<Articles>() {
            @Override
            public void success(Articles articles, Response response) {
                callback.onPageLoaded(articles.posts);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                callback.onError();
            }
        });
    }

    @Override
    public void getRecent(@NonNull final PageLoadCallback callback) {

        request.getRecent(new Callback<Articles>() {
            @Override
            public void success(Articles articles, Response response) {
                callback.onPageLoaded(articles.posts);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.onError();
            }
        });
    }


    /**
     * Interface containing methods to interact with the server
     */
    private interface ArticlesRequest {

        @GET("/get_posts?count=25")
        void getPage(@Query("page") int page, Callback<Articles> callback);


        @GET("/get_posts?count=5")
        void getRecent(Callback<Articles> callback);
    }

    private class Articles {
        public ArrayList<Article> posts;
    }
}
