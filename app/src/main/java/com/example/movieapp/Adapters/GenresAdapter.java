package com.example.movieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.example.movieapp.Models.Genre;

import java.util.ArrayList;

public class GenresAdapter {

    private Context context;

    public GenresAdapter(Context context, ArrayList<Genre> list) {
        this.context = context;
        this.list = list;
    }

    private ArrayList<Genre> list;


}
