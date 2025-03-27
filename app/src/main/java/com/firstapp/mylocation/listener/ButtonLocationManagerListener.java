package com.firstapp.mylocation.listener;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firstapp.mylocation.view.MainActivity;
import com.firstapp.mylocation.R;

import java.util.function.Consumer;

public class ButtonLocationManagerListener implements View.OnClickListener {

    private AppCompatActivity activity;
    private LocationManager locationManager;
    public ButtonLocationManagerListener(AppCompatActivity activity) {
        this.activity = activity;
        this.locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
    }

    @Override
    @SuppressLint({"MissingPermission", "NewApi"})
    public void onClick(View v) {
        //Specify Provider : GPS - NETWORK - PASSIVE
        String provider = LocationManager.GPS_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) Toast.makeText(activity,"Longitude: "+location.getLongitude()+" - Latitude: "+location.getLatitude(),Toast.LENGTH_LONG).show();
        else Toast.makeText(activity,"Location not available",Toast.LENGTH_SHORT).show();
    }

}
