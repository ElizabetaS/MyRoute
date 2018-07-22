package com.example.elizabeta.diplomska;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.elizabeta.diplomska.Model.User;
import com.google.gson.Gson;

public class PreferencesManager {

    private static final String USERID = "USERID";
    private static final String USER = "USER";

    private static SharedPreferences getPreferences(Context c) {
        return c.getApplicationContext().getSharedPreferences("MySharedPreffsFile", Activity.MODE_PRIVATE);
    }

    public static void setUserId(String id, Context c) {
        getPreferences(c).edit().putString(USERID, id).apply();
    }
    public static String getUserID(Context c) {
        return getPreferences(c).getString(USERID, "");
    }

    public static String getUserActive(Context c) {
        return getPreferences(c).getString("UserActive", "");
    }
    public static void setUserActive(String id, Context c) {
        getPreferences(c).edit().putString("UserActive", id).apply();
    }

    public static void addUser (ModelResponse user, Context c)
    {
        Gson gson = new Gson();
        String  mapString = gson.toJson(user);
        getPreferences(c).edit().putString("User", mapString).apply();
    }

    public static void removeUserID(Context c) {
        getPreferences(c).edit().remove(USERID).apply();
    }
    public static ModelResponse getUser (Context c){
        return new Gson().fromJson(getPreferences(c).getString("USER", ""),ModelResponse.class);
    }
public static void addRoute (User routes, Context c)
{
    Gson gson = new Gson();
    String  mapString = gson.toJson(routes);
    getPreferences(c).edit().putString("UserRoute", mapString).apply();
}


    public static User getRoute (Context c){
        return new Gson().fromJson(getPreferences(c).getString("UserRoute", ""),User.class);
    }

    public static void removeRouteList(Context c){

        getPreferences(c).edit().remove("UserRoute").apply();
    }


}
