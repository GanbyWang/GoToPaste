package com.example.wangyicheng.gotopaste;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by yaoyang on 2017/5/6.
 */

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button loginButton, regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(R.drawable.myreturn);

        // finish when press the back icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // get the buttons
        loginButton = (Button) findViewById(R.id.login_button);
        regButton = (Button) findViewById(R.id.register_button);

        // set listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: need to check if the user can log in
                    here we jump to user activity directly
                    if log in successfully, need to modify MainActivity.priority
                 */
                MainActivity.priority = 1;

                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                // here we don't need to finish login activity
            }
        });
    }
}
