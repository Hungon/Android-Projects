package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 3/7/2016.
 */
public class RaceScore {    
    // static variables
    // score images' setting
    private final static Point SCORE_IMAGE_SIZE = new Point(150,50);
    private final static int  SCORE_IMAGE_CHAIN_WIDTH = 150;
    private final static int  SCORE_IMAGE_EXCELLENT_WIDTH = 288;
    // SCORE
    // stay time
    private final static int    SCORE_IMAGE_STAY_TIME = 50;

    // kind of score image
    // SCORE
    private final static int SCORE_TYPE_SCORE = 0;
    // GOOD
    public final static int SCORE_TYPE_GOOD = 1;
    // COOL
    public final static int SCORE_TYPE_COOL = 2;
    // EXCELLENT
    public final static int SCORE_TYPE_EXCELLENT = 3;
    // Chain
    private final static int SCORE_TYPE_CHAIN = 4;
    // kind
    private final static int SCORE_IMAGE_KIND_OF_TYPE = 5;
    // evaluation box
    public final static int SCORE_EVALUATION_TYPE_BOX[] = {
            SCORE_TYPE_GOOD,SCORE_TYPE_COOL,SCORE_TYPE_EXCELLENT
    };
    
    // filed
    private Context         mContext;
    private Image           mImage;
    private Score           mScore[] = new Score[2];
    private BaseCharacter   mScoreImage[] = new BaseCharacter[SCORE_IMAGE_KIND_OF_TYPE];
    private Point           mStartingPos = new Point();
    private int             mArrivePosX;
    private int             mScoreColor[] = new int[2];
    private static int      mTotalPoint;
    private static int      mChainMax;

    /*
        Constructor
    */
    public RaceScore(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        // score class
        for (int i = 0; i < this.mScore.length; i++) {
            this.mScore[i] = new Score(activity, image);
        }
        // for score image
        for (int i = 0; i < this.mScoreImage.length; i++) {
            this.mScoreImage[i] = new BaseCharacter(image);
        }
    }

