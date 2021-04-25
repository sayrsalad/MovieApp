package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Fragments.MovieFragment;
import com.example.movieapp.Fragments.ProducerFragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProducerActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private TextInputLayout txtLayoutProducerName, txtLayoutEmailAddress, txtLayoutWebsite;
    private TextInputEditText txtProducerName, txtEmailAddress, txtWebsite;
    private Button btnAddProducer;
    private Producer producer;
    private ArrayList<Producer> producerArrayList;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_producer);
        init();
    }

    public void cancelProducer(View view) {
        super.onBackPressed();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddProducer = findViewById(R.id.btnAddProducer);

        txtLayoutProducerName = findViewById(R.id.txtLayoutProducerName);
        txtLayoutEmailAddress = findViewById(R.id.txtLayoutEmailAddress);
        txtLayoutWebsite = findViewById(R.id.txtLayoutWebsite);

        txtProducerName = findViewById(R.id.txtProducerName);
        txtEmailAddress = findViewById(R.id.txtEmailAddress);
        txtWebsite = findViewById(R.id.txtWebsite);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        btnAddProducer.setOnClickListener( v -> {
            add();
        });

    }

    private void add() {
        dialog.setMessage("Adding Producer");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_PRODUCER, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {

                    ProducerFragment.refreshLayout.setRefreshing(true);
                    JSONObject producerObject = object.getJSONObject("producer");

                    Producer producer = new Producer();
                    producer.setProducer_ID(producerObject.getInt("producer_ID"));
                    producer.setProducer_name(producerObject.getString("producer_name"));
                    producer.setProducer_email_address(producerObject.getString("producer_email_address"));
                    producer.setProducer_website(producerObject.getString("producer_website"));
                    producer.setProducer_status(producerObject.getString("producer_status"));

                    ProducerFragment.arrayList.add(0, producer);

                    ProducerFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    ProducerFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();

                    ProducerFragment.refreshLayout.setRefreshing(false);

                    StyleableToast.makeText(getApplicationContext(), "Producer Added", R.style.CustomToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            error.getMessage();
            StyleableToast.makeText(getApplicationContext(), "There was a problem adding the producer", R.style.CustomToast).show();
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
                map.put("producer_name", txtProducerName.getText().toString().trim());
                map.put("producer_email_address", txtEmailAddress.getText().toString().trim());
                map.put("producer_website", txtWebsite.getText().toString().trim());
                map.put("producer_status", "active");

                Log.d("producer", map.toString());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddProducerActivity.this );
        queue.add(request);
    }
}