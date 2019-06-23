package com.ntg.resttest.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ntg.resttest.R;
import com.ntg.resttest.domain.Pokemon;
import com.ntg.resttest.domain.info.Move;

import java.util.ArrayList;

public class PokemonInfoFragment extends Fragment {

    private final int[] imgDim = {100, 100};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pokemon_info, container, false);
    }

    public void setPokemon(Pokemon pokemon) {
        ImageView imageView = getView().findViewById(R.id.info_image);
        TextView nameView = getView().findViewById(R.id.info_name);
        TextView numberView = getView().findViewById(R.id.info_number);
        TextView typeView = getView().findViewById(R.id.info_type);
        ListView moveListView = getView().findViewById(R.id.move_list);

        nameView.setText("Name \t" + pokemon.getFormattedName());
        numberView.setText("No. \t" + pokemon.getNumber());
        typeView.setText("Type \t" + pokemon.getInfo().getTypes().get(0).getType().getFormattedName());

        ArrayList<String> moveNameList = new ArrayList<>();
        for (Move m : pokemon.getInfo().getMoves()) {
            moveNameList.add(m.getMove().getFormattedName());
        }

        moveListView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, moveNameList));

        Glide.with(this)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(imgDim[0], imgDim[1])
                .into(imageView);

        getView().setVisibility(View.VISIBLE);
    }

}
