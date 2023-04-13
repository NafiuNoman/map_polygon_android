package com.eebax.geofencing;

import java.util.List;
import java.util.ArrayList;
import java.util.List;
public class PolygonModel {


    private String type;

    private String area;

    private List<List<CoordinateModel>> coordinates;

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setArea(String area){
        this.area = area;
    }
    public String getArea(){
        return this.area;
    }
    public void setCoordinates(List<List<CoordinateModel>> coordinates){
        this.coordinates = coordinates;
    }
    public List<List<CoordinateModel>> getCoordinates(){
        return this.coordinates;
    }


}
