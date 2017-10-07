package com.tower.socialnetwork.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private String uid;
    private Integer postid;
    private String text;
    private Timestamp timestamp;
    private List<Comment> comments;

    SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
            "MM-dd hh:mm");

    public Post(String uid, Integer postid, String text, String timestamp, JSONArray comments){
        this.uid = uid;
        this.postid = postid;
        this.text = text;
        this.timestamp = Timestamp.valueOf(timestamp);
        this.comments = new ArrayList<>();
        for (int i=0;i<comments.length() ; i++){
            try {
                JSONObject comment = (JSONObject) comments.get(i);
                Comment c= new Comment(comment.getString("uid"),comment.getString("name"),comment.getString("text"),comment.getString("timestamp"));
                this.comments.add(c);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("ERROR",e.toString());
            }

        }
    }

    public String getPostText(){
        return text;
    }

    public String getPostTime(){
        return datetimeFormatter.format(timestamp);
    }
    public List<Comment> getCommentList(){
        return comments;
    }
}
