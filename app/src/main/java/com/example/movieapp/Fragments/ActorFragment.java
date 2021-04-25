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
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.ActorsAdapter;
import com.example.movieapp.Adapters.MoviesAdapter;
import com.example.movieapp.AddActorActivity;
import com.example.movieapp.AddMovieActivity;
import com.example.movieapp.Constant;
import com.example.movieapp.HomeActivity;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Movie;
import com.example.movieapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorFragment extends Fragment {
    private View view;
    public static  RecyclerView recyclerView;
    public static  ArrayList<Actor> arrayList;
    public static SwipeRefreshLayout refreshLayout;
    private ActorsAdapter actorsAdapter;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton btnAddActor;
    private static final int GALLERY_ADD_POST = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_actor_home, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerActor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeActor);
        toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        btnAddActor = view.findViewById(R.id.btnAddActor);

        getActors();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActors();
            }
        });

        btnAddActor.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_ADD_POST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==GALLERY_ADD_POST && resultCode== Activity.RESULT_OK){
            Uri imgUri = data.getData();
            Intent i = new Intent(this.getContext(), AddActorActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
    }

    private void getActors() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.ACTORS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("actors"));
                    for ( int i = 0; i < array.length(); i++) {
                        JSONObject actorObject = array.getJSONObject(i);
                        JSONArray movieArray = actorObject.getJSONArray("movie");

                        Actor actor = new Actor();
                        actor.setActor_ID(actorObject.getInt("actor_ID"));
                        actor.setActor_fname(actorObject.getString("actor_fname"));
                        actor.setActor_lname(actorObject.getString("actor_lname"));
                        actor.setActor_notes(actorObject.getString("actor_notes"));
                        actor.setActor_status(actorObject.getString("actor_status"));
                        actor.setActor_img(actorObject.getString("actor_img"));

                        ArrayList<Movie> movieArrayList = new ArrayList<Movie>();

                        for ( int a = 0; a < movieArray.length(); a++) {
                            JSONObject movieObject = movieArray.getJSONObject(a);

                            Movie movie = new Movie();

                            movie.setMovie_ID(movieObject.getInt("movie_ID"));
                            movie.setMovie_title(movieObject.getString("movie_title"));
                            movie.setMovie_story(movieObject.getString("movie_story"));
                            movie.setMovie_release_date(movieObject.getString("movie_release_date"));
                            movie.setMovie_film_duration(movieObject.getInt("movie_film_duration"));
                            movie.setMovie_additional_info(movieObject.getString("movie_additional_info"));
                            movie.setGenre_ID(movieObject.getInt("genre_ID"));
                            movie.setCertificate_ID(movieObject.getInt("certificate_ID"));
                            movie.setMovie_poster(movieObject.getString("movie_poster"));
                            movie.setMovie_status(movieObject.getString("movie_status"));

                            movieArrayList.add(movie);
                        }

                        actor.setMovies(movieArrayList);

                        arrayList.add(actor);
                    }

                    actorsAdapter = new ActorsAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(actorsAdapter);
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
