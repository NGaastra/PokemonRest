package com.ntg.resttest.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ntg.resttest.R;
import com.ntg.resttest.adapters.PokemonRecyclerViewAdapter;
import com.ntg.resttest.domain.Pokemon;
import com.ntg.resttest.domain.PokemonInfo;
import com.ntg.resttest.domain.PokemonListLink;
import com.ntg.resttest.interfaces.ApiServiceSingleton;
import com.ntg.resttest.interfaces.PokeApi;
import com.ntg.resttest.interfaces.RecyclerViewClickListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonListActivity extends AppCompatActivity implements RecyclerViewClickListener {

    private static final String TAG = "POKEMONLIST";

    private PokemonRecyclerViewAdapter pokemonRecyclerViewAdapter;
    private static PokeApi apiService;

    private final ArrayList<Pokemon> pokemonList = new ArrayList<>();

    private RelativeLayout progressBar;

    private int offset;
    private boolean loadedData = false;

    //TODO: Fix offset going higher than max and making null returning API calls indefinitly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);

        progressBar = findViewById(R.id.loadingPanel);
        progressBar.setVisibility(View.GONE);

        Toolbar listToolbar = findViewById(R.id.list_toolbar);
        setSupportActionBar(listToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pokemonRecyclerViewAdapter = new PokemonRecyclerViewAdapter(this, this);
        RecyclerView pokemonRecyclerView = findViewById(R.id.pokemon_list);
        pokemonRecyclerView.setAdapter(pokemonRecyclerViewAdapter);
        pokemonRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        pokemonRecyclerView.setLayoutManager(layoutManager);
        pokemonRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (loadedData) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            Log.i(TAG, "End found loading new data at offset: " + offset);

                            loadedData = false;
                            offset += 20;
                            obtainData(offset);
                        }
                    }
                }
            }
        });
        apiService = ApiServiceSingleton.getInstance();

        offset = 0;
        obtainData(offset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.list_toolbar_search);

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final Pokemon pokemon = new Pokemon();
                Call<PokemonInfo> pokemonInfoCall = apiService.getPokemonInfo("https://pokeapi.co/api/v2/pokemon/" + query.toLowerCase());

                pokemonInfoCall.enqueue(new Callback<PokemonInfo>() {
                    @Override
                    public void onResponse(Call<PokemonInfo> call, Response<PokemonInfo> response) {
                        Log.d(TAG, "BODY: " + response.toString());
                        if(response.code() != 404) {
                            Log.d(TAG, "called");
                            pokemon.setName(response.body().getName());
                            pokemon.setUrl("https://pokeapi.co/api/v2/pokemon/" + response.body().getId());
                            startPokedex(pokemon);
                        } else {
                            Toast.makeText(PokemonListActivity.this, "Pokemon not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PokemonInfo> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void startPokedex(Pokemon pokemon) {
        Intent intent = new Intent(this, PokedexActivity.class);
        if(pokemon != null) {
            intent.putExtra("POKEMON_ADD", pokemon);
        }
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        startPokedex(null);
        return true;
    }

    @Override
    public void recyclerViewOnClickListener(View v, int position) {
        startPokedex(pokemonRecyclerViewAdapter.getDataset().get(position));
    }

    private void obtainAllData(){
        final Call<PokemonListLink> pokemonListLinkCall = apiService.getPokemonListLink(20, pokemonRecyclerViewAdapter.getItemCount());

        pokemonListLinkCall.enqueue(new Callback<PokemonListLink>() {
            @Override
            public void onResponse(Call<PokemonListLink> call, Response<PokemonListLink> response) {
                pokemonRecyclerViewAdapter.addLinkToList(response.body().getResults());
                if(response.body().getNext() != null) {
                    obtainData(pokemonRecyclerViewAdapter.getItemCount());
                }
            }

            @Override
            public void onFailure(Call<PokemonListLink> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void obtainData(int offset) {
        new PokeApiTask().execute(offset);
    }

    class PokeApiTask extends AsyncTask<Integer, Void, PokemonListLink> {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected PokemonListLink doInBackground(Integer... offset) {
            Call<PokemonListLink> pokemonListLinkCall = apiService.getPokemonListLink(20, offset[0]);

            pokemonListLinkCall.enqueue(new Callback<PokemonListLink>() {
                @Override
                public void onResponse(Call<PokemonListLink> call, Response<PokemonListLink> response) {
                    loadedData = true;
                    pokemonRecyclerViewAdapter.addLinkToList(response.body().getResults());
                }

                @Override
                public void onFailure(Call<PokemonListLink> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
            return null;
        }

        protected void onPostExecute(PokemonListLink result) {
            progressBar.setVisibility(View.GONE);
        }


    }
}
