package com.example.cropdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class LoginActivity extends AppCompatActivity {

    private String emailString;
    private String passwordString;
    private Button btnLogin;
    private Button btnRegister;
    private EditText emailText;
    private EditText passwordText;

    public static void activityStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.login_login_btn);
        btnLogin.setOnClickListener(new BtnLoginClickListener());
        btnRegister = (Button) findViewById(R.id.login_register_btn);
        btnRegister.setOnClickListener(new BtnRegisterClickListener());
        emailText = (EditText) findViewById(R.id.login_email_field);
        passwordText = (EditText) findViewById(R.id.login_password_field);
    }

    class BtnLoginClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            emailString = emailText.getText().toString();
            passwordString = passwordText.getText().toString();
            new DBCommunication().execute("login");
        }
    }

    class BtnRegisterClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            emailString = emailText.getText().toString();
            passwordString = passwordText.getText().toString();
            Log.d("db debug","before con");
            new DBCommunication().execute("register");
        }
    }

    class DBCommunication extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("db debug", "in conn");
            String type = strings[0];
            String url = "jdbc:mysql://47.96.232.152:3306/cropdoctor";
            String query = "";
            int res = 0;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = (Connection) DriverManager.getConnection(url, "root", "root");
                Statement statement = conn.createStatement();
                // 判断操作类型
                if(type.equals("login")) {
                    // 查询指令
                    res = statement.executeUpdate(query);
                    if(res > 0) {
                        return "login success";
                    }
                    else {
                        return "login failed";
                    }
                } else if(type.equals("register")) {
                    query = "insert into user values(";
                    query += "100"; // uid
                    query += ",'" + emailString + "'";
                    query += ",'test'"; // 用户名
                    query += ",123456";
                    query += ");";
                    res = statement.executeUpdate(query);
                }
                if(res > 0) {
                    return "register success";
                }
                else {
                    return "register failed";
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            switch (result) {
                case "login success":
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    break;
                case "login failed":
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    break;
                case "register success":
                    Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    break;
                case "register failed":
                    Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

}