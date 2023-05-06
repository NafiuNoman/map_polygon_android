package com.eebax.geofencing.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.eebax.geofencing.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationManager {

    private FusedLocationProviderClient fusedLocationProviderClient;

    GoogleMap googleMap;
    LatLng currentLatLong;
    Activity activity;
    Context context;
    Boolean isBackgroundPermissionGranted = false;
    Boolean isFinePermissionGranted = false;
    Boolean isCoursePermissionGranted = false;

    public LocationManager(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

    }

    public void getCurrentLocation(GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == 0)
            isFinePermissionGranted = true;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == 0)
            isCoursePermissionGranted = true;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == 0)
            isBackgroundPermissionGranted = true;


        if (isBackgroundPermissionGranted && isCoursePermissionGranted && isFinePermissionGranted) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {

            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    1);


        }




        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 19));
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
            }

        });


    }


}
