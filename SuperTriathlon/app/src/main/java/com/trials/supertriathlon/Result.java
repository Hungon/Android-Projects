package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 2/3/2016.
 */
public class Result extends Scene implements HasProperties, HasRecords {

    // static variables
    // Result title
    // size
    public final static Point      RESULT_TOP_TITLE_SIZE = new Point(125,55);
    // Mode for level
    public final static int        RESULT_TITLE_LEVEL_WIDTH = 120;
    public final static float      RESULT_LEVEL_SCALE_RATE = 0.6f;

    // Record title
    // size
    public final static Point      RESULT_RECORD_TITLE_SIZE = new Point(250,55);
    // terminate position
    public final static int        RESULT_RECORD_TITLE_ARRIVE_POSITION_X = 100;
    // space-Y
    public final static int        RESULT_RECORD_TITLE_SPACE_Y = 80;

    // Score
    // scale rate
    private final static float      SCORE_SCALE_RATE = 0.9f;
    // bonus point scale rate
    private final static float      SCORE_BONUS_SCALE_RATE = 0.5f;
    // New record
    private final static Point      NEW_RECORD_SIZE = new Point(65,35);
    private final static Point      NEW_RECORD_SPACE = new Point(15,10);

    // Grade
    public final static Point      GRADE_SIZE = new Point(230,55);
    public final static int        GRADE_POSITION_X = 15;

    // Time point
    private final static int[]      TIME_POINT = {15000,25000,35000};

    // kind of grade
    public final static int        GRADE_PERFECT   = 0;
    public final static int        GRADE_ORDINARY  = 1;
    public final static int        GRADE_TRAINEE   = 2;

    // Score direction setting
    public final static int        SCORE_GRADUALLY_INTERVAL_FOR_TOTAL = 50;
    public final static int        SCORE_GRADUALLY_INTERVAL_FOR_CHAIN = 100;
    public final static int        SCORE_ROLLING_TIME_FOR_TIME        = 300;
    public final static int        SCORE_GRADUALLY_INTERVAL_FOR_BONUS = 50;

    // filed
    private Image           mImage;                              // image object
    private Context         mContext;                           // activity
    private Sound           mSound;                              // sound object to play SE or background music
    private String          mBGMfileName;
    private BaseCharacter   mTitles[] = new BaseCharacter[5];
    private Score           mScore[] = new Score[2];
    private BaseCharacter   mGrade[] = new BaseCharacter[2];
    private TimeScore       mTimeScore;
    private Menu            mMenu;
    private BaseCharacter   mNewRecord;
    private Animation       mNewRecordAni[] = new Animation[RECORD_KIND];
    private BaseCharacter   mLevel;
    // the flag that renewal record
    SystemManager.RECORD_RENEWAL   mRenewalF[] = new SystemManager.RECORD_RENEWAL[RECORD_KIND];
    // the bonus point
    private Score   mBonus[] = new Score[3];
    // + image for score
    private BaseCharacter   mPlus;

    /*
        Constructor
     */
    public Result(Context context, Image image) {
        // get image object
        this.mImage = image;
        // get activity
        this.mContext = context;
        // set sound
        this.mSound = new Sound(this.mContext);
        // base character
        for (int i = 0; i < this.mTitles.length; i++) {
            this.mTitles[i] = new BaseCharacter(image);
        }
        // score class
        for (int i = 0; i < this.mScore.length; i++) {
            this.mScore[i] = new Score(context, image);
        }
        // time score
        this.mTimeScore = new TimeScore(this.mContext, this.mImage);
        // menu
        this.mMenu = new Menu(context, image);
        // New Record
        this.mNewRecord = new BaseCharacter(image);
        for (int i = 0; i < this.mNewRecordAni.length; i++) {
            this.mNewRecordAni[i] = new Animation();
        }
        // Level
        this.mLevel = new BaseCharacter(image);
        // Bonus
        for (int i = 0; i < this.mBonus.length; i++) {
            this.mBonus[i] = new Score(context,image);
        }
        // + image
        this.mPlus = new BaseCharacter(image);
        // Grade
        for (int i = 0; i < this.mGrade.length; i++) {
            this.mGrade[i] = new BaseCharacter(image);
        }
    }

