package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_camera).setOnClickListener(new BtnCamClickListener());
    }

    class BtnCamClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }
}
