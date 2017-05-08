package com.example.wangyicheng.gotopaste;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by yaoyang on 2017/5/6.
 */

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setLogo(R.drawable.myreturn);
    }
}
