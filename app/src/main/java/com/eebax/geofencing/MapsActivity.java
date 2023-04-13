package com.eebax.geofencing;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.eebax.geofencing.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    PolygonModel polygonModel;
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    List<Geofence> geofenceList = new ArrayList<>();

    GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS = 10;//in meters
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int REQUEST_BACKGROUND_LOCATION_PERMISSION = 100;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);








        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);



    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (!hasBackgroundLocationPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_BACKGROUND_LOCATION_PERMISSION);
        }

        // Add a marker in Nikunjo 1 and move the camera
        LatLng latLng = new LatLng(23.82500073060955, 90.41795113718227);

        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Nikunjo 1"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f
        ));





//  polygonPointsModel = new PolygonPointsModel();

//        List<LatLng> polygonPoints = new ArrayList<>();
//
//        polygonPoints.add(new LatLng(23.826507116922876, 90.41830848256144));
//        polygonPoints.add(new LatLng(23.82674645179267, 90.41844381015355));
//        polygonPoints.add(new LatLng(23.826845486782062, 90.41872348717723));
//        polygonPoints.add(new LatLng(23.826762957629484, 90.41894903316405));
//
//        polygonPoints.add(new LatLng(23.826655669652645, 90.41930088490355));
//        polygonPoints.add(new LatLng(23.826358564023007, 90.41910691535487));
//        polygonPoints.add(new LatLng(23.82628428750929, 90.41856560498647));
//        polygonPoints.add(new LatLng(23.826556634518358, 90.41843027739436));
//
//        enableUserLocation();
//        addMarker(latLng);
//        addPolygon(polygonPoints);
//        addCircle(latLng, GEOFENCE_RADIUS);
////        addGeofence(latLng, GEOFENCE_RADIUS);//prevoius one
//        addGeofence(latLng, GEOFENCE_RADIUS, polygonPoints);//new one


//        mMap.setOnMapLongClickListener(this);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://run.mocky.io/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<PolygonModel> call = apiService.getAllPolygon();
        call.enqueue(new Callback<PolygonModel>() {
            @Override
            public void onResponse(Call<PolygonModel> call, Response<PolygonModel> response) {
                if (response.isSuccessful() && response.code() == 200) {

                    polygonModel = response.body();

                }
            }

            @Override
            public void onFailure(Call<PolygonModel> call, Throwable t) {

            }
        });

        if (polygonModel != null) {
            generateAllPolygon(polygonModel);

        }


    }

    private void generateAllPolygon(PolygonModel polygonModel) {

        List<List<CoordinateModel>> polygonList = polygonModel.getCoordinates();
        for (List<CoordinateModel> polygon : polygonList) {


            List<LatLng> polygonPoints = new ArrayList<>();


            for (CoordinateModel points : polygon) {

                //evey single point of the polygon
                polygonPoints.add(new LatLng(points.getLat(), points.getLong()));


            }

            addPolygon(polygonPoints);
        }


//        polygonModel = new PolygonModel(latLongModelList);


    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    private boolean hasBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true; // for Android versions lower than Q, background location permission is already granted with the FINE_LOCATION permission
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE || requestCode == REQUEST_BACKGROUND_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(geofenceHelper, "okokokoko", Toast.LENGTH_SHORT).show();
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public void onMapLongClick(LatLng latLng) {
//        if (Build.VERSION.SDK_INT >= 29) {
//            //We need background permission
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                handleMapLongClick(latLng);
//            } else {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
//                    //We show a dialog and ask for permission
//                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
//                } else {
//                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
//                }
//            }
//
//        } else {
//            handleMapLongClick(latLng);
//        }
//
//    }

//    private void handleMapLongClick(LatLng latLng) {
//        mMap.clear();
//        addMarker(latLng);
//        addCircle(latLng, GEOFENCE_RADIUS);
//        addGeofence(latLng, GEOFENCE_RADIUS);
//    }

    private void addGeofence(LatLng latLng, float radius, List<LatLng> polygonPoints) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, polygonPoints, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        geofenceList.add(geofence);

        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofenceList);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: Added...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = geofenceHelper.getErrorString(e);
                Log.d(TAG, "onFailure: " + e);
            }
        });
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);

    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeWidth(4)
                .fillColor(Color.argb(64, 255, 0, 0))
                .strokeColor(Color.argb(255, 255, 0, 0));

        mMap.addCircle(circleOptions);


    }


    private void addPolygon(List<LatLng> polygonPoints) {
        // Define the vertices of the polygon
//        List<LatLng> polygonPoints = new ArrayList<>();


//        polygonPoints.add(new LatLng(23.826507116922876, 90.41830848256144));
//        polygonPoints.add(new LatLng(23.82674645179267, 90.41844381015355));
//        polygonPoints.add(new LatLng(23.826845486782062, 90.41872348717723));
//        polygonPoints.add(new LatLng(23.826762957629484, 90.41894903316405));
//
//        polygonPoints.add(new LatLng(23.826655669652645, 90.41930088490355));
//        polygonPoints.add(new LatLng(23.826358564023007, 90.41910691535487));
//        polygonPoints.add(new LatLng(23.82628428750929, 90.41856560498647));
//        polygonPoints.add(new LatLng(23.826556634518358, 90.41843027739436));

// Create a PolygonOptions object to define the properties of the polygon
        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(polygonPoints) // Add the vertices of the polygon
                .strokeWidth(4)
                .strokeColor(Color.argb(255, 255, 0, 0))
                .fillColor(Color.argb(64, 255, 0, 0));


// Add the polygon to the map
        Polygon polygon = mMap.addPolygon(polygonOptions);

    }

}