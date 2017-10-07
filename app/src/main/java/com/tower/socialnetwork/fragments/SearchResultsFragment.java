package com.tower.socialnetwork.fragments;

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
import com.tower.socialnetwork.HomeActivity;
import com.tower.socialnetwork.R;
import com.tower.socialnetwork.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsFragment extends Fragment implements HomeActivity.DataToSearchFragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_fragment, container, false);
        return view;
    }

    @Override
    public void sendData(final String searchText) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String loginUrl = Constants.SERVER_URL + Constants.SEARCH;
        Log.e("TAG", loginUrl);
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            List<String> values = new ArrayList<>();
                            if (jsonResponse.getBoolean("status")) {
                                JSONArray results = jsonResponse.getJSONArray("data");
                                results = (JSONArray) results.get(0);
                                if (results.length() == 0) {
                                    values.add("User Not Found");
                                } else {
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject res = (JSONObject) results.get(i);
                                        values.add(res.getString("name"));
                                    }
                                }
                                addContentToList(values);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Failed to search on the fly", Toast.LENGTH_SHORT).show();
                            }
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
        queue.add(stringRequest);
    }

    private void addContentToList(List<String> values) {
        ListView listView = view.findViewById(R.id.list);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_selectable_list_item, values);
        listView.setAdapter(adapter);

    }
}
