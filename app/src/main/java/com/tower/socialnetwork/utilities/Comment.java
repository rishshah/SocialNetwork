package com.tower.socialnetwork.utilities;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Comment {
    private String uid;
    private String name;
    private String text;
    private Timestamp timestamp;

    SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
            "MMM-dd   hh:mm a");

    public Comment(String uid, String name, String text, String timestamp){
        this.uid = uid;
        this.name = name;
        this.text = text;
        this.timestamp = Timestamp.valueOf(timestamp);
    }

    public String getCommentText(){
        return text;
    }

    public String getCommenter(){
        return name;
    }

    public String getCommentTime(){
        return datetimeFormatter.format(timestamp);
    }

}
