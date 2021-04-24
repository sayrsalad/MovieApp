package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.movieapp.Models.Producer;
import com.example.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProducersAdapter extends RecyclerView.Adapter<ProducersAdapter.ProducerHolder> {

    private Context context;
    private ArrayList<Producer> list;
    private OnItemListener onItemListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

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
        holder.btnEditProducer.setId(producer.getProducer_ID());
        holder.btnDeleteProducer.setId(producer.getProducer_ID());

        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(list.get(position).getProducer_ID()));
        viewBinderHelper.closeLayout(String.valueOf(list.get(position).getProducer_ID()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProducerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView txtProducerName, txtProducerEmail, txtProducerWebsite;
        private ImageView btnEditProducer, btnDeleteProducer;
        OnItemListener onItemListener;
        private SwipeRevealLayout swipeRevealLayout;

        public ProducerHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            txtProducerName = itemView.findViewById(R.id.txtProducerName);
            txtProducerEmail = itemView.findViewById(R.id.txtProducerEmail);
            txtProducerWebsite = itemView.findViewById(R.id.txtProducerWebsite);

            btnEditProducer = itemView.findViewById(R.id.btnEditProducer);
            btnDeleteProducer = itemView.findViewById(R.id.btnDeleteProducer);

            swipeRevealLayout = itemView.findViewById(R.id.swipeProducerLayout);

            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);

            itemView.setClickable(true);

            btnEditProducer.setOnClickListener( v -> {
                Toast.makeText(context, "Edit" + String.valueOf(btnEditProducer.getId()), Toast.LENGTH_SHORT).show();
            });

            btnDeleteProducer.setOnClickListener( v -> {
                Toast.makeText(context, "Delete" + String.valueOf(btnDeleteProducer.getId()), Toast.LENGTH_SHORT).show();
            });
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
