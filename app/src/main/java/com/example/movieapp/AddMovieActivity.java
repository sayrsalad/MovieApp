package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Calendar;

public class AddMovieActivity extends AppCompatActivity {
    private static final int GALLERY_CHANGE_POST = 3;
    private Button btnAddMovie;
    private ImageView imgAddMoviePoster;
    private EditText txtAddMovieTitle, txtAddMovieStory, txtAddMovieReleaseDate, txtAddMovieFilmDuration, txtAddMovieAdditionalInfo, txtAddMovieGenre, txtAddMovieCertificate;
    private Bitmap bitmap = null;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        init();
    }

    private void init() {
        btnAddMovie = findViewById(R.id.btnAddMovie);
        imgAddMoviePoster = findViewById(R.id.imgAddMoviePoster);
        txtAddMovieTitle = findViewById(R.id.txtAddMovieTitle);
        txtAddMovieStory = findViewById(R.id.txtAddMovieStory);
        txtAddMovieReleaseDate = findViewById(R.id.txtAddMovieReleaseDate);
        txtAddMovieFilmDuration = findViewById(R.id.txtAddMovieFilmDuration);
        txtAddMovieAdditionalInfo = findViewById(R.id.txtAddMovieAdditionalInfo);
        txtAddMovieGenre = findViewById(R.id.txtAddMovieGenre);
        txtAddMovieCertificate = findViewById(R.id.txtAddMovieCertificate);

        imgAddMoviePoster.setImageURI(getIntent().getData());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtAddMovieReleaseDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    AddMovieActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateSetListener,
                    year,month,day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + month + "-" + dayOfMonth;
                txtAddMovieReleaseDate.setText(date);
            }
        };

    }

    public void cancelAddMovie (View view) {
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
        if(requestCode==GALLERY_CHANGE_POST && resultCode== Activity.RESULT_OK){
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