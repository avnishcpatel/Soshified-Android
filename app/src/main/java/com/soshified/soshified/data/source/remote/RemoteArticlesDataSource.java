package com.soshified.soshified.data.source.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesDataSource;
import com.soshified.soshified.data.source.ArticlesRepository;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Implementation of ArticlesDataSource that retrieves articles from the servers.
 */
public class RemoteArticlesDataSource implements ArticlesDataSource {

    private static RemoteArticlesDataSource INSTANCE;

    private static ArticlesRequest request;

    public static RemoteArticlesDataSource getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RemoteArticlesDataSource();
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
    public void setSource(int source) {
        String jsonEndpoint;
        switch (source) {
            case ArticlesRepository.ARTICLE_TYPE_NEWS:
                jsonEndpoint = "https://soshified.com/json/";
                break;
            case ArticlesRepository.ARTICLE_TYPE_STYLE:
                jsonEndpoint = "http://style.soshified.com/json/";
                break;
            case ArticlesRepository.ARTICLE_TYPE_SUBS:
                jsonEndpoint = "http://soshisubs.com/json/";
                break;
            default:
                jsonEndpoint = "http://soshified.com/json/";
                break;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Article.class, new ArticleDeserializer())
                .create();

        Retrofit mRestAdapter = new Retrofit.Builder()
                .baseUrl(jsonEndpoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        request = mRestAdapter.create(ArticlesRequest.class);
    }

    //TODO Implement
    @Override
    public Observable<Article> getArticleObservable(int id) {
        return null;
    }

    /**
     * Interface containing methods to interact with the server.
     */
    private interface ArticlesRequest {

        @GET("get_posts?count=25")
        Observable<Articles> getPage(@Query("page") int page);

        class Articles {
            public List<Article> posts;
        }
    }

}
