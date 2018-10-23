package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.view.KeyEvent;

/**
 * Created by USER on 2/11/2016.
 */
public class RecordView extends Scene implements HasRecords {

    // static variables
    // Record title
    // size
    public final static Point      RECORD_TITLE_SIZE = new Point(250,55);
    // starting position
    public final static Point      RECORD_TITLE_POSITION = new Point(GameView.GetScreenSize().x + 50,100);
    // terminate position
    public final static int        RECORD_TITLE_ARRIVE_POSITION_X = 100;
    // space-Y
    public final static int        RECORD_TITLE_SPACE_Y = 100;

    // Best record
    private final static Point      RECORD_VIEW_TITLE_POSITION = new Point(
            (GameView.GetScreenSize().x - Option.BEST_RECORD_BUTTON_SIZE.x) / 2, 35
    );

    // Score
    // rolling time
    private final static int        SCORE_ROLLING_TIME = 150;

    // Interval Time
    private final static int        INTERVAL_TIME = 50;

    // filed
    private Image           mImage;                      // image object
    private Context         mContext;                   // activity
    private Sound           mSound;                      // sound object to play SE or background music
    private String          mBGMfileName;
    private BaseCharacter   mTitles[] = new BaseCharacter[5];
    private Score           mScore[] = new Score[2];
    private Menu            mMenu;
    private TimeScore       mTimeScore;
    private BaseCharacter   mLevelButton[] = new BaseCharacter[3];
    private int             mProgress;
    private int             mNextProgress;
    private int             mIntervalTime;
    private BaseCharacter   mLevel;
    private BaseCharacter   mGrade[] = new BaseCharacter[2];
    private BaseCharacter   mStage[] = new BaseCharacter[3];
    private int             mStageViewNumber;

    /*
        Constructor
     */
    public RecordView(Context context, Image image) {
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
        // level
        this.mLevel = new BaseCharacter(image);
        // score class
        for (int i = 0; i < this.mScore.length; i++) {
            this.mScore[i] = new Score(context, image);
        }
        // menu
        this.mMenu = new Menu(context, image);
        // Time Score
        this.mTimeScore = new TimeScore(context,image);
        // level
        for (int i = 0; i < this.mLevelButton.length; i++) {
            this.mLevelButton[i] = new BaseCharacter(image);
        }
        // grade
        for (int i = 0; i < this.mGrade.length; i++) {
            this.mGrade[i] = new BaseCharacter(image);
        }
        // each stages
        for (int i = 0; i < this.mStage.length ; i++) {
            this.mStage[i] = new BaseCharacter(image);
        }
    }

