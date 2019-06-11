package com.ntg.resttest.interfaces;

import com.ntg.resttest.domain.PokemonInfo;
import com.ntg.resttest.domain.PokemonListLink;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PokeApi {

    @GET("pokemon/")
    Single<PokemonListLink> getPokemonListFirstlink();

    @GET("{url}")
    Call<PokemonInfo> getPokemonInfoByUrl(@Path("url") String url);

    @GET("pokemon")
    Call<PokemonListLink> getPokemonListLink(@Query("limit") int limit, @Query("offset") int offset);

    @GET("pokemon/{id}")
    Call<PokemonInfo> getPokemonInfoById(@Path("id") int id);

    @GET
    Single<PokemonInfo> getPokemonInfo(@Url String url);

    @GET
    Single<PokemonListLink> getFromUrl(@Url String url);

    @GET("pokemon/?offset=20&limit=20")
    Single<PokemonListLink> test();

}
