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

import java.util.ArrayList;
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
    public ArrayList<JsonElement> ParameterList = new ArrayList<JsonElement>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_assistant);

        this.ListenButton = (Button) findViewById(R.id.startListeningButton);
        this.StatusTextView = (TextView) findViewById(R.id.txtDebug);
        this.Initialize();
    }

    // Initialize controls and API.ai
    private void Initialize(){
        // Ask for permission to use RECORD_AUDIO
        ActivityCompat.requestPermissions(VirtualAssistantActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                1);

        // Configuration for API.ai
        final AIConfiguration config = new AIConfiguration("447b26a535ac45dfb92cda0b912da59d", ai.api.AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        AIService = AIService.getService(this, config);
        AIService.setListener(this);
        ListenButton = (Button) findViewById(R.id.startListeningButton);
        StatusTextView = (TextView) findViewById(R.id.virtualAssistantStatus);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    // Result has been found
    public void onResult(final AIResponse response) {
        Result result = response.getResult();

        if(result.getParameters() != null && !result.getParameters().isEmpty()){
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                ParameterList.add(entry.getValue());
            }
        }

        // Check if the action is "FindPlaceWithType"
        if(Objects.equals(result.getAction(), "FindPlaceWithType")){
            StatusTextView.setTypeface(null, Typeface.NORMAL);
            StatusTextView.setText(String.format("You said: '%s'. The parameters are '%s'", result.getResolvedQuery(),
                    ParameterList.toString()));;

            // Send ParameterList to MapsActivity/GooglePlacesActivity
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("Parameters", ParameterList.toString());
            startActivity(intent);
        }else{
            StatusTextView.setTypeface(null, Typeface.NORMAL);
            StatusTextView.setText(String.format("You said: '%s'. The parameters are '%s'", result.getResolvedQuery(),
                    ParameterString));
        }
    }

    // Error message for debugging
    @Override
    public void onError(AIError error) {

        StatusTextView.setText(error.toString());
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

        AIService.stopListening();
        Log.i("ListeningFinished", "Finished listening");
    }

    public void ListenButtonClick(final View view){

        AIService.startListening();
    }
}
