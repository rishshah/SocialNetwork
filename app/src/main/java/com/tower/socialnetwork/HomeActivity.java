package com.tower.socialnetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tower.socialnetwork.utilities.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String SERVER_URL = "http://10.42.0.196:8080/Backend/";
    private View mProgressView;
    private ListView mPostList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        id = getIntent().getExtras().getString("id");

        Toolbar appBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(appBar);
        getSupportActionBar().setTitle("Home");
        appBar.setTitleTextColor(getResources().getColor(R.color.colorLight));

        mPostList = (ListView) findViewById(R.id.post_list);
        mProgressView = findViewById(R.id.login_progress);
//        addContentToList();
        showMyPosts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_posts:
                showMyPosts();
                return true;

            case R.id.add_post:

                return true;

            case R.id.search:

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void showMyPosts() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUrl = SERVER_URL + "SeeMyPosts";
        showProgress(true);
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        showProgress(false);
                        List<String> values = new ArrayList<>();
                        List<Post> lPosts = new ArrayList<>();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                JSONArray posts = jsonResponse.getJSONArray("data");
                                for (int i=0; i<posts.length(); i++){
                                    JSONObject post = (JSONObject) posts.get(i);
                                    Post uPost = new Post(post.getString("uid"), post.getInt("postid"), post.getString("text"), post.getString("timestamp"),post.getJSONArray("Comment"));
                                    lPosts.add(uPost);
                                    values.add(uPost.getPostText());
                                }
//                                addContentToList(values);
                                addContentToList(lPosts);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Failed to load your posts" , Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse (VolleyError error){
                        Toast.makeText(getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT).show();
                    }
                }
        );
                    // Add the request to the RequestQueue.
        queue.add(stringRequest);
                }

//    private void addContentToList(List<String> values) {
//        ListView listView = (ListView) findViewById(R.id.post_list);
//
//        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_postview,values);
//
////        ListView listView = (ListView) findViewById(R.id.mobile_list);
//            listView.setAdapter(adapter);
//
//    }

    private void addContentToList(List<Post> values) {
        ListView listView = (ListView) findViewById(R.id.post_list);
        ArrayAdapter adapter = new PostAdapter(this,R.layout.activity_postview, new ArrayList<Post>(values));
        listView.setAdapter(adapter);

    }


    private void addContentToList() {
        String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
                "WebOS","Ubuntu","Windows7","Max OS X"};

        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_postview,mobileArray);

        ListView listView = (ListView) findViewById(R.id.post_list);
        listView.setAdapter(adapter);

    }


    private void showProgress(boolean is_visible) {
        if (is_visible) {
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            mProgressView.setVisibility(View.GONE);
        }
    }
}
