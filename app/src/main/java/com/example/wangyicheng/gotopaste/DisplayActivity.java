package com.example.wangyicheng.gotopaste;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    static private Context myContext;
    private static String[][] MIME_MapTable = {
            //{后缀名， MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml", "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };
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
                case HttpGet.GET_SUCC:
                    Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                    // update data locally
                    msgInfo.setFile(null);
                    fileURL = null;
                    break;

                case HttpGet.GET_FAIL:
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


        myContext = (Context) this;
        // get the parameter
        Bundle bundle = this.getIntent().getExtras();
        String originalData = bundle.getString("msgInfo");
        priority = bundle.getInt("priority");
        sharingCode = bundle.getString("sharingCode");

        // get the views
        timeText = (TextView) findViewById(R.id.timeLeftText);

        // if it's a private message, hide some views
        if(priority == 1) {
            timeText.setVisibility(View.GONE);
            LinearLayout addTimeBlock = (LinearLayout) findViewById(R.id.add_time_block);
            addTimeBlock.setVisibility(View.GONE);
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
            shareCode.setText("共享码：" + sharingCode);

            // display the time
            timeLeft = msgInfo.getTime();
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
                String data = "";
                // if the message is a private one
                // we need to add token
                if(priority == 0) {
                    data = "{\"shared_msg\":\"" + msgDisplay.getText().toString() + "\"}";
                } else if(priority == 1) {
                    data = "{\"shared_msg\":\"" + msgDisplay.getText().toString() + "\",";
                    data += "\"token\":\"" + MainActivity.token + "\"}";
                }
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
                    String query = "http://162.105.175.115:8004/delete/file/" + fileURL;
                    if (priority == 1)  {
                        query += "?token=" + MainActivity.token;
                    }
                    new HttpGet(query, deleteHandler, 1);
                }
            }
        });

        downloadFile = (TextView) findViewById(R.id.download_file);
        downloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInfo[] files = msgInfo.getFile();
                for (final FileInfo file: files) {
                    if (file.getUrl() != null){
                        new Thread(new Runnable() {
                            @Override
                            public void run(){
                                String downloadUrl = "http://162.105.175.115:8004/file" + file.getUrl();
                                if (priority == 1)
                                    downloadUrl += "?token=" + MainActivity.token;
                                try {
                                    Log.i("downloadUrl",downloadUrl);
                                    URL url = new URL(downloadUrl);
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    String SDCard = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS;
                                    String fileName = file.getUrl().substring(file.getUrl().lastIndexOf('/')+1);
                                    fileName = fileName.substring(sharingCode.length());
                                    String pathName = SDCard + '/' + fileName;
                                    Log.i("download path",pathName);
                                    final File newFile = new File(pathName);
                                    InputStream input = conn.getInputStream();
                                    OutputStream output;

                                    newFile.createNewFile();
                                    output = new FileOutputStream(newFile);
                                    byte[] buffer = new byte[1024];
                                    int res;
                                    while ((res = input.read(buffer,0,buffer.length)) != -1){
                                        output.write(buffer,0,res);
                                    }
                                    output.flush();
                                    output.close();
                                    Looper.prepare();
                                    alert = null;
                                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                                    alert = builder.setMessage("下载成功，是否打开文件？")
                                            .setNegativeButton("是", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    openFile(myContext,newFile);
                                                }
                                            })
                                            .setPositiveButton("否", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).create();
                                    alert.show();
                                    Toast.makeText(getApplicationContext(), "保存为\r\n" + Environment.DIRECTORY_DOWNLOADS + fileName, Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                } catch (MalformedURLException e) {
                                    Looper.prepare();
                                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    Looper.prepare();
                                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                // TODO: download the file
            }
        });


    }

//    public void openFile(Uri uri) {
//        Log.i("uri",uri.getPath());
//        Intent intent = new Intent();
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        File file = new File(String.valueOf(uri)); // set your audio path
//        intent.setDataAndType(Uri.fromFile(file), "*/*");
//
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        Notification noti = new NotificationCompat.Builder(this)
//                .setContentTitle("Download completed")
//                .setContentText("Song name")
//                .setContentIntent(pIntent).build();
//
//        noti.flags |= Notification.FLAG_AUTO_CANCEL;
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, noti);
//    }
    public static void openFile(Context context,File file){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(file), type);
        //跳转
        try {
            context.startActivity(intent);
        } catch (Exception e) {
//            logger.error("FileUtil", e);
            Toast.makeText(context, "找不到打开此文件的应用！", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 获得文件的mimeType
     * @param file
     * @return
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        if(file == null) return type;
        String fName = file.getName();
        // 取得扩展名
        String end = fName.substring(fName.lastIndexOf("."),
                fName.length()).toLowerCase();
        if (end.equals("")) return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }

        return type;
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