package com.soshified.soshified.data;

import com.annimon.stream.Stream;
import com.soshified.soshified.data.source.local.RealmArticle;

import java.util.ArrayList;

/**
 * Object representation of a single article (Wordpress Post)
 */
public class Article {

    private String title, postContent, thumbnail, authorName, comment_status;
    private long postDate;
    private int id;
    private ArrayList<Comment> comments;

    public Article(RealmArticle article) {
        id = article.getId();
        title = article.getTitle();
        postDate = article.getDate();
        postContent = article.getContent();
        thumbnail = article.getThumbnail();
        authorName = article.getAuthorName();
        comments = new ArrayList<>();
        Stream.of(article.getComments())
                .map(realmComment -> new Comment(realmComment))
                .forEach(comment -> comments.add(comment));

    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getCommentStatus() {
        return comment_status;
    }

    public void setCommentStatus(String comment_status) {
        this.comment_status = comment_status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return postDate;
    }

    public void setDate(long date) {
        this.postDate = date;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
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
