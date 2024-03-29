package com.tower.socialnetwork;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tower.socialnetwork.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mProgressView = findViewById(R.id.login_progress);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        // Reset errors.
        mUsername.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUsername.getText().toString();
        final String password = mPassword.getText().toString();

        boolean cancel = false;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_password_required));
            cancel = true;
        }
        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_username_required));
            cancel = true;
        }

        if (cancel)
            return;
        showProgress(true);

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        RequestQueue queue = Volley.newRequestQueue(this);
        String loginUrl = Constants.SERVER_URL + "Login";
        Log.e("TAG", loginUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG--------D--", response);
                        showProgress(false);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                //start new activity
                                navigateToHome();
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("TAG--------JSON EX--", e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG--------VOLLEY EX--", error.toString());
                        showProgress(false);
                        Toast.makeText(getApplicationContext(), "Didn't work", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", username);
                params.put("password", password);
                Log.e("TAG--------D--", username + " ," + password);
                return params;
            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getApplicationContext(), "Successfully Logged Out", Toast.LENGTH_SHORT).show();
    }

    private void showProgress(boolean is_visible) {
        if (is_visible) {
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            mProgressView.setVisibility(View.GONE);
        }
    }
}