    /*
        Initialize
    */
    public void InitRaceScore() {
        // load image file
        for (BaseCharacter score: this.mScoreImage) {
            score.LoadCharaImage(this.mContext, "scoreimages");
        }
        // setting
        mTotalPoint = 0;           // total point that succeed action.
        mChainMax = 0;             // the max value that consecutive action

        // get screen size
        Point screen = GameView.GetScreenSize();
        // each positions
        // Score
        Point scorePos = new Point();
        // Chain
        Point chainPos = new Point();
        // to diverge the each score's position from current orientation
        if (screen.x == 480) {      // when orientation is portrait
            scorePos = new Point(150,10);       // score image position
            chainPos = new Point(scorePos.x+SCORE_IMAGE_SIZE.x+10, 10);
            // the starting position for each values
            this.mStartingPos = new Point(screen.x+50, 450);
            // Arrive position
            this.mArrivePosX = 180;
            // score colour
            this.mScoreColor[0] = Score.SCORE_COLOR_RED;
            this.mScoreColor[1] = Score.SCORE_COLOR_BLUE;
        } else if (screen.x == 800) {
            scorePos = new Point(300,10);
            chainPos = new Point(scorePos.x+SCORE_IMAGE_SIZE.x+10, 10);
            // the starting position for each values
            this.mStartingPos = new Point(screen.x+50, 230);
            // Arrive position
            this.mArrivePosX = 350;
            // score colour
            this.mScoreColor[0] = Score.SCORE_COLOR_RED;
            this.mScoreColor[1] = Score.SCORE_COLOR_YELLOW;
        }

        // score image
        // each score images
        for (int i = 0; i < this.mScoreImage.length; i++) {
            this.mScoreImage[i].mSize.x = SCORE_IMAGE_SIZE.x;
            this.mScoreImage[i].mSize.y = SCORE_IMAGE_SIZE.y;
            this.mScoreImage[i].mPos.x = this.mStartingPos.x;
            this.mScoreImage[i].mPos.y = this.mStartingPos.y;
            this.mScoreImage[i].mType = i;
            this.mScoreImage[i].mOriginPos.y = SCORE_IMAGE_SIZE.y * i;
            this.mScoreImage[i].mMoveX = 5.0f;
            this.mScoreImage[i].mScale = 0.75f;
        }
        // each difference setting
        // SCORE position
        this.mScoreImage[0].mPos.x = scorePos.x;
        this.mScoreImage[0].mPos.y = scorePos.y;
        this.mScoreImage[0].mExistFlag = true;
        // Chain position
        this.mScoreImage[4].mPos.x = chainPos.x;
        this.mScoreImage[4].mPos.y = chainPos.y;
        // size
        this.mScoreImage[4].mSize.x = SCORE_IMAGE_CHAIN_WIDTH;
        this.mScoreImage[4].mExistFlag = true;
        // EXCELLENT size
        this.mScoreImage[3].mSize.x = SCORE_IMAGE_EXCELLENT_WIDTH;

        // Score number setting
        // for total point score
        this.mScore[0].InitScore(Score.SCORE_TYPE_GRADATION);
        // for consecutive success count
        this.mScore[1].InitScore(Score.SCORE_TYPE_GRADATION);
    }
    /*
        Update
    */
    public void UpdateRaceScore(int chain, int type, boolean updateF) {

        // update chain max
        // get consecutive success action count.
        if (chain != this.mScore[1].mIndicateNum) {
            this.mScore[1].mIndicateNum = chain;
            if (mChainMax < this.mScore[1].mIndicateNum)
                mChainMax = this.mScore[1].mIndicateNum;
        }
        // when showing action
        if (updateF) {
            // loop to value image
            for (int j = SCORE_TYPE_GOOD; j <= SCORE_TYPE_EXCELLENT; j++) {
                if (this.mScoreImage[j].mExistFlag) continue;
                // if current action and preview action are difference. to draw value image.
                if (this.mScoreImage[j].mType == type) {
                    // update total point
                    mTotalPoint += (100 * type) + (50 * chain);
                    this.mScoreImage[j].mExistFlag = true;
                    break;
                }
            }
        }
        // loop to value image
        for (int j = SCORE_TYPE_GOOD; j <= SCORE_TYPE_EXCELLENT; j++) {
            if (this.mScoreImage[j].mExistFlag) {
                // add move
                if (this.mArrivePosX < this.mScoreImage[j].mPos.x) {
                    this.mScoreImage[j].mPos.x -= this.mScoreImage[j].mMoveX;
                }
                if (this.mScoreImage[j].mPos.x <= this.mArrivePosX) {
                    // count time
                    this.mScoreImage[j].mTime++;
                    this.mScoreImage[j].mPos.x = this.mArrivePosX;
                }
                if (SCORE_IMAGE_STAY_TIME <= this.mScoreImage[j].mTime) {
                    // reset count time
                    this.mScoreImage[j].mTime = 0;
                    // to starting position
                    this.mScoreImage[j].mPos.x = this.mStartingPos.x;
                    this.mScoreImage[j].mExistFlag = false;
                    break;
                }
            }
        }
        // Update total point
        this.mScore[0].mTerminateNum = mTotalPoint;
        // to gradually increase number to total point
        this.mScore[0].mIndicateNum = this.mScore[SCORE_TYPE_SCORE].GraduallyNumber(
                this.mScore[0].mIndicateNum, this.mScore[0].mTerminateNum, 50);
    }
    /*
        Draw
    */
    public void DrawRaceScore() {
        // value image
        for (BaseCharacter score: this.mScoreImage) {
            if (!score.mExistFlag) continue;
            this.mImage.DrawScale(
                    score.mPos.x,
                    score.mPos.y,
                    score.mSize.x,
                    score.mSize.y,
                    score.mOriginPos.x,
                    score.mOriginPos.y,
                    score.mScale,
                    score.mBmp
            );
        }
        // Total point
        this.mScore[0].DrawScore(
                this.mScoreImage[SCORE_TYPE_SCORE].mPos.x-15,
                this.mScoreImage[SCORE_TYPE_SCORE].mPos.y + this.mScoreImage[0].mSize.y,
                this.mScore[0].mIndicateNum,6,this.mScoreColor[0],0.7f
        );
        // Chain
        this.mScore[1].DrawScore(
                this.mScoreImage[SCORE_TYPE_CHAIN].mPos.x,
                this.mScoreImage[SCORE_TYPE_CHAIN].mPos.y + this.mScoreImage[SCORE_TYPE_CHAIN].mSize.y,
                this.mScore[1].mIndicateNum,3,this.mScoreColor[1],0.7f
        );
    }
    /*
        Release
    */
    public void ReleaseRaceScore() {
        // base character class
        for (int i = 0; i < this.mScoreImage.length; i++) {
            this.mScoreImage[i].ReleaseCharaBmp();
            this.mScoreImage[i] = null;
        }
        // score class
        for (int i = 0; i < this.mScore.length; i++) {
            this.mScore[i].ReleaseScore();
            this.mScore[i] = null;
        }
        this.mContext = null;
        this.mImage = null;
    }
    /*
        Increase total point
    */
    public static void UpdateTotalPoint(int point) { mTotalPoint += point; }

    /********************************************************************
     Getter functions
     ********************************************************************/
    /*
        Get total point
     */
    public static int GetTotalPoint() { return mTotalPoint; }
    /*
        Get chain max
    */
    public static int GetChainMax() { return mChainMax; }
    /*
        Get current chain
    */
    public int  GetIndicateChain() { return this.mScore[1].mIndicateNum; }
}