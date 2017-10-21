package com.subhrajyoti.popmovies.dagger.app;

import android.content.Context;

import com.squareup.picasso.Picasso;

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
