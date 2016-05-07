package com.subhrajyoti.popmovies.models;


import android.os.Parcel;
import android.os.Parcelable;

public class ReviewModel  implements Parcelable {


    public String getId() {
        return id;
    }


    private String id;

    public String getcontent() {
        return content;
    }


    private String content;

    public String getAuthor() {
        return author;
    }


    private String author;

    public String getUrl() {
        return url;
    }

    private String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);


    }

    protected ReviewModel(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
        @Override
        public ReviewModel createFromParcel(Parcel in) {
            return new ReviewModel(in);
        }

        @Override
        public ReviewModel[] newArray(int size) {
            return new ReviewModel[size];
        }
    };
}