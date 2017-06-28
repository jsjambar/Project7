package com.example.jase.bertapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jase.bertapp.classes.DatabaseHandler;
import com.example.jase.bertapp.classes.QueryResult;

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
        AsyncTask<String, Void, QueryResult> task = new DatabaseHandler().execute("testtablename", "testvalues");
        //this.Debug.setText(String.format("Username: %s, password: %s", this.PasswordField.getText().toString(), this.PasswordField.getText().toString()));
    }
}