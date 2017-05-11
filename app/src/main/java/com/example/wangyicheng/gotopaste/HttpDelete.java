package com.example.wangyicheng.gotopaste;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wangyicheng on 2017/5/10.
 */

public class HttpDelete {
    // Set result
    static final int DELETE_SUCC = 6;
    static final int DELETE_FAIL = 7;

    // Set type

    // Set timeout
    private final int READ_TIMEOUT = 3000;
    private final int CONNECT_TIMEOUT = 3000;

    // the type is used to differ different request
    public HttpDelete(final String urlString, final Handler handler, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // URL
                    URL url = new URL(urlString);
                    // Create HttpURLConnection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // Set Request Method
                    connection.setRequestMethod("DELETE");
                    // Set Connect Timeout
                    connection.setConnectTimeout(CONNECT_TIMEOUT);
                    // Set Read Timeout
                    connection.setReadTimeout(READ_TIMEOUT);
                    // Set I/O options
                    // connection.setDoInput(true);
                    // connection.setDoOutput(true);
                    // connection.setUseCaches(false);
                    // Set Request Property
                    // connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("Content-Type", "application/json");

                    // System.out.println(connection.getResponseCode());
                    // Http Success
                    if (connection.getResponseCode() >= HttpURLConnection.HTTP_OK &&
                            connection.getResponseCode() < 300) {
                        // Get InputStream
                        InputStream inputStream = connection.getInputStream();
                        // Switch to
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        // Store feedback from the Net
                        String line;
                        String resultData = "";
                        while (((line = bufferedReader.readLine()) != null)) {
                            resultData += line;
                        }
                        // Close stream
                        inputStream.close();
                        // Close connection
                        connection.disconnect();

                        // Set Message (return to handler)
                        Message mg = Message.obtain();
                        mg.what = DELETE_SUCC;
                        mg.obj = resultData;
                        mg.arg1 = type;
                        handler.sendMessage(mg);
                    }
                    // Http not success
                    else {
                        // Get InputStream
                        InputStream inputStream = connection.getErrorStream();
                        // Switch to
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        // Store feedback from the Net
                        String line;
                        String resultData = "";
                        while (((line = bufferedReader.readLine()) != null)) {
                            resultData += line;
                        }
                        // Close stream
                        inputStream.close();
                        // Close connection
                        connection.disconnect();

                        // Set Message (return to handler)
                        Message mg = Message.obtain();
                        mg.what = DELETE_FAIL;
                        mg.obj = resultData;
                        mg.arg1 = type;
                        handler.sendMessage(mg);
                    }
                } catch (Exception e) {
                    // Set Message (return to handler)
                    Message mg = Message.obtain();
                    mg.what = DELETE_FAIL;
                    mg.obj = "请检查网络连接";
                    mg.arg1 = type;
                    handler.sendMessage(mg);
                    // Debug
                    e.printStackTrace();
                }
            }
        }).start();
    }
}