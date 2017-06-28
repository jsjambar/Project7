package com.example.jase.bertapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.jase.bertapp.classes.Sight;
import com.example.jase.bertapp.kdtree.KDTree;
import com.example.jase.bertapp.kdtree.exception.KeyDuplicateException;
import com.example.jase.bertapp.kdtree.exception.KeySizeException;
import com.example.jase.bertapp.parser.DirectionsJSONParser;
import com.example.jase.bertapp.utils.DirectionUtil;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.jase.bertapp.utils.JsonUtil.downloadUrl;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // User preferences
    String preference;
    String distance;

    // Instance of map to use in click listeners.
    private MapsActivity mapsActivity;

    // Google map vars
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = new LocationRequest();

    // Updatable marker.
    private Marker me;
    // First update var
    private int first = 0;

    // Rotterdam center.
    private LatLng rDam = new LatLng(51.9244201, 4.4777325);

    // KDTree for sight points
    private KDTree tree;

    private final List<Polyline> lines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instance of the application
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            preference = data.getString("step1");
            distance = data.getString("step2");

        }

        MapsInitializer.initialize(getApplicationContext());

        /*Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text = preference + " x " + distance;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();*/

        tree = new KDTree(2);

        mapsActivity = this;

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

        ImageButton button = (ImageButton) findViewById(R.id.BertButton);
        button.setOnClickListener((View v) -> launchVirtualAssistant());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == 100 && i != null && resultCode == RESULT_OK) {
            List<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(this, result.get(0), Toast.LENGTH_LONG).show();

            Sight sight;
            for (String res : result) {
                for (String part : res.split(" ")) {
                    if (part.length() <= 4 || part.equalsIgnoreCase("museum"))
                        continue;
                    if (Arrays.asList("near", "nearest", "dichtbij", "dichtbijzijnste", "dichtbijzijnde").contains(part.toLowerCase())) {
                        showPathToNearest();
                        break;
                    }
                    sight = Sight.getSight(part);
                    if (sight != null) {
                        showPathToSight(sight);
                        break;
                    }
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, i);
    }

    private void showPathToSight(Sight sight) {
        if (sight != null) {
            // Remove current lines
            for (Polyline line : lines) {
                line.remove();
            }
            lines.clear();

            // Getting URL to the Google Directions API
            String url = DirectionUtil.getDirectionsUrl(me.getPosition(), sight.getCoords());

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        }
    }

    private void showPathToNearest() {
        try {
            Sight nearest = (Sight) tree.nearest(new double[]{me.getPosition().latitude, me.getPosition().longitude});
            showPathToSight(nearest);
        } catch (KeySizeException e) {
            e.printStackTrace();
        }

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
            mMap.addMarker(new MarkerOptions().position(sight.getCoords()).title(sight.getTitle()).snippet(sight.getDescription()));
            try {
                tree.insert(new double[]{sight.getCoords().latitude, sight.getCoords().longitude}, sight);
            } catch (KeySizeException e) {
                e.printStackTrace();
            } catch (KeyDuplicateException e) {
                e.printStackTrace();
            }
        }

        // Add the marker of the user which gets updated in locationChanged()
        me = mMap.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                        .position(rDam)
                        .title("You're right here!")
                        .zIndex(1.0f)
        );

        mMap.setOnMapClickListener(latLng -> {
            // This is only for nearby testing purposes.
            //me.setPosition(latLng);

            // TODO: remove this and add to onLocationChanged
            try {
                double latitude = me.getPosition().latitude;
                double longtitude = me.getPosition().longitude;
                List<Sight> points = tree.rangeSearch(new double[]{latitude, longtitude}, 0.0004);
                if (!points.isEmpty()) {
                    String text = points.size() == 1 ? " point found " : " points found ";
                    Toast.makeText(mapsActivity, points.size() + text + points.get(0).getTitle(), Toast.LENGTH_LONG).show();
                }
            } catch (KeySizeException e) {
                e.printStackTrace();
            }

            if(first == 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                first++;
            }

        });

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

    //Sadly has to be in this class to remove the lines
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private final ParserTask parserTask;

        public DownloadTask() {
            this.parserTask = new ParserTask();
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }

        /** A class to parse the Google Places in JSON format */
        public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try{
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for(int i=0;i<result.size();i++){
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(4);
                    lineOptions.color(Color.BLACK);
                }
                Polyline polyline = mMap.addPolyline(lineOptions);
                lines.add(polyline);

                // Drawing polyline in the Google Map for the i-th route

            }
        }

    }

    public void launchVirtualAssistant(){
        Intent intent = new Intent(getApplicationContext(), VirtualAssistantActivity.class);
        startActivity(intent);
    }
}

