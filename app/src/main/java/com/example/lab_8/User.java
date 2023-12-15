package com.example.lab_8;

public class User {
    public String name;
    public String email;

    // хоосон байгуулагч функц нь автоматаар
    // DataSnapshot.getValue(User.class) функцийг дуудаж ажиллуулна.
    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Fix the return type of getName() method
    public String getName() {
        return name;
    }

    // Fix the return type of getEmail() method
    public String getEmail() {
        return email;
    }
}