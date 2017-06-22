package com.example.jase.bertapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.Objects;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class VirtualAssistantActivity extends AppCompatActivity implements AIListener{

    private AIService AIService;
    private Button ListenButton;
    private TextView StatusTextView;
    public String ParameterString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_assistant);
        this.Initialize();
    }

    // Initialize controls and API.ai
    private void Initialize(){
        // Asl for permission to use location
        ActivityCompat.requestPermissions(VirtualAssistantActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                1);

        // Configuration for API.ai
        final AIConfiguration config = new AIConfiguration("447b26a535ac45dfb92cda0b912da59d", ai.api.AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        this.AIService = AIService.getService(this, config);
        this.AIService.setListener(this);
        this.ListenButton = (Button) findViewById(R.id.startListeningButton);
        this.StatusTextView = (TextView) findViewById(R.id.virtualAssistantStatus);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    // Result has been found
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        if(result.getParameters() != null && !result.getParameters().isEmpty()){
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                this.ParameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        Log.i("Query: ", result.getResolvedQuery());
        Log.i("Action: ", result.getAction());

        this.StatusTextView.setTypeface(null, Typeface.NORMAL);
        this.StatusTextView.setText(String.format("You said: '%s'", result.getResolvedQuery()));

        // Check if the action is "FindLocation"
        if(Objects.equals(result.getAction(), "FindLocation")){
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("ParameterString", this.ParameterString);
            startActivity(intent);
        }
    }

    // Error message for debugging
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

        // Set the TextView font to bold
        this.StatusTextView.setTypeface(null, Typeface.BOLD);
        this.StatusTextView.setText("Started listening...");
    }

    @Override
    public void onListeningCanceled() {
    }

    @Override
    public void onListeningFinished() {
        Log.i("ListeningFinished", "Finished listening");
    }

    public void ListenButtonClick(final View view){
        this.AIService.startListening();
    }
}
