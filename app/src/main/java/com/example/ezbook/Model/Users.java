package com.example.ezbook.Model;



public class Users {

    String name,id,email,password;



    public Users(){



    }



    public Users(String name, String id, String email, String password) {

        this.name = name;

        this.id = id;

        this.email = email;

        this.password = password;

    }



    public String getName() {

        return name;

    }



    public void setName(String name) {

        this.name = name;

    }



    public String getId() {

        return id;

    }



    public void setId(String id) {

        this.id = id;

    }



    public String getEmail() {

        return email;

    }



    public void setEmail(String email) {

        this.email = email;

    }



    public String getPassword() {

        return password;

    }



    public void setPassword(String password) {

        this.password = password;

    }

}