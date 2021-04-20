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
import com.example.movieapp.Models.Movie;
import com.example.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder>{

    private Context context;
    private ArrayList<Movie> list;

    public MoviesAdapter(Context context, ArrayList<Movie> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_movie, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Movie movie = list.get(position);
        Picasso.get().load(Constant.URL+"storage/posters/" + movie.getMovie_poster()).resize(300, 0).into(holder.imgMoviePoster);
        holder.txtMovieTitle.setText(movie.getMovie_title());
        holder.txtMovieDesc.setText("     " + movie.getMovie_story());
        holder.txtMovieRelease.setText(movie.getMovie_release_date());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        private TextView txtMovieTitle, txtMovieDesc, txtMovieRelease;
        private ImageView imgMoviePoster;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            txtMovieTitle = itemView.findViewById(R.id.txtMovieTitle);
            txtMovieDesc = itemView.findViewById(R.id.txtMovieDesc);
            txtMovieRelease = itemView.findViewById(R.id.txtMovieRelease);
            imgMoviePoster = itemView.findViewById(R.id.imgMoviePoster);
        }
    }
}
