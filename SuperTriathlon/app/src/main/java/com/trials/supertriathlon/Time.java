package com.trials.supertriathlon;

import android.content.Context;

/**
 * Created by USER on 1/30/2016.
 */
public class Time {

    // static variables
    private final static float TIME_SCALE_RATE = 0.5f;
    private final static int   TIME_SPACEX = 10;

    // Filed
    private Context     mContext;
    private Image       mImage;
    private int         mMillisecond;
    private int         mSecond;
    private int         mMinute;
    private int         mHour;
    private Score       mScore;            // score class
    private BaseCharacter mPunctuate;
    private static int  mRecord[] = new int[3];

    /*
        Constructor
     */
    public Time(Context activity, Image image) {
        this.mMillisecond = 0;
        this.mSecond = 0;
        this.mMinute = 0;
        this.mHour = 0;
        this.mContext = activity;
        this.mImage = image;
        // score class
        this.mScore = new Score(activity, image);
        // Base character class to punctuate
        this.mPunctuate = new BaseCharacter(image);
    }

    /*
        Initialize
     */
    public void InitTime() {

        // using image files
        String imageFiles[] = {"punctuate"};
        // load file
        this.mPunctuate.LoadCharaImage(this.mContext, imageFiles[0]);

        // setting
        // punctuate
        this.mPunctuate.mSize.x = Score.PUNCTUATE_SIZE.x;
        this.mPunctuate.mSize.y = Score.PUNCTUATE_SIZE.y;
        this.mPunctuate.mScale = TIME_SCALE_RATE;
        this.mPunctuate.mOriginPos.x = this.mPunctuate.mSize.x * Score.PUNCTUATE_SLASH;

        // Initialize score
        this.mScore.InitScore(Score.SCORE_TYPE_NORMAL);
    }

    /*
        Update
     */
    public void UpdateTime(boolean countF) {
        if (countF) {
            // increase millisecond
            this.mMillisecond++;
            if (60 <= this.mMillisecond) {
                this.mMillisecond = 0;
                this.mSecond++;
            } else if (60 <= this.mSecond) {
                this.mSecond = 0;
                this.mMinute++;
            } else if (60 <= this.mMinute) {
                this.mMinute = 0;
                this.mHour++;
            }
            // get record
            mRecord[0] = this.mMinute;
            mRecord[1] = this.mSecond;
            mRecord[2] = this.mMillisecond;
        }
    }

    /*
        Draw time
     */
    public void DrawTime(int x, int y) {

        // for the millisecond to draw
        double millisecond = (double)this.mMillisecond * 1.67;

        // score size
        float scoreW = Score.GetScoreSize().x * TIME_SCALE_RATE;
        // calculate space.
        float space = scoreW * 2 + TIME_SPACEX;
        // draw minute
        this.mScore.DrawScore(x, y, this.mMinute, 2, Score.SCORE_COLOR_BLACK, TIME_SCALE_RATE);
        // punctuate
        this.mImage.DrawScale(x + (int)space, y,
                this.mPunctuate.mSize.x,
                this.mPunctuate.mSize.y,
                this.mPunctuate.mOriginPos.x,
                this.mPunctuate.mOriginPos.y,
                this.mPunctuate.mScale,
                this.mPunctuate.mBmp);
        // second
        this.mScore.DrawScore(x + (int) space, y, this.mSecond, 2, Score.SCORE_COLOR_BLACK, TIME_SCALE_RATE);
        // punctuate
        this.mImage.DrawScale(x + (int) space * 2, y,
                this.mPunctuate.mSize.x,
                this.mPunctuate.mSize.y,
                this.mPunctuate.mOriginPos.x,
                this.mPunctuate.mOriginPos.y,
                this.mPunctuate.mScale,
                this.mPunctuate.mBmp);
        // millisecond
        this.mScore.DrawScore(x + (int) space * 2, y, (int) millisecond, 2, Score.SCORE_COLOR_BLACK, TIME_SCALE_RATE);

    }

    /*
        Release
     */
    public void ReleaseTime() {
        this.mScore.ReleaseScore();        // score class
        this.mScore = null;
        this.mPunctuate.ReleaseCharaBmp(); // Base character class
        this.mPunctuate = null;
    }
    /*
        Get record
     */
    public static int[] GetTimeRecord() { return mRecord; }
}