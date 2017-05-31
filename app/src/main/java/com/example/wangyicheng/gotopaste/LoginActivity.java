package com.example.wangyicheng.gotopaste;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by yaoyang on 2017/5/6.
 */

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button loginButton, regButton;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HttpPost.POST_SUCC:
                    try {
                        // get the json object
                        JSONObject JSONInfo = new JSONObject(msg.obj.toString());

                        if (JSONInfo.getString("result").equals("Error: Username Does Not Exist!")) {
                            Toast.makeText(getApplicationContext(), "用户名不存在", Toast.LENGTH_LONG).show();

                        } else if (JSONInfo.getString("result").equals("Error: Wrong Password!")) {
                            Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_LONG).show();

                        } else if (JSONInfo.getString("result").equals("Success!")) {
                            // store the token
                            MainActivity.token = JSONInfo.getString("token");
                            // set the priority
                            MainActivity.priority = 1;

                            // start the new activity
                            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } catch(JSONException e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;

                case HttpPost.POST_FAIL:
                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.myreturn);

        // finish when press the back icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // get the buttons
        loginButton = (Button) findViewById(R.id.login_button);
        regButton = (Button) findViewById(R.id.register_button);

        // set listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get user name and password view
                EditText userName = (EditText) findViewById(R.id.user_name);
                EditText password = (EditText) findViewById(R.id.password);

                // check the inputs
                if(userName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "未填写用户名", Toast.LENGTH_LONG).show();
                    return;
                }

                if(password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "未填写密码", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    // get the password which is encrypted by MD5
                    String pwdMD5 = MD5.getMD5(password.getText().toString());

                    String data = "{\"username\":\"" + userName.getText().toString() + "\",";
                    data += "\"password\":\"" + pwdMD5 + "\"}";

                    // send HTTP POST request to log in
                    new HttpPost(data.getBytes(), "http://162.105.175.115:8004/user/login", handler, HttpPost.TYPE_LOGIN);

                } catch(NoSuchAlgorithmException e) {
                    Toast.makeText(getApplicationContext(), "加密算法缺失", Toast.LENGTH_LONG).show();
                }
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                // here we don't need to finish login activity
            }
        });
    }
}
