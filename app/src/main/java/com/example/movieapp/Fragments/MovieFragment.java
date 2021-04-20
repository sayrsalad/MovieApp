package com.example.movieapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.MoviesAdapter;
import com.example.movieapp.AddMovieActivity;
import com.example.movieapp.Constant;
import com.example.movieapp.HomeActivity;
import com.example.movieapp.Models.Movie;
import com.example.movieapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Movie> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private MoviesAdapter moviesAdapter;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton btnAddMovie;
    private static final int GALLERY_ADD_POST = 2;

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

                        Movie movie = new Movie();
                        movie.setMovie_ID(movieObject.getInt("movie_ID"));
                        movie.setMovie_title(movieObject.getString("movie_title"));
                        movie.setMovie_story(movieObject.getString("movie_story"));
                        movie.setMovie_release_date(movieObject.getString("movie_release_date"));
                        movie.setMovie_film_duration(movieObject.getInt("movie_film_duration"));
                        movie.setMovie_additional_info(movieObject.getString("movie_additional_info"));
                        movie.setMovie_poster(movieObject.getString("movie_poster"));

                        arrayList.add(movie);
                    }

                    moviesAdapter = new MoviesAdapter(getContext(), arrayList);
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
}
