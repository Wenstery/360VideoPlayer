package com.mote.player360;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mStartBtn;
    private String file_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartBtn = (Button) findViewById(R.id.player_start);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_path = "android.resource://" + getPackageName() + "/" + R.raw.lanbo;
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PlayerActivity.class);
                intent.putExtra(PlayerActivity.VideoPath, file_path);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
        }
    }
}
