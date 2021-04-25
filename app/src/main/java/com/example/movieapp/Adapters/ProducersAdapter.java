package com.example.movieapp.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.movieapp.Constant;
import com.example.movieapp.Fragments.MovieFragment;
import com.example.movieapp.Fragments.ProducerFragment;
import com.example.movieapp.HomeActivity;
import com.example.movieapp.Models.Producer;
import com.example.movieapp.R;
import com.example.movieapp.UpdateMovieActivity;
import com.example.movieapp.UpdateProducerActivity;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProducersAdapter extends RecyclerView.Adapter<ProducersAdapter.ProducerHolder> {

    private Context context;
    private ArrayList<Producer> list;
    private OnItemListener onItemListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SharedPreferences sharedPreferences;

    public ProducersAdapter(Context context, ArrayList<Producer> list, OnItemListener onItemListener) {
        this.context = context;
        this.list = list;
        this.onItemListener = onItemListener;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
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

        holder.btnEditProducer.setOnClickListener( v -> {
            Intent i = new Intent(((HomeActivity)context), UpdateProducerActivity.class);
            i.putExtra("producer_ID", producer.getProducer_ID());
            i.putExtra("producer_name", producer.getProducer_name());
            i.putExtra("producer_email_address", producer.getProducer_email_address());
            i.putExtra("producer_website", producer.getProducer_website());

            i.putExtra("position", position);
            context.startActivity(i);

            viewBinderHelper.closeLayout(String.valueOf(list.get(position).getProducer_ID()));
        });

        holder.btnDeleteProducer.setOnClickListener( v -> {
            viewBinderHelper.closeLayout(String.valueOf(list.get(position).getProducer_ID()));
            delete(producer.getProducer_ID(), position, holder);
        });

    }

    private void delete(int producer_id, int position, ProducerHolder holder) {
        Dialog dialog = new Dialog(holder.itemView.getContext());
        dialog.setContentView(R.layout.custom_delete_alert_dialog);
        TextView txtDeleteDialogTitle = dialog.findViewById(R.id.txtDeleteDialogTitle);
        TextView txtDeleteDialogMessage = dialog.findViewById(R.id.txtDeleteDialogMessage);

        txtDeleteDialogTitle.setText("Delete Producer");
        txtDeleteDialogMessage.setText("Are you sure you want to delete this producer?");

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        dialog.show();

        btnYes.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(holder.itemView.getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Deleting Producer");
            progressDialog.show();

            StringRequest request = new StringRequest(Request.Method.DELETE, Constant.DELETE_PRODUCER+"/"+producer_id, response -> {

                try {
                    JSONObject object = new JSONObject(response);
                    ProducerFragment.refreshLayout.setRefreshing(true);
                    if (object.getBoolean("success")) {

                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();

                        ProducerFragment.refreshLayout.setRefreshing(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                progressDialog.dismiss();
                StyleableToast.makeText(holder.itemView.getContext(), "Producer Deleted", R.style.CustomToast).show();
            }, error -> {
                dialog.dismiss();
                progressDialog.dismiss();
                error.printStackTrace();
                StyleableToast.makeText(holder.itemView.getContext(), "There was a problem deleting the producer", R.style.CustomToast).show();
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String token = sharedPreferences.getString("access_token", "");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Authorization", "Bearer" + token);
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(holder.itemView.getContext());
            queue.add(request);
        });

        btnNo.setOnClickListener(v -> {
            dialog.cancel();
        });
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
