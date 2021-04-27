package com.example.movieapp.Models;

import java.util.ArrayList;

public class Movie  {

    private int movie_ID, genre_ID, certificate_ID, movie_film_duration;
    private String movie_title;
    private String movie_story;
    private String movie_release_date;
    private String movie_additional_info;
    private String movie_poster;
    private String movie_status;
    private String genre_name;
    private String certificate_name;
    private ArrayList<Actor> arrayList;

    public ArrayList<Actor> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Actor> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<Producer> getProducer() {
        return producerArrayList;
    }

    public void setProducer(ArrayList<Producer> producerArrayList) {
        this.producerArrayList = producerArrayList;
    }

    private ArrayList<Producer> producerArrayList;
    private Genre genre;
    private Certificate certificate;

    public String getGenre_name() {
        return genre_name;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }

    public String getCertificate_name() {
        return certificate_name;
    }

    public void setCertificate_name(String certificate_name) {
        this.certificate_name = certificate_name;
    }

    public int getMovie_ID() {
        return movie_ID;
    }

    public String getMovie_ID_string() {
        return String.valueOf(movie_ID);
    }

    public void setMovie_ID(int movie_ID) {
        this.movie_ID = movie_ID;
    }

    public int getGenre_ID() {
        return genre_ID;
    }

    public void setGenre_ID(int genre_ID) {
        this.genre_ID = genre_ID;
    }

    public int getCertificate_ID() {
        return certificate_ID;
    }

    public void setCertificate_ID(int certificate_ID) {
        this.certificate_ID = certificate_ID;
    }

    public int getMovie_film_duration() {
        return movie_film_duration;
    }

    public void setMovie_film_duration(int movie_film_duration) {
        this.movie_film_duration = movie_film_duration;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getMovie_story() {
        return movie_story;
    }

    public void setMovie_story(String movie_story) {
        this.movie_story = movie_story;
    }

    public String getMovie_release_date() {
        return movie_release_date;
    }

    public void setMovie_release_date(String movie_release_date) {
        this.movie_release_date = movie_release_date;
    }

    public String getMovie_additional_info() {
        return movie_additional_info;
    }

    public void setMovie_additional_info(String movie_additional_info) {
        this.movie_additional_info = movie_additional_info;
    }

    public String getMovie_poster() {
        return movie_poster;
    }

    public void setMovie_poster(String movie_poster) {
        this.movie_poster = movie_poster;
    }

    public String getMovie_status() {
        return movie_status;
    }

    public void setMovie_status(String movie_status) {
        this.movie_status = movie_status;
    }

    public ArrayList getActor() {
        return arrayList;
    }

    public void setActor(ArrayList arrayList) {
        this.arrayList = arrayList;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
}
