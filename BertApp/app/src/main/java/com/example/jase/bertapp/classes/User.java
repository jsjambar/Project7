package com.example.jase.bertapp.classes;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

    private static JSONObject executeQuery(String command, String username, String password, String preference) throws NoSuchAlgorithmException, ExecutionException, InterruptedException, JSONException {
        String hashedPassword = Tools.Hash(password);
        String urlAppendix = "?";
        urlAppendix += ("username="+username);
        urlAppendix += ("&password="+hashedPassword);
        urlAppendix += ("&preference="+preference.replace(" ","_"));
        AsyncTask<String, Void, JSONObject> taskResult = new DatabaseHandler().execute("/users/"+command+urlAppendix);
        JSONObject result = taskResult.get();
        if (result == null) result = new JSONObject("{success: false, message: \"No response from server.\"}");
        Log.d("BERTBERTBERT", String.valueOf(result.getString("message")));
        return result;
    }

    public static JSONObject create(String username, String password, String preference) throws InterruptedException, ExecutionException, NoSuchAlgorithmException, JSONException {
        return executeQuery("register", username, password, preference);
    }

    public static JSONObject login(String username, String password) throws InterruptedException, ExecutionException, NoSuchAlgorithmException, JSONException {
        return executeQuery("login", username, password, "null");
    }
}
