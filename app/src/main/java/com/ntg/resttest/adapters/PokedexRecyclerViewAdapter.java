package com.ntg.resttest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ntg.resttest.R;
import com.ntg.resttest.domain.Pokemon;
import com.ntg.resttest.interfaces.RecyclerViewClickListener;
import com.ntg.resttest.interfaces.PokedexSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PokedexRecyclerViewAdapter extends RecyclerView.Adapter<PokedexRecyclerViewAdapter.ViewHolder>{

    private final int[] imgDim = {120, 120};

    private ArrayList<Pokemon> dataset;
    private ArrayList<Pokemon> filteredDataset;
    private Context context;
    private static RecyclerViewClickListener clickListener;

    public PokedexRecyclerViewAdapter(Context context, RecyclerViewClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        dataset = PokedexSingleton.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pokemon p = dataset.get(position);
        holder.nameTextView.setText(p.getFormattedName());
        holder.numberTextView.setText("#" + p.getNumber());

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

    private void sortData() {
        Collections.sort(PokedexSingleton.getInstance(), new Comparator<Pokemon>() {
            @Override
            public int compare(Pokemon p1, Pokemon p2) {
                return Integer.compare(p1.getNumber(), p2.getNumber());
            }
        });
    }

    public void addPokemonToList(Pokemon pokemon){
        PokedexSingleton.getInstance().add(pokemon);
        sortData();
        notifyDataSetChanged();
    }


    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                FilterResults filterResults = new FilterResults();
                if(charString.isEmpty()) {
                    filterResults.values = PokedexSingleton.getInstance();
                } else {
                    ArrayList<Pokemon> results = new ArrayList<>();
                    ArrayList<Pokemon> pokedex = PokedexSingleton.getInstance();

                    for(Pokemon p : pokedex) {
                        if(p.getName().contains(charString))
                            results.add(p);
                    }
                    filterResults.values = results;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataset = (ArrayList<Pokemon>) filterResults.values;

                sortData();
                notifyDataSetChanged();
            }
        };
    }

    public ArrayList<Pokemon> getDataset(){
        return dataset;
    }

    public void setDataset(ArrayList<Pokemon> dataset) {
        this.dataset = dataset;
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
