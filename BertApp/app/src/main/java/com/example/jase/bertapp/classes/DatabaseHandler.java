package com.example.jase.bertapp.classes;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jeroen on 28-06-17.
 */

public class DatabaseHandler extends AsyncTask<String, Void, QueryResult> {
    final static String ROOTURL = "http://takethestairs.today:3000";

    @Override
    protected QueryResult doInBackground(String... params) {
        try{
            URL url = new URL(ROOTURL + params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = IOUtils.toString(in, "UTF-8");
            return new QueryResult(true, result);
        }catch (MalformedURLException e) {
            return new QueryResult(false, "MalformedURLException: "+e.getMessage());
        } catch (IOException e) {
            return new QueryResult(false, "IOException: "+e.getMessage());
        }
    }
}
