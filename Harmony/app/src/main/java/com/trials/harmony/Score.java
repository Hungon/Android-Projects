package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 2/1/2016.
 */
public class Score extends CharacterEx {

    // static variables
    // kind of direction
    public final static int     SCORE_DIRECTION_GRADUALLY   = 0;
    public final static int     SCORE_DIRECTION_ROLLING     = 1;
    public final static int     SCORE_DIRECTION_ALPHA       = 2;
    // filed
    private  int            mIndicateNum;
    private  int            mTerminateNum;
    private int             mDigit;
    private int             mDivide;
    private Utility         mUtility;
    private int             mAddAlpha;
    private int             mFixedInterval;

    /*
        Constructor
    */
    public Score(Context activity, Image image) {
        super(activity,image);
        this.mContext = activity;
        this.mImage = image;
        this.mIndicateNum = 0;
        this.mTerminateNum = 0;
        this.mDigit = 0;
        this.mDivide = 10;
        this.mAddAlpha = 0;
        this.mFixedInterval = 0;
        // to allot the memory
        this.mUtility = new Utility(300);
    }

    /*
        Initialize score
    */
    public void InitScore(
            String fileName, Point pos, Point size, int directionType,
            int terminateNum, int digit
    ) {
        super.InitCharacterEx(fileName,pos.x,pos.y,size.x,size.y,255,1.0f,directionType);
        this.mTerminateNum = terminateNum;
        this.mDigit = digit;
        // to diverge initialization from the direction type.
        if (this.mType == SCORE_DIRECTION_ALPHA) {
            this.mAlpha = 0;
            this.mIndicateNum = this.mTerminateNum;
        }
    }
    /*
        Update score
        return value: true is updating false is finish
    */
    public boolean UpdateScore() {
        // to diverge the process from current direction type
        switch(this.mType) {
            case SCORE_DIRECTION_GRADUALLY:
                return this.IsGraduallyIncreasingNumber();
            case SCORE_DIRECTION_ROLLING:
                return this.IsRollingNumber();
            case SCORE_DIRECTION_ALPHA:
                return this.IsDirectingAlpha();
        }
        return false;
    }
    /*
        Draw number image
    */
    public void DrawScore(int x, int y, int color) {
        // get number
        int num = this.mIndicateNum;
        int rest;
        int numD;
        int digitCount = 0;

        // if got value is negative number, into positive.
        if (num < 0) num *= -1;

        do {
            // calculate digit.
            rest = num % 10;
            num /= 10;
            numD = this.mSize.x * rest;
            float spaceX = (this.mSize.x * this.mScale - 5.0f) * digitCount;
            float posX = x + (this.mSize.x * this.mScale - 5.0f)*this.mDigit;

            this.mImage.DrawAlphaAndScale(
                    (int)posX - (int)spaceX,
                    y,
                    this.mSize.x,
                    this.mSize.y,
                    this.mOriginPos.x + numD,
                    this.mOriginPos.y + (this.mSize.y * color),
                    this.mAlpha,
                    this.mScale,
                    this.mBmp
            );
            // count digit
            digitCount++;
        } while (0 < num || digitCount < this.mDigit);
    }
    public void DrawScore(int color) {
        // get number
        int num = this.mIndicateNum;
        int rest;
        int numD;
        int digitCount = 0;

        // if got value is negative number, into positive.
        if (num < 0) num *= -1;

        do {
            // calculate digit.
            rest = num % 10;
            num /= 10;
            numD = this.mSize.x * rest;
            float spaceX = (this.mSize.x * this.mScale - 5.0f) * digitCount;
            float posX = this.mPos.x + (this.mSize.x * this.mScale - 5.0f)*this.mDigit;

            this.mImage.DrawAlphaAndScale(
                    (int)posX - (int)spaceX,
                    this.mPos.y,
                    this.mSize.x,
                    this.mSize.y,
                    this.mOriginPos.x + numD,
                    this.mOriginPos.y + (this.mSize.y * color),
                    this.mAlpha,
                    this.mScale,
                    this.mBmp
            );
            // count digit
            digitCount++;
        } while (0 < num || digitCount < this.mDigit);
    }
    /*
          Release score
    */
    public void ReleaseScore() { 
        super.ReleaseCharacterEx();
        if (this.mUtility != null) {
            this.mUtility.ReleaseUtility();
            this.mUtility = null;
        }
    }

    /*********************************************************************
        Each direction functions
    *********************************************************************/
    /*
        The score's direction that gradually increase number.
    */
    private boolean IsGraduallyIncreasingNumber() {
        // minimum value
        if (this.mTerminateNum < 0) this.mTerminateNum = 0;
        // limit value
        if (this.mTerminateNum > 100000000) this.mTerminateNum = 999999999;
        if (this.mIndicateNum != this.mTerminateNum) {
            int rest;
            // if indicate number less than terminate number, to increase.
            if (this.mIndicateNum <= this.mTerminateNum) {
                rest = this.mTerminateNum - this.mIndicateNum;
                this.mIndicateNum += rest / this.mDivide;
                // if indicate number nears around terminate number, to substitute.
                if (rest < this.mDivide) {
                    this.mIndicateNum = this.mTerminateNum;
                }
            }
            // if indicate number is bigger than terminate number, to decrease.
            if (this.mIndicateNum >= this.mTerminateNum) {
                rest = this.mIndicateNum - this.mTerminateNum;
                this.mIndicateNum -= rest / this.mDivide;
                if (rest < this.mDivide) {
                    this.mIndicateNum = this.mTerminateNum;
                }
            }
            return true;
        }
        return false;
    }

    /*
        The score's direction that substitution after rolling process a while.
    */
    private boolean IsRollingNumber() {
        if (this.mIndicateNum != this.mTerminateNum) {
            // limit number to draw.
            int limit = 1;
            for (int i = 0; i < this.mDigit; i++) limit *= 10;
            int add = (limit - 1) / 9;
            // rolling process while frame count less than count max.
            if (this.mUtility.ToMakeTheInterval()) {
                this.mIndicateNum += add;
                // limit showing.
                if (limit <= Math.abs(this.mIndicateNum)) this.mIndicateNum = 0;
                // when frame count reached to count max, to substitute.
            } else {
                this.mIndicateNum = this.mTerminateNum;
            }
            return true;
        }
        return false;
    }
    /*
        to increase gradually alpha or decrease that.
    */
    private boolean IsDirectingAlpha() {
        return super.VariableAlpha(this.mAddAlpha,this.mFixedInterval);
    }
    /****************************************************
        Each setter functions
    **************************************************/
    /*
        Set the value to divide the indicate number.
    */
    public void SetGraduallyParameter(int divide) { this.mDivide = divide; }
    /*
        Set fixed interval time to rolling direction
    */
    public void SetFixedInterval(int fixedTime) {
        if (this.mUtility != null) {
            this.mUtility.SetFixedInterval(fixedTime);
        }
    }
    /*
        Set Alpha and fixed interval
    */
    public void SetAlphaAndFixedInterval(int addAlpha, int fixedTime) {
        this.mAddAlpha = addAlpha;
        this.mFixedInterval  = fixedTime;
    }
    void setIndicateNumber(int num) { this.mIndicateNum = num; }
    void setTerminateNumber(int num) { this.mTerminateNum = num; }
}