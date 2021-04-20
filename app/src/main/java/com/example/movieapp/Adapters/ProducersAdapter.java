package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.Models.Producer;
import com.example.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProducersAdapter extends RecyclerView.Adapter<ProducersAdapter.ProducerHolder> {

    private Context context;
    private ArrayList<Producer> list;

    public ProducersAdapter(Context context, ArrayList<Producer> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ProducersAdapter.ProducerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_producer, parent, false);
        return new ProducersAdapter.ProducerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProducersAdapter.ProducerHolder holder, int position) {
        Producer producer = list.get(position);
//        Picasso.get().load(Constant.URL+"storage/actor_profiles/" + actor.getActor_img()).resize(200, 0).into(holder.imgActorProfile);
        holder.txtProducerName.setText(producer.getProducer_name());
        holder.txtProducerEmail.setText(producer.getProducer_email_address());
        holder.txtProducerWebsite.setText(producer.getProducer_website());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProducerHolder extends RecyclerView.ViewHolder {

        private TextView txtProducerName, txtProducerEmail, txtProducerWebsite;

        public ProducerHolder(@NonNull View itemView) {
            super(itemView);
            txtProducerName = itemView.findViewById(R.id.txtProducerName);
            txtProducerEmail = itemView.findViewById(R.id.txtProducerEmail);
            txtProducerWebsite = itemView.findViewById(R.id.txtProducerWebsite);
        }
    }

}
