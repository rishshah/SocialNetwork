package com.tower.socialnetwork.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tower.socialnetwork.HomeActivity;
import com.tower.socialnetwork.R;
import com.tower.socialnetwork.utilities.Constants;
import com.tower.socialnetwork.utilities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsFragment extends Fragment implements HomeActivity.DataToSearchFragment {
    private List<String> values;
    private List<User> mUsers;
    private ListView mList;
    private ArrayAdapter mAdapter;
    private SearchResults mSearchResults;
    private RequestQueue mQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_list, container, false);

        values = new ArrayList<>();
        mUsers = new ArrayList<>();

        mList = view.findViewById(R.id.user_list);
        registerForContextMenu(mList);

        mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_selectable_list_item, values);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mList.getItemAtPosition(position).toString().equals(Constants.USER_NOT_FOUND))
                    mList.showContextMenuForChild(view);
            }
        });
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return mList.getItemAtPosition(position).toString().equals(Constants.USER_NOT_FOUND);
            }
        });

        return view;
    }

    @Override
    public void onStop () {
        super.onStop();
        super.onDestroy();
        if (mQueue != null) {
            mQueue.cancelAll(this);
        }
    }

    @Override
    public void sendData(final String searchText) {
        if(mQueue == null)
            mQueue  = Volley.newRequestQueue(getActivity());
        String loginUrl = Constants.SERVER_URL + Constants.SEARCH;
        mSearchResults.showProgress(true);
        Log.e("TAG", loginUrl);
        // Request a json response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        try {
                            values.clear();
                            mUsers.clear();
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                JSONArray results = jsonResponse.getJSONArray("data");
                                results = (JSONArray) results.get(0);
                                if (results.length() == 0) {
                                    values.add(Constants.USER_NOT_FOUND);
                                } else {
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject res = (JSONObject) results.get(i);
                                        User user = new User(res.getString("name"), res.getString("uid"), res.getString("email"));
                                        mUsers.add(user);
                                        values.add(user.toString());
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Failed to search on the fly", Toast.LENGTH_SHORT).show();
                            }
                            mSearchResults.showProgress(false);
                        } catch (JSONException e) {
                            Log.e("TAG--------JSON--EX--", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("searchstring", searchText);
                return params;
            }
        };
        mQueue.add(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mSearchResults = (SearchResults) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCreatePostListener");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.menu_selected_user, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Log.e("TAG----D--", mUsers.get(info.position).getUid());

        switch (item.getItemId()) {
            case R.id.follow_button:
                mSearchResults.followUser(mUsers.get(info.position).getUid(), true);
                return true;

            case R.id.unfollow_button:
                mSearchResults.followUser(mUsers.get(info.position).getUid(), false);
                return true;

            case R.id.show_posts:
                mSearchResults.closeSearchView();
                mSearchResults.displayViewPostFragment(Constants.SEE_USER_POSTS, false, mUsers.get(info.position).getUid());
                return true;

            case R.id.cancel:
                mSearchResults.closeSearchView();
                mSearchResults.displayViewPostFragment(Constants.SEE_MY_PLUS_FOLLOWERS_POSTS, false, null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}