    /*
        Initialize
     */
    public int Init() {
        SystemManager systemManager = new SystemManager(this.mContext);
        // using BGM
        this.mBGMfileName = "result";
        // using image file
        String imageFile = "resulttitles";
        // load image
        for (BaseCharacter ch : this.mTitles) ch.LoadCharaImage(this.mContext, imageFile);
        // New record
        this.mNewRecord.LoadCharaImage(this.mContext, "newrecord");
        // Level
        this.mLevel.LoadCharaImage(this.mContext, "level");
        // +
        this.mPlus.LoadCharaImage(this.mContext, "plus");
        // Grade
        for (BaseCharacter ch : this.mGrade) ch.LoadCharaImage(this.mContext, "grade");

        // get each records
        // total point
        this.mScore[0].mTerminateNum = RaceScore.GetTotalPoint();
        // chain
        this.mScore[1].mTerminateNum = RaceScore.GetChainMax();
        // time is common record
        int getTime[] = Time.GetTimeRecord();

        // Set time score
        this.mTimeScore.InitTimeScore();
        this.mTimeScore.SetTimeScoreDirection(getTime[0], getTime[1], getTime[2], Score.SCORE_DIRECTION_ROLLING, SCORE_ROLLING_TIME_FOR_TIME);

        // Save records that compare current record with best record.
        int currentRecord[] = new int[RECORD_KIND];
        // time record to millisecond
        int time = getTime[0] * 60 * 100 + getTime[1] * 100 + getTime[2];
        // Total point
        int level = Play.GetGameLevel();
        // for point
        int timeP = time;
        // when current stage is sea, to divide the subtraction value.
        int stage = Play.GetCurrentStageNumber();
        if (stage == STAGE_SEA) timeP /= 2;

        // Set each bonus point
        // chain
        this.mBonus[2].mTerminateNum = this.mScore[1].mTerminateNum * 100;
        // time
        this.mBonus[1].mTerminateNum = TIME_POINT[level] - timeP;
        // total bonus point
        this.mBonus[0].mTerminateNum = this.mBonus[1].mTerminateNum + this.mBonus[2].mTerminateNum;

        // total point
        this.mScore[0].mTerminateNum = currentRecord[RECORD_TOTAL] = this.mScore[0].mTerminateNum + this.mBonus[0].mTerminateNum;
        // Time
        currentRecord[RECORD_TIME] = time;
        // Chain max
        currentRecord[RECORD_CHAIN_MAX] = this.mScore[1].mTerminateNum;

        // update the best record and to get the flag to renewal
        this.mRenewalF = systemManager.updateBestRecord(stage,level,currentRecord);

        Point screen = GameView.GetScreenSize();
        // Setting
        // Result title
        this.mTitles[0].mSize.x = RESULT_TOP_TITLE_SIZE.x;
        this.mTitles[0].mSize.y = RESULT_TOP_TITLE_SIZE.y;
        this.mTitles[0].mPos.x = (screen.x - RESULT_TOP_TITLE_SIZE.x) >> 1;
        this.mTitles[0].mPos.y = 50;
        this.mTitles[0].mExistFlag = true;

        // Each record titles
        for (int i = 1; i < 4; i++) {
            this.mTitles[i].mPos.x = screen.x + 50;
            this.mTitles[i].mPos.y = 85 + ((RESULT_RECORD_TITLE_SIZE.y + RESULT_RECORD_TITLE_SPACE_Y) * i);
            this.mTitles[i].mSize.x = RESULT_RECORD_TITLE_SIZE.x;
            this.mTitles[i].mSize.y = RESULT_RECORD_TITLE_SIZE.y;
            this.mTitles[i].mExistFlag = true;
            this.mTitles[i].mMoveX = 4.0f;
            this.mTitles[i].mOriginPos.y = (RESULT_RECORD_TITLE_SIZE.y * i);
            this.mTitles[i].mScale = SCORE_SCALE_RATE;
        }
        // Level mode title
        this.mTitles[4].mPos.x = this.mTitles[0].mPos.x + this.mTitles[0].mSize.x;
        this.mTitles[4].mPos.y = this.mTitles[0].mPos.y + this.mTitles[0].mSize.y + 5;
        this.mTitles[4].mSize.x = RESULT_TITLE_LEVEL_WIDTH;
        this.mTitles[4].mSize.y = RESULT_RECORD_TITLE_SIZE.y;
        this.mTitles[4].mOriginPos.y = (RESULT_RECORD_TITLE_SIZE.y * 4);
        this.mTitles[4].mExistFlag = true;
        this.mTitles[4].mScale = SCORE_SCALE_RATE;

        // Grade title
        this.mGrade[0].mSize.x = GRADE_SIZE.x;
        this.mGrade[0].mSize.y = GRADE_SIZE.y;
        this.mGrade[0].mPos.x = GRADE_POSITION_X;
        this.mGrade[0].mPos.y = this.mTitles[0].mPos.y + this.mTitles[0].mSize.y + 5;
        this.mGrade[0].mExistFlag = true;
        this.mGrade[0].mScale = SCORE_SCALE_RATE;
        // grade
        this.mGrade[1].mSize.x = GRADE_SIZE.x;
        this.mGrade[1].mSize.y = GRADE_SIZE.y;
        this.mGrade[1].mPos.x = this.mGrade[0].mPos.x;
        this.mGrade[1].mPos.y = this.mGrade[0].mPos.y + this.mGrade[0].mSize.y;
        this.mGrade[1].mScale = SCORE_SCALE_RATE;
        this.mGrade[1].mExistFlag = true;
        // Check grade
        this.mGrade[1].mType = this.CheckGrade(this.mScore[0].mTerminateNum);
        this.mGrade[1].mOriginPos.y = this.mGrade[0].mSize.y + (this.mGrade[1].mSize.y * this.mGrade[1].mType);

        // replace total position with chain max position
        Point replace = new Point();
        replace.x = this.mTitles[1].mPos.x;
        replace.y = this.mTitles[1].mPos.y;
        this.mTitles[1].mPos.x = this.mTitles[3].mPos.x;
        this.mTitles[1].mPos.y = this.mTitles[3].mPos.y;
        this.mTitles[3].mPos.x = replace.x;
        this.mTitles[3].mPos.y = replace.y;

        // New record
        this.mNewRecord.mSize.x = NEW_RECORD_SIZE.x;
        this.mNewRecord.mSize.y = NEW_RECORD_SIZE.y;
        // animation setting
        for (Animation ani : this.mNewRecordAni) {
            ani.SetAnimation(0, 0, this.mNewRecord.mSize.x, this.mNewRecord.mSize.y, 1, 10, 0);
        }

        // Level image in the stage
        // width
        int levelW[] = {
                SelectMode.EASY_WIDTH,
                SelectMode.LEVEL_SIZE.x,
                SelectMode.HARD_WIDTH
        };
        // mode height
        float modeH = this.mTitles[4].mSize.y * this.mTitles[4].mScale;
        this.mLevel.mSize.y = SelectMode.LEVEL_SIZE.y;
        this.mLevel.mPos.x = this.mTitles[4].mPos.x;
        this.mLevel.mPos.y = this.mTitles[4].mPos.y + (int) modeH;
        this.mLevel.mOriginPos.y = this.mLevel.mSize.y * level;
        this.mLevel.mExistFlag = true;
        this.mLevel.mSize.x = levelW[level];
        this.mLevel.mScale = RESULT_LEVEL_SCALE_RATE;

        // +
        this.mPlus.mSize.x = 40;
        this.mPlus.mSize.y = 40;
        this.mPlus.mScale = SCORE_BONUS_SCALE_RATE;
        this.mPlus.mExistFlag = true;

        // Initialize score class
        // Each scores
        for (Score score : this.mScore) score.InitScore(Score.SCORE_TYPE_GRADATION);
        // Bonus point
        for (Score score : this.mBonus) score.InitScore(Score.SCORE_TYPE_NORMAL);

        // Initialize menu
        this.mMenu.InitMenuBack(Menu.BACK_SCENE_TITLE);

        // if ( false )		return	CScene.SCENE_ERROR;
        return Scene.SCENE_MAIN;
    }

