package com.example.movieapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.Adapters.ActorsAdapter;
import com.example.movieapp.Adapters.ProducersAdapter;
import com.example.movieapp.AddProducerActivity;
import com.example.movieapp.AuthActivity;
import com.example.movieapp.Constant;
import com.example.movieapp.HomeActivity;
import com.example.movieapp.Models.Actor;
import com.example.movieapp.Models.Producer;
import com.example.movieapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProducerFragment extends Fragment implements ProducersAdapter.OnItemListener {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Producer> arrayList;
    public static SwipeRefreshLayout refreshLayout;
    private ProducersAdapter producerAdapter;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton btnAddProducer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_producer_home, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerProducer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeProducer);
        toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        btnAddProducer = view.findViewById(R.id.btnAddProducer);

        getProducers();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducers();
            }
        });

        btnAddProducer.setOnClickListener(v -> {
            startActivity(new Intent(((HomeActivity)getContext()), AddProducerActivity.class));
        });
    }

    private void getProducers() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.PRODUCERS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("producers"));
                    for ( int i = 0; i < array.length(); i++) {
                        JSONObject producerObject = array.getJSONObject(i);

                        Producer producer = new Producer();
                        producer.setProducer_ID(producerObject.getInt("producer_ID"));
                        producer.setProducer_name(producerObject.getString("producer_name"));
                        producer.setProducer_email_address(producerObject.getString("producer_email_address"));
                        producer.setProducer_website(producerObject.getString("producer_website"));
                        producer.setProducer_status(producerObject.getString("producer_status"));

                        arrayList.add(producer);
                    }

                    producerAdapter = new ProducersAdapter(getContext(), arrayList, this);
                    recyclerView.setAdapter(producerAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("access_token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer" + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        Log.d("producer", arrayList.get(position).getProducer_name());
    }
}
