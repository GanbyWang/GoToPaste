package com.example.wangyicheng.gotopaste;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public class AccountActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button logoutButton, newPwdButton;
    private ImageView newPwd;
    private LinearLayout modifyPwdBlock, newPwdBlock;

    Handler logoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HttpPost.POST_SUCC:
                    try {
                        // get the json object
                        JSONObject JSONInfo = new JSONObject(msg.obj.toString());

                        if (JSONInfo.getString("result").equals("Success!")) {
                            // reset the token
                            MainActivity.token = null;
                            // reset the priority
                            MainActivity.priority = 0;

                            // restart the main activity
                            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "未知错误", Toast.LENGTH_LONG).show();
                        }

                    } catch(JSONException e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;

                case HttpPost.POST_FAIL:
                    Toast.makeText(getApplicationContext(), "登出失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    Handler modifyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HttpPut.PUT_SUCC:
                    try {
                        // get the json object
                        JSONObject JSONInfo = new JSONObject(msg.obj.toString());

                        if (JSONInfo.getString("result").equals("Error: Wrong Old Password!")) {
                            Toast.makeText(getApplicationContext(), "原始密码错误", Toast.LENGTH_LONG).show();

                        } else if(JSONInfo.getString("result").equals("Success!")) {
                            Toast.makeText(getApplicationContext(), "修改密码成功", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    } catch(JSONException e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;

                case HttpPut.PUT_FAIL:
                    Toast.makeText(getApplicationContext(), "修改密码失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // get the tool bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // set the back icon
        toolbar.setNavigationIcon(R.drawable.back);
        // finish when press the back icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // get the button
        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = "{\"token\":\"" + MainActivity.token + "\"}";
                // send an HTTP request to log out
                new HttpPost(data.getBytes(), "http://162.105.175.115:8004/user/logout", logoutHandler, HttpPost.TYPE_LOGOUT);
            }
        });

        // get some views and hide them
        newPwdButton = (Button) findViewById(R.id.new_pwd_button);
        newPwdButton.setVisibility(View.GONE);
        newPwdBlock = (LinearLayout) findViewById(R.id.new_pwd_block);
        newPwdBlock.setVisibility(View.GONE);

        // get the new password button
        newPwd = (ImageView) findViewById(R.id.new_pwd);
        newPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide some views
                logoutButton.setVisibility(View.GONE);
                modifyPwdBlock = (LinearLayout) findViewById(R.id.modify_pwd_block);
                modifyPwdBlock.setVisibility(View.GONE);

                // re-show some views
                newPwdButton.setVisibility(View.VISIBLE);
                newPwdBlock.setVisibility(View.VISIBLE);
            }
        });

        // set listener on the reset password button
        newPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the contents
                EditText oldPassword = (EditText) findViewById(R.id.old_password);
                EditText newPassword = (EditText) findViewById(R.id.new_password);
                EditText confirmNewPwd = (EditText) findViewById(R.id.confirm_new_pwd);
                String old_pwd = oldPassword.getText().toString();
                String new_pwd = newPassword.getText().toString();
                String confirm_new_pwd = confirmNewPwd.getText().toString();

                // check the inputs
                if(old_pwd.equals("")) {
                    Toast.makeText(getApplicationContext(), "您未输入原密码", Toast.LENGTH_LONG).show();
                    return;
                }

                if(new_pwd.equals("")) {
                    Toast.makeText(getApplicationContext(), "您未输入新密码", Toast.LENGTH_LONG).show();
                    return;
                }

                if(confirm_new_pwd.equals("")) {
                    Toast.makeText(getApplicationContext(), "您未输入新密码确认", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!new_pwd.equals(confirm_new_pwd)) {
                    Toast.makeText(getApplicationContext(), "新密码和新密码确认不一致", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    // get the password which is encrypted by MD5
                    String oldPwdMD5 = MD5.getMD5(old_pwd);
                    String newPwdMD5 = MD5.getMD5(new_pwd);

                    String data = "{\"token\":\"" + MainActivity.token + "\",";
                    data += "\"old_password\":\"" + oldPwdMD5 + "\",";
                    data += "\"new_password\":\"" + newPwdMD5 +"\"}";

                    new HttpPut(data.getBytes(), "http://162.105.175.115:8004/user/password", modifyHandler, HttpPut.TYPE_NEW_PWD);

                } catch(NoSuchAlgorithmException e) {
                    Toast.makeText(getApplicationContext(), "加密算法缺失", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
