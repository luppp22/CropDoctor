package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    private Uri imageUri;
    private Result resultData;
    private ResultAdapter resultAdapter;

    private static class Result {
        private String id;
        private String project;
        private String iteration;
        private String created;
        private List<Prediction> predictions;

        public static class Prediction {
            private double probability;
            private String tagId;
            private String tagName;

            public String getTagName() {
                return tagName;
            }

            public double getProbability() {
                return probability;
            }
        }

        public Result() {
            predictions = new ArrayList<>();
        }
    }

    // RecyclerView的Adapter
    public static class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
        private List<Result.Prediction> predictionList;
        private OnItemClickListener onItemClickListener;

        private static class ViewHolder extends RecyclerView.ViewHolder{
            private TextView textName;
            private TextView textProbability;

            public ViewHolder(View view) {
                super(view);
                textName = (TextView) view.findViewById(R.id.result_item_name);
                textProbability = (TextView) view.findViewById(R.id.result_item_probability);
            }
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public ResultAdapter(List<Result.Prediction> _predictionList) {
            predictionList = _predictionList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.result_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Result.Prediction prediction = predictionList.get(position);
            holder.textName.setText(prediction.getTagName());
            String strProbability = "概率：" + String.valueOf(prediction.getProbability());
            holder.textProbability.setText(strProbability);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return predictionList.size();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            onItemClickListener = listener;
        }
    }

    public static void activityStart(Context context, String uriString) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra("uriString", uriString);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra("uriString"));
        // 先创建一个空的数据源，供Adapter使用
        resultData = new Result();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.result_list);
        resultAdapter = new ResultAdapter(resultData.predictions);
        resultAdapter.setOnItemClickListener(new ResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 这里写点击事件
                Toast.makeText(
                        ResultActivity.this,
                        "clicked" + position,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        recyclerView.setAdapter(resultAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ResultActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                ResultActivity.this, DividerItemDecoration.VERTICAL));
        getResult();
    }

    // 发送request，得到response
    private void getResult() {
        String url = getString(R.string.url);
        String key = getString(R.string.key);
        File image = new File(imageUri.getPath());

        OkHttpClient client = new OkHttpClient();
        RequestBody reqBody = RequestBody.create(image, MediaType.parse("application/octet-stream"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Prediction-Key", key)
                .post(reqBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ResultActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                convertToClass(response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    // 将json数据转化为Result的实例，更新数据源
    private void convertToClass(String strResponse) {
        Gson gson = new Gson();
        // 不直接写成resultData = gson.fromJson的原因是会导致resultData指向的地址改变
        Result resultTemp = gson.fromJson(strResponse, new TypeToken<Result>() {}.getType());
        resultData.predictions.addAll(resultTemp.predictions);
        Iterator<Result.Prediction> iterator = resultData.predictions.iterator(); // 删除概率小于0.05的项
        while(iterator.hasNext()) {
            Result.Prediction prediction = iterator.next();
            if(prediction.probability < 0.05)
                iterator.remove();
        }
    }
}
