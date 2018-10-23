package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 2/1/2016.
 */
public class Score extends BaseCharacter {

    // static variables
    public final static int   SCORE_COLOR_WHITE        = 0;
    public final static int   SCORE_COLOR_BLACK        = 1;
    public final static int   SCORE_COLOR_RED          = 2;
    public final static int   SCORE_COLOR_BLUE         = 3;
    public final static int   SCORE_COLOR_GREEN        = 4;
    public final static int   SCORE_COLOR_YELLOW       = 5;
    public final static int   SCORE_COLOR_LIGHT_BLUE   = 6;
    public final static int   SCORE_KIND_OF_COLOR      = 7;
    // size
    private final static Point  SCORE_SIZE = new Point(40, 50);
    // punctuate
    public final static Point  PUNCTUATE_SIZE = new Point(29,50);
    // kind of type
    public final static int    PUNCTUATE_DOT   = 0;
    public final static int    PUNCTUATE_SLASH = 1;
    // color
    public final static int     PUNCTUATE_COLOR_BLACK = 0;
    public final static int     PUNCTUATE_COLOR_WHITE = 1;

    // kind
    public final static int    SCORE_TYPE_NORMAL       = 0;
    public final static int    SCORE_TYPE_GRADATION    = 1;

    // kind of direction
    public final static int     SCORE_DIRECTION_GRADUALLY   = 0;
    public final static int     SCORE_DIRECTION_ROLLING     = 1;

    // filed
    private Image           mImage;
    private Context         mContext;
    public  int             mIndicateNum;
    public  int             mTerminateNum;
    public int             mRollingCount;         // to rolling direction
    private BaseCharacter   mPunctuate;

    /*
        Constructor
    */
    public Score(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        this.mIndicateNum = 0;
        this.mTerminateNum = 0;
        this.mRollingCount = 0;
        // for time score
        this.mPunctuate = new BaseCharacter(image);
    }

    /*
        Initialize score
    */
    public void InitScore(int type) {
        // file
        String fileName[] = {
                "scorenumber1",     // normal
                "scorenumber2",     // gradation
        };
        // load file
        // number score
        this.mBmp = this.mImage.LoadImage(this.mContext, fileName[type]);
        // punctuate for time record
        this.mPunctuate.LoadCharaImage(this.mContext, "punctuate");
        // score number setting
        this.mSize.x = SCORE_SIZE.x;
        this.mSize.y = SCORE_SIZE.y;

        // the punctuate for time score
        this.mPunctuate.mSize.x = PUNCTUATE_SIZE.x;
        this.mPunctuate.mSize.y = PUNCTUATE_SIZE.y;
    }

    /*
        Draw number image
    */
    public void DrawScore(
            int x, int y, int number,
            int digit, int color,
            float scale
    ) {
        // get number
        int num = number;
        int rest = 0;
        int numD = 0;
        int digitCount = 0;

        // if got value is negative number, into positive.
        if (num < 0) num *= -1;

        do {
            // calculate digit.
            rest = num % 10;
            num /= 10;
            numD = this.mSize.x * rest;
            float spaceX = (this.mSize.x * scale - 5.0f) * digitCount;
            float posX = x + (this.mSize.x * scale - 5.0f)*digit;

            this.mImage.DrawScale(
                    (int)posX - (int)spaceX,
                    y,
                    this.mSize.x,
                    this.mSize.y,
                    this.mOriginPos.x + numD,
                    this.mOriginPos.y + (this.mSize.y * color),
                    scale,
                    this.mBmp
            );
            // count digit
            digitCount++;
        } while (num > 0 || digitCount < digit);
    }
    /*
          Release score
    */
    public void ReleaseScore() {
        this.mImage = null;
        this.mContext = null;
        this.ReleaseCharaBmp();
    }

    /*********************************************************************
        Each direction functions
    *********************************************************************/
    /*
        The score's direction that gradually increase number.
    */
    int GraduallyNumber(
            int indicateNumber,
            int terminateNumber,
            int divide
    ) {
        // minimum value
        if (terminateNumber < 0) terminateNumber = 0;
        // limit value
        if (terminateNumber > 100000000) terminateNumber = 999999999;

        if (indicateNumber != terminateNumber) {
            int rest;
            // if indicate number less than terminate number, to increase.
            if (indicateNumber <= terminateNumber) {
                rest = terminateNumber - indicateNumber;
                indicateNumber += rest / divide;
                // if indicate number nears around terminate number, to substitute.
                if (rest < divide) {
                    indicateNumber = terminateNumber;
                }
            }
            // if indicate number is bigger than terminate number, to decrease.
            if (indicateNumber >= terminateNumber) {
                rest = indicateNumber - terminateNumber;
                indicateNumber -= rest / divide;
                if (rest < divide) {
                    indicateNumber = terminateNumber;
                }
            }
        }
        return indicateNumber;
    }

    /*
        The score's direction that substitution after rolling process a while.
    */
    int RollingNumber(
            int indicate,
            int terminate,
            int rollingMax,
            int digit
    ) {
        // count frame
        this.mRollingCount++;
        // limit number to draw.
        int limit = 1;
        for (int i = 0;i < digit; i++) limit *= 10;
        int add = (limit - 1) / 9;
        // rolling process while frame count less than count max.
        if (this.mRollingCount < rollingMax) {
            indicate += add;
            // limit showing.
            if (Math.abs(indicate) >= limit) indicate = 0;
        }
        // when frame count reached to count max, to substitute.
        if (this.mRollingCount >= rollingMax) {
            indicate = terminate;
        }
        return indicate;
    }

    /********************************************************************
        Getter functions
    ********************************************************************/
    /*
        Get score's size
     */
    public static Point GetScoreSize() { return Score.SCORE_SIZE; }
}