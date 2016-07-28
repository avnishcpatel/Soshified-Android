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
    private static Article_Type mType;

    public static LocalArticlesDataSource getInstance() {
        if (INSTANCE == null)
            INSTANCE = new LocalArticlesDataSource();
        return INSTANCE;
    }

    @Override
    public Observable<List<Article>> getPageObservable(int page) {
        return Realm.getDefaultInstance().where(RealmArticle.class)
                .equalTo("type", mType.ordinal())
                .findAllSortedAsync("postDate", Sort.DESCENDING).asObservable()
                .filter(RealmResults::isLoaded)
                .flatMap(Observable::from)
                .buffer(25)
                .elementAt(page - 1)
                .flatMap(Observable::from)
                .map(realmArticle -> new Article(realmArticle))
                .toList();
    }

    @Override
    public void saveArticle(Article article) {
        Realm.getDefaultInstance().executeTransaction(realm ->
                realm.copyToRealmOrUpdate(new RealmArticle(article).withType(mType))
        );
    }

    @Override
    public void setSource(Article_Type sourceType) {
        mType = sourceType;
    }

    @Override
    public Observable<Article> getArticleObservable(int id) {
        return Realm.getDefaultInstance().where(RealmArticle.class).equalTo("id", id).findAllAsync()
                .asObservable()
                .flatMap(Observable::from)
                .first()
                .map(realmArticle -> new Article(realmArticle));
    }
}
