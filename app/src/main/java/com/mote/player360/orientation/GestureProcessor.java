package com.mote.player360.orientation;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.mote.player360.OpenglRender;

/**
 * Created by Wenstry on 2017/8/13.
 */

public class GestureProcessor {
    private OpenglRender mRender;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private Context mContext;
    private boolean gestureConf;
    private ToolShowCallback toolShowCallback;

    private static final float sDensity = Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;

    public GestureProcessor(Context context, OpenglRender render, boolean isGestureActive) {
        mContext = context;
        mRender = render;
        gestureConf = isGestureActive;
        init();
    }

    public void setToolShowCallback(ToolShowCallback callback) {
        toolShowCallback = callback;
    }

    public void init() {
        mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (toolShowCallback != null) {
                    toolShowCallback.updateTools();
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (gestureConf == true) {
                    mRender.getSphereRenderLayer().setDeltaX(mRender.getSphereRenderLayer().getDeltaX() + distanceX / sDensity * sDamping);
                    mRender.getSphereRenderLayer().setDeltaY(mRender.getSphereRenderLayer().getDeltaY() + distanceY / sDensity * sDamping);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                mRender.getSphereRenderLayer().updateScale(scaleFactor);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
    }

    public boolean handleTouchEvent(MotionEvent event) {
        boolean ret = mScaleGestureDetector.onTouchEvent(event);
        if (!mScaleGestureDetector.isInProgress()) {
            ret = mGestureDetector.onTouchEvent(event);
        }
        return ret;
    }

    public interface ToolShowCallback {
        void updateTools();
    }
}
