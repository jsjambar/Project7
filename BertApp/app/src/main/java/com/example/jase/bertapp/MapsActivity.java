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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

        tree = new KDTree(2);
        mapsActivity = this;

        Bundle data = getIntent().getExtras();
        if (data != null) {
            preference = data.getString("step1");
            distance = data.getString("step2");
        }

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

        ImageButton button = (ImageButton) findViewById(R.id.BertButton);
        button.setOnClickListener((View v) -> launchVirtualAssistant());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        //super.onActivityResult(requestCode, resultCode, i);
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
            // Request permission
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
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

            String url = getMapsApiDirectionsUrl(newLoc, new LatLng(51.9054439, 4.4644487));
            ReadTask downloadTask = new ReadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            first++;
        }
    }

    @Override
    // No permissions..
    public void onConnectionSuspended(int i) { }
    @Override
    // Connection to Google API failed..
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    public void launchVirtualAssistant(){
        Intent intent = new Intent(getApplicationContext(), VirtualAssistantActivity.class);
        startActivity(intent);
    }

    // make url for the call
    private String getMapsApiDirectionsUrl(LatLng origin,LatLng dest) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?"+parameters;
        return url;
    }

    // Do the URL call in the background
    private class ReadTask extends AsyncTask<String, Void , String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                MapHttpConnection http = new MapHttpConnection();
                data = http.readUr(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }
    // Function for in the background call
    public class MapHttpConnection {
        public String readUr(String mapsApiDirectionsUrl) throws IOException {
            String data = "";
            InputStream istream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(mapsApiDirectionsUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                istream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(istream));
                StringBuffer sb = new StringBuffer();
                String line ="";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
            }
            catch (Exception e) {
                Log.d("Exception reading:", e.toString());
            } finally {
                istream.close();
                urlConnection.disconnect();
            }
            return data;

        }
    }

    // Class to parse the data from JSON to list of points
    public class PathJSONParser {
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;
            try {
                jRoutes = jObject.getJSONArray("routes");
                for (int i=0 ; i < jRoutes.length() ; i ++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List<HashMap<String, String>> path = new ArrayList<HashMap<String,String>>();
                    for(int j = 0 ; j < jLegs.length() ; j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        for(int k = 0 ; k < jSteps.length() ; k ++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            for(int l = 0 ; l < list.size() ; l ++){
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat",
                                        Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng",
                                        Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }

    private class ParserTask extends AsyncTask<String,Integer, List<List<HashMap<String , String >>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {
            // TODO Auto-generated method stub
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(4);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);
        }}
}

