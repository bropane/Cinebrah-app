package com.cinebrah.cinebrah.utils;

import com.cinebrah.cinebrah.net.models.AccountCredential;
import com.cinebrah.cinebrah.net.models.Token;

/**
 * Created by Taylor on 3/4/2015.
 */
public class AccountStore {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_TOKEN = "token";

    public static AccountCredential getStoredAccountCredential() {
        String username = AccountStore.getUsername();
        String password = AccountStore.getPassword();
        return new AccountCredential(username, password);
    }

    public static void storeAccountCredential(AccountCredential credential) {
        AccountStore.storeUsername(credential.getUsername());
        AccountStore.storePassword(credential.getPassword());
    }

    public static String getUsername() {
        String username = AppConstants.getPreferences().getString(KEY_USERNAME, null);
        return username;
    }

    public static void storeUsername(String username) {
        AppConstants.getPreferences().edit().putString(KEY_USERNAME, username).commit();
    }

    public static String getPassword() {
        String password = AppConstants.getPreferences().getString(KEY_PASSWORD, null);
        return password;
    }

    public static void storePassword(String password) {
        AppConstants.getPreferences().edit().putString(KEY_PASSWORD, password).commit();
    }

    public static Token getToken() {
        String tokenString = AppConstants.getPreferences().getString(KEY_TOKEN, null);
        Token token = new Token(tokenString);
        return token;
    }

    public static void storeToken(Token token) {
        AppConstants.getPreferences().edit().putString(KEY_TOKEN, token.getRawToken()).commit();
    }
}
