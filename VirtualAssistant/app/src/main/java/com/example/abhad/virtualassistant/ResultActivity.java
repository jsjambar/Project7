package com.example.abhad.virtualassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle data = getIntent().getExtras();

        if(data != null) {
            String parameters = data.getString("ParameterString");
            TextView textView = (TextView) findViewById(R.id.textView2);
            textView.setText(parameters);
        }
    }
}
