package com.cinebrah.cinebrah.net.models;

/**
 * Created by Taylor on 3/3/2015.
 */
public class Token {

    private String auth_token;

    public Token(String authToken) {
        this.auth_token = authToken;
    }

    public static String getFormattedToken(String authToken) {
        return "Token " + authToken;
    }

    public String getRawToken() {
        return auth_token;
    }
}
