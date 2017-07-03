package com.example.jase.bertapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private String[] PreferenceChoices;
    private TextView headertext;
    private Spinner spinner;
    private TextView pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.PreferenceChoices = new String[]{
                "Food and drinks", "Going out", "Relaxation"
        };

        this.headertext = (TextView) findViewById(R.id.txtSignUp);
        this.username = (EditText) findViewById(R.id.txtUsername);
        this.password = (EditText) findViewById(R.id.txtPassword);
        this.debugText = (TextView) findViewById(R.id.txtDebug);
        this.pref = (TextView) findViewById(R.id.txtPref);
        this.confirmButton = (Button) findViewById(R.id.btnConfirm);
        this.spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, this.PreferenceChoices);
        spinner.setAdapter(adapter);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FINISHED: Check user input fields. (Local)
                // TODO: Make Log-in (Local and Server)
                // SORTA-DONE: Check if user already exists (Server)

                try {
                    JSONObject result = User.create(username.getText().toString(), password.getText().toString(), spinner.getSelectedItem().toString());
                    debugText.setText(result.getString("message"));
                    if (result.getBoolean("success")){
                        headertext.setText(String.format("Account created successfully.\nYou can now log in as\n\'%s\'.", username.getText().toString()));

                        username.setVisibility(View.INVISIBLE);
                        password.setVisibility(View.INVISIBLE);
                        debugText.setVisibility(View.INVISIBLE);
                        pref.setVisibility(View.INVISIBLE);
                        confirmButton.setVisibility(View.INVISIBLE);;
                        spinner.setVisibility(View.INVISIBLE);
                    }
                    else{
                        debugText.setText(String.format("Failed to register: %s", result.getString("message")));
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