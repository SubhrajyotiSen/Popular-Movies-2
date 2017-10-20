package com.subhrajyoti.popmovies.dagger.module;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.subhrajyoti.popmovies.dagger.scope.MovieApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class PicassoModule {

    @Provides
    @MovieApplicationScope
    public Picasso picasso(Context context) {
        return new Picasso.Builder(context).build();
    }
}
