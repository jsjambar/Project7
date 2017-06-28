package com.example.jase.bertapp;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PlaceSelectionListener {

    private TextView mPlaceDetailsText;
    private TextView mPlaceAttribution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.InitializeChoices();
        this.InitializeGooglePlaces();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_navigate) {
            // Handle main screen
            startActivity(new Intent(this, menu.class));
        } else if (id == R.id.nav_rotterdamDex) {
            // Handle RotterdamDex-Activity
        } else if (id == R.id.nav_virtualAssistant) {
            // Handle Virtual Assistant-Activity
            startActivity(new Intent(this, VirtualAssistantActivity.class));
        } else if (id == R.id.nav_login) {
            // Handle login-Activity
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_signup) {
            // Handle sign up-Activity
            startActivity(new Intent(this, SignUpActivity.class));
        } else if (id == R.id.nav_settings) {
            // Handle settings-Activity
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void InitializeChoices(){
        Button btnFood = (Button) findViewById(R.id.btnFood);
        Button btnGo = (Button) findViewById(R.id.btnGo);
        Button btnRelax = (Button) findViewById(R.id.btnRelax);

        Intent step2 = new Intent(getBaseContext(), menu2.class);

        btnFood.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2.putExtra("step1", "restaurant");
                startActivity(step2);
            }
        });

        btnGo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2.putExtra("step1", "night_club");
                startActivity(step2);
            }
        });

        btnRelax.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2.putExtra("step1", "spa");
                startActivity(step2);
            }
        });
    }

    void InitializeGooglePlaces(){
        // GOOGLE PLACES //

        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);

        // Retrieve the TextViews that will display details about the selected place.
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        mPlaceAttribution = (TextView) findViewById(R.id.place_attribution);
    }

    /**
     * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
     */
    @Override
    public void onPlaceSelected(Place place) {
        Log.i("Place: ", place.toString());
        // Format the returned place's details and display them in the TextView.
        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(),
                place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));

        CharSequence attributions = place.getAttributions();
        if (!TextUtils.isEmpty(attributions)) {
            mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
            Log.i("DetailsFound:", place.toString());
        } else {
            mPlaceAttribution.setText("");
            Log.i("DetailsNotFound: ", place.toString());
        }
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }
}
