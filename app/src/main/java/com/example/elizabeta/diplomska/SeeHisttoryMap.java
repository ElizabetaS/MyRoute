package com.example.elizabeta.diplomska;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.elizabeta.diplomska.Model.UserRResponse;
import com.example.elizabeta.diplomska.Model.UserRoute;
import com.example.elizabeta.diplomska.Model.UserRouteResponse;
import com.example.elizabeta.diplomska.api.RestApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeeHisttoryMap extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    ArrayList<LatLng> loc = new ArrayList<>();
    RestApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_histtory_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        api = new RestApi(this);
        String userRouteId = intent.getSerializableExtra("Route").toString();
        Call<UserRouteResponse> call = api.getRouteDetails(userRouteId);
        call.enqueue(new Callback<UserRouteResponse>() {
            @Override
            public void onResponse(Call<UserRouteResponse> call, Response<UserRouteResponse> response) {
                if (response.code() == 200) {
                    UserRouteResponse model = response.body();
                    for (int i = 0; i < model.locations.size(); i++) {
                        LatLng location = new LatLng(model.locations.get(i).latitude, model.locations.get(i).longitude);
                        loc.add(location);

                    }
                    if(loc.size()>0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc.get(0), 15.2f));
                        PolylineOptions line = new PolylineOptions();
                        mMap.addMarker(new MarkerOptions().position(loc.get(0))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        for (int j = 0; j < loc.size(); j++) {
                            line.add(loc.get(j));
                        }

                        mMap.addMarker(new MarkerOptions().position(loc.get(loc.size() - 1))).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        Polyline polylin = mMap.addPolyline(line);
                    }

                } else if (response.code() == 401) {
                    Toast.makeText(SeeHisttoryMap.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserRouteResponse> call, Throwable t) {
                Toast.makeText(SeeHisttoryMap.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }
}
