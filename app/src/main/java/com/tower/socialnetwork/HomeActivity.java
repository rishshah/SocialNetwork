package com.tower.socialnetwork;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
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
import com.tower.socialnetwork.fragments.PermissionCallback;
import com.tower.socialnetwork.fragments.SearchResults;
import com.tower.socialnetwork.fragments.SearchResultsFragment;
import com.tower.socialnetwork.fragments.ViewPostFragment;
import com.tower.socialnetwork.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements AddPostFragment.OnCreatePostListener, ViewPostFragment.OnViewPostListener, SearchResults{
    private View mProgressView;
    private DataToSearchFragment mData;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    private PermissionCallback permissionCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar appBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(appBar);
        getSupportActionBar().setTitle("Home");

        mProgressView = findViewById(R.id.home_progress);
        displayViewPostFragment(Constants.SEE_MY_PLUS_FOLLOWERS_POSTS, true,null);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        // Associate searchable configuration with the SearchView
        mSearchMenuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mData = null;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("TAG----C--", newText);

                if (mData != null) {
                    if (newText.length() >= 3) {
                        mData.sendData(newText);
                    }
                } else {
                    displaySearchFragment();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                displayViewPostFragment(Constants.SEE_MY_PLUS_FOLLOWERS_POSTS, false,null);
                return true;

            case R.id.my_posts:
                displayViewPostFragment(Constants.SEE_MY_POSTS, false,null);
                return true;

            case R.id.add_post_button:
                displayAddPostFragment();
                return true;

            case R.id.logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void createPost(final String postText, final String imageString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUrl = Constants.SERVER_URL + Constants.ADD_POST;
        showProgress(true);
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), "Post Created", Toast.LENGTH_SHORT).show();
                                displayViewPostFragment(Constants.SEE_MY_POSTS, false, null);
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG--------JSON--EX--", e.toString());
                        }
                        showProgress(false);
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
                params.put("image", imageString);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void followUser(final String user, final boolean follow) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUrl = Constants.SERVER_URL;
        if(follow) {
            loginUrl += Constants.FOLLOW;
        } else{
            loginUrl += Constants.UNFOLLOW;
        }
        showProgress(true);
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                if(follow){
                                    Toast.makeText(getApplicationContext(), "Now following " + user, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), user + " Unfollowed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),  jsonResponse.getString("message") + " " + user, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG--------JSON--EX--", e.toString());
                        }
                        showProgress(false);
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
                params.put("uid", user);
                return params;
            }
        };
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
    public void closeSearchView() {
        if(mSearchView != null && mSearchMenuItem != null) {
            mSearchView.clearFocus();
            mSearchView.onActionViewCollapsed();
            mSearchMenuItem.collapseActionView();
        }
        mData = null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent().putExtra("action", Constants.BACK);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(permissionCallback != null){
            permissionCallback.permissionGrantedCallback(requestCode, permissions, grantResults);
        } else{
            Log.e("TAG---D--ERR PCB","");
        }
    }
//    private void uploadImage(){
//        //Showing the progress dialog
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        //Disimissing the progress dialog
//                        loading.dismiss();
//                        //Showing toast message of the response
//                        Toast.makeText(MainActivity.this, s , Toast.LENGTH_LONG).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Dismissing the progress dialog
//                        loading.dismiss();
//
//                        //Showing toast
//                        Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                //Converting Bitmap to String
//                String image = getStringImage(bitmap);
//
//                //Getting Image Name
//                String name = editTextName.getText().toString().trim();
//
//                //Creating parameters
//                Map<String,String> params = new Hashtable<String, String>();
//
//                //Adding parameters
//                params.put(KEY_IMAGE, image);
//                params.put(KEY_NAME, name);
//
//                //returning parameters
//                return params;
//            }
//        };
//
//        //Creating a Request Queue
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        //Adding request to the queue
//        requestQueue.add(stringRequest);
//    }

    private void displayAddPostFragment() {
        closeSearchView();
        getSupportActionBar().setTitle("New Post");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new AddPostFragment();
        permissionCallback = (PermissionCallback) fragment;
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void displayViewPostFragment(String action, boolean add, String data) {
        closeSearchView();
        if (action.equals(Constants.SEE_MY_POSTS)) {
            getSupportActionBar().setTitle("My Posts");
        } else {
            getSupportActionBar().setTitle("Home");
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = new ViewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        bundle.putString("data", data);
        fragment.setArguments(bundle);

        if (add) {
            fragmentTransaction.add(R.id.fragment_container, fragment).commit();
        } else {
            fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
        }
    }

    private void displaySearchFragment() {
        getSupportActionBar().setTitle("Search User");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new SearchResultsFragment();
        mData = (DataToSearchFragment) fragment;
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    private void logout() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUrl = Constants.SERVER_URL + "Logout";
        showProgress(true);
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                Intent intent = new Intent().putExtra("action", Constants.LOGOUT);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to logout", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG--------JSON--EX--", e.toString());
                        }
                        showProgress(false);
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

    public interface DataToSearchFragment {
        void sendData(String data);
    }
}
