package com.example.wangyicheng.gotopaste;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.wangyicheng.gotopaste.MyAdapter;
import com.example.wangyicheng.gotopaste.R;


public class FileFragment extends Fragment {
    private View view;  // this variable is used to find id in a fragment
    private ListView file_list;
    private MyAdapter adapter = null;

    public FileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set view
        view = inflater.inflate(R.layout.fragment_file, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
