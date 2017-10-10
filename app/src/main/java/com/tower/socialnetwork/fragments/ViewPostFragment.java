package com.tower.socialnetwork.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
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
import com.tower.socialnetwork.PostAdapter;
import com.tower.socialnetwork.R;
import com.tower.socialnetwork.utilities.Constants;
import com.tower.socialnetwork.utilities.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPostFragment extends Fragment {
    private View view;
    private OnViewPostListener mOnViewPostListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RequestQueue mQueue;
    private List<Post> lPosts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_fragment, container, false);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        lPosts = new ArrayList<>();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });
        return view;
    }

    @Override
    public void onStop () {
        super.onStop();
        view = null;
        if (mQueue != null) {
            mQueue.cancelAll(this);
            mQueue = null;
        }
        if(lPosts != null){
            for (Post p:lPosts){
                if(p.getImage() != null){
                    p.setImage(null);
                }
            }
            lPosts = null;
        }
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnViewPostListener = (OnViewPostListener) context;
            Bundle bundle = this.getArguments();
            mQueue = Volley.newRequestQueue(getActivity());
            showPosts(bundle.getString("action"), bundle.getString("data"));
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCreatePostListener");
        }
    }

    private void showPosts(String action, final String data) {
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(getActivity());
        }
        String loginUrl = Constants.SERVER_URL + action;
        Log.e("TAG", loginUrl);
        mOnViewPostListener.showProgress(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(lPosts != null){
                                lPosts.clear();
                            } else{
                                lPosts = new ArrayList<>();
                            }
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                JSONArray posts = jsonResponse.getJSONArray("data");
                                lPosts.clear();
                                for (int i = 0; i < posts.length(); i++) {
                                    JSONObject post = (JSONObject) posts.get(i);
                                    Post uPost = new Post(post.getString("uid"), post.getString("name"), post.getInt("postid"), post.getString("text"), post.getString("timestamp"), post.getJSONArray("Comment"));
                                    if(!post.isNull("image")) {
                                        Bitmap x = getBitmapImage(post.getString("image"));
                                        Log.e("IMAGEBITMAP", x.toString());
                                        uPost.setImage(x);
                                    }
                                    lPosts.add(uPost);
                                }
                                addContentToList(lPosts);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Failed to load your posts", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG--------JSON EX--", e.toString());
                        }
                        mOnViewPostListener.showProgress(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT).show();
                        Log.e("TAG--------VOLLEY EX--", error.toString());
                        mOnViewPostListener.showProgress(false);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (data != null) {
                    params.put("uid", data);
                }
                return params;
            }
        };
        mQueue.add(stringRequest);
    }

    public interface OnViewPostListener {
        void showProgress(boolean is_visible);
    }

    private void addContentToList(List<Post> values) {
        ListView listView = view.findViewById(R.id.list);
        ArrayAdapter adapter = new PostAdapter(getActivity(), R.layout.item_post, new ArrayList<>(values));
        listView.setAdapter(adapter);

    }
    private Bitmap getBitmapImage(String imageString){
        byte[] decodedBytes = Base64.decode(
                imageString.substring(imageString.indexOf(",")  + 1),
                Base64.DEFAULT
        );
        final WeakReference<Bitmap> mBitmapReference = new WeakReference<>(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        return mBitmapReference.get();
    }
}
