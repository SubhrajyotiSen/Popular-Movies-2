package com.subhrajyoti.popmovies.dagger.app;

import com.squareup.picasso.Picasso;
import com.subhrajyoti.popmovies.retrofit.MovieService;

import dagger.Component;
import io.realm.Realm;

@MovieApplicationScope
@Component(modules = {MovieServiceModule.class, PicassoModule.class})
public interface MovieApplicationComponent {

    Picasso picasso();

    MovieService movieService();

    Realm realm();
}
