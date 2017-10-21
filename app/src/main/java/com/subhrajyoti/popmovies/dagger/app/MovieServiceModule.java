package com.subhrajyoti.popmovies.dagger.app;

import com.subhrajyoti.popmovies.retrofit.MovieService;
import com.subhrajyoti.popmovies.utils.Constants;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class MovieServiceModule {

    @Provides
    @MovieApplicationScope
    public Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(Constants.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @MovieApplicationScope
    public MovieService movieService(Retrofit retrofit) {
        return retrofit.create(MovieService.class);
    }

    @Provides
    @MovieApplicationScope
    public Realm realm() {
        return Realm.getDefaultInstance();
    }
}
