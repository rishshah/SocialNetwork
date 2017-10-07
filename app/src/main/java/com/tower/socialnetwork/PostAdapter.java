package com.tower.socialnetwork;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tower.socialnetwork.utilities.Comment;
import com.tower.socialnetwork.utilities.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bharat on 7/10/17.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    private ArrayList<Post> objects;

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
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_postview, null);
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

            TextView post = (TextView) v.findViewById(R.id.post);
            TextView time = (TextView) v.findViewById(R.id.time);
//            ListView comment_list = (ListView) v.findViewById(R.id.comment_list);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (post != null) {
                post.setText(i.getPostText());
            }
            if (time != null) {
                time.setText(i.getPostTime());
            }


            TableLayout replyContainer = (TableLayout) v.findViewById(R.id.table_show);
            replyContainer.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            i.print();

            for (Comment comment : i.getCommentList()) {
                View comments = inflater.inflate(R.layout.activity_commentview, null);
                TextView commText = (TextView) comments.findViewById(R.id.comment);
                TextView commWriter = (TextView) comments.findViewById(R.id.comment_writer);
                TextView commTime = (TextView) comments.findViewById(R.id.comment_time);

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

/*
            if(comment_list!=null){

                LinearLayout list = (LinearLayout) v.findViewById(R.id.table_show);
//                list.removeAllViews();

                for (Comment comment : i.getCommentList()) {
//                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View line = inflater.inflate(R.layout.activity_commentview, null);



                    TextView commText = (TextView) v.findViewById(R.id.comment);
                    TextView commWriter = (TextView) v.findViewById(R.id.comment_writer);
                    TextView commTime = (TextView) v.findViewById(R.id.comment_time);

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

//                    list.addView(new LinearLayout(this));

                }

            }
*/
        }

        // the view must be returned to our activity
        return v;

    }


}