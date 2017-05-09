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

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button regButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar3);
        toolbar.setNavigationIcon(R.drawable.myreturn);

        // return when press the back icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // get the button
        regButton = (Button) findViewById(R.id.register_button);
        // set listener
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: check whether the user has register successfully
                    here we jump to user activity directly
                    if register successfully, we need to modify MainActivity.priority
                */
                MainActivity.priority = 1;

                Intent intent = new Intent(RegisterActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
