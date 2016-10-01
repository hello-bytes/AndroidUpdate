package com.newjourney.testupdater;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.newjourney.updater.UpdateAgent;
import com.newjourney.updater.UpdateListener;
import com.newjourney.updater.UpdateResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckUpdate();
            }
        });
    }

    public void onCheckUpdate(){
        UpdateAgent.setUpdateListener(new UpdateListener() {
            @Override
            public void onUpdateReturned(UpdateResponse updateResponse) {
                switch(updateResponse.updateType){
                    case UpdateResponse.ForceUpdate:
                        UpdateAgent.showUpdateDialog(MainActivity.this,updateResponse);
                        break;
                    case UpdateResponse.HasNewVersion:
                        UpdateAgent.showUpdateDialog(MainActivity.this,updateResponse);
                        break;
                    case UpdateResponse.NoUpdateVer:
                        Toast.makeText(MainActivity.this,"当前版本已经是最新",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        UpdateAgent.checkUpdate(this);
    }
}
