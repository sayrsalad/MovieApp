package com.example.movieapp;

public class Constant {
    public static final String URL = "http://192.168.1.32:8000/";
    public static final String HOME = URL + "api";

    public static final String LOGIN = HOME + "/auth/login";
    public static final String LOGOUT = HOME + "/auth/logout";
    public static final String REGISTER = HOME + "/auth/register";
    public static final String USER_PROFILE = HOME + "/auth/user_profile";
    public static final String SAVE_USER_INFO = HOME + "/save_user_info ";

    public static final String MOVIES = HOME + "/movie ";
    public static final String ACTORS = HOME + "/actor ";
    public static final String PRODUCERS = HOME + "/producer ";

}
