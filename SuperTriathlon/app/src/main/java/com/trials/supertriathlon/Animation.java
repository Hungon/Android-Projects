package com.trials.supertriathlon;

import android.graphics.Point;

/**
 * Created by USER on 1/31/2016.
 */
public class Animation {

    // filed
    public Point        mStartPic;
    public Point        mSize;
    public int          mCountMax;
    public int          mCount;
    public int          mFrame;
    public int          mTime;
    public int          mType;
    public boolean      mReverseFlag;
    public int          mDirection;
    protected Point     mOriginPos;

    /*
        Constructor
     */
    public Animation() {
        this.mStartPic = new Point(0, 0);
        this.mSize = new Point(0, 0);
        this.mCountMax = 0;
        this.mCount = 0;
        this.mFrame = 0;
        this.mTime = 0;
        this.mType = 0;
        this.mOriginPos = new Point(0,0);
        this.mReverseFlag = false;
        this.mDirection = 0;
    }

    /*
        Initialize animation value
     */
    public void SetAnimation(
            int srcX, int srcY,
            int w, int h,
            int countMax,
            int frame,
            int type
    ) {
        this.mStartPic.x = srcX;
        this.mStartPic.y = srcY;
        this.mSize.x = w;
        this.mSize.y = h;
        this.mCountMax = countMax;
        this.mFrame = frame;
        this.mType = type;
    }

    /*
        Update animation process.
     */
    public boolean UpdateAnimation(Point src, boolean reverseFlag) {

        boolean ret = true;
        this.mTime++;          // count time

        if (!this.mReverseFlag) {
            if (this.mTime > this.mFrame) {
                this.mCount++;         // next animation
                this.mTime = 0;        // reset time.
            }
            if (this.mCount >= this.mCountMax) {
                if (!reverseFlag) {
                    this.mCount = 0;
                    ret = false;
                } else {
                    this.mReverseFlag = true;
                }
            }
        }
        if(this.mReverseFlag) {      // subtract count to reverse animation.
            if(this.mTime > this.mFrame) {
                this.mCount--;
                this.mTime = 0;
            }
            if (this.mCount <= 0) {
                ret = this.mReverseFlag = false;
            }
        }
        // update animation
        src.x = this.mStartPic.x + (this.mCount * this.mSize.x);
        src.y = this.mStartPic.y + (this.mDirection * this.mSize.y);
        return ret;
    }

    /*
        Rest animation
     */
    public void ResetAnimation() {
        this.mStartPic.x = 0;
        this.mStartPic.y = 0;
        this.mCount = 0;
        this.mTime = 0;
        this.mOriginPos.x = 0;
        this.mOriginPos.y = 0;
        this.mReverseFlag = false;
        this.mDirection = 0;
    }
    /*
        All reset the setting
    */
    public void ResetEveryAnimationSetting() {
        this.mStartPic = new Point(0, 0);
        this.mSize = new Point(0, 0);
        this.mCountMax = 0;
        this.mCount = 0;
        this.mFrame = 0;
        this.mTime = 0;
        this.mType = 0;
        this.mOriginPos = new Point(0,0);
        this.mReverseFlag = false;
        this.mDirection = 0;
    }
}