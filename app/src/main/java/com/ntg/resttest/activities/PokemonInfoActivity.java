package com.ntg.resttest.activities;

import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ntg.resttest.R;
import com.ntg.resttest.adapters.PokemonListAdapter;
import com.ntg.resttest.domain.Pokemon;
import com.ntg.resttest.domain.PokemonInfo;
import com.ntg.resttest.domain.PokemonListLink;
import com.ntg.resttest.domain.info.Move;
import com.ntg.resttest.interfaces.ApiServiceSingleton;
import com.ntg.resttest.interfaces.PokeApi;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonInfoActivity extends AppCompatActivity {

    private final String TAG = "PokemonInfoActivity";

    private final int[] imgDim = {400, 400};

    private Pokemon pokemon;
    private static PokeApi apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_info);

        apiService = ApiServiceSingleton.getInstance();
        pokemon = (Pokemon) getIntent().getSerializableExtra("POKEMON_EXTRA");
        Call<PokemonInfo> pokemonInfoCall = apiService.getPokemonInfoById(pokemon.getNumber());

        pokemonInfoCall.enqueue(new Callback<PokemonInfo>() {
            @Override
            public void onResponse(Call<PokemonInfo> call, Response<PokemonInfo> response) {
                Log.d(TAG, "BODY: " + response.toString());
                pokemon.setInfo(response.body());
                populateInfo();
            }

            @Override
            public void onFailure(Call<PokemonInfo> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });

    }

    public void populateInfo(){
        ImageView imageView = findViewById(R.id.info_image);
        TextView nameView = findViewById(R.id.info_name);
        TextView numberView = findViewById(R.id.info_number);
        TextView typeView = findViewById(R.id.info_type);
        ListView moveListView = findViewById(R.id.move_list);

        nameView.setText("Name \t" + pokemon.getFormattedName());
        numberView.setText("No. \t" + pokemon.getNumber());
        typeView.setText("Type \t" + pokemon.getInfo().getTypes().get(0).getType().getFormattedName());

        ArrayList<String> moveNameList = new ArrayList<>();
        for(Move m : pokemon.getInfo().getMoves()) {
            moveNameList.add(m.getMove().getFormattedName());
        }

        moveListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moveNameList));

        Glide.with(this)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(imgDim[0], imgDim[1])
                .into(imageView);

    }
}
