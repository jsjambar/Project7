package com.example.jase.bertapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    // User preferences
    private String preference;
    private String distance = "5000";
    private String FromVirtualAssistantPreference = "";

    // Instance of map to use in click listeners.
    private MapsActivity mapsActivity;

    // Google map vars
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = new LocationRequest();

    Polyline polyline = null;

    // Updatable marker.
    private Marker me;
    private LatLng myLoc = null;
    private LatLng myDest = null;
    // First update var
    private int first = 0;
    private int firstGet = 0;

    // Rotterdam center.
    private LatLng rDam = new LatLng(51.9244201, 4.4777325);

    // KDTree for sight points
    private KDTree tree;

    private final List<Polyline> lines = new ArrayList<>();

    // Store parameters as string
    private String TypeParameters;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instance of the application
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tree = new KDTree(2);
        mapsActivity = this;

        Intent intent = getIntent();

        Bundle data = getIntent().getExtras();

        if (data != null) {
            preference = data.getString("step1");

            if (data.containsKey("step2")) {
                distance = data.getString("step2");
            }

            // Check if parameter from VirtualAssistantAcvitity has been given
            if (data.containsKey("PARAMETER")) {
                this.FromVirtualAssistantPreference = data.getString("PARAMETER");
            }
        }

        MapsInitializer.initialize(getApplicationContext());
        tree = new KDTree(2);
        mapsActivity = this;

        MapsInitializer.initialize(getApplicationContext());
        // Mapfragment that we use for google maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Google API Client that we use to login our location
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
        mMap.setOnMarkerClickListener(this);

        // Add sights to the map.
        /*List<Sight> sights = Sight.getSights();

        for (Sight sight : sights) {
            mMap.addMarker(new MarkerOptions().position(sight.getCoords()).title(sight.getTitle()).snippet(sight.getDescription()));
            try {
                tree.insert(new double[]{sight.getCoords().latitude, sight.getCoords().longitude}, sight);
            } catch (KeySizeException e) {
                e.printStackTrace();
            } catch (KeyDuplicateException e) {
                e.printStackTrace();
            }
        }*/

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

            if (first == 0) {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onLocationChanged(Location location) {
        LatLng newLoc = new LatLng(location.getLatitude(), location.getLongitude());
        myLoc = newLoc;

        // Update our marker and move the camera to center the new position.
        me.setPosition(newLoc);

        if(myLoc != null && myDest != null) {
            String url = getMapsApiDirectionsUrl(myLoc, myDest);
            ReadTask downloadTask = new ReadTask();
            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }

        // Move camera to the user's current location for one time
        if(first == 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
            if(firstGet == 0) {
                String placesUrl = getPlacesUrl(preference, newLoc, distance);
                new JsonParser().execute(placesUrl);
            }
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getPlacesUrl(String preference, LatLng loc, String range) {
        //https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+Rotterdam&key=AIzaSyBKEDj2HEaWj4yheUYA0NQRtc0QsakDiLw

        //Refactor values into usable data
        Double rawLat = loc.latitude;
        Double rawLong = loc.longitude;
        String location = "location="+String.valueOf(rawLat)+","+String.valueOf(rawLong);
        String type = "type="+preference;
        String radius = "radius="+range;
        String key = "AIzaSyBKEDj2HEaWj4yheUYA0NQRtc0QsakDiLw";

        // If the preference from the Virtual Assistant is not empty, se the type to given preference
        if(!this.FromVirtualAssistantPreference.isEmpty() || this.FromVirtualAssistantPreference == null){

           if(Objects.equals(this.FromVirtualAssistantPreference, "Disco")){
               type = "type=night_club";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Museum")){
                type = "type=museum";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Movies")){
                type = "type=cinema";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Spa")){
               type = "type=spa";
           }
           else if(Objects.equals(this.FromVirtualAssistantPreference, "Movie")){
               type = "type=movie_theater";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Bus Station")){
               type = "type=bus_station";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Airport")){
               type = "type=airport";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Hospital")){
               type = "type=hospital";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Alcohol")){
               type = "type=liquor_store";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Beauty Salon")){
               type = "type=beauty_salon";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Meal Takeaway")){
               type = "type=meal_takeaway";
           }
           else if(Objects.equals(this.FromVirtualAssistantPreference, "Casino")){
               type = "type=casino";
           }else if(Objects.equals(this.FromVirtualAssistantPreference, "Meal Delivery")){
               type = "type=meal_delivery";
           }
        }

        //String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?"+location+"&"+radius+"&"+type+"point_of_interest&key="+key;

        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=rotterdam+point+of+interest&type"+type+"&language=en&key="+key;
        Log.i("Url:", url);

        return url;
    }

    // make url for the call
    private String getMapsApiDirectionsUrl(LatLng origin,LatLng dest) {
        String wayPoints = "Koopgoot,Rotterdam|Euromast,Rotterdam|Markthal,Rotterdam|Blijdorp,Rotterdam|Erasmusbrug|Kubuswoning,Rotterdam";

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&mode=walking&waypoints="+wayPoints;
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?"+parameters;
        return url;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // If location is selected we gotta do this...
        myDest = marker.getPosition();

        marker.showInfoWindow();

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle(marker.getTitle())
                        .setMessage("The route to your destination will now be shown.")
                        .setNeutralButton("Okay!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String url = getMapsApiDirectionsUrl(myLoc, myDest);
                            ReadTask downloadTask = new ReadTask();
                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }});
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
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

            if(polyline != null){
                polyline.remove();
            }

            polyline = mMap.addPolyline(polyLineOptions);
        }}

    public class JsonParser extends AsyncTask<Object, Object, JSONObject> {

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                InputStream is = new URL((String) params[0]).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject places) {
            try {
                JSONArray aPlaces = places.getJSONArray("results");
                for (int i = 0; i < aPlaces.length(); i++) {
                    JSONObject place = aPlaces.getJSONObject(i);
                    // Explode every location object for the data..
                    JSONObject location = place.getJSONObject("geometry");
                    JSONObject oLatLng = location.getJSONObject("location");
                    Double lat = Double.parseDouble(oLatLng.getString("lat"));
                    Double lng = Double.parseDouble(oLatLng.getString("lng"));

                    String placeName = place.getString("name");
                    LatLng loc = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(loc).title(placeName));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

