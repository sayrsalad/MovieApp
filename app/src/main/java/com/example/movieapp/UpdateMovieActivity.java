package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Fragments.MovieFragment;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Certificate;
import com.example.movieapp.Models.Genre;
import com.example.movieapp.Models.Movie;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateMovieActivity extends AppCompatActivity {

    private int position = 0, movie_ID = 0;
    private static final int GALLERY_CHANGE_POST = 3;
    private Button btnUpdateMovie;
    private ImageView imgUpdateMoviePoster;
    private TextInputLayout txtLayoutTitle, txtLayoutStory, txtLayoutReleaseDate, txtLayoutFilmDuration, txtLayoutAdditionalInfo, txtLayoutGenre, txtLayoutCertificate;
    private TextInputEditText txtTitle, txtStory, txtReleaseDate, txtFilmDuration, txtAdditionalInfo;
    private AutoCompleteTextView txtGenre, txtCertificate;
    private Bitmap bitmap = null;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private SharedPreferences sharedPreferences;
    private ArrayList<Genre> genreArrayList;
    private Genre genre;
    private ArrayList<Certificate> certificateArrayList;
    private Certificate certificate;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_movie);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnUpdateMovie = findViewById(R.id.btnUpdateMovie);
        imgUpdateMoviePoster = findViewById(R.id.imgUpdateMoviePoster);

        txtTitle = findViewById(R.id.txtTitle);
        txtStory = findViewById(R.id.txtStory);
        txtReleaseDate = findViewById(R.id.txtReleaseDate);
        txtFilmDuration = findViewById(R.id.txtFilmDuration);
        txtAdditionalInfo = findViewById(R.id.txtAdditionalInfo);
        txtGenre = findViewById(R.id.txtGenre);
        txtCertificate = findViewById(R.id.txtCertificate);

        txtLayoutTitle = findViewById(R.id.txtLayoutTitle);
        txtLayoutStory = findViewById(R.id.txtLayoutStory);
        txtLayoutReleaseDate = findViewById(R.id.txtLayoutReleaseDate);
        txtLayoutFilmDuration = findViewById(R.id.txtLayoutFilmDuration);
        txtLayoutAdditionalInfo = findViewById(R.id.txtLayoutAdditionalInfo);
        txtLayoutGenre = findViewById(R.id.txtLayoutGenre);
        txtLayoutCertificate = findViewById(R.id.txtLayoutCertificate);

        if (getIntent().getBooleanExtra("fromGallery", false)) {
            imgUpdateMoviePoster.setImageURI(getIntent().getData());
        } else {
            Picasso.get().load(Constant.URL+"storage/posters/" + getIntent().getStringExtra("movie_poster")).resize(500, 0).into(imgUpdateMoviePoster);
        }

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        getGenres();
        getCertificates();

        position = getIntent().getIntExtra("position", 0);
        movie_ID = getIntent().getIntExtra("movie_ID", 0);

        txtTitle.setText(getIntent().getStringExtra("movie_title"));
        txtStory.setText(getIntent().getStringExtra("movie_story"));
        txtReleaseDate.setText(getIntent().getStringExtra("movie_release_date"));
        txtFilmDuration.setText(String.valueOf(getIntent().getIntExtra("movie_film_duration", 0)));
        txtAdditionalInfo.setText(getIntent().getStringExtra("movie_additional_info"));
        txtGenre.setText(getIntent().getStringExtra("genre_name"));
        txtCertificate.setText(getIntent().getStringExtra("certificate_name"));

        txtGenre.setId(getIntent().getIntExtra("genre_ID", 0));
        txtCertificate.setId(getIntent().getIntExtra("certificate_ID", 0));

        txtReleaseDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    UpdateMovieActivity.this,
                    R.style.MyDialogTheme,
                    dateSetListener,
                    year, month, day);
            dialog.show();
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + month + "-" + dayOfMonth;
                txtReleaseDate.setText(date);
            }
        };

        txtGenre.setOnItemClickListener((parent, view, position, rowId) -> {
            Genre g = genreArrayList.get(position);
            txtGenre.setId(g.getGenre_ID());
        });

        txtCertificate.setOnItemClickListener((parent, view, position, rowId) -> {
            Certificate c = certificateArrayList.get(position);
            txtCertificate.setId(c.getCertificate_ID());
        });

        btnUpdateMovie.setOnClickListener(v -> {
            updateMovie();
        });
    }

    private void getGenres() {
        ArrayList<String> arrayList = new ArrayList<>();
        genreArrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.GENRES, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("genres"));
                    if (array.length() < 4) {
                        txtCertificate.setDropDownHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    for ( int i = 0; i < array.length(); i++) {
                        JSONObject genreObject = array.getJSONObject(i);

                        Genre genre = new Genre();

                        genre.setGenre_ID(genreObject.getInt("genre_ID"));
                        genre.setGenre_name(genreObject.getString("genre_name"));

                        arrayList.add(genreObject.getString("genre_name"));
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                this,
                                R.layout.custom_dropdown_item,
                                R.id.text_view_list_item,
                                arrayList
                        );

                        genreArrayList.add(genre);
                        txtGenre.setAdapter(arrayAdapter);
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

    private void getCertificates() {
        ArrayList<String> arrayList = new ArrayList<>();
        certificateArrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.CERTIFICATES, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("certificates"));
                    if (array.length() < 4) {
                        txtCertificate.setDropDownHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    for ( int i = 0; i < array.length(); i++) {
                        JSONObject certificateObject = array.getJSONObject(i);

                        Certificate certificate = new Certificate();

                        certificate.setCertificate_ID(certificateObject.getInt("certificate_ID"));
                        certificate.setCertificate_name(certificateObject.getString("certificate_name"));

                        arrayList.add(certificateObject.getString("certificate_name"));

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                this,
                                R.layout.custom_dropdown_item,
                                R.id.text_view_list_item,
                                arrayList
                        );

                        certificateArrayList.add(certificate);
                        txtCertificate.setAdapter(arrayAdapter);
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

    public void cancelMovie(View view) {
        super.onBackPressed();
    }

    public void changeMoviePoster(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        i.putExtra("fromGallery", true);
        startActivityForResult(i, GALLERY_CHANGE_POST);
    }

    private void updateMovie() {
        dialog.setMessage("Saving");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Constant.UPDATE_MOVIE+"/"+movie_ID, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {
                    MovieFragment.refreshLayout.setRefreshing(true);
                    JSONObject movieObject = object.getJSONObject("movie");
                    JSONObject genreObject = movieObject.getJSONObject("genre");
                    JSONObject certificateObject = movieObject.getJSONObject("certificate");

                    Genre genre = new Genre();
                    genre.setGenre_ID(genreObject.getInt("genre_ID"));
                    genre.setGenre_name(genreObject.getString("genre_name"));

                    Certificate certificate = new Certificate();
                    certificate.setCertificate_ID(certificateObject.getInt("certificate_ID"));
                    certificate.setCertificate_name(certificateObject.getString("certificate_name"));

                    Movie movie = MovieFragment.arrayList.get(position);
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

                    MovieFragment.arrayList.set(position, movie);

                    MovieFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    MovieFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Movie Updated", Toast.LENGTH_SHORT).show();
                    finish();



                    MovieFragment.refreshLayout.setRefreshing(false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            error.getMessage();
            Toast.makeText(this, "There was a problem adding the movie", Toast.LENGTH_SHORT).show();
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
                map.put("movie_title", txtTitle.getText().toString().trim());
                map.put("movie_story", txtStory.getText().toString().trim());
                map.put("movie_release_date", txtReleaseDate.getText().toString().trim());
                map.put("movie_film_duration", txtFilmDuration.getText().toString().trim());
                map.put("movie_additional_info", txtAdditionalInfo.getText().toString().trim());
                map.put("genre_ID", String.valueOf(txtGenre.getId()));
                map.put("certificate_ID", String.valueOf(txtCertificate.getId()));
                map.put("movie_poster", bitmapToString(bitmap));
                map.put("movie_status", "active");

                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(UpdateMovieActivity.this );
        queue.add(request);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CHANGE_POST && resultCode == Activity.RESULT_OK) {
            Uri imgUri = data.getData();
            imgUpdateMoviePoster.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}