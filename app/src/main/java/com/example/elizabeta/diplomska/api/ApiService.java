package com.example.elizabeta.diplomska.api;


import android.net.http.HttpResponseCache;

import com.example.elizabeta.diplomska.ModelResponse;
import com.example.elizabeta.diplomska.Model.UserRResponse;
import com.example.elizabeta.diplomska.Model.UserRoute;
import com.example.elizabeta.diplomska.Model.UserRouteResponse;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("users/register")
    Call<ModelResponse> register(@Body ModelResponse modelResponse);
    @POST("users/login")
    Call<ModelResponse> login(@Body ModelResponse modelResponse);
    @POST("users/{userUid}/routes")
    Call<UserRouteResponse> postRoute(@Path("userUid") String uId, @Body UserRoute userRoute );
    @GET("users/{userUid}")
    Call<ModelResponse> getUserInfo(@Path("userUid") String uId);
    @GET("users/{userUid}/routes")
    Call<ArrayList<UserRouteResponse>> getUserRoutes(@Path("userUid") String uId);
    @GET("routes/{routeUid}")
    Call<UserRouteResponse> getRoutesDetails(@Path("routeUid") String routeUid);
    @DELETE("routes/{routeUid}")
    Call<Void> deleteRoute(@Path("routeUid")String routeUid);
}
