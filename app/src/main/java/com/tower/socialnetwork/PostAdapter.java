package com.tower.socialnetwork;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PostAdapter extends ArrayAdapter<Post> {

    private ArrayList<Post> objects;
    private ArrayList<Boolean> moreCommentPressed;

    public PostAdapter(Context context, int resource, ArrayList<Post> data) {
        super(context, resource, data);
        this.objects = data;
        this.moreCommentPressed = new ArrayList<>(Arrays.asList(new Boolean[data.size()]));
        Collections.fill(this.moreCommentPressed, Boolean.FALSE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_post, null);
        }

		Post i = objects.get(position);
        if (i != null) {

            TextView post = v.findViewById(R.id.post);
            TextView time = v.findViewById(R.id.post_time);
            TextView postWriter = v.findViewById(R.id.post_maker);

            ImageView image = v.findViewById(R.id.imageView);

            if (post != null) {
                post.setText(i.getPostText());
            }
            if (time != null) {
                time.setText(i.getPostTime());
            }
            if (postWriter != null) {
                postWriter.setText(i.getPoster());
            }
            if (image != null) {
                Bitmap bitmapImage = i.getImage();
                image.setVisibility(View.GONE);
                if (bitmapImage != null) {
                    image.setImageBitmap(bitmapImage);
                    image.setVisibility(View.VISIBLE);
                }
            }

            Button commentButton = v.findViewById(R.id.add_comment_button);
            Button moreCommentButton = v.findViewById(R.id.more_comment_button);

            final Integer postid = i.getPostId();
            final View vCopy = v;
            if(i.getCommentList().size() <= 3){
                moreCommentButton.setVisibility(View.GONE);
            } else{
                moreCommentButton.setVisibility(View.VISIBLE);
            }
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vi) {
                    final String commentString = ((EditText) vCopy.findViewById(R.id.new_comment_text)).getText().toString();
                    if (TextUtils.isEmpty(commentString)) {
                        ((EditText) vCopy.findViewById(R.id.new_comment_text)).setError("Error");
                        return;
                    }
                    addComment(position, commentString, postid);
                    moreCommentPressed.set(position, true);
                    ((EditText) vCopy.findViewById(R.id.new_comment_text)).setText("");
                }
            });
            moreCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vi) {
                    moreCommentPressed.set(position, !moreCommentPressed.get(position));
                    notifyDataSetChanged();
                }
            });

            TableLayout replyContainer = v.findViewById(R.id.table_show);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            replyContainer.removeAllViews();

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

                if (commText != null) {
                    commText.setText(comment.getCommentText());
                }
                if (commWriter != null) {
                    commWriter.setText(comment.getCommenter());
                }
                if (commTime != null) {
                    commTime.setText(comment.getCommentTime());
                }

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
        return v;
    }

    private void addComment(final int position, final String commentString, final Integer postid) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String loginUrl = Constants.SERVER_URL + Constants.ADD_COMMNENT;
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
                                notifyDataSetChanged();
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
}