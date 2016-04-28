package com.soshified.soshified.data.source.local;

import com.soshified.soshified.data.Comment;

import io.realm.RealmObject;

/**
 * Realm version of the Comment POJO
 */
public class RealmComment extends RealmObject {
    private int id, parent;
    private String name, content, date;

    public RealmComment copyComment(Comment comment) {
        id = comment.getId();
        parent = comment.getParent();
        name = comment.getName();
        content = comment.getContent();
        date = comment.getDate();
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
