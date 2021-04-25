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

public class FilmographyAdapter extends RecyclerView.Adapter<FilmographyAdapter.FilmographyHolder> {

    private Context context;
    private ArrayList<Movie> list;

    public FilmographyAdapter(Context context, ArrayList<Movie> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FilmographyAdapter.FilmographyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_filmography, parent, false);
        return new FilmographyAdapter.FilmographyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmographyAdapter.FilmographyHolder holder, int position) {
        Movie movie = list.get(position);
        Picasso.get().load(Constant.URL+"storage/posters/" + movie.getMovie_poster()).resize(200, 0).into(holder.imgFilmographyPoster);
        holder.txtFilmographyTitle.setText(movie.getMovie_title());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FilmographyHolder extends RecyclerView.ViewHolder {

        private TextView txtFilmographyTitle;
        private ImageView imgFilmographyPoster;

        public FilmographyHolder(@NonNull View itemView) {
            super(itemView);
            txtFilmographyTitle = itemView.findViewById(R.id.txtFilmographyTitle);
            imgFilmographyPoster = itemView.findViewById(R.id.imgFilmographyPoster);
        }
    }
}