    /*
        Update
     */
    public int Update() {

        // Update score's direction
        // total point
        this.mScore[0].mIndicateNum = this.mScore[0].GraduallyNumber(
                this.mScore[0].mIndicateNum,this.mScore[0].mTerminateNum,SCORE_GRADUALLY_INTERVAL_FOR_TOTAL);
        // chain max
        this.mScore[1].mIndicateNum = this.mScore[1].GraduallyNumber(
                this.mScore[1].mIndicateNum,this.mScore[1].mTerminateNum,SCORE_GRADUALLY_INTERVAL_FOR_CHAIN);
        // time score
        this.mTimeScore.UpdateTimeScoreDirection();

        // Each bonus point
        for (int i = 0; i < this.mBonus.length; i++){
            this.mBonus[i].mIndicateNum = this.mBonus[i].GraduallyNumber(
                    this.mBonus[i].mIndicateNum,this.mBonus[i].mTerminateNum,SCORE_GRADUALLY_INTERVAL_FOR_BONUS);
        }

        // each record titles move to center from out of screen.
        for (int i = 1; i < 4; i++) {
            if (this.mTitles[i].mExistFlag) {
                if (RESULT_RECORD_TITLE_ARRIVE_POSITION_X < this.mTitles[i].mPos.x) {
                    this.mTitles[i].mPos.x -= this.mTitles[i].mMoveX;
                }
                if (this.mTitles[i].mPos.x <= RESULT_RECORD_TITLE_ARRIVE_POSITION_X) {
                    this.mTitles[i].mPos.x = RESULT_RECORD_TITLE_ARRIVE_POSITION_X;
                }
            }
        }

        // Update menu
        if (this.mMenu.UpdateMenu()) return Scene.SCENE_RELEASE;

        // Update animation
        for (int i = 0; i < this.mNewRecordAni.length; i++) {
            if (this.mRenewalF[i].equals(SystemManager.RECORD_RENEWAL.NEW_RECORD)) {
                this.mNewRecordAni[i].UpdateAnimation(this.mNewRecord.mOriginPos, true);
            }
        }

        // Play background music
        this.mSound.PlayBGM(this.mBGMfileName);

        return Scene.SCENE_MAIN;
    }

