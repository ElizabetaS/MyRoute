package com.example.elizabeta.diplomska.Model;

public class NewUser {
    public String firstName;
    public String lastName;
    public String emailAddress;
    public String password;
    public String uId;
    public NewUser(String firstName,String lastName,String emailAddress,String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
    }
}
