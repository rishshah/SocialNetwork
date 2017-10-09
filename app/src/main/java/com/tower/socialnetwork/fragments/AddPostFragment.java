package com.tower.socialnetwork.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tower.socialnetwork.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPostFragment extends Fragment implements PermissionCallback {
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int IMAGE_CHOOSE_ACTIVITY = 2;
    private EditText mPostText;
    private ImageView mImageView;
    private Button mImageButton;
    private String mImageString = null;
    private OnCreatePostListener mOnCreatePostListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_post_fragment, container, false);
        mImageView = view.findViewById(R.id.imageView);
        mImageButton = view.findViewById(R.id.add_post_button);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnCreatePostListener.createPost(mPostText.getText().toString(), mImageString);
            }
        });

        view.findViewById(R.id.add_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handlePermissions()) {
                    chooseImage();
                }
            }
        });

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

    public interface OnCreatePostListener {
        void createPost(String postText, String imageString);
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_CHOOSE_ACTIVITY);
    }

    private boolean handlePermissions() {
        boolean readStatus = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean writeStatus = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequests = new ArrayList<>();
        if (!readStatus)
            permissionRequests.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!writeStatus)
            permissionRequests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!readStatus || !writeStatus)
            ActivityCompat.requestPermissions(getActivity(), permissionRequests.toArray(new String[0]), MY_PERMISSIONS_REQUEST);

        return readStatus && writeStatus;
    }

    public void permissionGrantedCallback(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                } else {
                    mImageButton.setEnabled(false);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CHOOSE_ACTIVITY && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                mImageView.setImageBitmap(bitmap);
                mImageView.setVisibility(View.VISIBLE);
                mImageString = getStringImage(bitmap);
            } catch (IOException e) {
                Log.e("TAG---IO--EX", e.toString());
            }
        }
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
