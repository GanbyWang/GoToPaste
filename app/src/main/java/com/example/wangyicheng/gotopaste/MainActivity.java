package com.example.wangyicheng.gotopaste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    public static int priority = 0; // priority is used to differ normal user and log-in user
                                    // 0 for normal user and 1 for log-in user
    private LinearLayout postNew;

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
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            }
        });

        toolbar.inflateMenu(R.menu.main_toolbar_menu);

        // find the linearlayout
        postNew = (LinearLayout) findViewById(R.id.add_file);
        postNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
                // as the user might come back from posting
                // here we don't finish the activity
            }
        });
    }
}