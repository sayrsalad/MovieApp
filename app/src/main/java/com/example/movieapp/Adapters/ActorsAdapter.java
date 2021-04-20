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

public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.ActorHolder> {

    private Context context;
    private ArrayList<Actor> list;

    public ActorsAdapter(Context context, ArrayList<Actor> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ActorsAdapter.ActorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_actor, parent, false);
        return new ActorsAdapter.ActorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorsAdapter.ActorHolder holder, int position) {
        Actor actor = list.get(position);
        Picasso.get().load(Constant.URL+"storage/actor_profiles/" + actor.getActor_img()).resize(200, 0).into(holder.imgActorProfile);
        holder.txtActorName.setText(actor.getActor_fname() + " " + actor.getActor_lname());
        holder.txtActorNotes.setText("     " + actor.getActor_notes());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ActorHolder extends RecyclerView.ViewHolder {

        private TextView txtActorName, txtActorNotes;
        private ImageView imgActorProfile;

        public ActorHolder(@NonNull View itemView) {
            super(itemView);
            txtActorName = itemView.findViewById(R.id.txtActorName);
            txtActorNotes = itemView.findViewById(R.id.txtActorNotes);
            imgActorProfile = itemView.findViewById(R.id.imgActorProfile);
        }
    }
}
