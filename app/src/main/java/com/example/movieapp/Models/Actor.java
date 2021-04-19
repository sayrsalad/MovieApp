package com.example.movieapp.Models;

public class Actor {

    private int actor_ID;
    private String actor_fname, actor_lname, actor_notes, actor_img, actor_status;

    public int getActor_ID() {
        return actor_ID;
    }

    public void setActor_ID(int actor_ID) {
        this.actor_ID = actor_ID;
    }

    public String getActor_fname() {
        return actor_fname;
    }

    public void setActor_fname(String actor_fname) {
        this.actor_fname = actor_fname;
    }

    public String getActor_lname() {
        return actor_lname;
    }

    public void setActor_lname(String actor_lname) {
        this.actor_lname = actor_lname;
    }

    public String getActor_notes() {
        return actor_notes;
    }

    public void setActor_notes(String actor_notes) {
        this.actor_notes = actor_notes;
    }

    public String getActor_img() {
        return actor_img;
    }

    public void setActor_img(String actor_img) {
        this.actor_img = actor_img;
    }

    public String getActor_status() {
        return actor_status;
    }

    public void setActor_status(String actor_status) {
        this.actor_status = actor_status;
    }
}
