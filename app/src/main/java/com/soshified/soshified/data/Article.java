package com.soshified.soshified.data;

import com.soshified.soshified.data.source.local.RealmArticle;

/**
 * Object representation of a single article (Wordpress Post)
 */
public class Article {

    private String title, date, content, thumbnail, authorName;
    private int id;

    public Article copyArticle(RealmArticle article) {
        id = article.getId();
        title = article.getTitle();
        date = article.getDate();
        content = article.getContent();
        thumbnail = article.getThumbnail();
        authorName = article.getAuthorName();
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
