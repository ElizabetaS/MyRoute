package com.example.elizabeta.diplomska;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elizabeta.diplomska.Model.UserRResponse;
import com.example.elizabeta.diplomska.Model.UserRoute;
import com.example.elizabeta.diplomska.Model.UserRouteResponse;
import com.example.elizabeta.diplomska.api.RestApi;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    float speed;
    double distance;
    double latitude;
    int speedInt;
    double longitude;
    public static Context context;
    private TextView userName;
    private NavigationView navigationView;
    @BindView(R.id.start)
    Button startBtn;
    @BindView(R.id.finish)
    Button finishBtn;
    @BindView(R.id.speed)
    TextView speedInfo;
    @BindView(R.id.distance)
    TextView distanceInfo;
    @BindView(R.id.duration)
    TextView durationInfo;
    boolean canStart = false;
    boolean isConnected = false;
    boolean locationEnabled = false;
    String userId;
    String startTime;
    String endTime;
    Date oldDate = null;
    RestApi api;
    float avgSpeed;
    LatLng startMarker = null;
    ArrayList<LatLng> coordinates = new ArrayList<>();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        context = getApplicationContext();
        api = new RestApi(this);
        checkInternetConnection(this);
        if(!checkLocationPermission())
        {
            buildDialogForLocation();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userName);

        userId = PreferencesManager.getUserActive(this);
        if(userId!= null && !userId.isEmpty())
        {
            userName.setText(userId);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @OnClick(R.id.start)
    public void setStartBtn() {
        isConnected = checkInternetConnection(MainActivity.this);
        if(!isConnected)
        {
            buildDialogForInternet();
        }
        locationEnabled = isLocationEnabled();
        if (!locationEnabled) {
            buildDialogForLocation();
        }
        if (locationEnabled && isConnected) {

            finishBtn.setVisibility(View.VISIBLE);
            finishBtn.setClickable(true);
            oldDate =  Calendar.getInstance().getTime();
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            startTime = outputFmt.format(time);
            canStart = true;
            Toast.makeText(MainActivity.this, "Route start", Toast.LENGTH_SHORT).show();
            startBtn.setClickable(false);
            startBtn.setVisibility(View.GONE);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0, this);

        }
    }

    @OnClick(R.id.finish)
    public void setFinishBtn() {
       // locationManager.removeUpdates(this);
        startBtn.setClickable(true);
        finishBtn.setClickable(false);
        startBtn.setVisibility(View.VISIBLE);
        finishBtn.setVisibility(View.GONE);

        if(coordinates.size()>0)
        {
            String StartAddress = getAddress(coordinates.get(0).latitude,coordinates.get(0).longitude);
            String EndAddress = getAddress(coordinates.get(coordinates.size()-1).latitude,coordinates.get(coordinates.size()-1).longitude);
            String routeName = StartAddress + "-" + EndAddress;
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            endTime = outputFmt.format(time);
            distance = kilometers(coordinates.get(0).latitude,coordinates.get(0).longitude,coordinates.get(coordinates.size()-1).latitude,coordinates.get(coordinates.size()-1).longitude);
            DecimalFormat df = new DecimalFormat("#.##");
            distance = Double.valueOf(df.format(distance));
            String uId = PreferencesManager.getUserID(MainActivity.this);
            UserRoute userRoute = new UserRoute(routeName,Float.valueOf(speedInt),distance,startTime,endTime,coordinates);
            Call<UserRouteResponse> call = api.postRoute(uId,userRoute);
            call.enqueue(new Callback<UserRouteResponse>() {
                @Override
                public void onResponse(Call<UserRouteResponse> call, Response<UserRouteResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Route finished", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserRouteResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            recreate();

        }

    }
    public void buildDialogForLocation()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Location not enabled")
                .setMessage("Open location settings")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                locationEnabled = true;
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location permission")
                        .setMessage("Allow permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the userRoute once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }

    }

public String getAddress(double latitude,double longitude)
{
    String CityName="";
    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.ENGLISH);
    List<Address> addresses;
    try {
        addresses = geocoder.getFromLocation(latitude,longitude, 1);
         CityName = addresses.get(0).getLocality();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    return CityName;
}

    protected boolean isLocationEnabled(){
        String le = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(le);
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_my_route) {
            // Handle the camera action
        } else if (id == R.id.nav_maps) {
            Intent intent = new Intent(MainActivity.this, MyRoutes.class);
            startActivity(intent);
        }
        else if(id == R.id.logOut)
        {
            PreferencesManager.removeUserID(this);
            Intent intent = new Intent(MainActivity.this, WelcomeLayout.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            Log.d("Loc",latLng.toString());
            coordinates.add(latLng);
            if(startMarker==null)
            {
                startMarker = coordinates.get(0);
                mMap.addMarker(new MarkerOptions().position(startMarker));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startMarker, 15.2f));
            }
            mMap.getUiSettings().setZoomControlsEnabled(true);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.2f);
            mMap.animateCamera(cameraUpdate);
            if (coordinates.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(coordinates).color(Color.BLUE).width(10);
                mMap.addPolyline(opts);
            }
            Date currentTime = Calendar.getInstance().getTime();
        long diff = currentTime.getTime() - oldDate.getTime();
        long mills = Math.abs(diff);
        int hours = (int) (mills/(1000 * 60 * 60));
        int minutes = (int) (mills/(1000*60)) % 60;
        long seconds = (int) (mills / 1000) % 60;
        if (oldDate.before(currentTime)) {
            durationInfo.setText(hours + ":" + minutes + ":" + seconds);
        }
            speed = location.getSpeed();
            speedInt=(int) ((location.getSpeed()*3600)/1000);
            speedInfo.setText(speedInt + "km/h");

        double d = kilometers(coordinates.get(0).latitude,coordinates.get(0).longitude,coordinates.get(coordinates.size()-1).latitude,coordinates.get(coordinates.size()-1).longitude);
            distanceInfo.setText( String.format("%.2f", d) + "km");



    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }

    }
    public static boolean checkInternetConnection(Context ctx) {
        if (ctx == null)
            return false;

        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    public void buildDialogForInternet()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Network not enabled")
                .setMessage("Please turn on your internet connection")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id){
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                isConnected = true;
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public static double kilometers (double lat1, double long1, double lat2, double long2)
    {
        double degToRad= Math.PI / 180.0;
        double phi1 = lat1 * degToRad;
        double phi2 = lat2 * degToRad;
        double lam1 = long1 * degToRad;
        double lam2 = long2 * degToRad;

        return 6371.01 * Math.acos( Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1) );
    }
    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        String strDate = mdformat.format(calendar.getTime());
        return strDate;

    }
}