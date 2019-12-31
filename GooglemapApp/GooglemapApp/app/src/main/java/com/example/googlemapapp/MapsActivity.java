package com.example.googlemapapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private EditText searchtext;
    private static final float DEFAULT_ZOOM=15f;
    private static final String KEY_ = "AIzaSyBHddiaSK8WKSShBvwrn0DnXt19YCh_Hcs";



    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchtext = findViewById(R.id.input_search);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
    }


    private void init() {
        // Log.d("init: initialiazing");

        searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN ||
                        event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute method for searching
                    geoLocate();
                }

                return false;
            }
        });
    }

    private void geoLocate() {
        String searchstring = searchtext.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);

        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchstring, 1);
        } catch (IOException e) {
            Toast.makeText(this, "exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
        moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

        }


    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private void moveCamera(LatLng latLng,float zoom, String title) {
       // Log.d(TAG, "moveCamera: moving the camera to laT: "+latLng.latitude + ", lang: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("My Location")){
            MarkerOptions markerOptions=new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(markerOptions);

        }
        HideSoftKeyboard();

    }
    private void HideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        mMap = googleMap;
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location ) {

        lastLocation = location;

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        MarkerOptions markerOptions1=new MarkerOptions()
                .position(currentLocation)
                .title("your location" );
        mMap.addMarker(markerOptions1);
        //mMap.clear();
        markerOptions.title("You");


        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


        markerOptions.snippet("Your Current location");
        mMap.addMarker(markerOptions).setFlat(true);

    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

}