    /*
        Initialize
     */
    public int Init() {
        // using BGM
        this.mBGMfileName = "result";
        // using image file
        String imageFile[] = {
                "optiontitles",
                "resulttitles",
        };
        // load title image
        this.mTitles[0].LoadCharaImage(this.mContext, imageFile[0]);
        // load image, except for title
        for (int i = 1; i < this.mTitles.length; i++) {
            this.mTitles[i].LoadCharaImage(this.mContext, imageFile[1]);
        }
        // stage
        for (BaseCharacter stage: this.mStage) stage.LoadCharaImage(this.mContext,"stages");
        // for title
        for (BaseCharacter ch: this.mLevelButton) ch.LoadCharaImage(this.mContext,"level");
        // for indication
        this.mLevel.LoadCharaImage(this.mContext, "level");
        // grade
        for (BaseCharacter ch: this.mGrade) ch.LoadCharaImage(this.mContext, "grade");

        // progress
        this.mProgress = this.mNextProgress = 0;

        // Each level button
        for (int i = 0; i < this.mLevelButton.length; i++) {
            this.mLevelButton[i].mSize.y = SelectMode.LEVEL_SIZE.y;
            this.mLevelButton[i].mPos.x = SelectMode.LEVEL_POSITION.x;
            this.mLevelButton[i].mPos.y = SelectMode.LEVEL_POSITION.y+((this.mLevelButton[i].mSize.y+35)*i);
            this.mLevelButton[i].mOriginPos.y = this.mLevelButton[i].mSize.y*i;
            this.mLevelButton[i].mExistFlag = true;
        }
        // each width
        this.mLevelButton[0].mSize.x = SelectMode.EASY_WIDTH;
        this.mLevelButton[1].mSize.x = SelectMode.LEVEL_SIZE.x;
        this.mLevelButton[2].mSize.x = SelectMode.HARD_WIDTH;

        // Setting
        // Result title
        this.mTitles[0].mSize.x = Option.BEST_RECORD_BUTTON_SIZE.x;
        this.mTitles[0].mSize.y = Option.BEST_RECORD_BUTTON_SIZE.y;
        this.mTitles[0].mPos.x = RECORD_VIEW_TITLE_POSITION.x;
        this.mTitles[0].mPos.y = RECORD_VIEW_TITLE_POSITION.y;
        this.mTitles[0].mOriginPos.y = Option.OPTION_TITLE_SIZE.y;
        this.mTitles[0].mExistFlag = true;

        // Each record titles
        for (int i = 1; i < 4; i++) {
            this.mTitles[i].mPos.x = RECORD_TITLE_POSITION.x;
            this.mTitles[i].mPos.y = RECORD_TITLE_POSITION.y +
                    ((RECORD_TITLE_SIZE.y + RECORD_TITLE_SPACE_Y) * i);
            this.mTitles[i].mSize.x = Result.RESULT_RECORD_TITLE_SIZE.x;
            this.mTitles[i].mSize.y = RECORD_TITLE_SIZE.y;
            this.mTitles[i].mExistFlag = true;
            this.mTitles[i].mMoveX = 4.0f;
            this.mTitles[i].mOriginPos.y = (RECORD_TITLE_SIZE.y*i);
        }
        // Level mode title
        this.mTitles[4].mPos.x = this.mTitles[0].mPos.x+this.mTitles[0].mSize.x-15;
        this.mTitles[4].mPos.y = this.mTitles[0].mPos.y+this.mTitles[0].mSize.y+25;
        this.mTitles[4].mSize.x = Result.RESULT_TITLE_LEVEL_WIDTH;
        this.mTitles[4].mSize.y = RECORD_TITLE_SIZE.y;
        this.mTitles[4].mOriginPos.y = (RECORD_TITLE_SIZE.y*4);
        this.mTitles[4].mExistFlag = true;

        // Level image in the stage
        this.mLevel.mSize.y = SelectMode.LEVEL_SIZE.y;
        this.mLevel.mPos.x = this.mTitles[4].mPos.x;
        this.mLevel.mPos.y = this.mTitles[4].mPos.y+this.mTitles[4].mSize.y;
        this.mLevel.mExistFlag = true;
        this.mLevel.mScale = Result.RESULT_LEVEL_SCALE_RATE;

        // Grade title
        this.mGrade[0].mSize.x = Result.GRADE_SIZE.x;
        this.mGrade[0].mSize.y = Result.GRADE_SIZE.y;
        this.mGrade[0].mPos.x = Result.GRADE_POSITION_X;
        this.mGrade[0].mPos.y = this.mTitles[0].mPos.y+this.mTitles[0].mSize.y+5;
        this.mGrade[0].mExistFlag = true;
        this.mGrade[0].mScale = 0.9f;
        // grade
        this.mGrade[1].mSize.x = Result.GRADE_SIZE.x;
        this.mGrade[1].mSize.y = Result.GRADE_SIZE.y;
        this.mGrade[1].mPos.x = this.mGrade[0].mPos.x;
        this.mGrade[1].mPos.y = this.mGrade[0].mPos.y+this.mGrade[0].mSize.y;
        this.mGrade[1].mScale = 0.9f;
        this.mGrade[1].mExistFlag = true;

        // Each stages
        // common setting
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i].mSize.y = SelectMode.STAGE_BUTTON_SIZE.y;
            this.mStage[i].mPos.x = SelectMode.STAGE_BUTTON_POSITION.x;
            this.mStage[i].mPos.y = SelectMode.STAGE_BUTTON_POSITION.y+((this.mStage[i].mSize.y+35)*i);
            this.mStage[i].mOriginPos.y = this.mStage[i].mSize.y*i;
        }
        // off-road
        this.mStage[0].mSize.x = SelectMode.STAGE_BUTTON_SIZE.x;
        // road
        this.mStage[1].mSize.x = SelectMode.ROAD_WIDTH;
        // sea
        this.mStage[2].mSize.x = SelectMode.SEA_WIDTH;
        

        // Initialize score class
        for (Score score: this.mScore) {
            score.InitScore(Score.SCORE_TYPE_GRADATION);
        }

        // Initialize Back menu
        this.mMenu.InitMenuBack(Menu.BACK_SCENE_OPTION);

        // Initialize time score
        this.mTimeScore.InitTimeScore();

        // if ( false )		return	CScene.SCENE_ERROR;
        return Scene.SCENE_MAIN;
    }

    /*
        Update
     */
    public int Update() {

        // to make the blank time
        if (this.mProgress == this.mNextProgress) {
            // Get key event
            int keyEvent = GameView.GetKeyEvent();
            // when pressed back-key, back to preview.
            if (keyEvent == KeyEvent.KEYCODE_BACK) if (0 < this.mProgress) this.mNextProgress--;
            // diverge progress from progress number
            switch (this.mProgress) {
                case 0:
                    // to show each stages and set position
                    if (!this.mStage[0].mExistFlag) {
                        for (int i = 0; i < this.mStage.length; i++) {
                            this.mStage[i].mPos.x = SelectMode.STAGE_BUTTON_POSITION.x;
                            this.mStage[i].mPos.y = SelectMode.STAGE_BUTTON_POSITION.y+((this.mStage[i].mSize.y+35)*i);
                            this.mStage[i].mExistFlag = true;
                        }                    
                    }
                    // to select the stage
                    for (int i = 0; i < this.mStage.length; i++) {
                        if (Collision.CheckTouch(
                                this.mStage[i].mPos.x, this.mStage[i].mPos.y,
                                this.mStage[i].mSize.x, this.mStage[i].mSize.y,
                                this.mStage[i].mScale)) {
                            // not to show level
                            for (BaseCharacter ch : this.mStage) ch.mExistFlag = false;
                            // set stage number
                            this.mStageViewNumber = i;
                            // next progress
                            this.mNextProgress = 1;
                            break;
                        }
                    }
                    break;
                case 1:
                // to show each level
                if (!this.mLevelButton[0].mExistFlag) for (BaseCharacter ch : this.mLevelButton) ch.mExistFlag = true;
                // ask about game level.
                for (int i = 0; i < this.mLevelButton.length; i++) {
                    if (this.mLevelButton[i].mExistFlag) {
                        if (Collision.CheckTouch(
                                this.mLevelButton[i].mPos.x, this.mLevelButton[i].mPos.y,
                                this.mLevelButton[i].mSize.x, this.mLevelButton[i].mSize.y,
                                this.mLevelButton[i].mScale)) {
                            // not to show level
                            for (BaseCharacter ch : this.mLevelButton) ch.mExistFlag = false;

                            // stage title position
                            this.mStage[this.mStageViewNumber].mPos.y = RECORD_VIEW_TITLE_POSITION.y+Option.BEST_RECORD_BUTTON_SIZE.y+10;
                            this.mStage[this.mStageViewNumber].mPos.x = (GameView.GetScreenSize().x-this.mStage[this.mStageViewNumber].mSize.x)>>1;
                            this.mStage[this.mStageViewNumber].mExistFlag = true;
                            
                            // get level to show result in the past.
                            // and set the best record file.
                            this.SetRecord(i);
                            // initialize scores' direction
                            this.InitScoreDirection();

                            // next progress
                            this.mNextProgress = 2;
                            break;
                        }
                    }
                }
                    break;
                case 2:         // Update score's direction
                    this.UpdateScoreDirection();
                    break;
            }
        } else {
            this.mIntervalTime++;      // count time
            // make the interval time
            if (INTERVAL_TIME <= this.mIntervalTime) {
                if (this.mProgress == 2) this.mStage[this.mStageViewNumber].mExistFlag = false;
                this.mProgress = this.mNextProgress;
                this.mIntervalTime = 0;
            }
        }

        // Update menu
        if (this.mMenu.UpdateMenu()) return Scene.SCENE_RELEASE;

        // Play background music
        this.mSound.PlayBGM(this.mBGMfileName);

        return Scene.SCENE_MAIN;
    }

    /*
        Draw
     */
    public void Draw() {

        // each stages
        if (this.mProgress == 0) {                 // Select the stage
            for (BaseCharacter stage: this.mStage) {
                if (stage.mExistFlag) {
                    this.mImage.DrawScale(
                            stage.mPos.x,
                            stage.mPos.y,
                            stage.mSize.x,
                            stage.mSize.y,
                            stage.mOriginPos.x,
                            stage.mOriginPos.y,
                            stage.mScale,
                            stage.mBmp
                    );
                }
            }
        } else if (this.mProgress == 1) {          // Select the level
            // each level
            for (BaseCharacter level: this.mLevelButton) {
                if (level.mExistFlag) {
                    this.mImage.DrawScale(
                            level.mPos.x,
                            level.mPos.y,
                            level.mSize.x,
                            level.mSize.y,
                            level.mOriginPos.x,
                            level.mOriginPos.y,
                            level.mScale,
                            level.mBmp
                    );
                }
            }
        }
        // to show the best result in the past
        else if (this.mProgress == 2) {            // Record view
            if (this.mStage[this.mStageViewNumber].mExistFlag) {
                this.mImage.DrawScale(
                        this.mStage[this.mStageViewNumber].mPos.x,
                        this.mStage[this.mStageViewNumber].mPos.y,
                        this.mStage[this.mStageViewNumber].mSize.x,
                        this.mStage[this.mStageViewNumber].mSize.y,
                        this.mStage[this.mStageViewNumber].mOriginPos.x,
                        this.mStage[this.mStageViewNumber].mOriginPos.y,
                        this.mStage[this.mStageViewNumber].mScale,
                        this.mStage[this.mStageViewNumber].mBmp
                );
            }
            // Except for Best record, Each title
            for (int i = 1; i < this.mTitles.length; i++) {
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
            // Total point
            this.mScore[0].DrawScore(
                    this.mTitles[1].mPos.x,
                    this.mTitles[1].mPos.y + this.mTitles[1].mSize.y,
                    this.mScore[0].mIndicateNum, 6, Score.SCORE_COLOR_RED, 1.0f);
            // Time record
            this.mTimeScore.DrawTimeScore(
                    this.mTitles[2].mPos.x,
                    this.mTitles[2].mPos.y + this.mTitles[2].mSize.y,
                    1.0f, 15, Score.PUNCTUATE_COLOR_WHITE
            );
            // Chain max
            this.mScore[1].DrawScore(
                    this.mTitles[3].mPos.x,
                    this.mTitles[3].mPos.y + this.mTitles[3].mSize.y,
                    this.mScore[1].mIndicateNum, 3, Score.SCORE_COLOR_RED, 1.0f);
        }

        // Top title
        if (this.mTitles[0].mExistFlag) {
            this.mImage.DrawScale(
                    this.mTitles[0].mPos.x,
                    this.mTitles[0].mPos.y,
                    this.mTitles[0].mSize.x,
                    this.mTitles[0].mSize.y,
                    this.mTitles[0].mOriginPos.x,
                    this.mTitles[0].mOriginPos.y,
                    this.mTitles[0].mScale,
                    this.mTitles[0].mBmp
            );
        }

        // Menu
        this.mMenu.DrawMenu();
    }

    // to release
    public int Release() {
        // each stages
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i].ReleaseCharaBmp();
            this.mStage[i] = null;
        }
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
        // level
        for (int i = 0; i < this.mLevelButton.length; i++) {
            this.mLevelButton[i].ReleaseCharaBmp();
            this.mLevelButton[i] = null;
        }
        this.mContext = null;         // activity
        this.mImage = null;            // image object
        this.mSound.StopBGM();
        // Menu
        this.mMenu.ReleaseMenu();
        // Time score
        this.mTimeScore.ReleaseTimeScore();

        return Scene.SCENE_END;
    }

    /*
        Initialize scores' direction
     */
    private void InitScoreDirection() {
        // Each record titles' position-X
        for (int i = 1; i < 4; i++) {
            this.mTitles[i].mPos.x = RECORD_TITLE_POSITION.x;
        }
        // scores' direction
        this.mScore[0].mRollingCount = 0;
        this.mScore[1].mRollingCount = 0;
        this.mTimeScore.ResetRollingDirection();
    }

    /*
        Update scores' direction
     */
    private void UpdateScoreDirection() {
        // Each direction for scores
        // Total
        // rolling number
        this.mScore[0].mIndicateNum = this.mScore[0].RollingNumber(
                this.mScore[0].mIndicateNum, this.mScore[0].mTerminateNum, SCORE_ROLLING_TIME, 6);
        // Chain max
        // rolling number
        this.mScore[1].mIndicateNum = this.mScore[1].RollingNumber(
                this.mScore[1].mIndicateNum, this.mScore[1].mTerminateNum, SCORE_ROLLING_TIME,3);
        // Update Time score direction
        this.mTimeScore.UpdateTimeScoreDirection();

        // each record titles move to center from out of screen.
        for (int i = 1; i < 4; i++) {
            if (this.mTitles[i].mExistFlag) {
                if (RECORD_TITLE_ARRIVE_POSITION_X < this.mTitles[i].mPos.x) {
                    this.mTitles[i].mPos.x -= this.mTitles[i].mMoveX;
                }
                if (this.mTitles[i].mPos.x <= RECORD_TITLE_ARRIVE_POSITION_X) {
                    this.mTitles[i].mPos.x = RECORD_TITLE_ARRIVE_POSITION_X;
                }
            }
        }
    }
    /*
        Set the best record view based on selected level.
     */
    private void SetRecord(int level) {
        // to load the records
        SystemManager systemManager = new SystemManager(this.mContext);
        int records[] = systemManager.getTheRecords(this.mStageViewNumber,level);
        // total point
        this.mScore[0].mTerminateNum = records[RECORD_TOTAL];
        // check grade
        this.mGrade[1].mType = Result.CheckGrade(this.mStageViewNumber,this.mScore[0].mTerminateNum);
        this.mGrade[1].mOriginPos.y = this.mGrade[0].mSize.y+(this.mGrade[1].mSize.y*this.mGrade[1].mType);

        // time that is millisecond
        int time = records[RECORD_TIME];
        // calculate time
        int recordTime[] = new int[3];
        recordTime[0] = time / 6000;                          // minute
        recordTime[1] = (time - (recordTime[0]*6000)) / 100;  // second
        // millisecond
        recordTime[2] = time - (recordTime[0]*6000+recordTime[1]*100);
        // chain max
        this.mScore[1].mTerminateNum = records[RECORD_CHAIN_MAX];

        // setting
        this.mTimeScore.SetTimeScoreDirection(recordTime[0],recordTime[1],recordTime[2],Score.SCORE_DIRECTION_ROLLING,SCORE_ROLLING_TIME);

        // the level image
        // width
        int levelW[] = {
                SelectMode.EASY_WIDTH,
                SelectMode.LEVEL_SIZE.x,
                SelectMode.HARD_WIDTH
        };
        this.mLevel.mOriginPos.y = this.mLevel.mSize.y*level;
        this.mLevel.mSize.x = levelW[level];
    }
}