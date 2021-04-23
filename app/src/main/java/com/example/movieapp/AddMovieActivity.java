package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.movieapp.Models.Certificate;
import com.example.movieapp.Models.Genre;
import com.example.movieapp.Models.Movie;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
    private int genre_ID;
    private ArrayList<Genre> genreArrayList;
    private Genre genre;
    private ArrayList<Certificate> certificateArrayList;
    private Certificate certificate;

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

        });

        txtCertificate.setOnItemClickListener((parent, view, position, rowId) -> {
            Certificate c = certificateArrayList.get(position);

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

                        certificate.setGenre_ID(certificateObject.getInt("certificate_ID"));
                        certificate.setGenre_name(certificateObject.getString("certificate_name"));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CHANGE_POST && resultCode == Activity.RESULT_OK) {
            Uri imgUri = data.getData();
            imgAddMoviePoster.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}