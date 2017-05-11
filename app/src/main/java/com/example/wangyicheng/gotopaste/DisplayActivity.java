package com.example.wangyicheng.gotopaste;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private MsgInfo msgInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // get the parameter
        Bundle bundle = this.getIntent().getExtras();
        String originalData = bundle.getString("msgInfo");

        // resolve the parameter
        resolveParam(originalData);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.inflateMenu(R.menu.post_toolbar_menu);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("navigation","clicked");
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

    // this function is used to resolve the parameter
    // TODO: there might be some fields as null, must check first!
    void resolveParam(String originalData) {
        try {
            // get the jason object
            JSONObject jsonObject = new JSONObject(originalData);

            // set the msgInfo

            msgInfo.setTitle(jsonObject.getString("title"));
            msgInfo.setSharedMsg(jsonObject.getString("shared_msg"));
            msgInfo.setTime(Integer.valueOf(jsonObject.getString("time")));
            msgInfo.setResult(jsonObject.getString("result"));

            // cope with the file list
            JSONArray fileArray = jsonObject.getJSONArray("file");
            FileInfo[] fileInfoArray = new FileInfo[fileArray.length()];
            for(int i = 0; i < fileArray.length(); i++) {
                fileInfoArray[i].setFileName(fileArray.getJSONObject(i).getString("file_name"));
                fileInfoArray[i].setUrl(fileArray.getJSONObject(i).getString("url"));
            }
            msgInfo.setFile(fileInfoArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
