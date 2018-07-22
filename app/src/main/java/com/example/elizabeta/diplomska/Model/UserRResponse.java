package com.example.elizabeta.diplomska.Model;

import java.util.ArrayList;

public class UserRResponse {
    public String name;
    public String startTime;
    public String endTime;
    public float speed;
    public double distance;
    public String uid;

    public UserRResponse(String name, String startTime, String endTime, float speed, double distance) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.speed = speed;
        this.distance = distance;
    }
}
