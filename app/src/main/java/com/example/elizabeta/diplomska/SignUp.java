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
import android.widget.EditText;
import android.widget.Toast;

import com.example.elizabeta.diplomska.api.RestApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    @BindView(R.id.firstName)
    EditText firstName;
    @BindView(R.id.lastName)
    EditText lastName;
    @BindView(R.id.register_username)
    EditText userName;
    @BindView(R.id.register_password)
    EditText password;
    String firstN,lastN,userN,pass;
    public RestApi api;
    public ModelResponse modelResponse;
    boolean isConnected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        api = new RestApi(this);
        modelResponse = new ModelResponse();
        isConnected = checkInternetConnection(this);
    }
    @OnClick(R.id.signUp)
    public void signUpBtn(View view)
    {
        if (isConnected == false) {
            buildDialogForInternet();
        } else {
            firstN = firstName.getText().toString();
            lastN = lastName.getText().toString();
            userN = userName.getText().toString();
            pass = password.getText().toString();
            if (firstN.isEmpty()) {
                firstName.setError("First name must be fill");
            } else if (lastN.isEmpty()) {
                lastName.setError("Last name must be filled in");
            } else if (userN.isEmpty()) {
                userName.setError("Username must be filled in");
            } else if (pass.isEmpty()) {
                password.setError("Password must be filled in");
            } else if (pass.length() < 6) {
                password.setError("Password must contains at least 6 characters");
            } else {
                modelResponse.firstName = userN;
                modelResponse.lastName = lastN;
                modelResponse.emailAddress = userN;
                modelResponse.password = pass;
                Call<ModelResponse> call = api.register(modelResponse);
                call.enqueue(new Callback<ModelResponse>() {
                    @Override
                    public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ModelResponse> call, Throwable t) {
                        Toast.makeText(SignUp.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
                PreferencesManager.setUserId(userN, this);
                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);

            }
        }
    }
    public static boolean checkInternetConnection(Context ctx) {
        if (ctx == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void buildDialogForInternet()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
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
