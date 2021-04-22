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
    private OnItemListener onItemListener;

    public MoviesAdapter(Context context, ArrayList<Movie> list, OnItemListener onItemListener) {
        this.context = context;
        this.list = list;
        this.onItemListener = onItemListener;
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
        holder.txtMovieTitle.setText(movie.getMovie_title());
        holder.txtMovieReleaseYear.setText("("+movie.getMovie_release_date().substring(0,4)+")");
        holder.txtMovieID.setText(movie.getMovie_ID_string());
        holder.movie.setId(movie.getMovie_ID());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View movie;
        private TextView txtMovieTitle, txtMovieReleaseYear, txtMovieID;
        private ImageView imgMoviePoster;
        OnItemListener onItemListener;

        public MovieHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            movie = itemView;
            txtMovieTitle = itemView.findViewById(R.id.txtMovieTitle);
            txtMovieReleaseYear = itemView.findViewById(R.id.txtMovieReleaseYear);
            txtMovieID = itemView.findViewById(R.id.txtMovieID);
            imgMoviePoster = itemView.findViewById(R.id.imgMoviePoster);

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
