package com.soshified.soshified.articles;

import com.soshified.soshified.data.Article;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleListView
 */
public class ArticlesPresenter implements ArticlesContract.Presenter {

    public static final int ARTICLE_TYPE_NEWS = 0;
    public static final int ARTICLE_TYPE_STYLE = 1;
    public static final int ARTICLE_TYPE_SUBS = 2;

    private static ArticleListRequest request;

    private int mLastRequestedPage = 1;

    private ArticlesContract.View mArticlesView;

    public ArticlesPresenter(ArticlesContract.View articleListView) {
        mArticlesView = checkNotNull(articleListView);
        mArticlesView.setPresenter(this);
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

        RestAdapter mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(jsonEndpoint)
                .build();

        request = mRestAdapter.create(ArticleListRequest.class);

        mArticlesView.setupRecyclerView();
        mArticlesView.setupToolBar();
        fetchArticles();
    }

    @Override
    public void fetchArticles() {

        request.getPage(mLastRequestedPage, new Callback<Articles>() {
            @Override
            public void success(Articles articles, Response response) {
                mArticlesView.showArticles(articles.posts);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void fetchLatestArticles() {

        request.getRecent(new Callback<Articles>() {
            @Override
            public void success(Articles articles, Response response) {
                mArticlesView.refreshCompleted(true, articles.posts);
            }

            @Override
            public void failure(RetrofitError error) {
                mArticlesView.refreshCompleted(false, null);
            }
        });
    }

    @Override
    public void fetchNewPage() {

        mLastRequestedPage += 1;
        request.getPage(mLastRequestedPage, new Callback<Articles>() {
            @Override
            public void success(Articles articles, Response response) {
                mArticlesView.addNewPage(true, articles.posts);
            }

            @Override
            public void failure(RetrofitError error) {
                mArticlesView.addNewPage(false, null);
            }
        });
    }

    /**
     * Interface containing methods to interact with the server
     */
    private interface ArticleListRequest {

        @GET("/get_posts?count=25")
        void getPage(@Query("page") int page, Callback<Articles> callback);


        @GET("/get_posts?count=5")
        void getRecent(Callback<Articles> callback);
    }

    private class Articles {
        public ArrayList<Article> posts;
        public int count_total;
    }
}
