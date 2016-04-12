package com.soshified.soshified.data.source;

import com.soshified.soshified.articles.ArticlesPresenter;
import com.soshified.soshified.data.Article;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

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
    public Observable<List<Article>> getPageObservable(int page) {
        return request.getPage(page)
                .flatMap(articles -> Observable.from(articles.posts)).toList();
    }

    @Override
    public Observable<List<Article>> getRecentObservable() {
        return request.getRecent()
                .flatMap(articles -> Observable.from(articles.posts)).toList();
    }

    /**
     * Interface containing methods to interact with the server
     */
    private interface ArticlesRequest {

        @GET("/get_posts?count=25")
        Observable<Articles> getPage(@Query("page") int page);

        @GET("/get_posts?count=5")
        Observable<Articles> getRecent();

        class Articles {
            public List<Article> posts;
        }
    }

}
