package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;

/**
 * Created by Kohei Moroi on 7/30/2016.
 */
public class SentenceColour extends CharaBasic implements HasButtons {

    // static variables
    // filed
    public Utility  mInterval;

    // Constructor
    public SentenceColour(Context context, Image image) {
        super(context,image);
        this.mInterval = new Utility();
    }
    /*
        Initialize score of colour for using the play scene.
    */
    public void InitSentence(String imageFile, Point pos, Point size, int alpha, float scale, int type) {
        // to set each parameter
        super.init(
                imageFile,
                pos, size,
                alpha,scale,type);
    }
    /*
        Update the particular position that update by Bezier
        when is updating bezier, to return true.
    */
    public boolean UpdateSentence() {
        boolean res = false;
        // When to get the count that recognized correct count,
        // to draw the bezier
        if (this.mChara.mExistFlag) {
            res = true;
            // When current object's position reached to end position,
            // to appear gradually by variable alpha.
            if (super.movingByBezier()) {
                // when to appear clearly,
                // not to show
                if (this.mChara.mAlpha == 255) {
                    if (this.mUtility.ToMakeTheInterval(300)) {
                        if (0 < this.mVariableAlpha) {
                            this.mVariableAlpha *= -1;
                        }
                    }
                } else if (this.mChara.mAlpha < 5) {
                    this.ResetParameter();
                }
                this.mChara.VariableAlpha(this.mVariableAlpha, this.mFixedIntervalForAlpha);
            }
            // limit scale
            this.mChara.mScale = (float) this.mChara.mAlpha / 255;
            if (this.mMaxScale <= this.mChara.mScale) {
                this.mChara.mScale = this.mMaxScale;
            }
            // When score's scale reached to max value,
            // to move character's position by bezier after fixed interval.
            if (this.mReachedEndPosition && this.mBezierIndex == 0) {
                if (this.mInterval.ToMakeTheInterval(300)) {
                    this.mReachedEndPosition = false;
                    this.mBezierIndex = 1;
                    Point charaPos = RecognitionCharacter.GetPosition();
                    PointF charaSize = RecognitionCharacter.GetWholeSize();
                    super.setBezierCondition(charaPos,charaSize);
                }
            }
        }
        return res;
    }
    /*
        Create the object which will move by bezier
    */
    public boolean CreateObject(Point bezierPos, int type, int originY) {
        if (this.mChara.mExistFlag) return false;
        super.setObject(type,this.mChara.mSize.y*originY);
        // set bezier
        super.setBezierCondition(bezierPos,new PointF(0,0));
        return true;
    }
    /*
        Reset all parameter to update
    */
    private void ResetParameter() {
        super.resetParameterForBezier();
        this.mChara.mAlpha = this.mStartingAlpha;
        this.mChara.mScale = 0.0f;
        this.mChara.mExistFlag = false;
        this.mChara.mType = BUTTON_EMPTY;
        // when the current variable is negative,
        // to be return number that positive.
        if (this.mVariableAlpha < 0) this.mVariableAlpha *= -1;
        this.mUtility.ResetInterval();
    }
    /*
        Get whole size
    */
    public PointF GetWholeSize() {
        return new PointF(
                this.mChara.mSize.x*this.mChara.mScale,
                this.mChara.mSize.y*this.mChara.mScale);
    }
    /*
        Get current index for bezier
    */
    public int GetCurrentBezierIndex() { return this.mBezierIndex; }
    /*
        When the current position reached the end position,
        to return true
    */
    public boolean GetEndPosition() { return this.mReachedEndPosition; }
    /*
        Get the type
    */
    public int GetType() { return this.mChara.mType; }

}
