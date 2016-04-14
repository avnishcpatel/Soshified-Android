package com.soshified.soshified.data.source.local;

import com.soshified.soshified.data.Article;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Realm version of the the Article object, because of thread confinement
 */
public class RealmArticle extends RealmObject{

    private String title, date, content, thumbnail, authorName;
    @PrimaryKey private int id;

    public RealmArticle copyArticle(Article article) {
        id = article.getId();
        title = article.getTitle();
        date = article.getDate();
        content = article.getContent();
        thumbnail = article.getThumbnail();
        authorName = article.getAuthorName();
        return this;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getAuthorName() {
        return authorName;
    }
}
