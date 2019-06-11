package com.ntg.resttest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.ntg.resttest.domain.Pokemon;
import com.ntg.resttest.ifs.RecyclerViewClickListener;

import java.util.ArrayList;

public class PokemonRecyclerViewAdapter extends RecyclerView.Adapter<PokemonRecyclerViewAdapter.ViewHolder>{

    private final int[] imgDim = {120, 120};

    private ArrayList<Pokemon> dataset;
    private Context context;
    private static RecyclerViewClickListener clickListener;

    public PokemonRecyclerViewAdapter(Context context, RecyclerViewClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        dataset = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pokemon p = dataset.get(position);
        holder.nameTextView.setText(p.getName());
        holder.numberTextView.setText("# " + (position + 1));


        Glide.with(context)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + p.getNumber() + ".png")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .override(imgDim[0], imgDim[1])
                .into(holder.picImageView);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addLinkToList(ArrayList<Pokemon> pokemonList) {
        dataset.addAll(pokemonList);
        notifyDataSetChanged();
    }

    public ArrayList<Pokemon> getDataset(){
        return dataset;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

     private TextView nameTextView;
     private TextView numberTextView;
     private ImageView picImageView;

     public ViewHolder(View itemView) {
         super(itemView);
        itemView.setOnClickListener(this);

         nameTextView = itemView.findViewById(R.id.name);
         numberTextView = itemView.findViewById(R.id.type);
         picImageView = itemView.findViewById(R.id.image);
     }

     @Override
     public void onClick(View v) {
         clickListener.recyclerViewOnClickListener(v, this.getLayoutPosition());
     }
 }
}
