package com.example.wangyicheng.gotopaste;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayActivity extends AppCompatActivity {
    // the following variables are the views in the xml file
    private Toolbar toolbar;
    private EditText msgDisplay;
    private TextView shareCode;
    private TextView fileName;
    private TextView save;
    private TextView addTenMin;
    private TextView addOneHr;
    private TextView deleteFile;
    private TextView downloadFile;
    private TextView timeText;

    // some global variables
    private MsgInfo msgInfo = new MsgInfo();
    private int timeLeft;
    private TimeThread updateThread;
    private static final int msgKey1 = 1;
    private String result;
    private String fileURL;
    private String sharingCode;
    private int priority = -1;

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

    // this handler is used to modify the message
    Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // TODO: here we didn't do anything with the return value
                case HttpPut.PUT_SUCC:
                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_LONG).show();
                    break;

                case HttpPut.PUT_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    // this handler is used to delete the file
    Handler deleteHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // TODO: here we didn't do anything with the return value
                case HttpDelete.DELETE_SUCC:
                    Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                    // update data locally
                    msgInfo.setFile(null);
                    fileURL = null;
                    break;

                case HttpDelete.DELETE_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
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
                    timeText.setText("还有" + timeLeft + "秒失效");
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
        priority = bundle.getInt("priority");

        if(priority == 0) {

        }

         //resolve the parameter
        resolveParam(originalData);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);

        // set back icon listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //initialize text to be displayed
        msgDisplay = (EditText) findViewById(R.id.message);
        if(msgInfo.getResult() != null)
            msgDisplay.setText(msgInfo.getSharedMsg());

        // only when the message is a shared one do we show the code
        if(priority == 0) {
            // display the sharing code
            shareCode = (TextView) findViewById(R.id.share_code);
            sharingCode = bundle.getString("sharingCode");
            shareCode.setText("共享码：" + sharingCode);

            // display the time
            timeLeft = msgInfo.getTime();
            timeText = (TextView) findViewById(R.id.timeLeftText);
            timeText.setText("还有" + msgInfo.getTime() + "秒失效");
        }

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

        // set the listener of save
        save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: we haven't take token into considerations
                String data = "{\"shared_msg\":\"" + msgDisplay.getText().toString() + "\"}";
                new HttpPut(data.getBytes(),
                        "http://162.105.175.115:8004/message/" + sharingCode,
                        msgHandler, HttpPut.TYPE_MODIFY);
            }
        });

        // set listener on add_ten_minutes and send request
        addTenMin = (TextView) findViewById(R.id.add_ten_minutes);
        addTenMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpPut("{\"scope\":600}".getBytes(),
                        "http://162.105.175.115:8004/message/" + sharingCode + "/addtime",
                        handler_ten_minutes, HttpPut.TYPE_MODIFY);
            }
        });

        // set listener on add_sixty_minutes and send request
        addOneHr = (TextView) findViewById(R.id.add_sixty_minutes);
        addOneHr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpPut("{\"scope\":3600}".getBytes(),
                        "http://162.105.175.115:8004/message/" + sharingCode + "/addtime",
                        handler_sixty_minutes, HttpPut.TYPE_MODIFY);
            }
        });

        // set the listeners of the file operations: delete and download
        deleteFile = (TextView) findViewById(R.id.delete_file);
        deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the message has an attachment
                if(msgInfo.getFile() == null)
                    Toast.makeText(getApplicationContext(), "无附件文件！", Toast.LENGTH_LONG).show();
                else {
                    // send delete request
                    new HttpDelete("http://162.105.175.115:8004/file/" + fileURL,
                            deleteHandler, HttpDelete.DELETE_FILE);
                }
            }
        });

        downloadFile = (TextView) findViewById(R.id.download_file);
        downloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: download the file
            }
        });


    }

    // this function is used to resolve the parameter
    void resolveParam(String originalData) {
        try {
            // get the jason object
            JSONObject jsonObject = new JSONObject(originalData);

            // set the msgInfo
            // if the field is null, set as empty string
            msgInfo.setTitle(jsonObject.getString("title").equals(null) ? "" : jsonObject.getString("title"));
            msgInfo.setSharedMsg(jsonObject.getString("shared_msg").equals(null) ? "" : jsonObject.getString("shared_msg"));
            msgInfo.setTime(Integer.valueOf(jsonObject.getString("time")));
            msgInfo.setResult(jsonObject.getString("result"));

            // cope with the file list
            JSONArray fileArray = jsonObject.getJSONArray("file");
            // return immediately if there is no file
            if(fileArray.length() == 0) {
                msgInfo.setFile(null);
                return;
            }
            FileInfo[] fileInfoArray = new FileInfo[fileArray.length()];
            for(int i = 0; i < fileArray.length(); i++) {
                fileInfoArray[i] = new FileInfo();
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