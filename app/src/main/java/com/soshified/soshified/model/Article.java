package com.soshified.soshified.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Object representation of a single article (Wordpress Post)
 */
public class Article implements Parcelable {

    public String title, date, excerpt, content, mThumbnail, mAuthor;
    public Images thumbnail_images;
    public Author author;
    public int id;

    public String getImageUrl(){
        if (thumbnail_images != null)
            return thumbnail_images.large.url;
        else
            return null;
    }

    public String getAuthor() {
        return author.name;
    }


    private Article(Parcel in) {
        title = in.readString();
        date = in.readString();
        excerpt = in.readString();
        content = in.readString();
        mThumbnail = in.readString();
        mAuthor = in.readString();
        id = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(excerpt);
        dest.writeString(content);
        if(thumbnail_images != null)
            dest.writeString(thumbnail_images.large.url);
        else
            dest.writeString(null);
        dest.writeString(author.name);
        dest.writeInt(id);
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

}
