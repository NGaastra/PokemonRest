package com.ntg.resttest.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ntg.resttest.R;
import com.ntg.resttest.adapters.PokedexRecyclerViewAdapter;
import com.ntg.resttest.domain.Pokemon;
import com.ntg.resttest.domain.PokemonInfo;
import com.ntg.resttest.interfaces.PokedexSingleton;
import com.ntg.resttest.interfaces.RecyclerViewClickListener;
import com.ntg.resttest.interfaces.ApiServiceSingleton;
import com.ntg.resttest.interfaces.PokeApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokedexActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private static final String TAG = "POKEDEX";
    private static final String DATA_FILE = "data";

    private static File datafile;

    private PokedexRecyclerViewAdapter pokemonRecyclerViewAdapter;

    private static PokeApi apiService;

    private RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        apiService = ApiServiceSingleton.getInstance();

        progressBar = findViewById(R.id.loadingPanel);
        progressBar.setVisibility(View.GONE);

        Toolbar listToolbar = findViewById(R.id.list_toolbar);
        setSupportActionBar(listToolbar);

        datafile = new File(getFilesDir(), DATA_FILE);
        loadPokedex();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            final PokemonInfoFragment infoFragment = (PokemonInfoFragment) getSupportFragmentManager().findFragmentById(R.id.info_fragment);
            infoFragment.getView().setVisibility(View.INVISIBLE);
        }

        pokemonRecyclerViewAdapter = new PokedexRecyclerViewAdapter(this, this);
        RecyclerView pokemonRecyclerView = findViewById(R.id.pokemon_list);
        pokemonRecyclerView.setAdapter(pokemonRecyclerViewAdapter);
        pokemonRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        pokemonRecyclerView.setLayoutManager(layoutManager);

        addPokemonHandler((Pokemon) getIntent().getSerializableExtra("POKEMON_ADD"));
        removePokemonHandler(getIntent().getStringExtra("POKEMON_REMOVE"));
        TextView collectedText = findViewById(R.id.pokedex_collected_text);
        collectedText.setText("Collected Pokemon: " + Integer.toString(PokedexSingleton.getInstance().size()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePokedex();
    }

    private void savePokedex() {
        FileOutputStream outputStream;
        try {
            Log.d(TAG, "Saving at: " + datafile.getAbsolutePath());
            outputStream = openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
            outputStream.write(new Gson().toJson(PokedexSingleton.getInstance()).getBytes());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPokedex() {
        final Type REVIEW_TYPE = new TypeToken<ArrayList<Pokemon>>() {}.getType();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(datafile.getAbsoluteFile()));
            ArrayList<Pokemon> data = new Gson().fromJson(bufferedReader, REVIEW_TYPE);
            PokedexSingleton.setInstance(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void removePokemonHandler(String pokemonName) {
        if(pokemonName != null) {
            ArrayList<Pokemon> instance = PokedexSingleton.getInstance();
            for(int i = 0; i < instance.size(); i++) {
                Pokemon p = PokedexSingleton.getInstance().get(i);
                if(p.getName().equals(pokemonName)) {
                    instance.remove(i);
                    return;
                }
            }
        }
        Log.d(TAG, "Pokemon not found");
    }

    private void addPokemonHandler(Pokemon pokemon) {
        if(pokemon != null) {
            for(Pokemon p : PokedexSingleton.getInstance()) {
                if(p.getName().equals(pokemon.getName())) {
                    Toast.makeText(PokedexActivity.this, "Pokemon already added", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Log.d(TAG, "Pokemon " + ((Pokemon) getIntent().getSerializableExtra("POKEMON_ADD")).getName() + " added");
            pokemonRecyclerViewAdapter.addPokemonToList(pokemon);
        }
    }

    private void startPokemonList() {
        Intent intent = new Intent(this, PokemonListActivity.class);
        startActivity(intent);
    }

    private void startPokemonInfo(Pokemon pokemon) {
        Intent intent = new Intent(this, PokemonInfoActivity.class);
        intent.putExtra("POKEMON_EXTRA", pokemon);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pokedex_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.pokedex_toolbar_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pokemonRecyclerViewAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pokemonRecyclerViewAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pokedex_toolbar_add:
                startPokemonList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void recyclerViewOnClickListener(View v, int position) {
        final Pokemon pokemon = pokemonRecyclerViewAdapter.getDataset().get(position);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            startPokemonInfo(pokemon);
        } else {
            final PokemonInfoFragment infoFragment = (PokemonInfoFragment) getSupportFragmentManager().findFragmentById(R.id.info_fragment);
            Call<PokemonInfo> pokemonInfoCall = apiService.getPokemonInfoByName(pokemon.getName());


            pokemonInfoCall.enqueue(new Callback<PokemonInfo>() {
                @Override
                public void onResponse(Call<PokemonInfo> call, Response<PokemonInfo> response) {
                    Log.d(TAG, "BODY: " + response.toString());
                    pokemon.setInfo(response.body());
                    infoFragment.setPokemon(pokemon);
                    infoFragment.getView().setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<PokemonInfo> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        }
    }
}


