package com.eebax.geofencing.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.eebax.geofencing.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationManager {

    private FusedLocationProviderClient fusedLocationProviderClient;
    public static String mobileModel;

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
        //no need because for both model permission will be same
        mobileModel = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ? "WALTON" : "SAMSUNG";

    }

    public void getCurrentLocation(GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == 0)
            isFinePermissionGranted = true;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == 0)
            isCoursePermissionGranted = true;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == 0)
            isBackgroundPermissionGranted = true;

        // All the permission is granted
        if (isCoursePermissionGranted && isFinePermissionGranted) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 19));
                    googleMap.addMarker(new MarkerOptions().position(currentLatLong));
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                }

            });
            return;
        } else {
            //model is samsung and But  the permission is not granted
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);


        }


    }


}
