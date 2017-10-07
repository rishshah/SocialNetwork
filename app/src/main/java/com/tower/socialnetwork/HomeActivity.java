package com.tower.socialnetwork;


import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
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
import com.tower.socialnetwork.fragments.SearchResultsFragment;
import com.tower.socialnetwork.fragments.ViewPostFragment;
import com.tower.socialnetwork.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements AddPostFragment.OnCreatePostListener, ViewPostFragment.OnViewPostListener {
    private View mProgressView;
    private DataToSearchFragment mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar appBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(appBar);
        getSupportActionBar().setTitle("Home");

        mProgressView = findViewById(R.id.home_progress);
        displayViewPostFragment(Constants.SEE_MY_PLUS_FOLLOWERS_POSTS, true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                displayViewPostFragment(Constants.SEE_MY_POSTS, false);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("TAG----Q--", query);
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
                displayViewPostFragment(Constants.SEE_MY_PLUS_FOLLOWERS_POSTS, false);
                return true;

            case R.id.my_posts:
                displayViewPostFragment(Constants.SEE_MY_POSTS, false);
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
    public void createPost(final String postText) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUrl = Constants.SERVER_URL + Constants.ADD_POST;
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
                                displayViewPostFragment(Constants.SEE_MY_POSTS, false);
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG--------JSON--EX--", e.toString());
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
        Intent intent = new Intent().putExtra("action", Constants.BACK);
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
        fragment.setArguments(bundle);

        if (add) {
            fragmentTransaction.add(R.id.fragment_contatiner, fragment).commit();
        } else {
            fragmentTransaction.replace(R.id.fragment_contatiner, fragment).commit();
        }
    }

    private void displaySearchFragment() {
        getSupportActionBar().setTitle("Search User");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new SearchResultsFragment();
        mData = (DataToSearchFragment) fragment;
        fragmentTransaction.replace(R.id.fragment_contatiner, fragment).commit();
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
                        showProgress(false);
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

    public interface DataToSearchFragment {
        void sendData(String data);
    }
}
