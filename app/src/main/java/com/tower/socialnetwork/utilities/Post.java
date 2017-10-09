package com.tower.socialnetwork.utilities;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {
    private String uid;
    private String poster;
    private Integer postid;
    private String text;
    private Bitmap imageBitmap;
    private Timestamp timestamp;
    private List<Comment> comments;

    SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
            "MMM-dd   hh:mm a");

    public Post(String uid, String poster, Integer postid, String text, String timestamp, JSONArray comments){
        this.uid = uid;
        this.poster = poster;
        this.postid = postid;
        this.text = text;
        this.timestamp = Timestamp.valueOf(timestamp);
        this.imageBitmap = null;
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

    public void setImage(Bitmap bitmap){
        this.imageBitmap = bitmap;
    }
    public String getPostText(){
        return text;
    }

    public String getPostTime(){
        TimeDifference timediff = new TimeDifference(new Date(),timestamp);
        return timediff.getDifferenceString();
    }
    public List<Comment> getCommentList(){
        return comments;
    }
    public void print(){
        Log.e("DEBUG POST",postid + " " +text + "\n" );
        for(Comment c:comments){
            Log.e("DEBUG COMMENT", c.getCommentText());
        }

    }

    public Integer getPostId() {
        return postid;
    }

    public String getPoster() {
        return poster;
    }

    public String getUserId() {
        return uid;
    }

    public void addComment(JSONArray data) {
        try {
            JSONObject comment = (JSONObject) data.get(0);
            Comment c= new Comment(comment.getString("uid"),comment.getString("name"),comment.getString("text"),comment.getString("timestamp"));
            this.comments.add(c);
        } catch (JSONException e) {
            Log.e("TAG-----JSON--EX--", e.toString());
        }
    }

    public Bitmap getImage() {
        return imageBitmap;
    }
}
