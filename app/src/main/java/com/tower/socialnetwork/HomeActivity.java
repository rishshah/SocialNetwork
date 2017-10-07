package com.tower.socialnetwork;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tower.socialnetwork.fragments.AddPostFragment;
import com.tower.socialnetwork.fragments.ViewPostFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements AddPostFragment.OnCreatePostListener, ViewPostFragment.OnViewPostListener {
    private static final String SERVER_URL = "http://10.42.0.196:8080/Backend/";
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar appBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(appBar);
        getSupportActionBar().setTitle("Home");
        appBar.setTitleTextColor(getResources().getColor(R.color.colorLight));

        mProgressView = findViewById(R.id.login_progress);
        displayViewPostFragment("SeePosts", true);
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
            case R.id.home:
                displayViewPostFragment("SeeMyPosts", false);
                return true;

            case R.id.my_posts:
                displayViewPostFragment("SeePosts", false);
                return true;

            case R.id.add_post_button:
                displayAddPostFragment();
                return true;

            case R.id.search:
                return true;

            case R.id.logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void createPost(final String postText) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUrl = SERVER_URL + "CreatePost";
        showProgress(true);
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        showProgress(false);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), "Post Created", Toast.LENGTH_SHORT).show();
                                displayViewPostFragment("SeeMyPosts", false);
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("content", postText);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void showProgress(boolean is_visible) {
        if (is_visible) {
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            mProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent().putExtra("action", "Back");
        setResult(RESULT_OK, intent);
        finish();
    }

    private void displayAddPostFragment() {
        getSupportActionBar().setTitle("New Post");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new AddPostFragment();
        fragmentTransaction.replace(R.id.fragment_contatiner, fragment).commit();
    }

    private void displayViewPostFragment(String action, boolean add) {
        if (action.equals("SeeMyPosts")) {
            getSupportActionBar().setTitle("My Posts");
        } else {
            getSupportActionBar().setTitle("Home");
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = new ViewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        fragment.setArguments(bundle);

        if (add) {
            fragmentTransaction.add(R.id.fragment_contatiner, fragment).commit();
        } else {
            fragmentTransaction.replace(R.id.fragment_contatiner, fragment).commit();
        }
    }

    private void logout() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUrl = SERVER_URL + "Logout";
        showProgress(true);
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        showProgress(false);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                Intent intent = new Intent().putExtra("action", "Logout");
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to logout", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
