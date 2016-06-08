package com.soshified.soshified.data.source.local;

import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesDataSource;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

/**
 * Implementation of ArticlesDataSource that retrieves articles from the a local DB.
 * //TODO Implement
 */
public class LocalArticlesDataSource implements ArticlesDataSource {

    private static LocalArticlesDataSource INSTANCE;
    private static int mType;

    public static LocalArticlesDataSource getInstance() {
        if (INSTANCE == null)
            INSTANCE = new LocalArticlesDataSource();
        return INSTANCE;
    }

    @Override
    public Observable<List<Article>> getPageObservable(int page) {
        return Realm.getDefaultInstance().where(RealmArticle.class)
                .equalTo("type", mType)
                .findAllSortedAsync("postDate", Sort.DESCENDING).asObservable()
                .filter(RealmResults::isLoaded)
                .flatMap(Observable::from)
                .buffer(25)
                .elementAt(page - 1)
                .flatMap(Observable::from)
                .map(realmArticle -> new Article().copyArticle(realmArticle))
                .toList();
    }

    @Override
    public void saveArticle(Article article) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            RealmArticle realmArticle = new RealmArticle().copyArticle(article);
            realmArticle.setType(mType);
            realm.copyToRealmOrUpdate(realmArticle);
        });
    }

    @Override
    public void setSource(int source) {
        mType = source;
    }

    @Override
    public Observable<Article> getArticleObservable(int id) {
        return Realm.getDefaultInstance().where(RealmArticle.class).equalTo("id", id).findAllAsync()
                .asObservable()
                .flatMap(Observable::from)
                .first()
                .map(realmArticle -> new Article().copyArticle(realmArticle));
    }
}
