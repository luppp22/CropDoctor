package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO      = 1;
    public static final int CHOOSE_PHOTO    = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d("MainActivity", "MainActivity begin!");
        Button btnCam = (Button) findViewById(R.id.button_camera);
        btnCam.setOnClickListener(new BtnCamClickListener());
        Button btnPic = (Button) findViewById(R.id.button_picture);
        btnPic.setOnClickListener(new BtnPicClickListener());
        Button btnAbt = (Button) findViewById(R.id.button_about);
        btnAbt.setOnClickListener(new BtnAbtClickListener());
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

    // 图库按钮监听器
    class BtnPicClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, CHOOSE_PHOTO);
        }
    }

    class BtnAbtClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AboutActivity.activityStart(MainActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    PreviewActivity.activityStart(MainActivity.this, imageUri.toString());
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    PreviewActivity.activityStart(MainActivity.this, imageUri.toString());
                }
                break;
            default:
                break;
        }
    }
}