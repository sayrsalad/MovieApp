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
import com.example.movieapp.Fragments.MovieFragment;
import com.example.movieapp.HomeActivity;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Movie;
import com.example.movieapp.R;
import com.example.movieapp.UpdateMovieActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder>{

    private Context context;
    private ArrayList<Movie> list;
    private OnItemListener onItemListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SharedPreferences sharedPreferences;

    public MoviesAdapter(Context context, ArrayList<Movie> list, OnItemListener onItemListener) {
        this.context = context;
        this.list = list;
        this.onItemListener = onItemListener;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_movie, parent, false);
        return new MovieHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Movie movie = list.get(position);
        Picasso.get().load(Constant.URL+"storage/posters/" + movie.getMovie_poster()).resize(500, 0).into(holder.imgMoviePoster);
        holder.txtMovieTitle.setText(movie.getMovie_title() + " ("+movie.getMovie_release_date().substring(0,4)+")");
        holder.btnEditMovie.setId(movie.getMovie_ID());
        holder.btnDeleteMovie.setId(movie.getMovie_ID());
        holder.movieView.setId(movie.getMovie_ID());

        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(list.get(position).getMovie_ID()));
        viewBinderHelper.closeLayout(String.valueOf(list.get(position).getMovie_ID()));

        holder.btnMoreMovie.setOnClickListener( v -> {
            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.setContentView(R.layout.layout_movie_dialog);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            ArrayList<Actor> actors = movie.getActor();

            ImageView imgDialogMoviePoster  = dialog.findViewById(R.id.imgDialogMoviePoster);
            TextView txtDialogMovieTitle  = dialog.findViewById(R.id.txtDialogMovieTitle);
            TextView txtDialogMovieStory = dialog.findViewById(R.id.txtDialogMovieStory);

            RecyclerView movieCasts = dialog.findViewById(R.id.movieCasts);
            movieCasts.setLayoutManager(new GridLayoutManager(dialog.getContext(), 3));

            Picasso.get().load(Constant.URL+"storage/posters/" + movie.getMovie_poster()).resize(0, 4000).into(imgDialogMoviePoster);
            txtDialogMovieTitle.setText(movie.getMovie_title());
            txtDialogMovieStory.setText(movie.getMovie_story());

            CastsAdapter castsAdapter = new CastsAdapter(dialog.getContext(), actors);
            movieCasts.setAdapter(castsAdapter);

            viewBinderHelper.closeLayout(String.valueOf(list.get(position).getMovie_ID()));

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        holder.btnEditMovie.setOnClickListener( v -> {
            Intent i = new Intent(((HomeActivity)context), UpdateMovieActivity.class);
            i.putExtra("movie_ID", movie.getMovie_ID());
            i.putExtra("movie_title", movie.getMovie_title());
            i.putExtra("movie_story", movie.getMovie_story());
            i.putExtra("movie_release_date", movie.getMovie_release_date());
            i.putExtra("movie_film_duration", movie.getMovie_film_duration());
            i.putExtra("movie_additional_info", movie.getMovie_additional_info());
            i.putExtra("genre_ID", movie.getGenre_ID());
            i.putExtra("genre_name", movie.getGenre_name());
            i.putExtra("certificate_ID", movie.getCertificate_ID());
            i.putExtra("certificate_name", movie.getCertificate_name());
            i.putExtra("movie_poster", movie.getMovie_poster());
            i.putExtra("position", position);
            context.startActivity(i);

            viewBinderHelper.closeLayout(String.valueOf(list.get(position).getMovie_ID()));
        });

        holder.btnDeleteMovie.setOnClickListener( v -> {
            viewBinderHelper.closeLayout(String.valueOf(list.get(position).getMovie_ID()));
            delete(movie.getMovie_ID(), position, holder);
        });
    }

    private void delete(int movie_id, int position, MovieHolder holder) {
        Dialog dialog = new Dialog(holder.itemView.getContext());
        dialog.setContentView(R.layout.custom_delete_alert_dialog);

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

            StringRequest request = new StringRequest(Request.Method.DELETE, Constant.DELETE_MOVIES+"/"+movie_id, response -> {

                try {
                    JSONObject object = new JSONObject(response);
                    MovieFragment.refreshLayout.setRefreshing(true);
                    if (object.getBoolean("success")) {

                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();

                        MovieFragment.refreshLayout.setRefreshing(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                progressDialog.dismiss();
            }, error -> {
                dialog.dismiss();
                progressDialog.dismiss();
                error.printStackTrace();
                Toast.makeText(holder.itemView.getContext(), "There was a problem deleting the movie", Toast.LENGTH_SHORT).show();
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

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View movieView;
        private TextView txtMovieTitle, txtMovieID;
        private ImageView imgMoviePoster, btnMoreMovie, btnEditMovie, btnDeleteMovie;
        OnItemListener onItemListener;
        private SwipeRevealLayout swipeRevealLayout;

        public MovieHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            movieView = itemView;
            txtMovieTitle = itemView.findViewById(R.id.txtMovieTitle);
            txtMovieID = itemView.findViewById(R.id.txtMovieID);
            imgMoviePoster = itemView.findViewById(R.id.imgMoviePoster);

            btnMoreMovie= itemView.findViewById(R.id.btnMoreMovie);
            btnEditMovie = itemView.findViewById(R.id.btnEditMovie);
            btnDeleteMovie = itemView.findViewById(R.id.btnDeleteMovie);

            swipeRevealLayout = itemView.findViewById(R.id.swipeMovieLayout);

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
