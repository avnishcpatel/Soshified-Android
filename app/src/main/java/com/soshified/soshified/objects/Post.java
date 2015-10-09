package com.soshified.soshified.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    public String title, date, excerpt, content, mThumbnail;
    public Images thumbnail_images;

    public String getImageUrl(){
        return thumbnail_images.large.url;
    }


    private Post(Parcel in) {
        title = in.readString();
        date = in.readString();
        excerpt = in.readString();
        content = in.readString();
        mThumbnail = in.readString();
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
        dest.writeString(thumbnail_images.large.url);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };


//    public String getSubtitle() throws ParseException {
//        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
//        SimpleDateFormat toFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
//        Date date = fromFormat.parse(date_gmt);
//
//        return "Posted by " + author.username + " on " + toFormat.format(date);
//    }


}
