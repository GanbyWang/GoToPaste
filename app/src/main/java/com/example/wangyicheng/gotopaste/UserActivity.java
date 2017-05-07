package com.example.wangyicheng.gotopaste;

import android.graphics.Color;
import android.nfc.Tag;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;

// overwrite the listener in order to change the tabs
public class UserActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private BottomNavigationBar bottomBar;
    int lastSelectedPosition = 0;    // this variable record the last selected position
    private Fragment []fragments;    // allocate the fragments
    private FragmentManager fragmentManager;    // use the fragment manager to manage the fragments
    private String []tags;     // the tags of the fragments
    private Toolbar toolbar;
    private int state;      //this flag specify which fragment is displayed
                            //0 for file, 1 for msg, 2 for account

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // get the tool bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.inflateMenu(R.menu.toolbar_add_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO: accomplish the pushing file/message action
                Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        // get the bottom navigation bar
        bottomBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        // set the mode of the bottom bar
        bottomBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        // set the color of the bottom bar, same as the tool bar
        // set the active and inactive color of the tabs
        bottomBar.setBarBackgroundColor(R.color.colorPrimary)
        .setActiveColor(R.color.colorPrimaryDark)
        .setInActiveColor("#ffffff");

        // add the three tabs: files, messages and user
        bottomBar.addItem(new BottomNavigationItem(R.drawable.file_80, "文件"))
        .addItem(new BottomNavigationItem(R.drawable.message_80, "信息"))
        .addItem(new BottomNavigationItem(R.drawable.user_80, "账户"))
        .setFirstSelectedPosition(lastSelectedPosition)
        .initialise();

        // get the fragment manager
        fragmentManager = getSupportFragmentManager();

        // new 3 fragments
        fragments = new Fragment[3];
        fragments[0] = new FileFragment();
        fragments[1] = new MsgFragment();
        fragments[2] = new AccountFragment();

        // allocate the tags
        tags = new String[3];
        tags[0] = "fileFragment";
        tags[1] = "msgFragment";
        tags[2] = "accountFragment";

        // set the listener
        bottomBar.setTabSelectedListener(this);

        // use the onTabSelected function to set the default fragment
        // at present the default one is the file fragment
        onTabSelected(0);
        state = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_add_menu, menu);
        return true;
    }

    // in order to rewrite the tab listener, 3 functions have to be overwritten
    @Override
    public void onTabSelected(int position) {
        // if we change the fragment to account
        // we need to clear the menu
        if(position == 2 && state != 2) {
            toolbar.getMenu().clear();
        }
        // if we change the fragment from account
        // we need to inflate the menu again
        else if(state == 2 && position != 2) {
            toolbar.inflateMenu(R.menu.toolbar_add_menu);
        }

        // change the state and title
        state = position;
        changeToolbarTitle(position);

        // pop the existing fragments in the stack
        for (int i = 0, count = fragmentManager.getBackStackEntryCount(); i < count; i++) {
            fragmentManager.popBackStack();
        }
        // begin the fragment
        FragmentTransaction fg = fragmentManager.beginTransaction();
        // no arguments needed to transfer
        fragments[position].setArguments(null);
        fg.add(R.id.fragment_root, fragments[position], tags[position]);
        fg.addToBackStack(tags[position]);
        fg.commit();
    }

    // the following functions do nothing
    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    // this function changes the title according to the fragment
    private void changeToolbarTitle(int position) {
        String []titles = {"我的文件", "我的信息", "用户中心"};
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(titles[position]);
    }
}
