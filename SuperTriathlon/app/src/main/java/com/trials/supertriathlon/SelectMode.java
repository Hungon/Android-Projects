package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.KeyEvent;

/**
 * Created by USER on 2/1/2016.
 */
public class SelectMode extends Scene implements HasScenes {

    // static variables
    // kind of progress
    private final static int    PROGRESS_DIALOG       = -1;
    private final static int    PROGRESS_SELECT_STAGE = 0;
    private final static int    PROGRESS_SELECT_LEVEL = 1;
    private final static int    PROGRESS_ASK_BRIEF    = 2;
    // background image
    private final static Point  BG_SIZE = new Point(480, 800);
    // title logo
    private final static Point TITLE_SIZE = new Point(250, 55);            // size
    private final static Point TITLE_POSITION =
            new Point((GameView.GetScreenSize().x - TITLE_SIZE.x)>>1, 50);     // position
    // Each stages
    public final static Point STAGE_BUTTON_SIZE = new Point(170, 55);          // size
    public final static Point STAGE_BUTTON_POSITION =
            new Point((GameView.GetScreenSize().x - STAGE_BUTTON_SIZE.x)>>1, 280);  // position
    // road stage width
    public final static int    ROAD_WIDTH = 100;
    // sea stage width
    public final static int    SEA_WIDTH = 75;
    
    // level buttons
    public final static Point  LEVEL_SIZE = new Point(150,50);
    public final static Point  LEVEL_POSITION = new Point(
            (GameView.GetScreenSize().x-LEVEL_SIZE.x)>>1, 280);
    // easy size
    public final static int     EASY_WIDTH = 95;
    // hard
    public final static int     HARD_WIDTH = 100;
    // Screen image
    private final static Point  SCREEN_PORTRAIT_SIZE    = new Point(36,64);
    private final static Point  SCREEN_LANDSCAPE_SIZE   = new Point(64,36);

    // The termination position that player move to the end point.
    private final static int  PLAYER_ARRIVE_POSITION_Y = TITLE_POSITION.y+TITLE_SIZE.y+50;
    // player's speed
    private final static float  PLAYER_DEFAULT_SPEED = 5.0f;
    // enemy's speed
    private final static float  ENEMY_DEFAULT_SPEED = 4.0f;

    // interval time
    private final static int    INTERVAL_TIME = 50;

    // filed
    private Context         mContext;
    private Image           mImage;
    private BaseCharacter   mChara[] = new BaseCharacter[5];
    private Sound           mSound;                // sound object to play SE or background music
    private String          mBGMfileName;
    private Menu            mMenu;
    private BaseCharacter   mAnswer[] = new BaseCharacter[2];
    private int             mProgress;
    private int             mNextProgress;
    private int             mIntervalTime;
    private BaseCharacter   mLevel[] = new BaseCharacter[3];
    private BaseCharacter   mScreen;
    private BaseCharacter   mPlayer;
    private Animation       mPlayerAni;
    private BaseCharacter   mEnemy;
    private Animation       mEnAni;

