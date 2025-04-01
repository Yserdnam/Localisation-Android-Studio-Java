package com.firstapp.mylocation.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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
    private String provider;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_LOCATION_CODE = 200;

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
     * Init button with their specific listener
     */
    private void init(){
        //Runtime Permission
        //if using Android 10+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_LOCATION_CODE);
            }
        }
        else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
        }


        //Request Location using : Location Manager
        provider = LocationManager.GPS_PROVIDER; //specify provider
        //requestLocationManager();
        //Request Location using : Fused Location Provider
        requestFusedLocation();
        // Get All Components from view
        this.btnLocationManager = (Button) findViewById(R.id.btnLocationManager);
        this.btnFusedLocation = (Button) findViewById(R.id.btnFusedLocation);

        //Handle Button Action
        btnLocationManager.setOnClickListener(new ButtonLocationManagerListener(this,provider));
        btnFusedLocation.setOnClickListener(new ButtonFusedLocationListener(this));
    }

    /**
     * Request update using Location Manager
     */
    private void requestLocationManager(){
        //Check Permission : ACCESS_FINE_LOCATION or BACKGROUND_LOCATION (Android 10+) if granted -> do request
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
            /**
             * init Parameter:
             *  -provider : GPS_PROVIDER - NETWORK_PROVIDER - PASSIVE_PROVIDER
             *  -frequence of update : every 5s and a min distance of 5m
             */
            long period = 5000;
            float distanceMin = 5;

            //Init location Manager
            this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //Action to do when requesting location
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    //Toast.makeText(MainActivity.this,"Location Manager : "+location.toString(),Toast.LENGTH_SHORT).show();
                    Log.d("Location Manager",location.toString());
                }
                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    Toast.makeText(MainActivity.this,"Please enable location in 'Settings' to get Location Data",Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    Toast.makeText(MainActivity.this,"Location Data is available",Toast.LENGTH_SHORT).show();
                }
            };
            //Execute the request using all Parameters specified
            locationManager.requestLocationUpdates(provider, period, distanceMin, locationListener);
        }

    }

    /**
     * Request update using Fused Location Provider
     */
    private void requestFusedLocation(){
        //Check Permission : ACCESS_FINE_LOCATION or BACKGROUND_LOCATION (Android 10+) if granted -> do request
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Init FusedLocationProvider
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
                    .setFastestInterval(5000) //10s
                    .setInterval(10000); // 5s

            //Action to do when requesting location
            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    for (Location location : locationResult.getLocations())
                        if (location != null)
                            Log.d("Fused Location",location.toString());
                            //Toast.makeText(MainActivity.this,"Fused Location Provider : "+location.toString(),Toast.LENGTH_SHORT).show();
                }
            };

            //Execute the request : using all Parameters specified
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this.getApplicationContext(),"'Location Request' is now available : enable location in 'Settings' if you want to get location data!",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.getApplicationContext(),"Location permission is denied!",Toast.LENGTH_LONG).show();
        }
    }

}