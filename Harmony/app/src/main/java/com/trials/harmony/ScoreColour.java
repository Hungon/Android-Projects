package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by Kohei Moroi on 7/25/2016.
 */
public class ScoreColour {
    // static variables
    // filed
    private CharacterEx mScore;
    private Point[]     mBezierPosition;
    private Point       mStartingPosition;
    private int         mStartingAlpha;
    private Point       mTerminatePosition;
    private Utility     mUtility;
    public  Utility     mInterval;
    private int         mVariableAlpha;
    private int         mFixedIntervalForAlpha;
    private int         mTypeToCreate;
    private float       mMaxScale;
    private Point[]     mFixedBezierPosition;
    private boolean     mBezierIsDefault;

    // Constructor
    public ScoreColour(Context context, Image image) {
        // to allot the memory
        this.mScore = new CharacterEx(context,image);
        this.mUtility = new Utility();
        this.mInterval = new Utility();
        this.mStartingPosition = new Point();
        this.mTerminatePosition = new Point();
        this.mBezierIsDefault = false;
    }
    /*
        Initialize score of colour for using the play scene.
    */
    public void InitScore(String imageFile, Point pos, Point size, int alpha, float scale, int type) {
        // to set each parameter
        this.mScore.InitCharacterEx(
                imageFile,
                pos.x,pos.y,
                size.x,size.y,
                alpha,scale,type);
        // to set starting position
        this.mStartingPosition.x = pos.x;
        this.mStartingPosition.y = pos.y;
        this.mStartingAlpha = alpha;
        this.mScore.mExistFlag = false;
        this.mUtility.ResetInterval();
        this.mTypeToCreate = -1;
        this.mMaxScale = 1.0f;
        this.mScore.mSpeed = 5.0f;
    }
    /*
        Update the particular position that update by Bezier
        when is updating bezier, to return true.
    */
    public boolean UpdateScore(boolean upToNoExistence) {
        boolean res = false;
        // When to get the count that recognized correct count,
        // to draw the bezier
        if (this.mScore.mExistFlag) {
            if (this.mScore.mPos.y < this.mTerminatePosition.y) {
                this.mScore.UpdateBezier(this.mBezierPosition);
                res = true;
            } else if (this.mTerminatePosition.y <= this.mScore.mPos.y){
                this.mScore.mPos.x = this.mTerminatePosition.x;
                this.mScore.mPos.y = this.mTerminatePosition.y;
            }
            // When current object's position reached to end position,
            // to appear gradually in order to the variable alpha.
            if (this.mScore.mPos.x == this.mTerminatePosition.x &&
                this.mScore.mPos.y == this.mTerminatePosition.y) {
                // when to appear clearly,
                // not to show
                if (this.mScore.mAlpha == 255) {
                    // when direction way is finished, to return
                    if (!upToNoExistence) return false;
                    if (this.mUtility.ToMakeTheInterval(300)) {
                        if (0 < this.mVariableAlpha) {
                            this.mVariableAlpha *= -1;
                        }
                    }
                } else if (this.mScore.mAlpha < 5) {
                    this.ResetParameterForBezier();
                }
                this.mScore.VariableAlpha(this.mVariableAlpha,this.mFixedIntervalForAlpha);
            }
            // limit scale
            this.mScore.mScale = (float) this.mScore.mAlpha / 255;
            if (this.mMaxScale <= this.mScore.mScale) this.mScore.mScale = this.mMaxScale;
        }
        return res;
    }
    /*
        Draw each score of colour which are correct in Recognizer manager class dedicated
    */
    public void DrawScore() {
        this.mScore.DrawCharacterEx();
    }
    /*
        Release
    */
    public void ReleaseScore() {
        if (this.mScore != null) {
            this.mScore.ReleaseCharacterEx();
            this.mScore = null;
        }
        if (this.mBezierPosition != null) {
            this.mBezierPosition = null;
        }
        if (this.mFixedBezierPosition != null) {
            this.mFixedBezierPosition = null;
        }
        if (this.mTerminatePosition != null) this.mTerminatePosition = null;
        if (this.mUtility != null) this.mUtility = null;
        if (this.mInterval != null) this.mInterval = null;
        if (this.mStartingPosition != null) this.mStartingPosition = null;
    }
    /*
        Create the object
    */
    public boolean CreateObject(int kind) {
        if (this.mScore.mExistFlag) return false;
        this.mScore.mExistFlag = true;
        this.mScore.mType = kind;
        this.mScore.mOriginPos.y = this.mScore.mSize.y*kind;
        this.mTypeToCreate = -1;
        return true;
    }
    /*
        Reset all parameter
    */
    private void ResetParameterForBezier() {
        this.mScore.mPos.x = this.mStartingPosition.x;
        this.mScore.mPos.y = this.mStartingPosition.y;
        this.mScore.mAlpha = this.mStartingAlpha;
        this.mScore.mScale = 0.0f;
        this.mScore.mExistFlag = false;
        this.mScore.mType = -1;
        this.mScore.ResetMillisecond();
        // bezier coordination
        this.SetBezier(this.mFixedBezierPosition);
        // the bezier coordination was changed.
        this.mBezierIsDefault = true;
        // when the current variable is negative,
        // to be return number that positive.
        if (this.mVariableAlpha < 0) {
            this.mVariableAlpha *= -1;
        }
        this.mUtility.ResetInterval();
    }
    /*
        To move to the position
    */
    public void ToMoveToSpecifiedPosition(Point pos) { this.mScore.ToMoveToSpecifiedPosition(pos); }
    /*
        Set bezier setting
    */
    public void SetBezier(Point[] pos) {
        this.mBezierPosition = new Point[pos.length];
        System.arraycopy(pos,0,this.mBezierPosition,0,pos.length);
        // set end position
        this.mTerminatePosition.x = pos[pos.length-1].x;
        this.mTerminatePosition.y = pos[pos.length-1].y;
        // reset time
        this.mScore.ResetMillisecond();
    }
    /*
        Set default bezier coordination
    */
    public void SetFixedBezier(Point[] pos) {
        this.mFixedBezierPosition = new Point[pos.length];
        System.arraycopy(pos,0,this.mFixedBezierPosition,0,pos.length);
        // the bezier coordination was changed.
        this.mBezierIsDefault = true;
    }
    /*
        Set add alpha value and fixed interval time
    */
    public void SetVariableAlphaAndFixedInterval(int addAlpha, int fixedTime) {
        this.mVariableAlpha = addAlpha;
        this.mFixedIntervalForAlpha = fixedTime;
    }
    /*
        Set type to create
    */
    public void SetTypeToCreate(int type) { this.mTypeToCreate = type; }
    /*
        Set max scale
    */
    public void SetMaxScale(float scale) {
        this.mMaxScale = scale;
    }
    /*
        Set position
    */
    public void SetPosition(int x, int y) {
        this.mScore.mPos.x = x;
        this.mScore.mPos.y = y;
    }
    /*
        Was changed bezier default value
    */
    public void WasChangedBezier() { this.mBezierIsDefault = false; }
    /*
        Get existence
    */
    public boolean GetExistence() { return this.mScore.mExistFlag; }
    /*
        Get type to create
    */
    public int GetTypeToCreate() { return this.mTypeToCreate; }
    /*
        Get scale
    */
    public float GetScale() { return this.mScore.mScale; }
    /*
        Get max scale
    */
    public float GetMaxScale() { return this.mMaxScale; }
    /*
        Get position
    */
    public Point GetPosition() { return this.mScore.mPos; }
    /*
        Get center position
    */
    public Point GetCenterPosition() {
        PointF size = new PointF(
                this.mScore.mSize.x*this.mScore.mScale,
                this.mScore.mSize.y*this.mScore.mScale);
        return new Point(
                this.mScore.mPos.x+((int)size.x>>1),
                this.mScore.mPos.y+((int)size.y>>1));
    }
    /*
        Get the value that bezier is default or not
        true is default.
    */
    public boolean GetBezierIsDefault() { return this.mBezierIsDefault; }
}