    /*
        Draw
     */
    public void Draw() {

        // Each title
        // Result, Total, Time, Chain, Mode
        for (int i = 0; i < this.mTitles.length; i++) {
            if (this.mTitles[i].mExistFlag) {
                this.mImage.DrawScale(
                        this.mTitles[i].mPos.x,
                        this.mTitles[i].mPos.y,
                        this.mTitles[i].mSize.x,
                        this.mTitles[i].mSize.y,
                        this.mTitles[i].mOriginPos.x,
                        this.mTitles[i].mOriginPos.y,
                        this.mTitles[i].mScale,
                        this.mTitles[i].mBmp
                );
            }
        }
        // Grade
        for (BaseCharacter ch: this.mGrade) {
            if (ch.mExistFlag) {
                this.mImage.DrawScale(
                        ch.mPos.x,
                        ch.mPos.y,
                        ch.mSize.x,
                        ch.mSize.y,
                        ch.mOriginPos.x,
                        ch.mOriginPos.y,
                        ch.mScale,
                        ch.mBmp
                );
            }
        }
        // Level
        if (this.mLevel.mExistFlag) {
            this.mImage.DrawScale(
                    this.mLevel.mPos.x,
                    this.mLevel.mPos.y,
                    this.mLevel.mSize.x,
                    this.mLevel.mSize.y,
                    this.mLevel.mOriginPos.x,
                    this.mLevel.mOriginPos.y,
                    this.mLevel.mScale,
                    this.mLevel.mBmp
            );
        }
        // Chain max
        // title width
        float chainW = this.mTitles[3].mSize.y*this.mTitles[3].mScale;
        this.mScore[1].DrawScore(
                this.mTitles[3].mPos.x,
                this.mTitles[3].mPos.y+(int)chainW,
                this.mScore[1].mIndicateNum, 3, Score.SCORE_COLOR_RED, SCORE_SCALE_RATE);
        // Time record
        // title height
        float timeH = this.mTitles[2].mSize.y*this.mTitles[2].mScale;
        this.mTimeScore.DrawTimeScore(
                this.mTitles[2].mPos.x,
                this.mTitles[2].mPos.y + (int) timeH,
                SCORE_SCALE_RATE, 15, Score.PUNCTUATE_COLOR_WHITE
        );
        // Total point
        // title height
        float totalH = this.mTitles[1].mSize.y*this.mTitles[1].mScale;
        this.mScore[0].DrawScore(
                this.mTitles[1].mPos.x,
                this.mTitles[1].mPos.y+(int)totalH,
                this.mScore[0].mIndicateNum, 6, Score.SCORE_COLOR_RED, SCORE_SCALE_RATE);

        // Each bonus point
        for (int i = 0; i < this.mBonus.length; i++) {
            this.mBonus[i].DrawScore(
                    this.mTitles[4].mPos.x,
                    this.mTitles[i+1].mPos.y+RESULT_RECORD_TITLE_SPACE_Y+15,
                    this.mBonus[i].mIndicateNum,
                    4,Score.SCORE_COLOR_GREEN,SCORE_BONUS_SCALE_RATE);
            // + image
            // width
            float plusW = this.mPlus.mSize.x*this.mPlus.mScale;
            this.mImage.DrawScale(
                    this.mTitles[4].mPos.x-(int)plusW,
                    this.mTitles[i+1].mPos.y+RESULT_RECORD_TITLE_SPACE_Y+15,
                    this.mPlus.mSize.x,
                    this.mPlus.mSize.y,
                    this.mPlus.mOriginPos.x,
                    this.mPlus.mOriginPos.y,
                    this.mPlus.mScale,
                    this.mPlus.mBmp
            );
        }

        // if there is a flag that renewal record, to show the image.
        //1:total, 2:time, 3:chain max
        // Level mode title width
        float totalW = this.mTitles[1].mSize.x*this.mTitles[1].mScale;
        for (int i = 1; i <= 3; i++) {
            if (this.mRenewalF[i-1].equals(SystemManager.RECORD_RENEWAL.NEW_RECORD)) {
                this.mImage.DrawImage(
                        this.mTitles[i].mPos.x + (int) totalW + NEW_RECORD_SPACE.x,
                        this.mTitles[i].mPos.y,
                        this.mNewRecord.mSize.x,
                        this.mNewRecord.mSize.y,
                        this.mNewRecord.mOriginPos.x,
                        this.mNewRecord.mOriginPos.y,
                        this.mNewRecord.mBmp
                );
            }
        }

        // Menu
        this.mMenu.DrawMenu();
    }

