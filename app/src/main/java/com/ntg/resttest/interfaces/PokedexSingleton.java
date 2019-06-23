package com.ntg.resttest.interfaces;

import android.content.Context;

import com.google.gson.Gson;
import com.ntg.resttest.domain.Pokemon;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PokedexSingleton {

    private static ArrayList<Pokemon> instance;

    private PokedexSingleton() {}

    private static void init(){
        if(instance == null)
            instance = new ArrayList<>();
    }

    public static synchronized ArrayList<Pokemon> getInstance() {
        init();
        return instance;
    }

    public static void setInstance(ArrayList<Pokemon> instance) {
        PokedexSingleton.instance = instance;
    }



}
