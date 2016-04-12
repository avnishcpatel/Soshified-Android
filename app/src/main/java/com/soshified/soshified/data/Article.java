package com.soshified.soshified.data;

/**
 * Object representation of a single article (Wordpress Post)
 */
public class Article{

    public String title, date, content, mThumbnail, mAuthor;
    public Images thumbnail_images;
    public Author author;
    public int id;

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl(){
        if (thumbnail_images != null)
            return thumbnail_images.large.url;
        else
            return null;
    }

    public String getAuthor() {
        return author.name;
    }

    class Author {
        String name;
    }

    class Images {

        Size large;

        class Size {
            String url;
        }
    }

}
