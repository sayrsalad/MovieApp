package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Constant;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CastsAdapter extends RecyclerView.Adapter<CastsAdapter.CastHolder> {

    private Context context;
    private ArrayList<Actor> list;

    public CastsAdapter(Context context, ArrayList<Actor> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CastsAdapter.CastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cast, parent, false);
        return new CastsAdapter.CastHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastsAdapter.CastHolder holder, int position) {
        Actor actor = list.get(position);
        Picasso.get().load(Constant.URL+"storage/actor_profiles/" + actor.getActor_img()).resize(200, 0).into(holder.imgCastProfile);
        holder.txtCastName.setText(actor.getActor_fname() + " " + actor.getActor_lname());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CastHolder extends RecyclerView.ViewHolder {

        private TextView txtCastName;
        private ImageView imgCastProfile;

        public CastHolder(@NonNull View itemView) {
            super(itemView);
            txtCastName = itemView.findViewById(R.id.txtCastName);
            imgCastProfile = itemView.findViewById(R.id.imgCastProfile);
        }
    }
}
