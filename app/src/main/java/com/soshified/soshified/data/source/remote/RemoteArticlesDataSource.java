package com.soshified.soshified.data.source.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesDataSource;
import com.soshified.soshified.data.source.ArticlesRepository;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
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
            case ArticlesRepository.ARTICLE_TYPE_NEWS:
                jsonEndpoint = "http://soshified.com/json";
                break;
            case ArticlesRepository.ARTICLE_TYPE_STYLE:
                jsonEndpoint = "whatever the style url is";
                break;
            case ArticlesRepository.ARTICLE_TYPE_SUBS:
                jsonEndpoint = "whatever the subs endpoint is";
                break;
            default:
                jsonEndpoint = "http://soshified.com/json";
                break;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Article.class, new ArticleDeserializer())
                .create();

        RestAdapter mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(jsonEndpoint)
                .setConverter(new GsonConverter(gson))
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
                .flatMap(articles -> Observable.from(articles.posts))
                .toList();
    }

    /**
     * Not used since we don't save articles to the server.
     * @param article Not Used.
     */
    @Override
    public void saveArticle(Article article) {

    }

    @Override
    public Observable<Article> getArticleObservable(int id) {
        return null;
    }

    /**
     * Interface containing methods to interact with the server.
     */
    private interface ArticlesRequest {

        @GET("/get_posts?count=25")
        Observable<Articles> getPage(@Query("page") int page);

        class Articles {
            public List<Article> posts;
        }
    }

}
