package com.example.elizabeta.diplomska;

import com.google.gson.annotations.SerializedName;

public class ModelResponse {
    @SerializedName("firstName")
    public String firstName;
    @SerializedName("lastName")
    public String lastName;
    @SerializedName("emailAddress")
    public String emailAddress;
    @SerializedName("password")
    public String password;
    @SerializedName("uid")
    public String uid;

}
