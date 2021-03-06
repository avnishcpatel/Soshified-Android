package com.soshified.soshified.data.source.local;

import com.annimon.stream.Stream;
import com.soshified.soshified.data.Article;
import com.soshified.soshified.data.source.ArticlesDataSource;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Realm version of the the Article object, because of thread confinement
 */
public class RealmArticle extends RealmObject{

    private String title, content, thumbnail, authorName, comment_status;
    private long postDate;
    private RealmList<RealmComment> comments;
    private int type;
    @PrimaryKey private int id;

    public RealmArticle() {

    }

    public RealmArticle(Article article) {
        id = article.getId();
        title = article.getTitle();
        postDate = article.getDate();
        content = article.getPostContent();
        thumbnail = article.getThumbnail();
        authorName = article.getAuthorName();
        comments = new RealmList<>();
        Stream.of(article.getComments())
                .map(comment -> new RealmComment().copyComment(comment))
                .forEach(realmComment -> comments.add(realmComment));
    }

    public int getType() {
        return type;
    }

    public void setType(ArticlesDataSource.Article_Type type) {
        this.type = type.ordinal();
    }

    public RealmArticle withType(ArticlesDataSource.Article_Type type) {
        this.setType(type);
        return this;
    }
    public RealmList<RealmComment> getComments() {
        return comments;
    }

    public String getCommentStatus() {
        return comment_status;
    }

    public int getId() {
        return id;
    }

    public long getDate() {
        return postDate;
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
