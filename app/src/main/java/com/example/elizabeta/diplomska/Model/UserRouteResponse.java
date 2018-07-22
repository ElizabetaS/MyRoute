package com.example.elizabeta.diplomska.Model;

import com.example.elizabeta.diplomska.Locations;
import com.example.elizabeta.diplomska.Model.NewUser;
import com.example.elizabeta.diplomska.Model.UserRoute;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserRouteResponse {
    @SerializedName("user")
    public NewUser user;
    @SerializedName("locations")
    public ArrayList<Locations> locations;
    @SerializedName("name")
    public String name;
    @SerializedName("startTime")
    public String startTime;
    @SerializedName("endTime")
    public String endTime;
    @SerializedName("speed")
    public float speed;
    @SerializedName("distance")
    public double distance;
    @SerializedName("uid")
    public String uid;
}
