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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by wangyicheng on 2017/5/6.
 */

public class MyAdapter extends BaseAdapter {

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

    private int deletePosition;
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

            }
        });

        return convertView;
    }
}
