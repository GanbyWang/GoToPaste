package com.example.wangyicheng.gotopaste;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the tool bar and set title and sub-title
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setLogo(R.drawable.navigation_icon);
        toolbar.setTitle("gotoPaste");
        toolbar.setSubtitle("即时分享信息和文件");

        // show the menu (only 1 element) and set listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO: accomplish the log-in jump
                return true;
            }
        });

        toolbar.inflateMenu(R.menu.main_toolbar_menu);
    }
}