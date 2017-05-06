package com.example.wangyicheng.gotopaste;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class UserActivity extends AppCompatActivity {

    private BottomNavigationBar bottomBar;
    int lastSelectedPosition = 0;    // this variable record the last selected position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // get the bottom navigation bar
        bottomBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        bottomBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        // add the three tabs: files, messages and user
        bottomBar.addItem(new BottomNavigationItem(R.drawable.file_80, "文件").setActiveColorResource(R.color.colorPrimaryDark))
        .addItem(new BottomNavigationItem(R.drawable.message_80, "信息").setActiveColorResource(R.color.colorPrimaryDark))
        .addItem(new BottomNavigationItem(R.drawable.user_80, "账户").setActiveColorResource(R.color.colorPrimaryDark))
        .setFirstSelectedPosition(lastSelectedPosition)
        .initialise();
    }
}
