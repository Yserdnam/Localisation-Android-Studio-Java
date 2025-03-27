package com.firstapp.mylocation.listener;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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

public class ButtonFusedLocationListener implements View.OnClickListener {

    FusedLocationProviderClient fusedLocationClient;
    private AppCompatActivity activity;
    public ButtonFusedLocationListener(AppCompatActivity activity) {
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        if(ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) Toast.makeText(activity,"Longitude: "+location.getLongitude()+" - Latitude: "+location.getLatitude(),Toast.LENGTH_LONG).show();
            else Toast.makeText(activity,"Location not available",Toast.LENGTH_SHORT).show();
        });
    }
}
