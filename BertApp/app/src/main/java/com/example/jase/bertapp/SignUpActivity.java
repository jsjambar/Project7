package com.example.jase.bertapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jase.bertapp.classes.User;

public class SignUpActivity extends AppCompatActivity {

    private EditText UsernameField;
    private EditText PasswordField;
    private TextView Debug;
    private Button SignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.UsernameField = (EditText) findViewById(R.id.username);
        this.PasswordField = (EditText) findViewById(R.id.password);
        this.Debug = (TextView) findViewById(R.id.debug);
        this.SignupButton = (Button) findViewById(R.id.signUpButton);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    public void createUser(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://takethestairs.today:3000";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Debug.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Debug.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);

        //this.Debug.setText(String.format("Username: %s, password: %s", this.PasswordField.getText().toString(), this.PasswordField.getText().toString()));
    }
}