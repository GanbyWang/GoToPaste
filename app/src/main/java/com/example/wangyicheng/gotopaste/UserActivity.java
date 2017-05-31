package com.example.wangyicheng.gotopaste;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

// overwrite the listener in order to change the tabs
public class UserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton postButton;
    MyAdapter listAdapter = null;

    Handler postHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // if it's a POST request and it succeeded
                case HttpPost.POST_SUCC:
                    try {
                        // get the jason object
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());
                        String sharingCode = jsonInfo.getString("share_code");

                        // transfer the sharing code as a parameter
                        Bundle bundle = new Bundle();
                        bundle.putString("sharingCode", sharingCode);

                        Intent intent = new Intent(UserActivity.this, PostActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        // as the user might come back from posting
                        // here we don't finish the activity

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                // if the post request fails
                case HttpPost.POST_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    // this handler is used for query a sharing code
    Handler getHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // if it's a POST request and it succeeded
                case HttpPost.POST_SUCC:
                    try {
                        // get the jason object
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                // if the GET request fails
                case HttpPost.POST_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Find the tool bar and set title and sub-title
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // set the back icon
        toolbar.setNavigationIcon(R.drawable.back);
        // finish when press the back icon
        // back to main activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // show the menu (only 1 element) and set listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // jump to account activity
                Intent intent = new Intent(UserActivity.this, AccountActivity.class);
                startActivity(intent);
                // as the user might come back from posting
                // here we don't finish the activity
                return true;
            }
        });

        toolbar.inflateMenu(R.menu.main_toolbar_menu);

        // get the post button
        postButton = (FloatingActionButton) findViewById(R.id.post_button);
        // set the listener
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "{\"token\":\"" + MainActivity.token + "\"}";
                new HttpPost(data.getBytes(), "http://162.105.175.115:8004/message/new", postHandler, HttpPost.TYPE_NEW);
            }
        });

        new HttpGet("http://162.105.175.115:8004/message/all?token=" + MainActivity.token, getHandler, HttpGet.TYPE_LIST);
    }
}
