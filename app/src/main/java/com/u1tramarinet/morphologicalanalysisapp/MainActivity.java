package com.u1tramarinet.morphologicalanalysisapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.u1tramarinet.twitter_api.TwitterApi;
import com.u1tramarinet.twitter_api.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private TwitterApi twitterApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twitterApi = TwitterApi.getInstance();
        if (twitterApi.needLogin()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Success login!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}