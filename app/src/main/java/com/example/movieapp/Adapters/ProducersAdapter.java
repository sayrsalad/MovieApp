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
    private OnItemListener onItemListener;

    public ProducersAdapter(Context context, ArrayList<Producer> list, OnItemListener onItemListener) {
        this.context = context;
        this.list = list;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public ProducersAdapter.ProducerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_producer, parent, false);
        return new ProducersAdapter.ProducerHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProducersAdapter.ProducerHolder holder, int position) {
        Producer producer = list.get(position);
        holder.txtProducerName.setText(producer.getProducer_name());
        holder.txtProducerEmail.setText(producer.getProducer_email_address());
        holder.txtProducerWebsite.setText(producer.getProducer_website());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProducerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView txtProducerName, txtProducerEmail, txtProducerWebsite;
        OnItemListener onItemListener;

        public ProducerHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            txtProducerName = itemView.findViewById(R.id.txtProducerName);
            txtProducerEmail = itemView.findViewById(R.id.txtProducerEmail);
            txtProducerWebsite = itemView.findViewById(R.id.txtProducerWebsite);

            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

            itemView.setClickable(true);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

}
