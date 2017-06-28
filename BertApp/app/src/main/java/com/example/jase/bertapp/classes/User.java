package com.example.jase.bertapp.classes;

import android.os.AsyncTask;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class User {
    // No properties in Java? C# is better
    // Dat klopt.

    private String username;
    private String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public static QueryResult create(String username, String password) throws NoSuchAlgorithmException, ExecutionException, InterruptedException {
        String hashedPassword = Tools.Hash(password);
        String urlAppendix = "?";
        urlAppendix += ("username="+username);
        urlAppendix += ("password="+hashedPassword);
        AsyncTask<String, Void, QueryResult> result = new DatabaseHandler().execute("/users/register"+urlAppendix);
        QueryResult qr = result.get();
        Log.d("BERTBERTBERT", qr.message);
        return qr;
    }
}
