package com.ntg.resttest.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ntg.resttest.R;
import com.ntg.resttest.activities.PokemonInfoActivity;
import com.ntg.resttest.domain.Pokemon;
import com.ntg.resttest.domain.PokemonInfo;
import com.ntg.resttest.domain.PokemonListLink;
import com.ntg.resttest.interfaces.ApiServiceSingleton;
import com.ntg.resttest.interfaces.PokeApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonListAdapter extends ArrayAdapter<Pokemon> {

    private final String TAG = "PokemonListAdapter";
    private final int[] imgDim = {120, 120};

    private PokeApi apiService;

    private ArrayList<Pokemon> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        ImageView image;
    }

    public PokemonListAdapter(ArrayList<Pokemon> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
        apiService = ApiServiceSingleton.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pokemon pokemon = getItem(position);
        final ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.name);
            viewHolder.txtType = convertView.findViewById(R.id.type);
            viewHolder.image = convertView.findViewById(R.id.image);

            convertView.setTag(viewHolder);
        }  else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtName.setText(pokemon.getFormattedName());
        viewHolder.txtType.setText("# " + (position + 1));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                final Pokemon pokemon = dataSet.get(position);

                openInfo(pokemon);
            }
        });

        Glide.with(mContext)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(imgDim[0], imgDim[1])
                .into(viewHolder.image);

        return convertView;
    }

    public void openInfo(Pokemon pokemon){
        Intent intent = new Intent(mContext, PokemonInfoActivity.class);
        intent.putExtra("pokemon", pokemon);
        mContext.startActivity(intent);
    }

    public void addLinkToList(ArrayList<Pokemon> pokemonList){
        dataSet.addAll(pokemonList);
        notifyDataSetChanged();
    }
}
