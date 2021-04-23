package com.example.movieapp.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.CastsAdapter;
import com.example.movieapp.Adapters.MoviesAdapter;
import com.example.movieapp.AddMovieActivity;
import com.example.movieapp.Constant;
import com.example.movieapp.HomeActivity;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Certificate;
import com.example.movieapp.Models.Genre;
import com.example.movieapp.Models.Movie;
import com.example.movieapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieFragment extends Fragment implements MoviesAdapter.OnItemListener{
    private static final String TAG = "MovieFragment";
    private View view;
    public static RecyclerView recyclerView, movieCasts;
    public static ArrayList<Movie> arrayList;

    public static SwipeRefreshLayout refreshLayout;
    private MoviesAdapter moviesAdapter;
    private CastsAdapter castsAdapter;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton btnAddMovie;
    private static final int GALLERY_ADD_POST = 2;
    private Dialog dialog;
    private TextView txtDialogMovieTitle, txtDialogMovieStory;
    private ImageView imgDialogMoviePoster;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_movie_home, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerMovie);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeMovie);
        toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        btnAddMovie = view.findViewById(R.id.btnAddMovie);

        getMovies();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMovies();
            }
        });

        btnAddMovie.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_ADD_POST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_POST && resultCode== Activity.RESULT_OK){
            Uri imgUri = data.getData();
            Intent i = new Intent(this.getContext(), AddMovieActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
    }

    private void getMovies() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.MOVIES, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("movies"));
                    for ( int i = 0; i < array.length(); i++) {
                        JSONObject movieObject = array.getJSONObject(i);
                        JSONObject genreObject = movieObject.getJSONObject("genre");
                        JSONObject certificateObject = movieObject.getJSONObject("certificate");

                        Genre genre = new Genre();
                        genre.setGenre_ID(genreObject.getInt("genre_ID"));
                        genre.setGenre_name(genreObject.getString("genre_name"));

                        Certificate certificate = new Certificate();
                        certificate.setCertificate_ID(certificateObject.getInt("certificate_ID"));
                        certificate.setCertificate_name(certificateObject.getString("certificate_name"));

                        Movie movie = new Movie();
                        movie.setGenre(genre);
                        movie.setCertificate(certificate);
                        movie.setMovie_ID(movieObject.getInt("movie_ID"));
                        movie.setMovie_title(movieObject.getString("movie_title"));
                        movie.setMovie_story(movieObject.getString("movie_story"));
                        movie.setMovie_release_date(movieObject.getString("movie_release_date"));
                        movie.setMovie_film_duration(movieObject.getInt("movie_film_duration"));
                        movie.setMovie_additional_info(movieObject.getString("movie_additional_info"));
                        movie.setMovie_poster(movieObject.getString("movie_poster"));

                        JSONArray actorArray = movieObject.getJSONArray("actor");
                        ArrayList<Actor> actorArrayList = new ArrayList<Actor>();

                        for ( int a = 0; a < actorArray.length(); a++) {
                            JSONObject actorObject = actorArray.getJSONObject(a);

                            Actor actor = new Actor();
                            actor.setActor_ID(actorObject.getInt("actor_ID"));
                            actor.setActor_img(actorObject.getString("actor_img"));
                            actor.setActor_fname(actorObject.getString("actor_fname"));
                            actor.setActor_lname(actorObject.getString("actor_lname"));
                            actor.setActor_notes(actorObject.getString("actor_notes"));
                            actorArrayList.add(actor);
                        }

                        movie.setActor(actorArrayList);

                        arrayList.add(movie);
                    }

                    moviesAdapter = new MoviesAdapter(getContext(), arrayList, this);
                    recyclerView.setAdapter(moviesAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("access_token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer" + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        Movie movie = arrayList.get(position);
        dialog = new Dialog((HomeActivity)getContext());
        dialog.setContentView(R.layout.layout_movie_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        ArrayList<Actor> actors = movie.getActor();

        imgDialogMoviePoster  = dialog.findViewById(R.id.imgDialogMoviePoster);
        txtDialogMovieTitle  = dialog.findViewById(R.id.txtDialogMovieTitle);
        txtDialogMovieStory = dialog.findViewById(R.id.txtDialogMovieStory);

        movieCasts = dialog.findViewById(R.id.movieCasts);
        movieCasts.setLayoutManager(new GridLayoutManager(dialog.getContext(), 3));

        Picasso.get().load(Constant.URL+"storage/posters/" + movie.getMovie_poster()).resize(0, 4000).into(imgDialogMoviePoster);
        txtDialogMovieTitle.setText(movie.getMovie_title());
        txtDialogMovieStory.setText(movie.getMovie_story());

        castsAdapter = new CastsAdapter(dialog.getContext(), actors);
        movieCasts.setAdapter(castsAdapter);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }


}
