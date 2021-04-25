package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.ActorsAdapter;
import com.example.movieapp.Fragments.ActorFragment;
import com.example.movieapp.Fragments.MovieFragment;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Certificate;
import com.example.movieapp.Models.Genre;
import com.example.movieapp.Models.Movie;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddActorActivity extends AppCompatActivity {
    private static final int GALLERY_CHANGE_POST = 3;
    private ImageView imgAddActorProfile;
    private TextInputLayout txtLayoutFirstName, txtLayoutLastName, txtLayoutNotes;
    private TextInputEditText txtFirstName, txtLastName, txtNotes;
    private Button btnAddActor;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_actor);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddActor = findViewById(R.id.btnAddActor);
        imgAddActorProfile = findViewById(R.id.imgAddActorProfile);

        txtLayoutFirstName = findViewById(R.id.txtLayoutFirstName);
        txtLayoutLastName = findViewById(R.id.txtLayoutLastName);
        txtLayoutNotes = findViewById(R.id.txtLayoutNotes);

        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtNotes = findViewById(R.id.txtNotes);

        imgAddActorProfile.setImageURI(getIntent().getData());

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnAddActor.setOnClickListener( v -> {
            add();
        });
    }

    private void add() {
        dialog.setMessage("Adding Actor");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_ACTOR, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    ActorFragment.refreshLayout.setRefreshing(true);

                    JSONObject actorObject = object.getJSONObject("actor");

                    Actor actor = new Actor();
                    actor.setActor_ID(actorObject.getInt("actor_ID"));
                    actor.setActor_fname(actorObject.getString("actor_fname"));
                    actor.setActor_lname(actorObject.getString("actor_lname"));
                    actor.setActor_notes(actorObject.getString("actor_notes"));
                    actor.setActor_status(actorObject.getString("actor_status"));
                    actor.setActor_img(actorObject.getString("actor_img"));

                    ActorFragment.arrayList.add(0, actor);

                    ActorFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    ActorFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();

                    ActorFragment.refreshLayout.setRefreshing(false);
                }
                StyleableToast.makeText(getApplicationContext(), "Actor Added", R.style.CustomToast).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            StyleableToast.makeText(getApplicationContext(), "There was a problem adding the actor", R.style.CustomToast).show();
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
                map.put("actor_fname", txtFirstName.getText().toString().trim());
                map.put("actor_lname", txtLastName.getText().toString().trim());
                map.put("actor_notes", txtNotes.getText().toString().trim());
                map.put("actor_img", bitmapToString(bitmap));
                map.put("actor_status", "active");

                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddActorActivity.this );
        queue.add(request);
    }

    public void cancelActor(View view) {
        super.onBackPressed();
    }

    public void changeActorProfile(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, GALLERY_CHANGE_POST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CHANGE_POST && resultCode == Activity.RESULT_OK) {
            Uri imgUri = data.getData();
            imgAddActorProfile.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }

        return "";
    }
}