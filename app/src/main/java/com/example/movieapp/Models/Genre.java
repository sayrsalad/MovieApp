package com.example.movieapp.Models;

public class Genre {

    private int genre_ID;
    private String genre_name, genre_status;

    public int getGenre_ID() {
        return genre_ID;
    }

    public void setGenre_ID(int genre_ID) {
        this.genre_ID = genre_ID;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }

    public String getGenre_status() {
        return genre_status;
    }

    public void setGenre_status(String genre_status) {
        this.genre_status = genre_status;
    }
}
