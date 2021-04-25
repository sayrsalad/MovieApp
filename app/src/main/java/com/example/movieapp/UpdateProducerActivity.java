package com.example.movieapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.movieapp.Models.Producer;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProducerActivity extends AppCompatActivity {

    private int position = 0, producer_ID = 0;

    private SharedPreferences sharedPreferences;

    private TextInputLayout txtLayoutProducerName, txtLayoutEmailAddress, txtLayoutWebsite;
    private TextInputEditText txtProducerName, txtEmailAddress, txtWebsite;
    private Button btnUpdateProducer;
    private Producer producer;
    private ArrayList<Producer> producerArrayList;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_producer);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnUpdateProducer = findViewById(R.id.btnUpdateProducer);

        txtLayoutProducerName = findViewById(R.id.txtLayoutProducerName);
        txtLayoutEmailAddress = findViewById(R.id.txtLayoutEmailAddress);
        txtLayoutWebsite = findViewById(R.id.txtLayoutWebsite);

        txtProducerName = findViewById(R.id.txtProducerName);
        txtEmailAddress = findViewById(R.id.txtEmailAddress);
        txtWebsite = findViewById(R.id.txtWebsite);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        position = getIntent().getIntExtra("position", 0);
        producer_ID = getIntent().getIntExtra("producer_ID", 0);

        txtProducerName.setText(getIntent().getStringExtra("producer_name"));
        txtEmailAddress.setText(getIntent().getStringExtra("producer_email_address"));
        txtWebsite.setText(getIntent().getStringExtra("producer_website"));

        btnUpdateProducer.setOnClickListener( v -> {
            update();
        });
    }

    private void update() {
        dialog.setMessage("Updating Producer");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Constant.UPDATE_PRODUCER+"/"+producer_ID, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {

                    ProducerFragment.refreshLayout.setRefreshing(true);
                    JSONObject producerObject = object.getJSONObject("producer");

                    Producer producer = ProducerFragment.arrayList.get(position);
                    producer.setProducer_ID(producerObject.getInt("producer_ID"));
                    producer.setProducer_name(producerObject.getString("producer_name"));
                    producer.setProducer_email_address(producerObject.getString("producer_email_address"));
                    producer.setProducer_website(producerObject.getString("producer_website"));
                    producer.setProducer_status(producerObject.getString("producer_status"));

                    ProducerFragment.arrayList.set(position, producer);

                    ProducerFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    ProducerFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Producer Added", Toast.LENGTH_SHORT).show();
                    finish();

                    ProducerFragment.refreshLayout.setRefreshing(false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "There was a problem updating the producer", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            error.getMessage();
            Toast.makeText(this, "There was a problem updating the producer", Toast.LENGTH_SHORT).show();
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
        RequestQueue queue = Volley.newRequestQueue(UpdateProducerActivity.this );
        queue.add(request);
    }

    public void cancelProducer(View view) {
        super.onBackPressed();
    }
    
    
}