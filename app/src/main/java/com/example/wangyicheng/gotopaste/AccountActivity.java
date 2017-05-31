package com.example.wangyicheng.gotopaste;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button logoutButton;

    Handler handler = new Handler() {
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
                new HttpPost(data.getBytes(), "http://162.105.175.115:8004/user/logout", handler, HttpPost.TYPE_LOGOUT);
            }
        });
    }
}
