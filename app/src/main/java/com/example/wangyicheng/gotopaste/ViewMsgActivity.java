package com.example.wangyicheng.gotopaste;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// This activity is responsible for showing a message
// The message can be a private or a shared one
// For a shared message, user can modify it as well as changing the sharing time
// For a private one, user can modify it
public class ViewMsgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_msg);
    }
}
