package com.example.jase.bertapp;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
        implements NavigationView.OnNavigationItemSelectedListener{

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(MainNavigation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            finish();
            startActivity(getIntent());

            return;
        }

        this.InitializeChoices();
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
        //getMenuInflater().inflate(R.menu.main_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_virtualAssistant) {
            // Handle Virtual Assistant-Activity
            startActivity(new Intent(this, VirtualAssistantActivity.class));
        } else if (id == R.id.nav_login) {
            // Handle login-Activity
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_signup) {
            // Handle sign up-Activity
            startActivity(new Intent(this, SignUpActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void InitializeChoices(){
        Button btnFood = (Button) findViewById(R.id.btnFood);
        Button btnGo = (Button) findViewById(R.id.btnGo);
        Button btnRelax = (Button) findViewById(R.id.btnRelax);
        Button buttonUseVirtualAssistant = (Button) findViewById(R.id.buttonUseVirtualAssistant);

        Intent step2 = new Intent(getBaseContext(), menu2.class);
        Intent virtualAssistantIntent = new Intent(getBaseContext(), VirtualAssistantActivity.class);

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

        buttonUseVirtualAssistant.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(virtualAssistantIntent);
            }
        });
    }
}
