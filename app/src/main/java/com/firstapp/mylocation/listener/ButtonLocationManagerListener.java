package com.firstapp.mylocation.listener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.firstapp.mylocation.view.MainActivity;
import com.firstapp.mylocation.R;

import java.util.function.Consumer;

public class ButtonLocationManagerListener implements View.OnClickListener {


    private AppCompatActivity activity;
    private LocationManager locationManager;
    private String provider;
    public ButtonLocationManagerListener(AppCompatActivity activity,String provider) {
        this.activity = activity;
        this.provider = provider;
        this.locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
    }

    @Override
    public void onClick(View v) {
        //Check Permission : ACCESS_FINE_LOCATION or BACKGROUND_LOCATION (Android 10+) if granted -> do request
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Get The Last Known Location using the provider -> get it from temp memory of the phone
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                //Show Location
                Toast.makeText(activity.getApplicationContext(),"Longitude: "+location.getLongitude()+" - Latitude: "+location.getLatitude(),Toast.LENGTH_SHORT).show();
                if(activity instanceof MainActivity){
                    TextView lblLongitudeLM = (TextView) activity.findViewById(R.id.lblLongitudeLM);
                    TextView lblLatitudeLM = (TextView) activity.findViewById(R.id.lblLatitudeLM);
                    //Change TextView
                    lblLongitudeLM.setText("Longitude: "+location.getLongitude());
                    lblLatitudeLM.setText("Latitude: "+location.getLatitude());
                }
            }
            else Toast.makeText(activity,"Location is not available",Toast.LENGTH_SHORT).show();
        }
    }

}
