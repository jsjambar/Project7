package com.example.jase.bertapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.jase.bertapp.classes.Sight;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // google map vars
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = new LocationRequest();

    // Updatable marker.
    Marker me;
    // First update var
    private int first = 0;

    //Rotterdam center.
    private LatLng rDam = new LatLng(51.9244201, 4.4777325);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instance of the application
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MapsInitializer.initialize(getApplicationContext());

        // Mapfragment that we use for google maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Google API Client that we use to get our location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // If the client exists, we connect.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        // After we're connected, we create a location request with our current location.
        createLocationRequest(mLocationRequest);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        PackageManager pm = this.getPackageManager();
        int hasPermission = pm.checkPermission(
                Manifest.permission.RECORD_AUDIO,
                this.getPackageName());
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d(this.getPackageName(), "No permission");
        }

        ImageButton button = (ImageButton) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    public void promptSpeechInput() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to Bert");

        try {
            startActivityForResult(i, 100);
        } catch (Exception e) {
            Toast.makeText(this, "Sorry your device does not support your speech language.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == 100 && i != null && resultCode == RESULT_OK) {
            List<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, i);
    }

    /* Initial Map */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Center map on Rotterdam (this should be the location of the user)
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rDam, 15));

        // Add sights to the map.
        List<Sight> sights = Sight.getSights();
        for (Sight sight : sights) {
            mMap.addMarker(new MarkerOptions().position(sight.coords).title(sight.title).snippet(sight.description));
        }

        // Add the marker of the user which gets updated in locationChanged()
        me = mMap.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                        .position(rDam)
                        .title("You're right here!")
                        .zIndex(1.0f)
        );
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    protected void createLocationRequest(LocationRequest ourLoc) {
        ourLoc.setInterval(4000);
        ourLoc.setFastestInterval(2000);
        ourLoc.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());

        // Update our marker and move the camera to center the new position.
        me.setPosition(newLoc);
        if(first == 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
            first++;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        // Closed permissions..
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Connection to Google API failed..
    }
}
