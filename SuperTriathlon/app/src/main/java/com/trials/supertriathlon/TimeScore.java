package com.trials.supertriathlon;

import android.content.Context;

/**
 * Created by USER on 2/12/2016.
 */
public class TimeScore {

    // static variables
    // Filed
    private Context     mContext;
    private Image       mImage;
    private BaseCharacter mPunctuate;
    private Score       mTimeScore[] = new Score[3];
    private int         mDirectionTime;
    private int         mDirectionType;

    /*
    Constructor
 */
    public TimeScore(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        this.mDirectionTime = 0;
        this.mDirectionType = 0;
        // Base character class to punctuate
        this.mPunctuate = new BaseCharacter(image);
        // time score
        for (int i = 0; i < this.mTimeScore.length; i++) {
            this.mTimeScore[i] = new Score(activity,image);
        }
    }

    /*
     Initialize time score
  */
    public void InitTimeScore() {
        // using image files
        String imageFiles[] = {"punctuate"};
        // load file
        this.mPunctuate.LoadCharaImage(this.mContext, imageFiles[0]);

        // setting
        // punctuate
        this.mPunctuate.mSize.x = Score.PUNCTUATE_SIZE.x;
        this.mPunctuate.mSize.y = Score.PUNCTUATE_SIZE.y;
        this.mPunctuate.mScale = 1.0f;
        this.mPunctuate.mOriginPos.x = this.mPunctuate.mSize.x * Score.PUNCTUATE_DOT;
        // Time score
        for (int i = 0; i < this.mTimeScore.length; i++) {
            this.mTimeScore[i].InitScore(Score.SCORE_TYPE_GRADATION);
        }
    }

    /*
        Set direction for time score
     */
    public void SetTimeScoreDirection(int minute, int second, int millisecond, int directionType, int directionTime) {
        this.mDirectionTime = directionTime;
        this.mDirectionType = directionType;
        this.mTimeScore[0].mTerminateNum = minute;            // minute
        this.mTimeScore[1].mTerminateNum = second;            // second
        this.mTimeScore[2].mTerminateNum = millisecond;       // millisecond
    }

    /*
        Update direction for time score
     */
    public void UpdateTimeScoreDirection() {
        // gradually direction
        if (this.mDirectionType == Score.SCORE_DIRECTION_GRADUALLY) {
            for (int i = 0; i < this.mTimeScore.length; i++) {
                this.mTimeScore[i].mIndicateNum = this.mTimeScore[i].GraduallyNumber(
                        this.mTimeScore[i].mIndicateNum,
                        this.mTimeScore[i].mTerminateNum, 2
                );
            }
        }// Rolling
        else if (this.mDirectionType == Score.SCORE_DIRECTION_ROLLING) {
            for (int i = 0; i < this.mTimeScore.length; i++) {
                this.mTimeScore[i].mIndicateNum = this.mTimeScore[i].RollingNumber(
                        this.mTimeScore[i].mIndicateNum,
                        this.mTimeScore[i].mTerminateNum,
                        this.mDirectionTime, 2
                );
            }
        }
    }

    /*
        Draw time
    */
    public void DrawTimeScore(int x, int y, float scale, int spaceX, int color) {

        // score size
        float scoreW = Score.GetScoreSize().x * scale;
        // calculate space.
        float space = scoreW * 2 + spaceX;
        // draw minute
        this.mTimeScore[0].DrawScore(x, y, this.mTimeScore[0].mIndicateNum, 2, Score.SCORE_COLOR_RED, scale);
        // punctuate real width
        float punW = this.mPunctuate.mSize.x*scale;
        // punctuate
        this.mImage.DrawScale(x+(int)(space+punW/2) - 5, y,
                this.mPunctuate.mSize.x,
                this.mPunctuate.mSize.y,
                this.mPunctuate.mOriginPos.x,
                this.mPunctuate.mOriginPos.y+(color*this.mPunctuate.mSize.y),
                this.mPunctuate.mScale,
                this.mPunctuate.mBmp);
        // second
        this.mTimeScore[1].DrawScore(x + (int) space, y, this.mTimeScore[1].mIndicateNum, 2, Score.SCORE_COLOR_RED, scale);
        // punctuate
        this.mImage.DrawScale((x+(int)(space*2+punW/2)) - 5, y,
                this.mPunctuate.mSize.x,
                this.mPunctuate.mSize.y,
                this.mPunctuate.mOriginPos.x,
                this.mPunctuate.mOriginPos.y+(color*this.mPunctuate.mSize.y),
                this.mPunctuate.mScale,
                this.mPunctuate.mBmp);
        // millisecond
        this.mTimeScore[2].DrawScore(x + (int) space * 2, y, this.mTimeScore[2].mIndicateNum, 2, Score.SCORE_COLOR_RED, scale);
    }
    /*
        Release time score
     */
    public void ReleaseTimeScore() {
        // Time score
        for (int i = 0; i < this.mTimeScore.length; i++) {
            this.mTimeScore[i].ReleaseScore();
            this.mTimeScore[i] = null;
        }
    }
    /*
        Reset rolling direction
     */
    public void ResetRollingDirection() {
        for (Score score: this.mTimeScore) score.mRollingCount = 0;
    }
}