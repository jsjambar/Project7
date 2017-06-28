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

public class SignUpActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView debugText;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.username = (EditText) findViewById(R.id.txtUsername);
        this.password = (EditText) findViewById(R.id.txtPassword);
        this.debugText = (TextView) findViewById(R.id.txtDebug);
        this.confirmButton = (Button) findViewById(R.id.btnConfirm);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Check user input fields.
                // TODO: Log-in
                // TODO: Check if user already exists.
                // TODO: ^^ Server-side stuff ^^

                try {
                    JSONObject result = User.create(username.getText().toString(), password.getText().toString());
                    debugText.setText(result.getString("message"));
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