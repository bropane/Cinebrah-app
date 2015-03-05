package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 3/3/2015.
 */
public class Registered {
    private String email;
    private String username;
    private int id;
    private String auth_token;

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return auth_token;
    }
}
