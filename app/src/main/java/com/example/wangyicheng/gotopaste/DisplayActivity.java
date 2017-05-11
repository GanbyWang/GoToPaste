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

public class DisplayActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MsgInfo msgInfo;
    private EditText msgDisplay;
    private TextView shareCode;
    private TextView fileName;
    private LinearLayout saveLayout;
    private LinearLayout addTimeLayout;
    private LinearLayout fileLayout;
    private TimeThread updateThread;
    private static final int msgKey1 = 1;
    private String result;
    private String fileURL;



    private int timeLeft;
    private TextView timeText;
    private String timeString;
    Handler handler_ten_minutes = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what)
            {
                case HttpPut.PUT_SUCC:
                    try{
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());
                        result = jsonInfo.getString("result");
                        timeLeft += 600;
                        msgInfo.setTime(timeLeft);
                        Toast.makeText(getApplicationContext(), "修改时间成功", Toast.LENGTH_LONG).show();

                    }
                    catch (JSONException e){
                        e.printStackTrace();

                }
                    break;
                case HttpPut.PUT_FAIL:
                    Toast.makeText(getApplicationContext(), "修改时间失败", Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    Handler handler_sixty_minutes = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what)
            {
                case HttpPut.PUT_SUCC:
                    try{
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());
                        result = jsonInfo.getString("result");
                        timeLeft += 3600;
                        msgInfo.setTime(timeLeft);
                        Toast.makeText(getApplicationContext(), "修改时间成功", Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e){
                        e.printStackTrace();

                    }
                    break;
                case HttpPut.PUT_FAIL:
                    Toast.makeText(getApplicationContext(), "修改时间失败", Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    timeLeft = timeLeft - 1;
                    msgInfo.setTime(timeLeft);
                    timeString = Integer.toString(timeLeft);
                    timeText.setText("还有"+ timeString+"秒失效");
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
        toolbar.inflateMenu(R.menu.post_toolbar_menu);
        //initialize text to be displayed
        msgDisplay = (EditText) findViewById(R.id.message);
        if(msgInfo.getResult() != null)
            msgDisplay.setText(msgInfo.getResult());

        shareCode = (TextView) findViewById(R.id.share_code);
        shareCode.setText(msgInfo.getSharedMsg());

        timeLeft = msgInfo.getTime();
        timeText = (TextView) findViewById(R.id.timeLeftText);
        timeString = Integer.toString(timeLeft);
        timeText.setText(timeString);

        fileName = (TextView) findViewById(R.id.file_name);
        if(msgInfo.getFile() == null)
        {
            fileName.setText("没有附件文件");
            fileURL = null;
        }
        else
        {
            fileName.setText(msgInfo.getFile()[0].getFileName());
            fileURL = msgInfo.getFile()[0].getUrl();
        }




        //count down thread

        updateThread = new TimeThread();
        updateThread.start();


        //set listener on save
        saveLayout = (LinearLayout) findViewById(R.id.save_layout);
        saveLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //to be continued;
            }
        });

        // set listener on addtime
        addTimeLayout = (LinearLayout) findViewById(R.id.addTimeLayout);
        addTimeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId())
                {
                    case R.id.add_ten_minutes:
                        new HttpPut("\"scope\":600".getBytes(), "http://162.105.175.115:8004/message/" + shareCode+"/addtime", handler_ten_minutes, HttpPut.PUT_MODIFY);
                        ;
                        break;
                    case R.id.add_sixty_minutes:
                        new HttpPut("\"scope\":3600".getBytes(), "http://162.105.175.115:8004/message/" + shareCode+"/addtime", handler_sixty_minutes, HttpPut.PUT_MODIFY);
                        break;
                    default:
                }
            }
        });

        //set listener on files
        fileLayout = (LinearLayout) findViewById(R.id.filelayout);
        fileLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId())
                {
                    case R.id.download_file:

                        break;
                    case R.id.delete_file:
                        break;
                    default:
                }
            }
        });




        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("navigation","clicked");
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i("menu","clicked");
                return true;
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
}
