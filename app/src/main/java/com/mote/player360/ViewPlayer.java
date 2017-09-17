package com.mote.player360;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by Wenstery on 2017/8/8.
 */

public class ViewPlayer implements
        SurfaceTexture.OnFrameAvailableListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {
    private static final String TAG = "ViewPlayer";
    private SurfaceTexture mSurfaceTexture;
    private MediaPlayer mMediaPlayer;
    private PlayerCallback mViewPlayerCallback;
    private RenderCallback mRenderCallback;
    private Context mContext;
    private PlayerStatus mPlayerStatus;
    private VideoSizeCallback mVideoSizeCallback;

    public ViewPlayer(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
    }

    public void setRenderCallback(RenderCallback renderCallback) {
        this.mRenderCallback = renderCallback;
    }

    public void setViewPlayerCallback(PlayerCallback playerCallback) {
        this.mViewPlayerCallback = playerCallback;
    }

    public void setVideoSizeCallback(VideoSizeCallback videoSizeCallback) {
        this.mVideoSizeCallback = videoSizeCallback;
    }

    public void setSurface(int textureID) {
        mSurfaceTexture = new SurfaceTexture(textureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        Surface surface = new Surface(mSurfaceTexture);
        mMediaPlayer.setSurface(surface);
        surface.release();
    }

    public void doTextureUpdate(float[] sTMatrix) {
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(sTMatrix);
    }

    public void openRemoteFile(String path) {
        try {
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMediaPlayerFromUri(Uri uri) {
        try {
            mMediaPlayer.setDataSource(mContext, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
    }

    public void prepare() {
        if (mMediaPlayer != null) {
            mMediaPlayer.prepareAsync();
        }
    }

    public void start() {
        if (mPlayerStatus == PlayerStatus.PREPARED || mPlayerStatus == PlayerStatus.PAUSED || mPlayerStatus == PlayerStatus.PAUSED_BY_USER) {
            mMediaPlayer.start();
            setPlayerStatus(PlayerStatus.PLAYING);
        }
    }

    public void Pause() {
        if (mPlayerStatus == PlayerStatus.PLAYING) {
            mMediaPlayer.pause();
            setPlayerStatus(PlayerStatus.PAUSED);
        }
    }

    public void stop() {
        PlayerStatus status = getPlayerStatus();
        if (status == PlayerStatus.PLAYING
                || status == PlayerStatus.PREPARED
                || status == PlayerStatus.PAUSED
                || status == PlayerStatus.PAUSED_BY_USER) {
            mMediaPlayer.stop();
            setPlayerStatus(PlayerStatus.STOPPED);
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(null);
            if (mSurfaceTexture != null) {
                mSurfaceTexture = null;
            }
            stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setPlayerStatus(PlayerStatus status) {
        this.mPlayerStatus = status;
    }

    public PlayerStatus getPlayerStatus() {
        return mPlayerStatus;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setPlayerStatus(PlayerStatus.COMPLETE);
        if (mViewPlayerCallback != null) {
            mViewPlayerCallback.requestFinish();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        return false;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mRenderCallback.renderImmediately();
        if (mViewPlayerCallback != null) {
            mViewPlayerCallback.updateProgress();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setPlayerStatus(PlayerStatus.PREPARED);
        if (mViewPlayerCallback != null) {
            mViewPlayerCallback.updateInfo();
        }
        start();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        if (mVideoSizeCallback != null)
            mVideoSizeCallback.notifyVideoSizeChanged(width, height);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void seekTo(int pos) {
        if (mMediaPlayer != null) {
            PlayerStatus playerStatus = getPlayerStatus();
            if (playerStatus == PlayerStatus.PLAYING
                    || playerStatus == PlayerStatus.PAUSED
                    || playerStatus == PlayerStatus.PAUSED_BY_USER)
                mMediaPlayer.seekTo(pos);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    public interface VideoSizeCallback {
        void notifyVideoSizeChanged(int width, int height);
    }

    public interface PlayerCallback {
        void updateProgress();

        void updateInfo();

        void requestFinish();
    }

    public interface RenderCallback {
        void renderImmediately();
    }

    public enum PlayerStatus {
        IDLE, PREPARED, PLAYING, PAUSED_BY_USER, PAUSED, STOPPED, COMPLETE
    }
}
