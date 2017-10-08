package com.tower.socialnetwork;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tower.socialnetwork.utilities.Comment;
import com.tower.socialnetwork.utilities.Constants;
import com.tower.socialnetwork.utilities.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bharat on 7/10/17.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    private ArrayList<Post> objects;
    private ArrayList<Boolean> moreCommentPressed;

    // View lookup cache
    private static class ViewHolder {
        TextView txtPost;
        TextView txtTime;
        TextView txtVersion;
//        ImageView info;
    }

    public PostAdapter(Context context, int resource, ArrayList<Post> data) {
        super(context, resource, data);
        this.objects = data;
        this.moreCommentPressed = new ArrayList<Boolean>(Arrays.asList(new Boolean[data.size()]));
        Collections.fill(this.moreCommentPressed, Boolean.FALSE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_post, null);
        }

		/*
         * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        Post i = objects.get(position);

        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView post = v.findViewById(R.id.post);
            TextView time = v.findViewById(R.id.post_time);
            TextView postWriter = v.findViewById(R.id.post_maker);

            Button commentButton = v.findViewById(R.id.add_comment_button);
            Button moreCommentButton = v.findViewById(R.id.more_comment_button);
            final Integer postid = i.getPostId();

//            ListView comment_list = (ListView) v.findViewById(R.id.comment_list);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (post != null) {
                post.setText(i.getPostText());
            }
            if (time != null) {
                time.setText(i.getPostTime());
            }
            if (postWriter != null) {
                postWriter.setText(i.getUserId());
            }

            final View vCopy = v;
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vi) {
                    final String commentString = ((EditText) vCopy.findViewById(R.id.new_comment_text)).getText().toString();
                    addComment(position, commentString, postid);
                    moreCommentPressed.set(position, true);
                    ((EditText) vCopy.findViewById(R.id.new_comment_text)).setText("");
                }
            });

            moreCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vi) {
                    moreCommentPressed.set(position, !moreCommentPressed.get(position));
                    updateView();
                }
            });


            TableLayout replyContainer = (TableLayout) v.findViewById(R.id.table_show);
            replyContainer.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            i.print();
            int commentToDisplay = 0;
            for (Comment comment : i.getCommentList()) {
                if (commentToDisplay == 3 && !moreCommentPressed.get(position)) {
                    break;
                }
                commentToDisplay++;
                View comments = inflater.inflate(R.layout.item_comment, null);
                TextView commText = comments.findViewById(R.id.comment);
                TextView commWriter = comments.findViewById(R.id.comment_writer);
                TextView commTime = comments.findViewById(R.id.comment_time);

                // check to see if each individual textview is null.
                // if not, assign some text!
                if (commText != null) {
                    commText.setText(comment.getCommentText());
                }
                if (commWriter != null) {
                    commWriter.setText(comment.getCommenter());
                }

                if (commTime != null) {
                    commTime.setText(comment.getCommentTime());
                }


//for changing your tablelayout parameters
                TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

                int leftMargin = 3;
                int topMargin = 2;
                int rightMargin = 3;
                int bottomMargin = 2;
                tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                comments.setLayoutParams(tableRowParams);
                TableRow tr = (TableRow) comments;
                replyContainer.addView(tr);
            }

        }

        // the view must be returned to our activity
        return v;

    }

    private void addComment(final int position, final String commentString, final Integer postid) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String loginUrl = Constants.SERVER_URL + Constants.ADD_COMMNENT;
        // Request a json response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                Toast.makeText(getContext().getApplicationContext(), "Comment Created", Toast.LENGTH_SHORT).show();
                                Post p = objects.get(position);
                                p.addComment(jsonResponse.getJSONArray("data"));
                                updateView();
                            } else {
                                Toast.makeText(getContext().getApplicationContext(), "Failed to create Comment", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG--------JSON--EX--", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext().getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("content", commentString);
                params.put("postid", postid.toString());
                return params;
            }

        };
        queue.add(stringRequest);
    }

    private void updateView() {
        this.notifyDataSetChanged();
    }

}