package com.ntg.resttest.domain;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ntg.resttest.interfaces.ApiServiceSingleton;
import com.ntg.resttest.interfaces.PokeApi;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pokemon implements Serializable {

    private static final String TAG = "POKEMON";

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;

    private transient PokemonInfo info;

    public Pokemon(){}

    public Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumber() {
        String[] pUrl = url.split("/");
        return Integer.parseInt(pUrl[pUrl.length - 1]);
    }

    public void obtainInfo(){
        PokeApi apiService = ApiServiceSingleton.getInstance();
        Call<PokemonInfo> pokemonInfoCall = apiService.getPokemonInfoByUrl(url);

        pokemonInfoCall.enqueue(new Callback<PokemonInfo>() {
            @Override
            public void onResponse(Call<PokemonInfo> call, Response<PokemonInfo> response) {
                setInfo(response.body());
            }

            @Override
            public void onFailure(Call<PokemonInfo> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public String getFormattedName(){
        return (name.substring(0, 1).toUpperCase() + name.substring(1)).replace("-", " ");
    }

    public void setInfo(PokemonInfo info){
        this.info = info;
    }

    public PokemonInfo getInfo() {
        return info;
    }
}