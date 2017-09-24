package com.mote.player360;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText mEditText;
    private String mVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.video_path);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mVideoPath = mEditText.getText().toString();
            }
        });
        mVideoPath = mEditText.getText().toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean bPlane = false;
        switch (id) {
            case R.id.plane_item:
                bPlane = true;
                break;
            case R.id.pano_item:
                break;
        }
        if (mVideoPath.equals("") || mVideoPath.equals(getString(R.string.init_path))) {
            mVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.lanbo;
        }
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.VideoPath, mVideoPath);
        intent.putExtra(PlayerActivity.PlaneConf, bPlane);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
        }
    }
}
