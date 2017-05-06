package com.example.wangyicheng.gotopaste;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// This activity is responsible for showing a file
// The file can be a private or a shared one
// For a shared file, user can download it as well as changing the sharing time
// For a private one, user can download it
public class ViewFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);
    }
}
