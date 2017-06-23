package com.example.jase.bertapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jase.bertapp.R;

public class menu2 extends AppCompatActivity {

    public String step1;
    public String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            step1 = extras.getString("step1");
        }

        Intent mapIntent = new Intent(getBaseContext(), MapsActivity.class);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        EditText fieldDistance = (EditText) findViewById(R.id.distance);

        btnSearch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = fieldDistance.getText().toString();
                mapIntent.putExtra("step1", step1);
                mapIntent.putExtra("step2", distance);
                startActivity(mapIntent);
            }
        });

    }

}
