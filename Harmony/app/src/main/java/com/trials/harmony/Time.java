package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 1/30/2016.
 */
public class Time implements HasScoreImage {

    // static variables
    private final static float TIME_SCALE_RATE = 0.5f;
    private final static int   TIME_SPACE_X = 10;
    // punctuate
    private final static Point  PUNCTUATE_SIZE = new Point(29,50);
    private final static int    PUNCTUATE_DOT   = 0;
    private final static int    PUNCTUATE_SLASH = 1;
    private final static int    PUNCTUATE_COLOUR_BLACK = 0;
    private final static int    PUNCTUATE_COLOUR_WHITE = 1;
    // Filed
    private Image       mImage;
    private int         mMillisecond;
    private int         mSecond;
    private int         mMinute;
    private Score       mScore[];
    private CharacterEx mPunctuate[];
    private static int  mTime[];

    public Time(Context context, Image image) {
        this.mImage = image;
        // to time
        this.mScore = new Score[3];
        for (int i = 0; i < this.mScore.length; i++) {
            this.mScore[i] = new Score(context, image);
        }
        // Base character class to punctuate
        this.mPunctuate = new CharacterEx[2];
        for (int i = 0; i < this.mPunctuate.length; i++) {
            this.mPunctuate[i] = new CharacterEx(context,image);
        }
        mTime = new int[3];
        for (int i = 0; i < mTime.length; i++) mTime[i] = 0;
    }

    public void InitTime(int x, int y, int numberType, int directionType) {
        // time
        for (Score t: this.mScore) {
            t.InitScore(
                    FILE_SCORE_NUMBER[numberType],
                    new Point(x, y), SCORE_NUMBER_SIZE,
                    directionType, 0, 2);
            t.mScale = TIME_SCALE_RATE;
            t.SetAlphaAndFixedInterval(2,2);
        }
        // score size
        float scoreW = SCORE_NUMBER_SIZE.x * TIME_SCALE_RATE;
        // calculate space.
        float space = scoreW * 2 + TIME_SPACE_X;
        for (int i = 0; i < this.mPunctuate.length; i++) {
            this.mPunctuate[i].InitCharacterEx(
                    "punctuate",
                    this.mScore[0].mPos.x + ((int) space * (i + 1)), this.mScore[0].mPos.y,
                    PUNCTUATE_SIZE.x, PUNCTUATE_SIZE.y,
                    PUNCTUATE_SIZE.x, PUNCTUATE_SIZE.y,     // the image is / and colour is white
                    0, TIME_SCALE_RATE, PUNCTUATE_SLASH);
        }
    }
    public void UpdateTime() {
        int duration = Sound.GetDuration()-Sound.GetCurrentPlaybackPosition();
        mTime[0] = duration/60000;
        duration -= mTime[0]*60000;
        mTime[1] = duration/1000;
        duration -= mTime[1]*1000;
        mTime[2] = duration%100;
        // set each number
        int time[] = {mTime[0],mTime[1],mTime[2]};
        for (int i = 0; i < this.mScore.length; i++) {
            this.mScore[i].setIndicateNumber(time[i]);
            this.mScore[i].UpdateScore();           // update direction
        }
        for (CharacterEx p:this.mPunctuate) p.VariableAlpha(2,2);
    }
    public void DrawTime() {
        int x = this.mScore[0].mPos.x, y = this.mScore[0].mPos.y;
        // score size
        float scoreW = SCORE_NUMBER_SIZE.x * TIME_SCALE_RATE;
        // calculate space.
        float space = scoreW * 2 + TIME_SPACE_X;
        for (int i = 0; i < 3; i++) {
            this.mScore[i].DrawScore(x+((int)space*i), y, SCORE_NUMBER_COLOR_WHITE);
            if (i == 2) break;
            // punctuate
            this.mPunctuate[i].DrawCharacterEx();
        }
    }
    public void ReleaseTime() {
        for (int i = 0; i < this.mScore.length; i++) {
            this.mScore[i].ReleaseScore();
            this.mScore[i] = null;
        }
        for (int i = 0; i < this.mPunctuate.length; i++) {
            this.mPunctuate[i].ReleaseCharacterEx();
            this.mPunctuate[i] = null;
        }
    }
    static int[] getTime() { return mTime; }
}
