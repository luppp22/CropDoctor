package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class PreviewActivity extends AppCompatActivity {

    public static void activityStart(Context context, String uriString) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("uriString", uriString);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PreviewActivity", "PreviewActivity begin");
        setContentView(R.layout.activity_preview);
        ImageView imagePreview = (ImageView) findViewById(R.id.image_preview);
        Intent fromMain = getIntent();
        if (fromMain != null) {
            Uri imageUri = Uri.parse(fromMain.getStringExtra("uriString"));
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                Log.d("PreviewActivity", "ERROR !!!");
                e.printStackTrace();
            }
            imagePreview.setImageBitmap(bitmap);
        }

    }
}
