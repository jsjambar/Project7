package com.example.jase.bertapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jase.bertapp.classes.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class LogInActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView debugText;
    private Button confirmButton;
    private TextView headertext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        this.username = (EditText) findViewById(R.id.txtUsername);
        this.password = (EditText) findViewById(R.id.txtPassword);
        this.debugText = (TextView) findViewById(R.id.txtDebug);
        this.confirmButton = (Button) findViewById(R.id.btnConfirm);
        this.headertext = (TextView) findViewById(R.id.txtLogIn);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FINISHED: Check user input fields. (Local)
                // TODO: Check if user already exists (Server)
                try {
                    JSONObject result = User.login(username.getText().toString(), password.getText().toString());
                    if (result.getBoolean("success")){
                        headertext.setText(String.format("Logged in successfully as \'%s\'.", username.getText().toString()));

                        username.setVisibility(View.INVISIBLE);
                        password.setVisibility(View.INVISIBLE);
                        debugText.setVisibility(View.INVISIBLE);
                        confirmButton.setVisibility(View.INVISIBLE);;
                    }
                    else{
                        debugText.setText(String.format("Failed to log in: %s", result.getString("message")));
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
