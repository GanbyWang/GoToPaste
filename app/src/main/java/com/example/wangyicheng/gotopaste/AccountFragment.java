package com.example.wangyicheng.gotopaste;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccountFragment extends Fragment {
    private View view;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and set view
        view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
