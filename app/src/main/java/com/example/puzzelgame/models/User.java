package com.example.puzzelgame.models;

public  class User {
    public String name, email, phone;
    private int score;
    public int level;

    public User() { }

    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.level = 1; // default level
        this.score=0; // default score
    }
}
