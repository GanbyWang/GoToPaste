package com.example.wangyicheng.gotopaste;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;

    public static int priority = 0; // priority is used to differ normal user and log-in user
                                    // 0 for normal user and 1 for log-in user
    public static String token = null;
    // token is stored

    private LinearLayout postNew;
    private String sharingCode;
    private Button queryButton;
    private String querySharingCode;

    private MsgInfo msgInfo;

    // this handler is used for query a sharing code
    Handler queryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // if it's a POST request and it succeeded
                case HttpGet.GET_SUCC:
                    try {
                        // get the jason object
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());

                        // if the sharing code is illegal or empty
                        if(!jsonObject.getString("result").equals("200")) {
                            Toast.makeText(getApplicationContext(), "该共享码不存在", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // jump to display activity
                        // leave the resolution to the next activity
                        Bundle bundle = new Bundle();
                        bundle.putString("msgInfo", msg.obj.toString());
                        bundle.putString("sharingCode", querySharingCode);
                        bundle.putInt("priority", 0);

                        Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        // as the user might come back from posting
                        // here we don't finish the activity

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                // if the GET request fails
                case HttpGet.GET_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    // this handler is used for creating a new shared message
    Handler newHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // if it's a POST request and it succeeded
                case HttpPost.POST_SUCC:
                    try {
                        // get the jason object
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());
                        sharingCode = jsonInfo.getString("share_code");

                        // transfer the sharing code as a parameter
                        Bundle bundle = new Bundle();
                        bundle.putString("sharingCode", sharingCode);
                        bundle.putInt("priority", 0);

                        Intent intent = new Intent(MainActivity.this, PostActivity.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the tool bar and set title and sub-title
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setLogo(R.drawable.navigation_icon);
        toolbar.setTitle("gotoPaste");
        toolbar.setSubtitle("即时分享信息和文件");

        // show the menu (only 1 element) and set listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(token == null && priority == 0) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                } else if (token != null && priority == 1) {
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        toolbar.inflateMenu(R.menu.main_toolbar_menu);

        // find the linearlayout
        postNew = (LinearLayout) findViewById(R.id.add_file);
        postNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send a post request
                // no token (send as empty) and set type as TYPE_NEW
                new HttpPost("{}".getBytes(), "http://162.105.175.115:8004/message/new", newHandler, HttpPost.TYPE_NEW);
            }
        });

        // find the query button
        queryButton = (Button) findViewById(R.id.query_button);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView shareNumber = (TextView) findViewById(R.id.share_number);
                querySharingCode = shareNumber.getText().toString();

                // the user haven't put in sharing code
                if(querySharingCode.equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入共享码", Toast.LENGTH_LONG).show();
                    return;
                }

                // send a get request
                // no token (send as empty) and set type as TYPE_GETMSG
                new HttpGet("http://162.105.175.115:8004/message/" + querySharingCode, queryHandler, HttpGet.TYPE_GETMSG);
            }
        });
    }
}