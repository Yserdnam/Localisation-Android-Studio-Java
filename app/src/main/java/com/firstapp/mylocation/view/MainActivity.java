package com.firstapp.mylocation.view;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.firstapp.mylocation.R;
import com.firstapp.mylocation.listener.ButtonFusedLocationListener;
import com.firstapp.mylocation.listener.ButtonLocationManagerListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private Button btnLocationManager;
    private Button btnFusedLocation;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialisation
        this.init();
    }

    /**
     * Initialisation of all Components and Action to do
     * Request location using :
     *  - Location Manager
     *  - Fused Location
     * Init button with there specific listener
     */
    private void init(){
        //request update Location with : Location Manager
        this.requestLocationManager();
        //request update Location with : Fused Location
        this.requestFusedLocation();

        // Get All Components from view
        this.btnLocationManager = (Button) findViewById(R.id.btnLocationManager);
        this.btnFusedLocation = (Button) findViewById(R.id.btnFusedLocation);

        //Handle Button Action
        btnLocationManager.setOnClickListener(new ButtonLocationManagerListener(this));
        btnFusedLocation.setOnClickListener(new ButtonFusedLocationListener(this));
    }

    /**
     * Request Location Update For Location Manager
     * Check Permission : ACCESS_FINE_LOCATION for precise location
     * Init all parameters to perform request:
     *  -provider
     *  -period
     *  -distance
     * Execute request update location
     */
    private void requestLocationManager(){
        //Runtime Permission
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        /**
         * init Parameter:
         *  -provider : GPS_PROVIDER - NETWORK_PROVIDER - PASSIVE_PROVIDER
         *  -frequence of update : every 5s and a min distance of 5m
         */
        String provider = LocationManager.GPS_PROVIDER;
        long period = 5000;
        float distanceMin = 5;

        this.locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        //Execute the request using all Parameters specified
        locationManager.requestLocationUpdates(provider, period, distanceMin, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                TextView lblLongitudeLM = (TextView) findViewById(R.id.lblLongitudeLM);
                TextView lblLatitudeLM = (TextView) findViewById(R.id.lblLatitudeLM);
                //Change TextView
                lblLongitudeLM.setText("Longitude: "+location.getLongitude());
                lblLatitudeLM.setText("Latitude: "+location.getLatitude());
                Toast.makeText(MainActivity.this,"Longitude: "+location.getLongitude()+" - Latitude: "+location.getLatitude(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(MainActivity.this,"Location disabled",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                Toast.makeText(MainActivity.this,"Location enabled",Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Request update using Fused Location Provider
     * Check Permission : ACCESS_FINE_LOCATION
     * Init parameter for request :
     *  -priority (depends on precision and energy consumption)
     *  -interval
     * Init callBack to be performed after request location
     * Execute the request
     */
    private void requestFusedLocation(){
        //Runtime Permission
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //Parameter for the request location : Interval , PRIORITY_
        /**
         *  HIGH_ACCURACY : Requests the most accurate location available using GPS, Wi-Fi, and mobile network data. => High energy consumption
         *  BALANCED_POWER_ACCURACY : Provides moderate accuracy by using Wi-Fi and cell towers but avoids GPS to save battery. => Moderate—more efficient than high accuracy but less precise
         *  LOW_POWER : Requests location updates that rely only on cell towers to minimize battery consumption => Low—best for conserving battery
         *  PRIORITY_NO_POWER : Receives location updates only when other apps request location data. It does not initiate location requests itself => Minimal—doesn't consume additional power for location updates
         */
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // 10s
                .setFastestInterval(5000); // 5s

        //Action to get the location
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //Toast.makeText(MainActivity.this,"Longitude: "+location.getLongitude()+" - Latitude: "+location.getLatitude(),Toast.LENGTH_SHORT).show();
                        TextView lblLongitudeFL = (TextView) findViewById(R.id.lblLongitudeFL);
                        TextView lblLatitudeFL = (TextView) findViewById(R.id.lblLatitudeFL);
                        //Change TextView
                        lblLongitudeFL.setText("Longitude: "+location.getLongitude());
                        lblLatitudeFL.setText("Latitude: "+location.getLatitude());
                    }
                }
            }
        };

        //Execute the request : using all Parameters specified
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Stop request location update
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}