package com.example.wangyicheng.gotopaste;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// overwrite the listener in order to change the tabs
public class UserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton postButton;
    MyAdapter listAdapter = null;
    private String[] abstracts, msgIds;
    private int msgNum;
    private ListView listView;
    private ImageButton seekButton;

    public Handler postHandler = new Handler() {
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
                        bundle.putInt("priority", 1);

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
    public Handler getHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // if it's a GET request and it succeeded
                case HttpGet.GET_SUCC:
                    try {
                        // get the json object
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());

                        String result = jsonObject.getString("result");
                        Log.i("result", result);

                        // get the message array
                        JSONArray jsonArray = jsonObject.getJSONArray("message_list");

                        // get the number of messages
                        msgNum = jsonArray.length();

                        Log.i("msgNum", "" + msgNum);

                        // allocate the list
                        msgIds = new String[msgNum];
                        abstracts = new String[msgNum];
                        for(int i = 0; i < msgNum; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            msgIds[i] = new String();
                            abstracts[i] = new String();
                            // get the info
                            msgIds[i] = object.getString("msgid");
                            abstracts[i] = object.getString("abstract");

                            if(abstracts[i] == null)
                                abstracts[i] = "null";

                            Log.i("msgid", msgIds[i]);
                            Log.i("abstract", abstracts[i]);

                            if(abstracts[i].length() >= 25) {
                                abstracts[i] = abstracts[i].substring(0, 25);
                            }
                        }

                        // get the list view
                        listView = (ListView) findViewById(R.id.msg_list);

                        // allocate the adapter
                        if(listAdapter == null) {
                            listAdapter = new MyAdapter(UserActivity.this);
                        } else {
                            listAdapter.notifyDataSetChanged();
                        }

                        // get the data
                        listAdapter.myData = getData();

                        listView.setAdapter(listAdapter);

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

        // get the seek button
        seekButton = (ImageButton) findViewById(R.id.seek_button);
        // set the listener
        seekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the content of searching
                EditText seekContent = (EditText) findViewById(R.id.find_content);
                String seek_content = seekContent.getText().toString();

                // check the input
                if(seek_content.equals("")) {
                    Toast.makeText(getApplicationContext(), "检索内容不可为空", Toast.LENGTH_LONG).show();
                    new HttpGet("http://162.105.175.115:8004/message/all?token=" + MainActivity.token, getHandler, HttpGet.TYPE_LIST);
                    return;
                }

                // update the data
                listAdapter.updateDataBySearch(seek_content);
            }
        });

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

        // send the GET request to get the list
        Log.i("token", MainActivity.token);
        new HttpGet("http://162.105.175.115:8004/message/all?token=" + MainActivity.token, getHandler, HttpGet.TYPE_LIST);
    }

    // when the activity resumes
    // update the list
    @Override
    protected void onResume() {
        super.onResume();
        new HttpGet("http://162.105.175.115:8004/message/all?token=" + MainActivity.token, getHandler, HttpGet.TYPE_LIST);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i = 0; i < msgNum; i++) {
            map = new HashMap<String, Object>();
            map.put("abstract", abstracts[i]);
            map.put("msgid", msgIds[i]);

            list.add(map);
        }
        return list;
    }
}
