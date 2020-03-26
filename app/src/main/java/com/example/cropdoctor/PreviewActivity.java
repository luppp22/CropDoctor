package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class PreviewActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private String uriString;
    private final int C_WIDTH = 512;

    public static void activityStart(Context context, String uriString) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("uriString", uriString);
        context.startActivity(intent);
    }

    class btnUploadListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            double ratio = C_WIDTH / (double)bitmap.getWidth();
            int cHeight = (int)(bitmap.getHeight() * ratio);
            File cImage = new File(getExternalCacheDir(), "compressed_image.png");

            try {
                if(cImage.exists()) {
                    cImage.delete();
                }
                cImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap compressedImage = Bitmap.createBitmap(
                    512, cHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(compressedImage);
            Rect rect = new Rect(0, 0, C_WIDTH, cHeight);
            canvas.drawBitmap(bitmap, null, rect, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);

            try {
                FileOutputStream fos = new FileOutputStream(cImage);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri cUri = Uri.fromFile(cImage);

            ResultActivity.activityStart(PreviewActivity.this, cUri.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ImageView imagePreview = findViewById(R.id.image_preview);
        Button btnUpload = findViewById(R.id.button_confirm_image);
        btnUpload.setOnClickListener(new btnUploadListener());
        Intent intent = getIntent();
        if (intent != null) {
            uriString = intent.getStringExtra("uriString");
            Uri imageUri = Uri.parse(uriString);
            bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imagePreview.setImageBitmap(bitmap);
        }

    }

    private String getImagePath(Uri uri) {
        String path = null;
        Cursor cursor = getContentResolver().query(
                uri, null, null, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
