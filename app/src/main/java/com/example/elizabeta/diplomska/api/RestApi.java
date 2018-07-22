package com.example.elizabeta.diplomska.api;

import android.content.Context;


import com.example.elizabeta.diplomska.ModelResponse;
import com.example.elizabeta.diplomska.Model.UserRResponse;
import com.example.elizabeta.diplomska.Model.UserRoute;
import com.example.elizabeta.diplomska.Model.UserRouteResponse;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {
    Context activity;

    public static final int requste_max_time_in_seconds = 20;

    public RestApi(Context activity) {
        this.activity = activity;
    }

        public Retrofit getRetrofitInstance()

        {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor())
                    .readTimeout(requste_max_time_in_seconds, TimeUnit.SECONDS)
                    .connectTimeout(requste_max_time_in_seconds, TimeUnit.SECONDS)
                    .build();

            return  new Retrofit.Builder()
                    .baseUrl(ApiConstants.baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

        }
    public ApiService request(){return  getRetrofitInstance().create(ApiService.class);}


    public Call<ModelResponse> register(ModelResponse modelResponse) {
        return request().register(modelResponse);
    }
    public Call<ModelResponse> login(ModelResponse modelResponse) {
        return request().login(modelResponse);
    }
    public Call<UserRouteResponse> postRoute(String uId, UserRoute modelResponse) {
        return request().postRoute(uId,modelResponse);
    }
    public Call<ModelResponse> getUserDetails(String uId) {
        return request().getUserInfo(uId);
    }
    public Call<ArrayList<UserRouteResponse>> getUserRoutes(String uId) {
        return request().getUserRoutes(uId);
    }
        public Call<UserRouteResponse> getRouteDetails(String route) {
            return request().getRoutesDetails(route);
    }
    public Call<Void> deleteRoute(String route) {
        return request().deleteRoute(route);
    }
}
