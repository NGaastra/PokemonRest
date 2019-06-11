package com.ntg.resttest.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ntg.resttest.R;
import com.ntg.resttest.adapters.PokemonListAdapter;
import com.ntg.resttest.adapters.PokemonRecyclerViewAdapter;
import com.ntg.resttest.domain.Pokemon;
import com.ntg.resttest.domain.PokemonInfo;
import com.ntg.resttest.domain.PokemonListLink;
import com.ntg.resttest.ifs.RecyclerViewClickListener;
import com.ntg.resttest.interfaces.ApiServiceSingleton;
import com.ntg.resttest.interfaces.PokeApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.GridLayout.HORIZONTAL;

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

    public void viewPokemonInfo(Pokemon pokemon){
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent intent = new Intent(this, PokemonInfoActivity.class);
            intent.putExtra("POKEMON_EXTRA", pokemon);
            startActivity(intent);
        } else {
            PokemonInfoFragment infoFragment = (PokemonInfoFragment) getSupportFragmentManager().findFragmentById(R.id.info_fragment);
            infoFragment.setPokemon(pokemon);
        }
    }

    @Override
    public void recyclerViewOnClickListener(View v, int position) {
        final Pokemon pokemon = pokemonRecyclerViewAdapter.getDataset().get(position);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent intent = new Intent(this, PokemonInfoActivity.class);
            intent.putExtra("POKEMON_EXTRA", pokemon);
            startActivity(intent);
        } else {
            final PokemonInfoFragment infoFragment = (PokemonInfoFragment) getSupportFragmentManager().findFragmentById(R.id.info_fragment);
            Call<PokemonInfo> pokemonInfoCall = apiService.getPokemonInfoById(pokemon.getNumber());

            pokemonInfoCall.enqueue(new Callback<PokemonInfo>() {
                @Override
                public void onResponse(Call<PokemonInfo> call, Response<PokemonInfo> response) {
                    Log.d(TAG, "BODY: " + response.toString());
                    pokemon.setInfo(response.body());
                    infoFragment.setPokemon(pokemon);
                }

                @Override
                public void onFailure(Call<PokemonInfo> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        }
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