    // to release
    public int Release() {
        // base character
        for (int i = 0; i < this.mTitles.length; i++) {
            this.mTitles[i].ReleaseCharaBmp();
            this.mTitles[i] = null;
        }
        // Level
        this.mLevel.ReleaseCharaBmp();
        this.mLevel = null;
        // score class
        for (int i = 0; i < this.mScore.length; i++) {
            this.mScore[i].ReleaseScore();
            this.mScore[i] = null;
        }
        for (int i = 0; i < this.mBonus.length; i++) {
            this.mBonus[i].ReleaseScore();
            this.mBonus[i] = null;
        }
        // time score
        this.mTimeScore.ReleaseTimeScore();
        this.mTimeScore = null;
        this.mContext = null;         // activity
        this.mImage = null;            // image object
        this.mSound.StopBGM();
        // New record
        this.mNewRecord.ReleaseCharaBmp();
        this.mNewRecord = null;
        for (int i = 0; i < this.mNewRecordAni.length; i++) {
            this.mNewRecordAni[i] = null;
        }
        // +
        this.mPlus.ReleaseCharaBmp();
        this.mPlus = null;
        // Grade
        for (int i = 0; i < this.mGrade.length; i++) {
            this.mGrade[i].ReleaseCharaBmp();
            this.mGrade[i] = null;
        }
        // Menu
        this.mMenu.ReleaseMenu();
        return Scene.SCENE_END;
    }

    /*
        Check grade
    */
    private int CheckGrade(int totalPoint) {
        // get current stage number
        int stage = Play.GetCurrentStageNumber();
        if (stage == STAGE_OFF_ROAD || stage == STAGE_ROAD) {
            if (30000 <= totalPoint) {
                return GRADE_PERFECT;
            } else if (20000 <= totalPoint) {
                return GRADE_ORDINARY;
            }
        } else if (stage == STAGE_SEA) {
            if (100000 <= totalPoint) {
                return GRADE_PERFECT;
            } else if (70000 <= totalPoint) {
                return GRADE_ORDINARY;
            }
        }
        return GRADE_TRAINEE;
    }
    /*
        Check grade
    */
    public static int  CheckGrade(int stage, int totalPoint) {
        if (stage == STAGE_OFF_ROAD || stage == STAGE_ROAD) {
            if (30000 <= totalPoint) {
                return GRADE_PERFECT;
            } else if (20000 <= totalPoint) {
                return GRADE_ORDINARY;
            }
        } else if (stage == STAGE_SEA) {
            if (100000 <= totalPoint) {
                return GRADE_PERFECT;
            } else if (70000 <= totalPoint) {
                return GRADE_ORDINARY;
            }
        }
        return GRADE_TRAINEE;
    }
}