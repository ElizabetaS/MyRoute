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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.elizabeta.diplomska.Model.UserRoute;
import com.example.elizabeta.diplomska.api.RestApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText username, password;
    @BindView(R.id.email_sign_in_button)
    Button btnlogIn;
    String userN="";
    String pass="";
    RestApi api;
    ModelResponse modelResponse;
    boolean isConnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        ButterKnife.bind(this);
        modelResponse= new ModelResponse();
        api = new RestApi(this);
        isConnected = checkInternetConnection(this);
    }

    @OnClick(R.id.email_sign_in_button)
    public void btnLogIn() {

        if (isConnected == false) {
            buildDialogForInternet();
        } else {
            userN = username.getText().toString();
            pass = password.getText().toString();
            if (userN.isEmpty()) {
                username.setError("Username is required");
            } else if (pass.isEmpty()) {
                password.setError("Password is required");
            } else if (pass.length() < 6) {
                password.setError("Password must contains at least 6 characters");
            } else {
                modelResponse.emailAddress = userN;
                modelResponse.password = pass;
                Call<ModelResponse> call = api.login(modelResponse);
                call.enqueue(new Callback<ModelResponse>() {
                    @Override
                    public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                        if (response.code() == 200) {
                            ModelResponse hasUser = response.body();
                                String userId = hasUser.uid;
                                String userActive = hasUser.emailAddress;
                                PreferencesManager.setUserActive(userActive,LoginActivity.this);
                                PreferencesManager.setUserId(userId,LoginActivity.this);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                        }
                        else if(response.code() == 404)
                        {
                            Toast.makeText(LoginActivity.this, "User with that email not found.", Toast.LENGTH_SHORT).show();

                        }
                        else if(response.code() == 406)
                        {
                            Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ModelResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
}
