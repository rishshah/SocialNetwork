package com.tower.socialnetwork.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.tower.socialnetwork.R;

public class AddPostFragment extends Fragment implements View.OnClickListener {
    private EditText mPostText;
    private OnCreatePostListener mOnCreatePostListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_post_fragment, container, false);
        view.findViewById(R.id.add_post_button).setOnClickListener(this);
        mPostText = view.findViewById(R.id.post_text);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnCreatePostListener = (OnCreatePostListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCreatePostListener");
        }
    }

    @Override
    public void onClick(View v) {
        mOnCreatePostListener.createPost(mPostText.getText().toString());
    }

    public interface OnCreatePostListener {
        void createPost(String postText);
    }
}
