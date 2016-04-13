package com.soshified.soshified.data.source.local;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesDataSource;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

/**
 * Implementation of ArticlesDataSource that retrieves articles from the a local DB.
 * //TODO Implement
 */
public class LocalArticlesDataSource implements ArticlesDataSource {

    private static LocalArticlesDataSource INSTANCE;

    private LocalArticlesDataSource(int type){

    }

    public static LocalArticlesDataSource getInstance(int type) {
        if (INSTANCE == null)
            INSTANCE = new LocalArticlesDataSource(type);


        return INSTANCE;
    }

    @Override
    public Observable<List<Article>> getPageObservable(int page) {
        //TODO Improve this
        RealmResults<RealmArticle> realmArticles = Realm.getDefaultInstance().where(RealmArticle.class).findAll();
        return Observable.from(realmArticles)
                .filter(RealmObject::isLoaded)
                .flatMap(realmArticle -> Observable.from(realmArticles))
                .map(realmArticle -> new Article().copyArticle(realmArticle))
                .toList();
    }

    @Override
    public void saveArticle(Article article) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            RealmArticle realmArticle = realm.createObject(RealmArticle.class);
            realmArticle.copyArticle(article);
        });
    }
}