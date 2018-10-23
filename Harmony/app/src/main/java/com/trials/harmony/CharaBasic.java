package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by Kohei Moroi on 7/30/2016.
 */
abstract class CharaBasic {
    // static variables
    // filed
    protected CharacterEx mChara;
    protected Point[]     mBezierPosition;
    protected Point       mStartingPosition;
    protected int         mStartingAlpha;
    protected Point       mTerminatePosition;
    protected Utility     mUtility;
    protected int         mVariableAlpha;
    protected int         mFixedIntervalForAlpha;
    protected int         mTypeToCreate;
    protected float       mMaxScale;
    protected Point[]     mFixedBezierPosition;
    protected int         mBezierIndex;
    protected boolean     mReachedEndPosition;
    private CharacterEx.SMOOTH_MOVE mSmoothingDirection;

    // Constructor
    CharaBasic(Context context, Image image) {
        // to allot the memory
        this.mUtility = new Utility();
        this.mStartingPosition = new Point();
        this.mTerminatePosition = new Point();
        this.mReachedEndPosition = false;
        this.mChara = new CharacterEx(context,image);
        this.mSmoothingDirection = null;
    }
    protected void init(String imageFile, Point pos, Point size, int alpha, float scale, int type) {
        // to set each parameter
        this.mChara.InitCharacterEx(
                imageFile,
                pos.x,pos.y,
                size.x,size.y,
                alpha,scale,type);
        // to set starting position
        this.mStartingPosition.x = pos.x;
        this.mStartingPosition.y = pos.y;
        this.mStartingAlpha = alpha;
        this.mChara.mExistFlag = false;
        this.mUtility.ResetInterval();
        this.mTypeToCreate = -1;
        this.mMaxScale = 1.0f;
        this.mChara.mSpeed = 5.0f;
        this.mBezierIndex = 0;
        this.mReachedEndPosition = false;
    }
    protected boolean movingByBezier() {
        boolean res = false;
        if (this.mChara.mPos.y < this.mTerminatePosition.y) {
            this.mChara.UpdateBezier(this.mBezierPosition);
            this.mReachedEndPosition = false;
            res = true;
        } 
        if (this.mTerminatePosition.y < this.mChara.mPos.y) {
            this.mChara.mPos.x = this.mTerminatePosition.x;
            this.mChara.mPos.y = this.mTerminatePosition.y;
            // as reached the end position
            this.mReachedEndPosition = true;
        }
        return res;
    }
    protected void Draw() {
        this.mChara.DrawCharacterEx();
    }
    protected void Release() {
        if (this.mChara != null) {
            this.mChara.ReleaseCharacterEx();
            this.mChara = null;
        }
        if (this.mBezierPosition != null) {
            this.mBezierPosition = null;
        }
        if (this.mFixedBezierPosition != null) {
            this.mFixedBezierPosition = null;
        }
        if (this.mTerminatePosition != null) this.mTerminatePosition = null;
        if (this.mUtility != null) this.mUtility = null;
        if (this.mStartingPosition != null) this.mStartingPosition = null;
    }
    protected void setBezierCondition(Point dst,PointF dstSize) {
        int max = 5;
        Point bezier[] = new Point[max];
        // get center position of destination object
        Point charaCenter = new Point(
                dst.x + ((int)dstSize.x >> 1),
                dst.y + ((int)dstSize.y >> 1));
        // get center position of the starting object
        Point colourPos = new Point(
                this.mChara.mPos.x+(this.mChara.mSize.x>>1),
                this.mChara.mPos.y+(this.mChara.mSize.y>>1));
        Point differencePos = new Point(
                charaCenter.x - colourPos.x,
                charaCenter.y - colourPos.y);
        // Bezier coordination
        for (int j = 0; j < bezier.length; j++) bezier[j] = new Point();
        // add value
        Point addPos = new Point(
                differencePos.x / max,
                differencePos.y / max);
        for (int j = 0; j < bezier.length; j++) {
            if (j == max - 1) {
                // eventually, to substitute the center position of character to the last of bezier position.
                bezier[j].x = charaCenter.x;
                bezier[j].y = charaCenter.y;
                break;
            }
            bezier[j].x = colourPos.x + (addPos.x * j);
            bezier[j].y = colourPos.y + (addPos.y * (j - 1));
        }
        this.setBezier(bezier);
    }
    /*
        Set add alpha value and fixed interval time
    */
    protected void setVariableAlpha(int addAlpha, int fixedTime) {
        this.mVariableAlpha = addAlpha;
        this.mFixedIntervalForAlpha = fixedTime;
    }

    /*
        Set bezier setting
    */
    private void setBezier(Point[] pos) {
        this.mBezierPosition = new Point[pos.length];
        System.arraycopy(pos,0,this.mBezierPosition,0,pos.length);
        // set end position
        this.mTerminatePosition.x = pos[pos.length-1].x;
        this.mTerminatePosition.y = pos[pos.length-1].y;
        // reset time
        this.mChara.ResetMillisecond();
    }
    protected void setObject(int type, int srcY) {
        this.mChara.mExistFlag = true;
        this.mChara.mType = type;
        this.mChara.mOriginPos.y = srcY;
        this.mTypeToCreate = type;
    }
    public void setSmoothingDirection(CharacterEx.SMOOTH_MOVE direction) {this.mSmoothingDirection = direction;}

    protected void resetParameterForBezier() {
        this.mChara.mPos.x = this.mStartingPosition.x;
        this.mChara.mPos.y = this.mStartingPosition.y;
        this.mChara.ResetMillisecond();
        this.mBezierIndex = 0;
        this.mReachedEndPosition = false;
    }

}