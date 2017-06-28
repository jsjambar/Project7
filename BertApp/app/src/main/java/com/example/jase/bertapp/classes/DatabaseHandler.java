package com.example.jase.bertapp.classes;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jeroen on 28-06-17.
 */

public class DatabaseHandler extends AsyncTask<String, Void, JSONObject> {
    final static String ROOTURL = "http://takethestairs.today:3000";

    @Override
    protected JSONObject doInBackground(String... params) {
        try{
            URL url = new URL(ROOTURL + params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String resultString = IOUtils.toString(in, "UTF-8");
            JSONObject result = new JSONObject(resultString);
            return result;
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
