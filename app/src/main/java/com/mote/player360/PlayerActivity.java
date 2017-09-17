package com.mote.player360;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mote.player360.orientation.GestureProcessor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Wenstery on 2017/7/24.
 */

public class PlayerActivity extends Activity {
    private static final String TAG = "PlayerActivity";
    private ToggleButton playBtn;
    private RelativeLayout progressTool;
    private TextView realTimeText;
    private TextView totalTimeText;
    private SeekBar processSeekBar;
    private Timer hideToolTimer;
    private HideToolTimerTask hideToolTimerTask;
    private boolean toolVisible = false;
    private boolean seekBarTouched = false;
    private GLSurfaceView glSurfaceView;
    private ViewPlayer mPlayer;
    private OpenglRender mRender;
    private GestureProcessor mGestureProcessor;
    private boolean isOrientationActive = true;
    private boolean isGestureActive = true;
    public static String VideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_player);
        toolVisible = true;
        init();

    }

    private void init() {
        viewInit();
        String videoPath = getIntent().getStringExtra(VideoPath);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.surface_view);
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        String v = info.getGlEsVersion();
        if (v.equalsIgnoreCase("3.0")) {
            glSurfaceView.setEGLContextClientVersion(3);
        } else {
            glSurfaceView.setEGLContextClientVersion(2);
        }
        mPlayer = new ViewPlayer(this);
        mPlayer.setMediaPlayerFromUri(Uri.parse(videoPath));
        mPlayer.setRenderCallback(new ViewPlayer.RenderCallback() {
            @Override
            public void renderImmediately() {
                glSurfaceView.requestRender();
            }
        });
        mPlayer.setPlayerStatus(ViewPlayer.PlayerStatus.IDLE);
        mPlayer.prepare();
        mRender = new OpenglRender(this);
        mRender.setViewPlayer(mPlayer);
        mRender.setOrientationActive(isOrientationActive);
        glSurfaceView.setRenderer(mRender);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glSurfaceView.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            glSurfaceView.setPreserveEGLContextOnPause(true);
        }
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startHideToolTimer();
                return mGestureProcessor.handleTouchEvent(event);
            }
        });
        mPlayer.setViewPlayerCallback(new ViewPlayer.PlayerCallback() {
            @Override
            public void updateProgress() {
                if (seekBarTouched == false) {
                    int pos = mPlayer.getCurrentPosition();
                    if (pos >= 0) {
                        processSeekBar.setProgress(pos);
                        String curTimeText = getShowTime(pos);
                        realTimeText.setText(curTimeText);
                    }
                }
            }

            @Override
            public void updateInfo() {
                startHideToolTimer();
                processSeekBar.setProgress(0);
                int duration = mPlayer.getDuration();
                processSeekBar.setMax(duration);
                String timeText = getShowTime(duration);
                realTimeText.setText(R.string.init_time);
                totalTimeText.setText(timeText);
            }

            @Override
            public void requestFinish() {
                finish();
            }
        });
        mGestureProcessor = new GestureProcessor(this, mRender, isGestureActive);
        mGestureProcessor.setToolShowCallback(toolShowCallback);

    }

    public void viewInit() {
        progressTool = (RelativeLayout) findViewById(R.id.player_toolbar_progress);
        playBtn = (ToggleButton) findViewById(R.id.play_btn);
        realTimeText = (TextView) findViewById(R.id.time_cur);
        totalTimeText = (TextView) findViewById(R.id.time_total);
        processSeekBar = (SeekBar) findViewById(R.id.progress_seek_bar);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHideToolTimer();
                changePlayingStatus();
            }
        });

        processSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                endHideToolTimer();
                seekBarTouched = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seekTo(seekBar.getProgress());
                seekBarTouched = false;
                startHideToolTimer();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        if (mPlayer != null && mPlayer.getPlayerStatus() == ViewPlayer.PlayerStatus.PLAYING) {
            mPlayer.Pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        if (mPlayer != null && mPlayer.getPlayerStatus() == ViewPlayer.PlayerStatus.PAUSED)
            mPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mRender != null) {
            mRender.destroy();
            mRender = null;
        }
    }

    private class HideToolTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toolHide();
                }
            });
        }
    }

    private void startHideToolTimer() {
        endHideToolTimer();
        hideToolTimer = new Timer();
        hideToolTimerTask = new HideToolTimerTask();
        hideToolTimer.schedule(hideToolTimerTask, 3000);

    }

    private void endHideToolTimer() {
        if (hideToolTimer != null) {
            hideToolTimer.cancel();
        }
        if (hideToolTimerTask != null) {
            hideToolTimerTask.cancel();
        }
    }

    private void toolHide() {
        if (!toolVisible) {
            return;
        }
        toolVisible = false;
        progressTool.setVisibility(View.GONE);
    }

    public void toolShow() {
        if (toolVisible) {
            return;
        }
        toolVisible = true;
        progressTool.setVisibility(View.VISIBLE);
    }

    private void changePlayingStatus() {
        if (mPlayer.getPlayerStatus() == ViewPlayer.PlayerStatus.PLAYING) {
            mPlayer.Pause();
        } else if (mPlayer.getPlayerStatus() == ViewPlayer.PlayerStatus.PAUSED) {
            mPlayer.start();
        }
    }

    private static String getShowTime(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("00:mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    private GestureProcessor.ToolShowCallback toolShowCallback = new GestureProcessor.ToolShowCallback() {
        @Override
        public void updateTools() {
            if (toolVisible == true) {
                toolHide();
            } else {
                toolShow();
            }
        }
    };

}
