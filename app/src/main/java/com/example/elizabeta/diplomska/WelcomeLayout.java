package com.example.elizabeta.diplomska;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeLayout extends AppCompatActivity {

    public String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_layout);
        ButterKnife.bind(this);
        userId = PreferencesManager.getUserID(this);
        if (userId != null && !userId.isEmpty()) {
            Intent intent = new Intent(WelcomeLayout.this, MainActivity.class);
            startActivity(intent);
        }

    }
    @OnClick(R.id.logIn)
    public void LogInClick(View view)
    {
        Intent intent = new Intent(WelcomeLayout.this, LoginActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.join)
    public void JoinClick(View view)
    {
        Intent intent = new Intent(WelcomeLayout.this, SignUp.class);
        startActivity(intent);
    }


}
