package com.example.elizabeta.diplomska;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elizabeta.diplomska.Model.UserRResponse;
import com.example.elizabeta.diplomska.Model.UserRoute;
import com.example.elizabeta.diplomska.Model.UserRouteResponse;
import com.example.elizabeta.diplomska.api.RestApi;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRoutes extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.recView)
    RecyclerView recyclerView;
    @BindView(R.id.items)
    TextView items;
    private TextView userName;
    private NavigationView navigationView;
    HistoryAdapter adapter;
    RestApi api;
    String uId;
    String userActive;
    ArrayList<UserRouteResponse> model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ButterKnife.bind(this);
        items.setVisibility(View.INVISIBLE);
        uId = PreferencesManager.getUserActive(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        api = new RestApi(this);
        uId = PreferencesManager.getUserID(this);
        userActive = PreferencesManager.getUserActive(this);
        navigationView = (NavigationView) findViewById(R.id.nav_my_route);
        navigationView.setNavigationItemSelectedListener(this);
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userName);
        if(uId != null && !uId.isEmpty())
        {
            userName.setText(userActive);
        }
        Call<ArrayList<UserRouteResponse>> call = api.getUserRoutes(uId);
        call.enqueue(new Callback<ArrayList<UserRouteResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<UserRouteResponse>> call, Response<ArrayList<UserRouteResponse>> response) {
                if (response.code() == 200) {
                     model=response.body();
                     adapter = new HistoryAdapter(MyRoutes.this, model, new Listener() {
                        @Override
                        public void onRowClick(UserRouteResponse userRoute, int position) {
                        }

                        @Override
                        public void onLongClick(UserRouteResponse userRoute, int position) {

                        }
                    });
                     if(generateList()!=null)
                     {
                         items.setVisibility(View.INVISIBLE);
                         adapter.setItems(generateList());
                         recyclerView.setAdapter(adapter);
                     }
                     else
                     {
                         items.setVisibility(View.VISIBLE);
                     }

                } else if (response.code() == 401) {
                    Toast.makeText(MyRoutes.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserRouteResponse>> call, Throwable t) {
                Toast.makeText(MyRoutes.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });



    }

    ArrayList<UserRouteResponse> generateList()
    {
        if(model.size()>0) {
            return model;
        }
        return null;

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
            Intent intent = new Intent(MyRoutes.this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_maps) {

        }
        else if(id == R.id.logOut)
        {
            PreferencesManager.removeUserID(this);
            Intent intent = new Intent(MyRoutes.this, WelcomeLayout.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
