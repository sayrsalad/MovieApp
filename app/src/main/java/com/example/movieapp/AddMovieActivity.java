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
import android.util.Log;
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
import com.example.movieapp.Adapters.MoviesAdapter;
import com.example.movieapp.Fragments.MovieFragment;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Certificate;
import com.example.movieapp.Models.Genre;
import com.example.movieapp.Models.Movie;
import com.example.movieapp.Models.Producer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddMovieActivity extends AppCompatActivity {
    private static final int GALLERY_CHANGE_POST = 3;
    private Button btnAddMovie;
    private ImageView imgAddMoviePoster;
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
    private int genre_ID = 0, certificate_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddMovie = findViewById(R.id.btnAddMovie);
        imgAddMoviePoster = findViewById(R.id.imgAddMoviePoster);

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

        imgAddMoviePoster.setImageURI(getIntent().getData());

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        getGenres();
        getCertificates();

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtReleaseDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    AddMovieActivity.this,
                    R.style.MyDialogTheme,
                    dateSetListener,
                    year, month, day);
            dialog.show();
        });

        dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String date = year + "-" + month + "-" + dayOfMonth;
            txtReleaseDate.setText(date);
        };

        txtGenre.setOnItemClickListener((parent, view, position, rowId) -> {
            Genre g = genreArrayList.get(position);
            genre_ID = g.getGenre_ID();
        });

        txtCertificate.setOnItemClickListener((parent, view, position, rowId) -> {
            Certificate c = certificateArrayList.get(position);
            certificate_ID = c.getCertificate_ID();
        });

        btnAddMovie.setOnClickListener( v -> {
            add();
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
        startActivityForResult(i, GALLERY_CHANGE_POST);
    }

    private void add() {

        dialog.setMessage("Adding Movie");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_MOVIE, response -> {

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
                    movie.setGenre_name(genreObject.getString("genre_name"));
                    movie.setCertificate_name(certificateObject.getString("certificate_name"));

                    movie.setGenre_ID(movieObject.getInt("genre_ID"));
                    movie.setCertificate_ID(movieObject.getInt("certificate_ID"));

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

                    JSONArray producerArray = movieObject.getJSONArray("producer");
                    ArrayList<Producer> producerArrayList = new ArrayList<Producer>();

                    for ( int a = 0; a < producerArray.length(); a++) {
                        JSONObject producerObject = producerArray.getJSONObject(a);

                        Producer producer = new Producer();
                        producer.setProducer_ID(producerObject.getInt("producer_ID"));
                        producer.setProducer_name(producerObject.getString("producer_name"));
                        producerArrayList.add(producer);
                    }

                    movie.setActor(actorArrayList);
                    movie.setProducer(producerArrayList);

                    MovieFragment.arrayList.add(0, movie);

                    MovieFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    MovieFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();

                    MovieFragment.refreshLayout.setRefreshing(false);

                    StyleableToast.makeText(getApplicationContext(), "Movie Added", R.style.CustomToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            StyleableToast.makeText(getApplicationContext(), "There was a problem adding the movie", R.style.CustomToast).show();
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
                map.put("movie_title", txtTitle.getText().toString().trim());
                map.put("movie_story", txtStory.getText().toString().trim());
                map.put("movie_release_date", txtReleaseDate.getText().toString().trim());
                map.put("movie_film_duration", txtFilmDuration.getText().toString().trim());
                map.put("movie_additional_info", txtAdditionalInfo.getText().toString().trim());
                map.put("genre_ID", genre_ID+"");
                map.put("certificate_ID", certificate_ID+"");
                map.put("movie_poster", bitmapToString(bitmap));
                map.put("movie_status", "active");

                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddMovieActivity.this );
        queue.add(request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CHANGE_POST && resultCode == Activity.RESULT_OK) {
            Uri imgUri = data.getData();
            imgAddMoviePoster.setImageURI(imgUri);
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