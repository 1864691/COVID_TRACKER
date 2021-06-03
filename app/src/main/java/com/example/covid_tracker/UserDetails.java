package com.example.covid_tracker;

public class UserDetails {
    private int id;
    private String username,location,status, date;

    public UserDetails(int id, String username, String location, String status, String date) {
        this.id=id;
        this.username = username;
        this.location = location;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getUsername(){

        return username;
    }

    public String getLocation(){

        return location;
    }

    public String getStatus(){

        return status;
    }

    public String getDate(){

        return date;
    }

}

