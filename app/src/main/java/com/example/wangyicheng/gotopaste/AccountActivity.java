package com.example.wangyicheng.gotopaste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class AccountActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // get the tool bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // set the back icon
        toolbar.setNavigationIcon(R.drawable.back);
        // finish when press the back icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // get the button
        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: deal with logging out and jump to main activity
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
