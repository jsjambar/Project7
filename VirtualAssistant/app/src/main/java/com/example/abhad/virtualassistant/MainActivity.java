package com.example.abhad.virtualassistant;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.Map;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener{

    private AIService AIService;
    private Button ListenButton;
    public String ParameterString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                1);

        final AIConfiguration config = new AIConfiguration("447b26a535ac45dfb92cda0b912da59d", ai.api.AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        this.AIService = AIService.getService(this, config);
        this.AIService.setListener(this);
        this.ListenButton = (Button) findViewById(R.id.startListeningButton);
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        if(result.getParameters() != null && !result.getParameters().isEmpty()){
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                this.ParameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        TextView textView = (TextView) findViewById(R.id.textView);

        // Show results in TextView.
        textView.setText("Query:" + result.getResolvedQuery() +
                "\nAction: " + result.getAction() +
                "\nParameters: " + this.ParameterString);

        Log.i("Query: ", result.getResolvedQuery());
        Log.i("Action: ", result.getResolvedQuery());
        Log.i("Parameters: ", result.getResolvedQuery());
    }

    @Override
    public void onError(AIError error) {
        Log.i("Error: ", error.getMessage());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        Log.i("ListeningStarted", "Started listening");
    }

    @Override
    public void onListeningCanceled() {
    }

    @Override
    public void onListeningFinished() {
        Log.i("ListeningFinished", "Finished listening");
    }

    public void ListenButtonClick(final View view){
        Log.i("Click", "CLICK");
        this.AIService.startListening();
    }
}
