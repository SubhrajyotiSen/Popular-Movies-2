package com.subhrajyoti.myapplication;


import android.app.Application;

import com.subhrajyoti.myapplication.Retrofit.MovieAPI;

public class App extends Application {
 private static MovieAPI.MovieClient movieClient;

 @Override
 public void onCreate() {
 super.onCreate();

 movieClient = new MovieAPI.MovieClient();
 }

 public static MovieAPI.MovieClient getMovieClient() {
 return movieClient;
 }
 }
