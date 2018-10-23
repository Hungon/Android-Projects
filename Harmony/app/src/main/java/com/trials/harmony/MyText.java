package com.trials.harmony;

import android.content.Context;
import android.icu.util.DateInterval;

/**
 * Created by Kohei Moroi on 8/25/2016.
 */
public class MyText extends CharacterEx {
    private String mCurrentText;
    private int mColour;
    private int mVariableAlpha;
    private SMOOTH_MOVE mSmoothingDirection;
    public Utility mUtility;
    private int mIntervalForAlpha;

    public MyText(Context context, Image image) {
        super(context,image);
        this.mCurrentText = "";
        this.mVariableAlpha = 0;
        this.mUtility = new Utility();
        this.mSmoothingDirection = null;
        this.mIntervalForAlpha = 2;
    }
    public void InitMyText(String mes,int x, int y, int size, int colour, int alpha, int type) {
        this.mCurrentText = mes;
        this.mPos.x = x;
        this.mPos.y = y;
        this.mSize.y = size;
        this.mColour = colour;
        this.mAlpha = alpha;
        this.mType = type;
        this.mExistFlag = true;
    }
    boolean UpdateMyTextAlpha() {
        return this.VariableAlphaWhenUpToZeroNoExistence(this.mVariableAlpha,this.mIntervalForAlpha);
    }
    boolean UpdateMyTextSmoothing() {
        return this.VariableSmoothing(this.mSmoothingDirection);
    }
    public void DrawMyTextByAlpha() {
        if (!this.mExistFlag) return;
        this.mImage.drawText(this.mCurrentText,this.mPos.x,this.mPos.y,this.mSize.y,this.mColour,this.mAlpha);
    }
    public void ReleaseMyText() {
        this.mCurrentText = null;
        this.ReleaseCharacterEx();
        if (this.mUtility != null) {
            this.mUtility.ReleaseUtility();
            this.mUtility = null;
        }
    }
    public void SetVariableAlpha(int alpha) { this.mVariableAlpha = alpha; }
    void setIntervalForAlpha(int interval) { this.mIntervalForAlpha = interval; }
    public void SetTextWidth(int w) { this.mSize.x = w; }
    public void setSmoothingDirection(SMOOTH_MOVE direction) {this.mSmoothingDirection = direction;}
    public boolean GetExistence() { return this.mExistFlag; }
    public String GetCurrentText() { return (this.mCurrentText==null)?"":this.mCurrentText; }
    public int GetVariableAlpha() { return this.mVariableAlpha; }
}