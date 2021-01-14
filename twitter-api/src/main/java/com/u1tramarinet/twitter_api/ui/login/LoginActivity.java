package com.u1tramarinet.twitter_api.ui.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.u1tramarinet.twitter_api.R;

public class LoginActivity extends AppCompatActivity {
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_AUTH_TOKEN = "user_name";
    public static final String KEY_AUTH_SECRET = "user_name";
    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginButton = findViewById(R.id.loginButton);
        initializeButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeButton() {
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Intent intent = new Intent();
                intent.putExtra(KEY_USER_ID, result.data.getUserId());
                intent.putExtra(KEY_USER_NAME, result.data.getUserName());
                intent.putExtra(KEY_AUTH_TOKEN, result.data.getAuthToken().token);
                intent.putExtra(KEY_AUTH_SECRET, result.data.getAuthToken().secret);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(LoginActivity.this, "Failed to login...", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}