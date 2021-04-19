package com.example.movieapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.movieapp.Constant;
import com.example.movieapp.HomeActivity;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Movie;
import com.example.movieapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Actor> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private ActorsAdapter actorsAdapter;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;

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

        getActors();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActors();
            }
        });
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

                        Actor actor = new Actor();
                        actor.setActor_ID(actorObject.getInt("actor_ID"));
                        actor.setActor_fname(actorObject.getString("actor_fname"));
                        actor.setActor_lname(actorObject.getString("actor_lname"));
                        actor.setActor_notes(actorObject.getString("actor_notes"));
                        actor.setActor_status(actorObject.getString("actor_status"));
                        actor.setActor_img(actorObject.getString("actor_img"));

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
