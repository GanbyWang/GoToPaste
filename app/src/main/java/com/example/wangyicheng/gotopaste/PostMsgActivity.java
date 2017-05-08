package com.example.wangyicheng.gotopaste;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;

// This activity is responsible for pushing messages
// Shared messages and private messages included
public class PostMsgActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_msg);

        checkBox = (CheckBox) findViewById(R.id.checkBox2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.back);
        // show the menu (only 1 element)
        toolbar.inflateMenu(R.menu.post_toolbar_menu);
    }
}
