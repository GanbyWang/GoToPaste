package com.example.wangyicheng.gotopaste;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;

// overwrite the listener in order to change the tabs
public class UserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Find the tool bar and set title and sub-title
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // show the menu (only 1 element) and set listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // jump to account activity
                Intent intent = new Intent(UserActivity.this, AccountActivity.class);
                startActivity(intent);
                // as the user might come back from posting
                // here we don't finish the activity
                return true;
            }
        });

        toolbar.inflateMenu(R.menu.main_toolbar_menu);

        // get the post button
        postButton = (FloatingActionButton) findViewById(R.id.post_button);
        // set the listener
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, PostActivity.class);
                startActivity(intent);
                // as the user might come back from posting
                // here we don't finish the activity
            }
        });
    }
}
