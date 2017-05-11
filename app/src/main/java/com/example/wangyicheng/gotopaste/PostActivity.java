package com.example.wangyicheng.gotopaste;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout file_button;
    private TextView codeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        file_button = (LinearLayout) findViewById(R.id.add_file);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.inflateMenu(R.menu.post_toolbar_menu);

        // get the parameter "sharing code"
        Bundle bundle = this.getIntent().getExtras();
        String sharingCode = bundle.getString("sharingCode");

        // inflate the sharing code
        codeView = (TextView) findViewById(R.id.share_code);
        codeView.setText("共享码：" + sharingCode);

        file_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
        // set the
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.i("menu","clicked");
                return true;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.i("uri", uri.toString());
        }
    }
}
