package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

public class AddActorActivity extends AppCompatActivity {
    private static final int GALLERY_CHANGE_POST = 3;
    private ImageView imgAddActorProfile;
    private TextInputLayout txtLayoutFirstName, txtLayoutLastName, txtLayoutNotes;
    private TextInputEditText txtFirstName, txtLastName, txtNotes;
    private Button btnAddActor;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_actor);
        init();
    }

    private void init() {
        btnAddActor = findViewById(R.id.btnAddActor);
        imgAddActorProfile = findViewById(R.id.imgAddActorProfile);

        txtLayoutFirstName = findViewById(R.id.txtLayoutFirstName);
        txtLayoutLastName = findViewById(R.id.txtLayoutLastName);
        txtLayoutNotes = findViewById(R.id.txtLayoutNotes);

        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtNotes = findViewById(R.id.txtNotes);

        imgAddActorProfile.setImageURI(getIntent().getData());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}