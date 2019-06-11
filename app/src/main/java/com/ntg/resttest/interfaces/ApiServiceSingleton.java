package com.ntg.resttest.interfaces;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceSingleton {

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private static Retrofit retrofit;
    private static PokeApi instance;

    private ApiServiceSingleton() {}

    public static synchronized PokeApi getInstance() {
        if(retrofit == null)
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        if(instance == null)
            instance = retrofit.create(PokeApi.class);
        return instance;
    }
}
