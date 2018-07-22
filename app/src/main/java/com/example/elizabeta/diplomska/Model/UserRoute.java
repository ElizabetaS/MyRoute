package com.example.elizabeta.diplomska.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by elisp on 09.5.2018.
 */

public class UserRoute implements Serializable {
    public String name;
    public float speed;
    public double distance;
    public String startTime;
    public String endTime;
    public ArrayList<LatLng> locations = new ArrayList<>();
    public String uid;

    public UserRoute(String name, float speed, double distance,String startTime, String endTime, ArrayList<LatLng> locations) {
        this.name = name;
        this.speed = speed;
        this.distance = distance;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locations = locations;
    }
}
