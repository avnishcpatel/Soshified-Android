package com.soshified.soshified.data.source;

import com.soshified.soshified.data.Article;

import java.util.List;

import rx.Observable;

/**
 * Interface for retrieving tasks
 */
public interface ArticlesDataSource {

    enum Article_Type {News, Style, Subs}

    Observable<List<Article>> getPageObservable(int page);

    Observable<Article> getArticleObservable(int id);

    void saveArticle(Article article);

    void setSource(Article_Type sourceType);


}
