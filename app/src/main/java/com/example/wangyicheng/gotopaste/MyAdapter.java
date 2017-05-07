package com.example.wangyicheng.gotopaste;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by wangyicheng on 2017/5/6.
 */

public class MyAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    // myData stores all the information needed
    public List<Map<String, Object>> myData;

    // the following functions are required when extending BaseAdapter
    public MyAdapter(Context context) {
        this.myInflater = LayoutInflater.from(context);
        myData = null;  // set the data as null
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
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            holder.more = (ImageView) convertView.findViewById(R.id.more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set the texts
        holder.title.setText((String) myData.get(position).get("title"));
        holder.time.setText((String) myData.get(position).get("time"));
        holder.size.setText((String) myData.get(position).get("size"));
        holder.more.setTag(position);

        // set the listener
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
}
