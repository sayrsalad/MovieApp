package com.example.movieapp.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.movieapp.Constant;
import com.example.movieapp.Fragments.ActorFragment;
import com.example.movieapp.Fragments.ProducerFragment;
import com.example.movieapp.HomeActivity;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Movie;
import com.example.movieapp.R;
import com.example.movieapp.UpdateActorActivity;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.ActorHolder> {

    private Context context;
    private ArrayList<Actor> list;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SharedPreferences sharedPreferences;

    public ActorsAdapter(Context context, ArrayList<Actor> list) {
        this.context = context;
        this.list = list;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
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
        holder.txtActorNotes.setText(actor.getActor_notes());

        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(list.get(position).getActor_ID()));
        viewBinderHelper.closeLayout(String.valueOf(list.get(position).getActor_ID()));

        holder.btnMoreActor.setOnClickListener( v -> {
            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.setContentView(R.layout.layout_actor_dialog);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            ArrayList<Movie> movies = actor.getMovies();

            ImageView imgDialogActorProfile  = dialog.findViewById(R.id.imgDialogActorProfile);
            TextView txtDialogActorName  = dialog.findViewById(R.id.txtDialogActorName);
            TextView txtDialogActorNotes = dialog.findViewById(R.id.txtDialogActorNotes);

            RecyclerView actorMovies = dialog.findViewById(R.id.actorMovies);
            actorMovies.setLayoutManager(new GridLayoutManager(dialog.getContext(), 3));

            Picasso.get().load(Constant.URL+"storage/actor_profiles/" + actor.getActor_img()).resize(0, 4000).into(imgDialogActorProfile);
            txtDialogActorName.setText(actor.getActor_fname() + " " + actor.getActor_lname());
            txtDialogActorNotes.setText(actor.getActor_notes());

            FilmographyAdapter filmographyAdapter = new FilmographyAdapter(dialog.getContext(), movies);
            actorMovies.setAdapter(filmographyAdapter);

            viewBinderHelper.closeLayout(String.valueOf(list.get(position).getActor_ID()));

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        holder.btnEditActor.setOnClickListener( v -> {
            Intent i = new Intent(((HomeActivity)context), UpdateActorActivity.class);
            i.putExtra("actor_ID", actor.getActor_ID());
            i.putExtra("actor_fname", actor.getActor_fname());
            i.putExtra("actor_lname", actor.getActor_lname());
            i.putExtra("actor_notes", actor.getActor_notes());
            i.putExtra("actor_img", actor.getActor_img());
            i.putExtra("position", position);
            context.startActivity(i);

            viewBinderHelper.closeLayout(String.valueOf(list.get(position).getActor_ID()));
        });

        holder.btnDeleteActor.setOnClickListener( v -> {
            viewBinderHelper.closeLayout(String.valueOf(list.get(position).getActor_ID()));
            delete(actor.getActor_ID(), position, holder);
        });
    }

    private void delete(int actor_id, int position, ActorHolder holder) {
        Dialog dialog = new Dialog(holder.itemView.getContext());
        dialog.setContentView(R.layout.custom_delete_alert_dialog);
        TextView txtDeleteDialogTitle = dialog.findViewById(R.id.txtDeleteDialogTitle);
        TextView txtDeleteDialogMessage = dialog.findViewById(R.id.txtDeleteDialogMessage);

        txtDeleteDialogTitle.setText("Delete Actor");
        txtDeleteDialogMessage.setText("Are you sure you want to delete this actor?");

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        dialog.show();

        btnYes.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(holder.itemView.getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Deleting Movie");
            progressDialog.show();

            StringRequest request = new StringRequest(Request.Method.DELETE, Constant.DELETE_ACTOR+"/"+actor_id, response -> {

                try {
                    JSONObject object = new JSONObject(response);
                    ActorFragment.refreshLayout.setRefreshing(true);
                    if (object.getBoolean("success")) {

                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();

                        ActorFragment.refreshLayout.setRefreshing(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                progressDialog.dismiss();
                StyleableToast.makeText(holder.itemView.getContext(), "Actor Deleted", R.style.CustomToast).show();
            }, error -> {
                dialog.dismiss();
                progressDialog.dismiss();
                error.printStackTrace();
                StyleableToast.makeText(holder.itemView.getContext(), "There was a problem deleting the actor", R.style.CustomToast).show();
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

    class ActorHolder extends RecyclerView.ViewHolder {

        private TextView txtActorName, txtActorNotes;
        private ImageView imgActorProfile, btnMoreActor, btnEditActor, btnDeleteActor;
        private SwipeRevealLayout swipeRevealLayout;

        public ActorHolder(@NonNull View itemView) {
            super(itemView);
            txtActorName = itemView.findViewById(R.id.txtActorName);
            txtActorNotes = itemView.findViewById(R.id.txtActorNotes);
            imgActorProfile = itemView.findViewById(R.id.imgActorProfile);

            swipeRevealLayout = itemView.findViewById(R.id.swipeActorLayout);

            btnMoreActor = itemView.findViewById(R.id.btnMoreActor);
            btnEditActor = itemView.findViewById(R.id.btnEditActor);
            btnDeleteActor = itemView.findViewById(R.id.btnDeleteActor);

        }
    }
}
