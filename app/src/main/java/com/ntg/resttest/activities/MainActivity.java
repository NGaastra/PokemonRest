package com.ntg.resttest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ntg.resttest.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void openList(View v){
        Intent intent = new Intent(this, PokemonListActivity.class);
        startActivity(intent);
    }

}
