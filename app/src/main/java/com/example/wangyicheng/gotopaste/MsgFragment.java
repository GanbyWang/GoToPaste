package com.example.wangyicheng.gotopaste;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class MsgFragment extends Fragment {
    private View view;
    private ListView msg_list;
    private MyAdapter adapter = null;

    public MsgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set view
        view = inflater.inflate(R.layout.fragment_msg, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
