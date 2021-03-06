package com.example.wangyicheng.gotopaste;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by wangyicheng on 2017/5/6.
 */

public class MyAdapter extends BaseAdapter {

    // this function is used to seek the content
    public void updateDataBySearch(String seekContent) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        // search will happen only when the data is not empty
        if(myData.size() > 0) {
            for (int i = 0; i < myData.size(); i++) {
                if (myData.get(i).get("abstract").toString().indexOf(seekContent) != -1) {
                    map = new HashMap<String, Object>();
                    map.put("abstract", myData.get(i).get("abstract"));
                    map.put("msgid", myData.get(i).get("msgid"));

                    list.add(map);
                }
            }

            myData = list;
            notifyDataSetChanged();
        }
    }

    Handler deleteHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // if it's a POST request and it succeeded
                case HttpGet.GET_SUCC:
                    try {
                        // get the json object
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());

                        Toast.makeText(myContext, "删除成功", Toast.LENGTH_LONG).show();

                        // remove the data and update the list
                        myData.remove(deletePosition);
                        notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                // if the post request fails
                case HttpGet.GET_FAIL:
                    Toast.makeText(myContext, "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    // this handler is used for query a msg
    Handler queryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // if it's a POST request and it succeeded
                case HttpGet.GET_SUCC:
                    try {
                        // get the json object
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());

                        // jump to display activity
                        // leave the resolution to the next activity
                        Bundle bundle = new Bundle();
                        bundle.putString("msgInfo", msg.obj.toString());
                        bundle.putString("sharingCode", myData.get(displayPosition).get("msgid").toString());
                        bundle.putInt("priority", 1);

                        Intent intent = new Intent(myContext, DisplayActivity.class);
                        intent.putExtras(bundle);
                        myContext.startActivity(intent);
                        // as the user might come back from posting
                        // here we don't finish the activity

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                // if the GET request fails
                case HttpGet.GET_FAIL:
                    Toast.makeText(myContext, "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private int deletePosition;
    private int displayPosition;
    private LayoutInflater myInflater;
    private Context myContext;
    // myData stores all the information needed
    public List<Map<String, Object>> myData = new ArrayList<>();

    // the following functions are required when extending BaseAdapter
    public MyAdapter(Context context) {
        this.myInflater = LayoutInflater.from(context);
        myContext = context;
    }

    @Override
    public int getCount() {
        return myData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    // the most important function
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            // inflate the view and get the views
            convertView = myInflater.inflate(R.layout.listview_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete) ;
            holder.more = (ImageView) convertView.findViewById(R.id.more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set the texts
        holder.title.setText((String) myData.get(position).get("abstract"));
        holder.more.setTag(position);

        // set the listeners
        // delete the file
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // use a dialog to ensure the user wants to delete a message
                AlertDialog alert = null;
                AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                alert = builder.setMessage("您确定要删除本条消息吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // send HTTP request
                                String URL = "http://162.105.175.115:8004/delete/message/";
                                URL += myData.get(position).get("msgid");
                                URL += "?token=" + MainActivity.token;
                                new HttpGet(URL, deleteHandler, HttpGet.DELETE_MSG);

                                // record the delete position
                                deletePosition = position;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create();
                alert.show();
            }
        });

        // view the details
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // record which message is to be diplayed
                displayPosition = position;
                String URL = "http://162.105.175.115:8004/message/";
                URL += myData.get(position).get("msgid");
                URL += "?token=" + MainActivity.token;
                new HttpGet(URL, queryHandler, HttpGet.TYPE_GETMSG);
            }
        });

        return convertView;
    }
}
