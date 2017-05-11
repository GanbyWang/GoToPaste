package com.example.wangyicheng.gotopaste;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DisplayActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MsgInfo msgInfo;
    private EditText msgDisplay;
    private TextView shareCode;
    private TextView fileName;
    private LinearLayout saveLayout;
    private LinearLayout fileLayout;
    private TimeThread updateThread;
    private static final int msgKey1 = 1;
    private String result;
    private String fileURL;

    private TextView addTenMin;
    private TextView addOneHr;
    private TextView deleteFile;
    private TextView downloadFile;

    private int timeLeft;
    private TextView timeText;
    private String timeString;

    // this handler is used to prolong 10 min
    Handler handler_ten_minutes = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case HttpPut.PUT_SUCC:
                    try {
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());
                        result = jsonInfo.getString("result");

                        // update the left time
                        timeLeft += 600;
                        msgInfo.setTime(timeLeft);
                        Toast.makeText(getApplicationContext(), "修改时间成功", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case HttpPut.PUT_FAIL:
                    Toast.makeText(getApplicationContext(), "修改时间失败", Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };

    // this handler is used to prolong 1 hr
    Handler handler_sixty_minutes = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case HttpPut.PUT_SUCC:
                    try {
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());
                        result = jsonInfo.getString("result");

                        // update the left time
                        timeLeft += 3600;
                        msgInfo.setTime(timeLeft);
                        Toast.makeText(getApplicationContext(), "修改时间成功", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case HttpPut.PUT_FAIL:
                    Toast.makeText(getApplicationContext(), "修改时间失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    // this thread is used to update time
    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }

    // used for TimeThread
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    timeLeft = timeLeft - 1;
                    timeText.setText("还有"+ timeLeft + "秒失效");
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // get the parameter
        Bundle bundle = this.getIntent().getExtras();
        String originalData = bundle.getString("msgInfo");

         //resolve the parameter
        resolveParam(originalData);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);

        //initialize text to be displayed
        msgDisplay = (EditText) findViewById(R.id.message);
        if(msgInfo.getResult() != null)
            msgDisplay.setText(msgInfo.getResult());

        // display the sharing code
        shareCode = (TextView) findViewById(R.id.share_code);
        shareCode.setText("共享码：" + bundle.getString("sharingCode"));

        // display the time
        timeLeft = msgInfo.getTime();
        timeText = (TextView) findViewById(R.id.timeLeftText);
        timeText.setText("还有" + msgInfo.getTime() + "秒失效");

        // display the file name
        fileName = (TextView) findViewById(R.id.file_name);
        if(msgInfo.getFile() == null) {
            fileName.setText("没有附件文件");
            fileURL = null;
        }
        else {
            fileName.setText(msgInfo.getFile()[0].getFileName());
            // get the file url
            fileURL = msgInfo.getFile()[0].getUrl();
        }

        // count down thread
        updateThread = new TimeThread();
        updateThread.start();

        //set listener on save
        saveLayout = (LinearLayout) findViewById(R.id.save_layout);
        saveLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO: to be continued
            }
        });

        // set listener on add_ten_minutes and send request
        addTenMin = (TextView) findViewById(R.id.add_ten_minutes);
        addTenMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpPut("{\"scope\":600}".getBytes(),
                        "http://162.105.175.115:8004/message/" + shareCode + "/addtime",
                        handler_ten_minutes, HttpPut.PUT_MODIFY);
            }
        });

        // set listener on add_sixty_minutes and send request
        addOneHr = (TextView) findViewById(R.id.add_sixty_minutes);
        addOneHr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpPut("{\"scope\":3600}".getBytes(),
                        "http://162.105.175.115:8004/message/" + shareCode + "/addtime",
                        handler_ten_minutes, HttpPut.PUT_MODIFY);
            }
        });

        // set the listeners of the file opereations: delete and download
        deleteFile = (TextView) findViewById(R.id.delete_file);
        deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: delete the file
            }
        });

        downloadFile = (TextView) findViewById(R.id.download_file);
        downloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: download the file
            }
        });

        // set back icon listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // this function is used to resolve the parameter
    // TODO: there might be some fields as null, must check first!
    void resolveParam(String originalData) {
        try {
            // get the jason object
            JSONObject jsonObject = new JSONObject(originalData);

            // set the msgInfo
            msgInfo.setTitle(jsonObject.getString("title"));
            msgInfo.setSharedMsg(jsonObject.getString("shared_msg"));
            msgInfo.setTime(Integer.valueOf(jsonObject.getString("time")));
            msgInfo.setResult(jsonObject.getString("result"));

            // cope with the file list
            JSONArray fileArray = jsonObject.getJSONArray("file");
            FileInfo[] fileInfoArray = new FileInfo[fileArray.length()];
            for(int i = 0; i < fileArray.length(); i++) {
                fileInfoArray[i].setFileName(fileArray.getJSONObject(i).getString("file_name"));
                fileInfoArray[i].setUrl(fileArray.getJSONObject(i).getString("url"));
            }
            msgInfo.setFile(fileInfoArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // stop the time thread when this activity is to be destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();

        updateThread.interrupt();
    }
}
