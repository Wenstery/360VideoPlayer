package com.mote.player360;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Button mStartBtn;
    private EditText mEditText;
    private String video_path;

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
                video_path = mEditText.getText().toString();
            }
        });
        video_path = mEditText.getText().toString();
        mStartBtn = (Button) findViewById(R.id.player_start);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (video_path.equals("") || video_path.equals(getString(R.string.init_path))) {
                    video_path = "android.resource://" + getPackageName() + "/" + R.raw.lanbo;
                }
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PlayerActivity.class);
                intent.putExtra(PlayerActivity.VideoPath, video_path);
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
