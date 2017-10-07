package com.tower.socialnetwork.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tower.socialnetwork.R;
import com.tower.socialnetwork.utilities.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewPostFragment extends Fragment {
    private View view;
    private OnViewPostListener mOnViewPostListener;
    public static final String SERVER_URL = "http://10.42.0.196:8080/Backend/";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_post_fragment,container,false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnViewPostListener = (OnViewPostListener) activity;
            Bundle bundle = this.getArguments();
            showPosts(bundle.getString("action"));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCreatePostListener");
        }
    }

    private void showPosts(String action) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String loginUrl = SERVER_URL + action;
        mOnViewPostListener.showProgress(true);
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        mOnViewPostListener.showProgress(false);
                        List<String> values = new ArrayList<>();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                JSONArray posts = jsonResponse.getJSONArray("data");
                                for (int i = 0; i < posts.length(); i++) {
                                    JSONObject post = (JSONObject) posts.get(i);
                                    Post uPost = new Post(post.getString("uid"), post.getInt("postid"), post.getString("text"), post.getString("timestamp"));
                                    values.add(uPost.text);
                                }
                                addContentToList(values);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public  interface OnViewPostListener{
        void showProgress(boolean is_visible);
    }

    private void addContentToList(List<String> values) {
        ListView listView = view.findViewById(R.id.post_list);
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.activity_listview, values);
        listView.setAdapter(adapter);

    }
    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        Toolbar appBar = (Toolbar) findViewById(R.id.app_bar);
//        setSupportActionBar(appBar);
//        getSupportActionBar().setTitle("Home");
//
//        setProgressBar(findViewById(R.id.login_progress));
//        showPosts(getIntent().getExtras().getString("action"));
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (getIntent().getExtras().getString("action").equals("SeeMyPosts")) {
//            super.onBackPressed();
//        }
//        Intent intent = new Intent().putExtra("action", "Back");
//        setResult(RESULT_OK, intent);
//        finish();
//        moveTaskToBack(true);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                if (data.getStringExtra("action").equals("PostCreated")) {
//                    Toast.makeText(getApplicationContext(), "Post Created", Toast.LENGTH_SHORT).show();
//                    showPosts("SeePosts");
//                } else if (data.getStringExtra("action").equals("Back")) {
//                    showPosts("SeePosts");
//                } else if (data.getStringExtra("action").equals("SeeMyPosts")) {
//                    showPosts("SeeMyPosts");
//                } else if (data.getStringExtra("action").equals("Logout")) {
//                    Intent intent = new Intent().putExtra("action", "Logout");
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            }
//        }
//    }
}
