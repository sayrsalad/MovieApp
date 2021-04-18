package com.example.movieapp.Models;

public class Movie {

    private int movie_ID, genre_ID, certificate_ID, movie_film_duration;
    private String movie_title, movie_story, movie_release_date, movie_additional_info, movie_poster, movie_status;

    public int getMovie_ID() {
        return movie_ID;
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
}
