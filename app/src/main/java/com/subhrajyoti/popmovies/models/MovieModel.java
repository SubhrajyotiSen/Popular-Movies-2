package com.subhrajyoti.popmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class MovieModel extends RealmObject implements Parcelable{

    private String release_date;
    private String original_title;
    private String poster_path;
    private String overview;
    private float vote_average;
    private String backdrop_path;
    private String movie_id;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private String tag;

    public MovieModel(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(original_title);
        dest.writeString(poster_path);
        dest.writeFloat(vote_average);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(movie_id);

    }

    protected MovieModel(Parcel in) {
        original_title = in.readString();
        poster_path = in.readString();
        vote_average = in.readFloat();
        backdrop_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        movie_id = in.readString();
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public String getoriginal_title() {
        return original_title;
    }



    public String getposter_path() {
        return poster_path;
    }




    public String getOverview() {
        return overview;
    }



    public float getvote_average() {
        return vote_average;
    }



    public String getrelease_date() {
        return release_date;
    }




    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getMovie_id() {
        return movie_id;
    }




}
