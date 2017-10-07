package com.tower.socialnetwork.utilities;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Post {
    private String uid;
    private Integer postid;
    public String text;
    private Timestamp timestamp;

    SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
            "MM-dd hh:mm");

    public Post(String uid, Integer postid, String text, String timestamp){
        this.uid = uid;
        this.postid = postid;
        this.text = text;
        this.timestamp = Timestamp.valueOf(timestamp);
    }
}
