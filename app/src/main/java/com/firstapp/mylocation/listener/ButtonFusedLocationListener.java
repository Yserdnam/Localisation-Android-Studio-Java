package com.firstapp.mylocation.listener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firstapp.mylocation.view.MainActivity;
import com.firstapp.mylocation.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class ButtonFusedLocationListener implements View.OnClickListener {

    FusedLocationProviderClient fusedLocationClient;
    private AppCompatActivity activity;
    public ButtonFusedLocationListener(AppCompatActivity activity) {
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        //Check Permission : ACCESS_FINE_LOCATION or BACKGROUND_LOCATION (Android 10+) if granted -> do request
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //Get The Last Known Location -> get it from temp memory of the phone
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        //Show Location
                        Toast.makeText(activity.getApplicationContext(),"Longitude: "+location.getLongitude()+" - Latitude: "+location.getLatitude(),Toast.LENGTH_SHORT).show();
                        if(activity instanceof MainActivity){
                            TextView lblLongitudeFL = (TextView) activity.findViewById(R.id.lblLongitudeFL);
                            TextView lblLatitudeFL= (TextView) activity.findViewById(R.id.lblLatitudeFL);
                            //Change TextView
                            lblLongitudeFL.setText("Longitude: "+location.getLongitude());
                            lblLatitudeFL.setText("Latitude: "+location.getLatitude());
                        }
                    }
                    else Toast.makeText(activity,"Location not available",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
