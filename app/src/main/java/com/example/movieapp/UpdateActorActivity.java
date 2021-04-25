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
import com.example.movieapp.Fragments.ActorFragment;
import com.example.movieapp.Fragments.MovieFragment;
import com.example.movieapp.Models.Actor;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateActorActivity extends AppCompatActivity {

    private int position = 0, actor_ID = 0;
    private static final int GALLERY_CHANGE_POST = 3;
    private ImageView imgUpdateActorProfile;
    private TextInputLayout txtLayoutFirstName, txtLayoutLastName, txtLayoutNotes;
    private TextInputEditText txtFirstName, txtLastName, txtNotes;
    private Button btnUpdateActor;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_actor);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnUpdateActor = findViewById(R.id.btnUpdateActor);
        imgUpdateActorProfile = findViewById(R.id.imgUpdateActorProfile);

        txtLayoutFirstName = findViewById(R.id.txtLayoutFirstName);
        txtLayoutLastName = findViewById(R.id.txtLayoutLastName);
        txtLayoutNotes = findViewById(R.id.txtLayoutNotes);

        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtNotes = findViewById(R.id.txtNotes);

        txtFirstName.setText(getIntent().getStringExtra("actor_fname"));
        txtLastName.setText(getIntent().getStringExtra("actor_lname"));
        txtNotes.setText(getIntent().getStringExtra("actor_notes"));

        if (getIntent().getBooleanExtra("fromGallery", false)) {
            imgUpdateActorProfile.setImageURI(getIntent().getData());
        } else {
            Picasso.get().load(Constant.URL+"storage/actor_profiles/" + getIntent().getStringExtra("actor_img")).resize(500, 0).into(imgUpdateActorProfile);
        }

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        position = getIntent().getIntExtra("position", 0);
        actor_ID = getIntent().getIntExtra("actor_ID", 0);

        btnUpdateActor.setOnClickListener( v -> {
            update();
        });
    }

    private void update() {
        dialog.setMessage("Updating Actor");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Constant.UPDATE_ACTOR+"/"+actor_ID, response -> {

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

                    ActorFragment.arrayList.set(position, actor);

                    ActorFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    ActorFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Actor Updated", Toast.LENGTH_SHORT).show();
                    finish();

                    ActorFragment.refreshLayout.setRefreshing(false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "There was a problem updating the actor", Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(UpdateActorActivity.this );
        queue.add(request);
    }

    public void cancelActor(View view) {
        super.onBackPressed();
    }

    public void changeActorProfile(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        i.putExtra("fromGallery", true);
        startActivityForResult(i, GALLERY_CHANGE_POST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CHANGE_POST && resultCode == Activity.RESULT_OK) {
            Uri imgUri = data.getData();
            imgUpdateActorProfile.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
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