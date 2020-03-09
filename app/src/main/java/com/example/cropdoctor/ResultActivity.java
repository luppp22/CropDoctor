package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    private Uri imageUri;
    private TextView responseText;

    public static void activityStart(Context context, String uriString) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra("uriString", uriString);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        responseText = findViewById(R.id.response_text);
        Intent intent = getIntent();
        if (intent != null) {
            imageUri = Uri.parse(intent.getStringExtra("uriString"));
        }
        sendRequest();
    }

    private void sendRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = getString(R.string.url);
                String key = getString(R.string.key);
                File image = new File(imageUri.getPath());

                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody reqBody = RequestBody.create(image, MediaType.parse("application/octet-stream"));
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Prediction-Key", key)
                            .post(reqBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String resData = response.body().string();
                    showResponse(resData);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("ResultActivity", "ERROR !!!");
                }
            }
        }).start();
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });
    }
}
