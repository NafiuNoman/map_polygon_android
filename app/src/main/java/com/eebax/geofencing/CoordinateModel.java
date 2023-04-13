package com.eebax.geofencing;

import com.google.gson.annotations.SerializedName;

public class CoordinateModel {


    private double lat;

    @SerializedName("long")
    private double longValue;



    public void setLat(double lat){
        this.lat = lat;
    }
    public double getLat(){
        return this.lat;
    }
    public void setLong(double longValue){
        this.longValue = longValue;
    }
    public double getLong(){
        return this.longValue;
    }
}
