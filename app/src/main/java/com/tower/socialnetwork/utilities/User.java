package com.tower.socialnetwork.utilities;

public class User {
    private String name;
    private String uid;
    private String email;

    public User(){
    }
    public User(String name, String uid, String email){
        this.name = name;
        this.uid = uid;
        this.email = email;
    }
    public String getUid(){
        return uid;
    }
    @Override
    public String toString() {
        return name + "    |    " + email + "    |    " + uid;
    }
}
