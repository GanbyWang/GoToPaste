package com.example.wangyicheng.gotopaste;

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

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button regButton;
    private EditText userName, email, password, confirmPwd;
    private String user_name, e_mail, pass_word, confirm_pwd, pwdMD5;

    // this handler is used to cope with registering
    Handler registerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HttpPost.POST_SUCC:
                    try {
                        // get the json object
                        JSONObject JSONInfo = new JSONObject(msg.obj.toString());

                        if (JSONInfo.getString("result").equals("Error: Username Has Existed!")) {
                            Toast.makeText(getApplicationContext(), "用户名已存在", Toast.LENGTH_LONG).show();

                        } else if (JSONInfo.getString("result").equals("Error: Email Address Has Existed!")) {
                            Toast.makeText(getApplicationContext(), "邮箱已注册", Toast.LENGTH_LONG).show();

                        } else if (JSONInfo.getString("result").equals("Success!")) {
                            // register successfully
                            // send another HTTP request to log in
                            String data = "{\"username\":\"" + user_name + "\",";
                            data += "\"password\":\"" + pwdMD5 + "\"}";

                            new HttpPost(data.getBytes(), "http://162.105.175.115:8004/user/login", loginHandler, HttpPost.TYPE_LOGIN);
                        }

                    } catch(JSONException e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;

                case HttpPost.POST_FAIL:
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    // this handler is used to cope with log in
    Handler loginHandler = new Handler() {
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
                            Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
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
        setContentView(R.layout.activity_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.myreturn);

        // return when press the back icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // get the button
        regButton = (Button) findViewById(R.id.register_button);
        // set listener
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the texts
                userName = (EditText) findViewById(R.id.user_name);
                email = (EditText) findViewById(R.id.email);
                password = (EditText) findViewById(R.id.password);
                confirmPwd = (EditText) findViewById(R.id. confirm_password);

                // transfer to strings
                user_name = userName.getText().toString();
                e_mail = email.getText().toString();
                pass_word = password.getText().toString();
                confirm_pwd = confirmPwd.getText().toString();

                // check the inputs
                if(user_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "未填写用户名", Toast.LENGTH_LONG).show();
                    return;
                }

                if(e_mail.equals("")) {
                    Toast.makeText(getApplicationContext(), "未填写邮箱", Toast.LENGTH_LONG).show();
                    return;
                }

                if(pass_word.equals("")) {
                    Toast.makeText(getApplicationContext(), "未填写密码", Toast.LENGTH_LONG).show();
                    return;
                }

                if(confirm_pwd.equals("")) {
                    Toast.makeText(getApplicationContext(), "未填写确认密码", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!pass_word.equals(confirm_pwd)) {
                    Toast.makeText(getApplicationContext(), "密码与确认密码不一致", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    // get the password which is encrypted by MD5
                    pwdMD5 = MD5.getMD5(password.getText().toString());

                    // store the arguments
                    String data = "{\"username\":\"" + user_name + "\",";
                    data += "\"password\":\"" + pwdMD5 + "\",";
                    data += "\"email_address\":\"" + e_mail + "\"}";

                    // send HTTP POST request
                    new HttpPost(data.getBytes(), "http://162.105.175.115:8004/user/signup", registerHandler, HttpPost.TYPE_SIGNUP);

                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(getApplicationContext(), "加密算法缺失", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
