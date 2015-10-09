package com.soshified.soshified.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Mapping of  /posts
 */
public class PostOld implements Parcelable {
    public String title, date_gmt, excerpt, content;
    FeaturedImage featured_image;
    Author author;

    private PostOld(Parcel in) {
        title = in.readString();
        date_gmt = in.readString();
        excerpt = in.readString();
        content = in.readString();
        author = new Author(in.readString());
        featured_image = new FeaturedImage(in.readString(), in.readInt(), in.readInt());
    }

    public String getImage(){
        return featured_image.source;
    }

    public int getWidth(){
        if(featured_image.attachment_meta != null)
            return featured_image.attachment_meta.width;
        else
            return 0;
    }

    public int getHeight(){
        if(featured_image.attachment_meta != null)
            return featured_image.attachment_meta.height;
        else
            return 0;
    }

    public String getSubtitle() throws ParseException {
        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat toFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        Date date = fromFormat.parse(date_gmt);

        return "Posted by " + author.username + " on " + toFormat.format(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date_gmt);
        dest.writeString(excerpt);
        dest.writeString(content);
        dest.writeString(author.username);
        dest.writeString(featured_image.source);
        if(featured_image.attachment_meta != null){
            dest.writeInt(featured_image.attachment_meta.width);
            dest.writeInt(featured_image.attachment_meta.height);
        }
    }

    public static final Parcelable.Creator<PostOld> CREATOR = new Parcelable.Creator<PostOld>() {
        public PostOld createFromParcel(Parcel in) {
            return new PostOld(in);
        }

        public PostOld[] newArray(int size) {
            return new PostOld[size];
        }
    };

    class Author {
        String username;

        public Author(String username){
            this.username = username;
        }
    }

    class FeaturedImage {
        String source;
        AttachmentMeta attachment_meta;

        public FeaturedImage(String source, int width, int height){
            this.source = source;
            attachment_meta = new AttachmentMeta();
            attachment_meta.width = width;
            attachment_meta.height = height;
        }

        class AttachmentMeta {
            int width, height;
        }
    }
}
