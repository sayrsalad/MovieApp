package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.ActorsAdapter;
import com.example.movieapp.Fragments.ActorFragment;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Genre;
import com.example.movieapp.Models.Movie;
import com.example.movieapp.Models.Role;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddActorRoleActivity extends AppCompatActivity {

    private int movie_ID = 0, actor_ID = 0, role_ID = 0;
    private TextInputEditText txtMovie, txtCharacter;
    private AutoCompleteTextView txtActor, txtRole;
    private SharedPreferences sharedPreferences;
    private Button btnAddCast;
    private ProgressDialog dialog;
    private ArrayList<Actor> actorArrayList;
    private ArrayList<Role> roleArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_actor_role);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        txtMovie = findViewById(R.id.txtMovie);
        txtActor = findViewById(R.id.txtActor);
        txtRole = findViewById(R.id.txtRole);
        txtCharacter = findViewById(R.id.txtCharacter);

        movie_ID = getIntent().getIntExtra("movie_ID", 0);
        txtMovie.setText(getIntent().getStringExtra("movie_title"));

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        btnAddCast = findViewById(R.id.btnAddCast);

        getActors();
        getRoles();

        txtActor.setOnItemClickListener((parent, view, position, rowId) -> {
            Actor a = actorArrayList.get(position);
            actor_ID = a.getActor_ID();
        });

        txtRole.setOnItemClickListener((parent, view, position, rowId) -> {
            Role r = roleArrayList.get(position);
            role_ID = r.getRole_ID();
        });

        btnAddCast.setOnClickListener( v -> {
            add();
        });
    }

    private void getActors() {
        ArrayList<String> arrayList = new ArrayList<>();
        actorArrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.ACTORS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("actors"));
                    if (array.length() < 4) {
                        txtActor.setDropDownHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    for ( int i = 0; i < array.length(); i++) {
                        JSONObject actorObject = array.getJSONObject(i);

                        Actor actor = new Actor();

                        actor.setActor_ID(actorObject.getInt("actor_ID"));

                        arrayList.add(actorObject.getString("actor_fname") + " " + actorObject.getString("actor_lname"));
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                this,
                                R.layout.custom_dropdown_item,
                                R.id.text_view_list_item,
                                arrayList
                        );

                        actorArrayList.add(actor);
                        txtActor.setAdapter(arrayAdapter);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("access_token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer" + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }

    private void getRoles() {
        ArrayList<String> arrayList = new ArrayList<>();
        roleArrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.ROLES, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("roles"));
                    if (array.length() < 4) {
                        txtRole.setDropDownHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    for ( int i = 0; i < array.length(); i++) {
                        JSONObject roleObject = array.getJSONObject(i);

                        Role role = new Role();

                        role.setRole_ID(roleObject.getInt("role_ID"));
                        role.setRole_name(roleObject.getString("role_name"));

                        arrayList.add(roleObject.getString("role_name"));
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                this,
                                R.layout.custom_dropdown_item,
                                R.id.text_view_list_item,
                                arrayList
                        );

                        roleArrayList.add(role);
                        txtRole.setAdapter(arrayAdapter);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("access_token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer" + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext() );
        queue.add(request);
    }

    private void add() {
        dialog.setMessage("Adding Cast");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_MOVIE_ACTOR_ROLE, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {



                    finish();

                }
                StyleableToast.makeText(getApplicationContext(), "Cast Added", R.style.CustomToast).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            StyleableToast.makeText(getApplicationContext(), "There was a problem adding the cast", R.style.CustomToast).show();
            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("access_token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer" + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("movie_ID", movie_ID+"");
                map.put("actor_ID", actor_ID+"");
                map.put("role_ID", role_ID+"");
                map.put("character_name", txtCharacter.getText().toString().trim());

                Log.d("movie_ID", movie_ID+"");
                Log.d("actor_ID", actor_ID+"");
                Log.d("role_ID", role_ID+"");
                Log.d("character_name", txtCharacter.getText().toString().trim());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddActorRoleActivity.this );
        queue.add(request);
    }

    public void cancelCast(View view) {
        super.onBackPressed();
    }
}