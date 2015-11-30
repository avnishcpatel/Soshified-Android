package com.soshified.soshified.presenter;

import com.soshified.soshified.model.ArticleList;
import com.soshified.soshified.view.ArticleListView;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Presenter Implementation to deal with all the 'presenter' stuff for ArticleListView
 */
public class ArticleListPresenterImpl implements ArticleListPresenter {

    public static final int ARTICLE_TYPE_NEWS = 0;
    public static final int ARTICLE_TYPE_STYLE = 1;
    public static final int ARTICLE_TYPE_SUBS = 2;

    private static ArticleListRequest request;

    private int mLastRequestedPage = 1;

    private ArticleListView mArticleListView;

    public ArticleListPresenterImpl(ArticleListView articleListView) {
        mArticleListView = articleListView;
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

        mArticleListView.setupRecyclerView();
        mArticleListView.setupToolBar();
        fetchArticles();
    }

    @Override
    public void fetchArticles() {

        request.getPage(mLastRequestedPage, new Callback<ArticleList>() {
            @Override
            public void success(ArticleList articleList, Response response) {
                mArticleListView.loadArticles(articleList);


            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void fetchLatestArticles() {

        request.getRecent(new Callback<ArticleList>() {
            @Override
            public void success(ArticleList articleList, Response response) {
                mArticleListView.refreshCompleted(true, articleList);
            }

            @Override
            public void failure(RetrofitError error) {
                mArticleListView.refreshCompleted(false, null);
            }
        });
    }

    @Override
    public void fetchNewPage() {

        mLastRequestedPage += 1;
        request.getPage(mLastRequestedPage, new Callback<ArticleList>() {
            @Override
            public void success(ArticleList articleList, Response response) {
                mArticleListView.addNewPage(true, articleList);
            }

            @Override
            public void failure(RetrofitError error) {
                mArticleListView.addNewPage(false, null);
            }
        });
    }

    /**
     * Interface containing methods to interact with the server
     */
    private interface ArticleListRequest {

        @GET("/get_posts?count=25")
        void getPage(@Query("page") int page, Callback<ArticleList> callback);


        @GET("/get_posts?count=5")
        void getRecent(Callback<ArticleList> callback);
    }
}
