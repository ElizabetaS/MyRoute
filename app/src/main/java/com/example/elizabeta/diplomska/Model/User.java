package com.example.elizabeta.diplomska.Model;

public class User {

    public String userName;
    public String password;
    public UserRoute userRoutes;

    public User(String userName, String password, UserRoute userRouts)
    {
        this.userName  = userName;
        this.password = password;
        this.userRoutes = userRouts;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userRouteRoutes=" + userRoutes +
                '}';
    }
}
