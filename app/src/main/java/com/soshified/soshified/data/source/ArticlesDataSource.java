package com.soshified.soshified.data.source;

import com.soshified.soshified.data.Article;

import java.util.List;

import rx.Observable;

/**
 * Interface for retrieving tasks
 */
public interface ArticlesDataSource {

    Observable<List<Article>> getPageObservable(int page);



}
