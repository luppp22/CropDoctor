package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class PreviewActivity extends AppCompatActivity {

    private String uriString;

    public static void activityStart(Context context, String uriString) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("uriString", uriString);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("PreviewActivity", "PreviewActivity begin");
        setContentView(R.layout.activity_preview);
        ImageView imagePreview = findViewById(R.id.image_preview);
        Button btnUpload = findViewById(R.id.button_confirm_image);
        btnUpload.setOnClickListener(new btnUploadListener());
        Intent intent = getIntent();
        if (intent != null) {
            uriString = intent.getStringExtra("uriString");
            Uri imageUri = Uri.parse(uriString);
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imagePreview.setImageBitmap(bitmap);
        }

    }

    class btnUploadListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ResultActivity.activityStart(PreviewActivity.this, uriString);
        }
    }
}