    /*
        Constructor
     */
    public SelectMode(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        // Initialize BaseCharacter class.
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i] = new BaseCharacter(this.mImage);
        }
        // set sound
        this.mSound = new Sound(this.mContext);
        //Menu
        this.mMenu = new Menu(activity, image);
        // Answer
        for (int i = 0; i < 2; i++) this.mAnswer[i] = new BaseCharacter(image);
        // Level
        for (int i = 0; i < this.mLevel.length; i++) this.mLevel[i] = new BaseCharacter(image);
        // Screen
        this.mScreen = new BaseCharacter(image);
        // Player character
        this.mPlayer = new BaseCharacter(image);
        this.mPlayerAni = new Animation();
        // Enemy character
        this.mEnemy = new BaseCharacter(image);
        this.mEnAni = new Animation();
    }

    /*
        Initialize
     */
    public int Init() {

        // using image files
        String imageFiles[] = {
                "selectbg",         // background image
                "selecttitle",      // title
                "stages",           // each stages
                "stages",           // each stages
                "stages",           // each stages
        };
        // load image file.
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i].LoadCharaImage(this.mContext, imageFiles[i]);
        }
        // Level
        for (BaseCharacter ch: this.mLevel) ch.LoadCharaImage(this.mContext, "level");
        // Answer
        for (BaseCharacter ch: this.mAnswer) ch.LoadCharaImage(this.mContext, "menu");
        // Screen
        this.mScreen.LoadCharaImage(this.mContext,"screen");
        // using BGM
        this.mBGMfileName = "selectmode";
        // using SE
        String seFiles[] = {
                "click",
        };
        for(int i = 0; i < seFiles.length; i++) this.mSound.CreateSound(seFiles[i]);

        // progress
        this.mNextProgress = this.mProgress = PROGRESS_SELECT_STAGE;
        // interval time
        this.mIntervalTime = 0;

        /*
            Set each images.
         */
        // background image
        this.mChara[0].mSize = BG_SIZE;
        // title logo
        // position
        this.mChara[1].mPos = TITLE_POSITION;
        // size
        this.mChara[1].mSize = TITLE_SIZE;
        // drawing flag
        this.mChara[1].mExistFlag = true;

        // Each stages
        // common setting
        for (int i = 2; i < 5; i++) {
            this.mChara[i].mSize.y = STAGE_BUTTON_SIZE.y;
            this.mChara[i].mPos.x = STAGE_BUTTON_POSITION.x-50;
            this.mChara[i].mPos.y = STAGE_BUTTON_POSITION.y+((this.mChara[i].mSize.y+35)*(i-2));
            this.mChara[i].mOriginPos.y = this.mChara[i].mSize.y*(i-2);
            this.mChara[i].mExistFlag = true;
        }
        // off-road
        this.mChara[2].mSize.x = STAGE_BUTTON_SIZE.x;
        // road
        this.mChara[3].mSize.x = ROAD_WIDTH;
        // sea
        this.mChara[4].mSize.x = SEA_WIDTH;

        // Screen
        // the position that is starting position
        this.mScreen.mPos.x = this.mChara[2].mPos.x+STAGE_BUTTON_SIZE.x+35;
        this.mScreen.mExistFlag = true;

        // Each level button
        for (int i = 0; i < this.mLevel.length; i++) {
            this.mLevel[i].mSize.y = LEVEL_SIZE.y;
            this.mLevel[i].mPos.x = LEVEL_POSITION.x;
            this.mLevel[i].mPos.y = LEVEL_POSITION.y+((this.mLevel[i].mSize.y+35)*i);
            this.mLevel[i].mOriginPos.y = this.mLevel[i].mSize.y*i;
        }
        // each width
        this.mLevel[0].mSize.x = EASY_WIDTH;
        this.mLevel[1].mSize.x = LEVEL_SIZE.x;
        this.mLevel[2].mSize.x = HARD_WIDTH;

        // Answer
        // common
        for (int i = 0; i < 2; i++) {
            this.mAnswer[i].mSize.x = Option.ANSWER_SIZE.x;
            this.mAnswer[i].mSize.y = Option.ANSWER_SIZE.y;
            this.mAnswer[i].mOriginPos.y = Menu.MENU_BUTTON_SIZE.y + this.mAnswer[i].mSize.y*i;
            this.mAnswer[i].mPos.y = GameView.GetScreenSize().y>>1;
        }
        // yes position
        this.mAnswer[0].mPos.x = (GameView.GetScreenSize().x>>1) - (this.mAnswer[0].mSize.x + 30);
        // no position
        this.mAnswer[1].mPos.x = (GameView.GetScreenSize().x>>1) + 30;

        // Initialize Menu
        this.mMenu.InitMenuBack(Menu.BACK_SCENE_TITLE);

        return SCENE_MAIN;
    }

    /*
        Update
    */
    public int Update() {

        // Diverge process from progress
        if (this.mProgress == this.mNextProgress) {
            // Get key event
            int keyEvent = GameView.GetKeyEvent();
            // when pressed back-key, back to preview.
            if (keyEvent == KeyEvent.KEYCODE_BACK) {
                if (PROGRESS_SELECT_STAGE < this.mProgress) {
                    this.mNextProgress--;
                    if (this.mNextProgress == PROGRESS_SELECT_STAGE) {
                        this.ResetPlayerAndEnemy();
                    }
                }
            }
            switch (this.mProgress) {
                case PROGRESS_SELECT_STAGE:             // Select the stage
                    // loop to stage max
                    for (int i = 2; i < 5; i++) {
                        // In the pressed back-key, to show each stages and reset each setting
                        if (!this.mChara[2].mExistFlag){
                            for (int j = 2; j < 5; j++) this.mChara[j].mExistFlag = true;
                            // reset select stage
                            Play.SetStageNumberToNext(Play.STAGE_NOTHING);
                            // game level
                            Play.SetGameLevel(Play.LEVEL_NOTHING);
                        }
                        // when pressed start button, transition to main scene.
                        if (this.mChara[i].mExistFlag) {
                            if (Collision.CheckTouch(
                                    this.mChara[i].mPos.x, this.mChara[i].mPos.y,
                                    this.mChara[i].mSize.x, this.mChara[i].mSize.y,
                                    this.mChara[i].mScale)) {

                                // stage number
                                int stage[] = {Play.STAGE_OFF_ROAD,Play.STAGE_ROAD,Play.STAGE_SEA};
                                // not to show each stages
                                for (int j = 2; j < 5; j++) this.mChara[j].mExistFlag = false;

                                // set the stage number to next
                                Play.SetStageNumberToNext(stage[i-2]);

                                // to initialize the player character by selected stage.
                                this.SetPlayerAndEnemy(stage[i - 2]);

                                // play SE
                                this.mSound.PlaySE();
                                // next progress
                                this.mNextProgress = PROGRESS_SELECT_LEVEL;
                                break;
                            }
                        }
                    }
                    break;
                case PROGRESS_SELECT_LEVEL:             // To set game level
                    // to show each level
                    if (!this.mLevel[0].mExistFlag) for (BaseCharacter ch : this.mLevel) ch.mExistFlag = true;
                    // ask about game level.
                    for (int i = 0; i < this.mLevel.length; i++) {
                        if (this.mLevel[i].mExistFlag) {
                            if (Collision.CheckTouch(
                                    this.mLevel[i].mPos.x, this.mLevel[i].mPos.y,
                                    this.mLevel[i].mSize.x, this.mLevel[i].mSize.y,
                                    this.mLevel[i].mScale)) {
                                // Each game level
                                int level[] = {Play.LEVEL_EASY, Play.LEVEL_NORMAL,Play.LEVEL_HARD};
                                // to show response buttons
                                for (BaseCharacter ch : this.mAnswer) ch.mExistFlag = true;
                                // not to show level
                                for (BaseCharacter ch: this.mLevel) ch.mExistFlag = false;

                                // Set game level to the stage
                                Play.SetGameLevel(level[i]);

                                // set next progress
                                this.mNextProgress = PROGRESS_ASK_BRIEF;
                                // play SE
                                this.mSound.PlaySE();
                            }
                        }
                    }
                    break;
                case PROGRESS_ASK_BRIEF:             // To check to show the stage brief or not.
                    // ask to check the stage brief or not.
                    for (int i = 0; i < 2; i++) {
                        if (this.mAnswer[i].mExistFlag) {
                            if (Collision.CheckTouch(
                                    this.mAnswer[i].mPos.x, this.mAnswer[i].mPos.y,
                                    this.mAnswer[i].mSize.x, this.mAnswer[i].mSize.y,
                                    this.mAnswer[i].mScale)) {
                                // next scene
                                int scene[] = {SCENE_BRIEF_STAGE, SCENE_PLAY};
                                // transition to scene
                                Wipe.CreateWipe(scene[i], Wipe.TYPE_PENETRATION);
                                // play SE
                                this.mSound.PlaySE();
                                return SCENE_RELEASE;
                            }
                        }
                    }
                    break;
                default:
                    this.mNextProgress = this.mProgress = PROGRESS_SELECT_STAGE;
                    break;
            }
        } else {
            // to make the interval
            this.mIntervalTime++;
            if (INTERVAL_TIME <= this.mIntervalTime) {
                // set next progress
                this.mProgress = this.mNextProgress;
                // reset count time
                this.mIntervalTime = 0;
            }
        }

        // Update player character
        if (this.mPlayer.mExistFlag) {
            // the terminate position-X
            Point screen = GameView.GetScreenSize();
            int terminateX = (screen.x-this.mPlayer.mSize.x)>>1;
            // update move
            if (this.mPlayer.mPos.x != terminateX ||
                this.mPlayer.mPos.y != PLAYER_ARRIVE_POSITION_Y)
            {
                this.mPlayer.mPos.x += this.mPlayer.mMoveX;
                this.mPlayer.mPos.y += this.mPlayer.mMoveY;
            }
            if (terminateX < this.mPlayer.mPos.x) {
                this.mPlayer.mPos.x = terminateX;
            }
            if (this.mPlayer.mPos.y < PLAYER_ARRIVE_POSITION_Y) {
                this.mPlayer.mPos.y = PLAYER_ARRIVE_POSITION_Y;
            }
            // update animation
            this.mPlayerAni.UpdateAnimation(this.mPlayer.mOriginPos, false);
        }
        // Update enemy character
        if (this.mEnemy.mExistFlag) {
            // the terminate position-X
            Point screen = GameView.GetScreenSize();
            int terminateX = (screen.x - this.mEnemy.mSize.x) >> 1;
            // update move
            if (terminateX < this.mEnemy.mPos.x) {
                this.mEnemy.mPos.x += this.mEnemy.mMoveX;
            }
            if (this.mEnemy.mPos.x <= terminateX) {
                this.mEnemy.mPos.x = terminateX;
            }
            // except for off-road and road, update animation
            if (this.mEnemy.mType == Play.STAGE_SEA) {
                this.mEnAni.UpdateAnimation(this.mEnemy.mOriginPos, false);
            }
        }

        // update menu
        if (this.mMenu.UpdateMenu()) return Scene.SCENE_RELEASE;

        // Play background music
        this.mSound.PlayBGM(this.mBGMfileName);

        return SCENE_MAIN;
    }


    /*
        Draw
     */
    public void Draw() {
        // BG
        this.mImage.DrawImageFast(
                this.mChara[0].mPos.x,
                this.mChara[0].mPos.y,
                this.mChara[0].mBmp
        );
        // Title
        if (this.mChara[1].mExistFlag) {
            this.mImage.DrawScale(
                    this.mChara[1].mPos.x,
                    this.mChara[1].mPos.y,
                    this.mChara[1].mSize.x,
                    this.mChara[1].mSize.y,
                    this.mChara[1].mOriginPos.x,
                    this.mChara[1].mOriginPos.y,
                    this.mChara[1].mScale,
                    this.mChara[1].mBmp
            );
        }

        if (this.mProgress == PROGRESS_SELECT_STAGE) {
            // Each stages
            for (int i = 2; i < this.mChara.length; i++) {
                if (this.mChara[i].mExistFlag) {
                    this.mImage.DrawScale(
                            this.mChara[i].mPos.x,
                            this.mChara[i].mPos.y,
                            this.mChara[i].mSize.x,
                            this.mChara[i].mSize.y,
                            this.mChara[i].mOriginPos.x,
                            this.mChara[i].mOriginPos.y,
                            this.mChara[i].mScale,
                            this.mChara[i].mBmp
                    );
                    // Screen that notice the orientation.
                    // size
                    Point size[] = {
                            new Point(SCREEN_PORTRAIT_SIZE.x,SCREEN_PORTRAIT_SIZE.y),
                            new Point(SCREEN_PORTRAIT_SIZE.x,SCREEN_PORTRAIT_SIZE.y),
                            new Point(SCREEN_LANDSCAPE_SIZE.x,SCREEN_LANDSCAPE_SIZE.y),
                    };
                    // origin position
                    int originPosY[] = {0,0,SCREEN_PORTRAIT_SIZE.y};
                    // the position to adjust
                    int adjustY[] = {0,0,10};
                    if (this.mScreen.mExistFlag) {
                        this.mImage.DrawImage(
                                this.mScreen.mPos.x,
                                this.mChara[i].mPos.y+adjustY[i-2],
                                size[i-2].x,size[i-2].y,
                                0,originPosY[i-2],
                                this.mScreen.mBmp
                        );
                    }
                }
            }
            // level
        } else if (this.mProgress == PROGRESS_SELECT_LEVEL) {
            // each level
            for (int i = 0; i < this.mLevel.length; i++) {
                if (this.mLevel[i].mExistFlag) {
                    this.mImage.DrawScale(
                            this.mLevel[i].mPos.x,
                            this.mLevel[i].mPos.y,
                            this.mLevel[i].mSize.x,
                            this.mLevel[i].mSize.y,
                            this.mLevel[i].mOriginPos.x,
                            this.mLevel[i].mOriginPos.y,
                            this.mLevel[i].mScale,
                            this.mLevel[i].mBmp
                    );
                }
            }
            // to show confirmation text that check to the stage brief.
        } else if (this.mProgress == PROGRESS_ASK_BRIEF) {
            // text
            this.mImage.drawText("ステージ説明を確認しますか？", 85, 300, 22, Color.BLACK);
            // Response buttons
            for (int i = 0; i < 2; i++) {
                if (this.mAnswer[i].mExistFlag) {
                    this.mImage.DrawScale(
                            this.mAnswer[i].mPos.x,
                            this.mAnswer[i].mPos.y,
                            this.mAnswer[i].mSize.x,
                            this.mAnswer[i].mSize.y,
                            this.mAnswer[i].mOriginPos.x,
                            this.mAnswer[i].mOriginPos.y,
                            this.mAnswer[i].mScale,
                            this.mAnswer[i].mBmp
                    );
                }
            }
        }
        // Enemy
        if (this.mEnemy.mExistFlag) {
            this.mImage.DrawImage(
                    this.mEnemy.mPos.x,
                    this.mEnemy.mPos.y,
                    this.mEnemy.mSize.x,
                    this.mEnemy.mSize.y,
                    this.mEnemy.mOriginPos.x,
                    this.mEnemy.mOriginPos.y,
                    this.mEnemy.mBmp
            );
        }
        // player
        if (this.mPlayer.mExistFlag) {
            this.mImage.DrawImage(
                    this.mPlayer.mPos.x,
                    this.mPlayer.mPos.y,
                    this.mPlayer.mSize.x,
                    this.mPlayer.mSize.y,
                    this.mPlayer.mOriginPos.x,
                    this.mPlayer.mOriginPos.y,
                    this.mPlayer.mBmp
            );
        }
        // Menu
        this.mMenu.DrawMenu();
    }

    /*
        Release
     */
    public int Release() {
        // Release BaseCharacter class.
        for (int i = 0; i < this.mChara.length; i++) {
            // Release BaseCharacter's bitmap object.
            this.mChara[i].ReleaseCharaBmp();
            this.mChara[i] = null;
        }
        // Screen image
        this.mScreen.ReleaseCharaBmp();
        this.mScreen = null;
        // answer
        for (int i = 0; i < 2; i++) {
            this.mAnswer[i].ReleaseCharaBmp();
            this.mAnswer[i] = null;
        }
        // level
        for (int i = 0; i < this.mLevel.length; i++) {
            this.mLevel[i].ReleaseCharaBmp();
            this.mLevel[i] = null;
        }
        this.mContext = null;         // activity
        this.mImage = null;            // image object
        this.mSound.StopBGM();
        // player
        this.mPlayer.ReleaseCharaBmp();
        this.mPlayer = null;
        // enemy
        this.mEnemy.ReleaseCharaBmp();
        this.mEnemy = null;
        // Menu
        this.mMenu.ReleaseMenu();
        return SCENE_END;
    }
    /*
        Initialize the player character and enemy character
    */
    private void SetPlayerAndEnemy(int stage) {
        // screen size
        Point screen = GameView.GetScreenSize();
        int countMax[] = new int[2];
        // to diverge the initialization from selected stage.
        switch(stage) {
            case Play.STAGE_OFF_ROAD:
                // Player
                // loading the file
                this.mPlayer.LoadCharaImage(this.mContext,"offroadplayer");
                this.mPlayer.mSize.x = OffroadPlayer.PLAYER_SIZE.x;
                this.mPlayer.mSize.y = OffroadPlayer.PLAYER_SIZE.y;
                this.mPlayer.mPos.x = (screen.x-this.mPlayer.mSize.x)>>1;
                this.mPlayer.mPos.y = screen.y+100;
                this.mPlayer.mMoveX = 0.0f;
                this.mPlayer.mMoveY = PLAYER_DEFAULT_SPEED*-1;
                this.mPlayer.mExistFlag = true;
                countMax[0] = 2;
                // Enemy that is jump point
                this.mEnemy.LoadCharaImage(this.mContext,"offroadjump");
                this.mEnemy.mSize.x = OffroadObstacles.OBSTACLE_JUMP_POINT_SIZE.x;
                this.mEnemy.mSize.y = OffroadObstacles.OBSTACLE_JUMP_POINT_SIZE.y;
                this.mEnemy.mPos.x = screen.x+100;
                this.mEnemy.mPos.y = 600;
                this.mEnemy.mMoveX = ENEMY_DEFAULT_SPEED*-1;
                this.mEnemy.mMoveY = 0.0f;
                this.mEnemy.mType = Play.STAGE_OFF_ROAD;
                this.mEnemy.mExistFlag = true;
                countMax[1] = 0;
                break;
            case Play.STAGE_ROAD:
                // loading the file
                this.mPlayer.LoadCharaImage(this.mContext,"roadplayer");
                this.mPlayer.mSize.x = RoadPlayer.RUNNER_SIZE.x;
                this.mPlayer.mSize.y = RoadPlayer.RUNNER_SIZE.y;
                this.mPlayer.mPos.x = (screen.x-this.mPlayer.mSize.x)>>1;
                this.mPlayer.mPos.y = screen.y+100;
                this.mPlayer.mMoveX = 0.0f;
                this.mPlayer.mMoveY = PLAYER_DEFAULT_SPEED*-1;
                this.mPlayer.mExistFlag = true;
                countMax[0] = 4;
                // Enemy that is hurdle
                this.mEnemy.LoadCharaImage(this.mContext,"roadhurdle");
                this.mEnemy.mSize.x = RoadObstacles.HURDLE_SIZE.x;
                this.mEnemy.mSize.y = RoadObstacles.HURDLE_SIZE.y;
                this.mEnemy.mPos.x = screen.x+100;
                this.mEnemy.mPos.y = 600;
                this.mEnemy.mMoveX = ENEMY_DEFAULT_SPEED*-1;
                this.mEnemy.mMoveY = 0.0f;
                this.mEnemy.mType = Play.STAGE_ROAD;
                this.mEnemy.mExistFlag = true;
                countMax[1] = 0;
                break;
            case Play.STAGE_SEA:
                // loading the file
                this.mPlayer.LoadCharaImage(this.mContext,"swimmer");
                this.mPlayer.mSize.x = SeaPlayer.SWIMMER_SIZE.x;
                this.mPlayer.mSize.y = SeaPlayer.SWIMMER_SIZE.y;
                this.mPlayer.mPos.x = -100;
                this.mPlayer.mPos.y = PLAYER_ARRIVE_POSITION_Y;
                this.mPlayer.mMoveX = PLAYER_DEFAULT_SPEED;
                this.mPlayer.mMoveY = 0.0f;
                this.mPlayer.mExistFlag = true;
                countMax[0] = 3;
                // Enemy that is sunfish
                this.mEnemy.LoadCharaImage(this.mContext,"sunfish");
                this.mEnemy.mSize.x = SeaEnemyManager.SUNFISH_SIZE.x;
                this.mEnemy.mSize.y = SeaEnemyManager.SUNFISH_SIZE.y;
                this.mEnemy.mPos.x = screen.x+100;
                this.mEnemy.mPos.y = 600;
                this.mEnemy.mMoveX = ENEMY_DEFAULT_SPEED*-1;
                this.mEnemy.mMoveY = 0.0f;
                this.mEnemy.mType = Play.STAGE_SEA;
                this.mEnemy.mExistFlag = true;
                countMax[1] = SeaEnemyManager.ANIMATION_COMMON_COUNT_MAX;
                break;
        }
        // animation setting
        // player
        this.mPlayerAni.SetAnimation(
                0,0,
                this.mPlayer.mSize.x,
                this.mPlayer.mSize.y,
                countMax[0],10,0
        );
        // except for off-road and road, enemy
        if (stage == Play.STAGE_SEA) {
            this.mEnAni.SetAnimation(
                    0, 0,
                    this.mEnemy.mSize.x, this.mEnemy.mSize.y,
                    countMax[1], 10, 0
            );
        }
    }
    /*
        Reset player
    */
    private void ResetPlayerAndEnemy() {
        // player
        this.mPlayer.mOriginPos.x = 0;
        this.mPlayer.mOriginPos.y = 0;
        this.mPlayer.mExistFlag = false;
        this.mPlayerAni.ResetEveryAnimationSetting();
        // enemy
        this.mEnemy.mOriginPos.x = 0;
        this.mEnemy.mOriginPos.y = 0;
        this.mEnemy.mExistFlag = false;
        this.mEnAni.ResetEveryAnimationSetting();
    }
}