package com.tower.socialnetwork.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tower.socialnetwork.R;

public class AddPostFragment extends Fragment implements View.OnClickListener{
    private View view;
    private EditText mPostText;
    private OnCreatePostListener mOnCreatePostListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_post_fragment,container,false);
        view.findViewById(R.id.add_post_button).setOnClickListener(this);
        mPostText= (EditText) view.findViewById(R.id.post_text);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnCreatePostListener = (OnCreatePostListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCreatePostListener");
        }
    }

    @Override
    public void onClick(View v) {
        mOnCreatePostListener.createPost(mPostText.getText().toString());
    }

    public interface OnCreatePostListener{
        void createPost(String postText);
    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.create_post_fragment);
//        final EditText mPostText= (EditText) findViewById(R.id.post_text);
//        setProgressBar(findViewById(R.id.login_progress));
//
//        getSupportActionBar().setTitle("New Post");
//
//        findViewById(R.id.add_post_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createPost(mPostText.getText().toString());
//                Intent intent = new Intent().putExtra("action", "PostCreated");
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
//    }
//
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent().putExtra("action", "Back");
//        setResult(RESULT_OK, intent);
//        finish();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if(resultCode == RESULT_OK) {
//                if(data.getStringExtra("action").equals("PostCreated")) {
//                    Toast.makeText(getApplicationContext(), "Post Created", Toast.LENGTH_SHORT).show();
//                    onBackPressed();
//                }
//                else if(data.getStringExtra("action").equals("Back")) {
//                    onBackPressed();
//                }
//                else if(data.getStringExtra("action").equals("SeeMyPosts")) {
//                    Intent intent = new Intent().putExtra("action", "SeeMyPosts");
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            }
//        }
//    }
}
