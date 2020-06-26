package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class UserActivity extends AppCompatActivity {

    public static void activityStart(Context context, String uid) {
        if(uid.equals("-1")) {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT);
            return;
        }
        Intent intent = new Intent(context, UserActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }
}