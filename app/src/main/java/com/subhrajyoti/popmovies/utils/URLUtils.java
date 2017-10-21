package com.subhrajyoti.popmovies.utils;

import com.subhrajyoti.popmovies.BuildConfig;

public class URLUtils {

    public static String makeImageURL(String posterPath) {
        return Constants.IMAGE_URL + "/w342" + posterPath + "?api_key?=" + BuildConfig.API_KEY;
    }

    public static String makeThumbnailURL(String thumbnailId) {
        return Constants.YT_THUMB_URL.concat(thumbnailId).concat("/hqdefault.jpg");
    }
}
