package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d("MainActivity", "MainActivity begin!");
        Button btnCam = (Button) findViewById(R.id.button_camera);
        btnCam.setOnClickListener(new BtnCamClickListener());
    }

    // 相机按钮监听器
    class BtnCamClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
            try {
                if(outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(MainActivity.this,
                        "com.example.cropdoctor.fileprovider", outputImage);
            } else {
                imageUri = Uri.fromFile(outputImage);
            }
            //隐式intent调用系统相机
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) { // 拍照成功，转到preview活动
                    PreviewActivity.activityStart(MainActivity.this, imageUri.toString());
                }
                break;
            default:
                break;
        }
    }
}
