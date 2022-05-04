package com.example.able_project;

public class User {
//    public int id;
    public String userName;
    public String phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName, String phoneNumber) {
//        this.id = id;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
    }

}
