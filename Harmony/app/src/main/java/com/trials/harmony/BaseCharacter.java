package com.trials.harmony;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by USER on 1/28/2016.
 */
abstract class BaseCharacter {

    // static variables
    protected final static int PRIORITY_BACKWARD = 0;
    protected final static int PRIORITY_FORWARD = 1;
    // field
    protected Point mPos;                // position
    protected Point mSize;               // size
    protected PointF mMove;              // move
    protected float mSpeed;              // variable speed
    protected float mPreviewSpeed;       // preview speed
    protected int mTime;                 // time
    protected boolean mExistFlag;        // exist flag
    protected Point mOriginPos;          // origin position
    protected int mAlpha;                // alpha value
    protected float mScale;              // scale rate
    protected int mType;                 // kind of type
    protected Rect mRect;
    protected int mPriority;
    protected int mAngle;
    protected Point mPreviewPos;
    protected Bitmap mBmp;                // bitmap object to draw the character image.
    protected Image mImage;               // image object
    protected Context mContext;
    protected int mCount;
    /*
        Constructor
    */
    protected BaseCharacter() {
        this.mPos = new Point(0, 0);
        this.mSize = new Point(0, 0);
        this.mMove = new PointF(0,0);
        this.mSpeed = 0.0f;
        this.mPreviewSpeed = 0.0f;
        this.mTime = 0;
        this.mExistFlag = false;
        this.mOriginPos = new Point(0, 0);
        this.mAlpha = 255;
        this.mScale = 1.0f;
        this.mType = 0;
        this.mRect = new Rect(0, 0, 0, 0);
        this.mPreviewPos = new Point(0, 0);
        this.mPriority = PRIORITY_BACKWARD;
        this.mAngle = 0;
        this.mCount = 0;
        this.mImage = null;
        this.mContext = null;
        this.mBmp = null;
    }
    /*
        Load image file.
    */
    protected void LoadCharaImage(String fileName) {
        if (this.mContext != null) {
            this.mBmp = this.mImage.LoadImage(this.mContext, fileName);
        }
    }
    /*
        Release process
    */
    protected void ReleaseBaseChara() {
        if (this.mImage != null) this.mImage = null;
        if (this.mBmp != null) this.mBmp = null;
        if (this.mContext != null) this.mContext = null;
    }
}