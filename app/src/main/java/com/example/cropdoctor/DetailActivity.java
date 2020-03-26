package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DetailActivity extends AppCompatActivity {
    private String tagId;
    private String tagName;
    private String textData;
    private String link;
    private Drawable imgDrawable;

    class BtnLinkClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            startActivity(intent);
        }

    }

    public static void activityStart(Context context, String diseaseID, String diseaseName) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("diseaseID", diseaseID);
        intent.putExtra("diseaseName", diseaseName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        tagId = intent.getStringExtra("diseaseID");
        TextView diseaseName = (TextView) findViewById(R.id.detail_name);
        //ImageView imgView = (ImageView) findViewById(R.id.detail_pic);
        Button btnLink = (Button) findViewById(R.id.button_link);
        TextView textView = (TextView) findViewById(R.id.text_data);
        diseaseName.setText(intent.getStringExtra("diseaseName"));
        //getImageData();
        //imgView.setImageDrawable(imgDrawable);
        getTextData();
        textView.setText(textData);
        btnLink.setOnClickListener(new BtnLinkClickListener());
    }

    /*
    private void getImageData() {
        String fileName = "image/" + tagId + ".jpg";
        try {
            InputStream inputStream = getAssets().open(fileName);
            imgDrawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */

    private void getTextData() {
        String line = null;
        textData = "";
        String fileName = "text/" + tagId + ".txt";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            while ((line = br.readLine()) != null) {
                if (line.contains("http")) {
                    link = line;
                } else {
                    textData += line + "\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(DetailActivity.this, "数据异常", Toast.LENGTH_SHORT).show();
            finish();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    finish();
                }
            }
        }
    }
}
