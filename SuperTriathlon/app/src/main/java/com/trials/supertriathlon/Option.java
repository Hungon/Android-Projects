package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Created by USER on 2/11/2016.
 */
public class Option extends Scene implements HasRecords, HasScenes, HasProperties {

    // static variables
    // size
    public final static Point OPTION_TITLE_SIZE = new Point(140, 55);
    private final static Point OPTION_TITLE_POSITION = new Point(
            (GameView.GetScreenSize().x - OPTION_TITLE_SIZE.x)>>1, 50
    );

    //　Each buttons setting
    // button space Y
    private final static int BUTTON_SPACE_Y = 35;
    // Best record button
    public final static Point BEST_RECORD_BUTTON_SIZE = new Point(250, 55);          // size
    private final static Point BEST_RECORD_BUTTON_POSITION =
            new Point((GameView.GetScreenSize().x - BEST_RECORD_BUTTON_SIZE.x)>>1, 330);  // position
    // Reset record button
    private final static Point RESET_RECORD_BUTTON_SIZE = new Point(250, 55);          // size
    private final static Point RESET_RECORD_BUTTON_POSITION =
            new Point((GameView.GetScreenSize().x - RESET_RECORD_BUTTON_SIZE.x)>>1,
                    BEST_RECORD_BUTTON_POSITION.y + BEST_RECORD_BUTTON_SIZE.y + BUTTON_SPACE_Y);  // position

    // Stage images
    private final static int        STAGE_DEFAULT_ALPHA     = 100;
    private final static int        STAGE_SELECTED_ALPHA    = 255;

    // Answer
    // yes and no
    public final static Point ANSWER_SIZE = new Point(55, 40);

    // kind of image
    private final static int IMAGE_TITLE        = 0;
    private final static int IMAGE_BEST_RECORD  = 1;
    private final static int IMAGE_RESET_RECORD = 2;
    private final static int IMAGE_KIND         = 3;
    
    // kind of the stage
    private final static byte STAGE_BIT_OFF_ROAD    = 0x01;
    private final static byte STAGE_BIT_ROAD        = 0x02;
    private final static byte STAGE_BIT_SEA         = 0x04;

    // Interval time
    private final static int    INTERVAL_TIME = 40;

    // response flag
    private final static int    RESPONSE_NOTHING        = 0;
    private final static int    RESPONSE_SELECT_STAGE   = 1;
    private final static int    RESPONSE_WAITING        = 2;
    private final static int    RESPONSE_DO_RESET       = 3;
    private final static int    RESPONSE_RETURN_TO_OPTION_MENU = 4;

    // filed
    private Image               mImage;              // image object
    private Context             mContext;           // activity
    private BaseCharacter       mChara[] = new BaseCharacter[IMAGE_KIND];
    private Sound               mSound[] = new Sound[2];
    private String              mBGMfileName;
    private Menu                mMenu;
    private BaseCharacter       mAnswer[] = new BaseCharacter[2];
    private int                 mResponseType;       // the response that diverge process from the type.
    private int                 mResponseTypeNext;
    private int                 mIntervalTime;       // to make the blank time a while.
    private int                 mVariableInterval;
    private BaseCharacter       mStage[] = new BaseCharacter[3];
    private byte                mSelectStage;
    private BaseCharacter       mOk;
    
    // constructor
    public Option(Context context, Image image) {
        // get image object
        this.mImage = image;
        // get activity
        this.mContext = context;
        // Initialize BaseCharacter class.
        for (int i = 0; i < IMAGE_KIND; i++) {
            this.mChara[i] = new BaseCharacter(this.mImage);
        }
        // set sound
        for (int i = 0; i < this.mSound.length; i++) this.mSound[i] = new Sound(this.mContext);
        // Menu class
        this.mMenu = new Menu(context, image);
        // for answer
        for (int i = 0; i < 2; i++) this.mAnswer[i] = new BaseCharacter(image);
        // Each stages
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i] = new BaseCharacter(image);
        }
        // Ok button
        this.mOk = new BaseCharacter(image);
    }

    // each override functions
    // to initialize
    public int Init() {

        // using image files' name.
        String fileName = "optiontitles";
        // load image file.
        for (int i = 0; i < IMAGE_KIND; i++) {
            this.mChara[i].LoadCharaImage(this.mContext, fileName);
        }
        // Answer
        for (int i = 0; i < 2; i++) this.mAnswer[i].LoadCharaImage(this.mContext, "menu");
        // Stages
        for (BaseCharacter stage: this.mStage) stage.LoadCharaImage(this.mContext, "stages");
        // OK button
        this.mOk.LoadCharaImage(this.mContext, "accept");
        
        // for response
        this.mResponseTypeNext = this.mResponseType = RESPONSE_NOTHING;
        // interval time
        this.mIntervalTime = 0;
        // the time that interrupt with process.
        this.mVariableInterval = INTERVAL_TIME;
        // select the stage to reset the best record
        this.mSelectStage = 0;
        // to make the blank time
        this.mStage[1].mTime = this.mStage[0].mTime = 0;

        // using BGM
        this.mBGMfileName = "selectmode";
        // using SE
        String seFiles[] = {"click", "cancel"};
        // create SE
        for (int i = 0; i < seFiles.length; i++) this.mSound[i].CreateSound(seFiles[i]);

        /*
            Set each images.
         */

        // Each buttons setting
        //common setting
        int cnt = 0;
        for (int i = 0; i < IMAGE_KIND; i++) {
            this.mChara[i].mExistFlag = true;
            this.mChara[i].mScale = 1.0f;
            this.mChara[i].mOriginPos.y = cnt * OPTION_TITLE_SIZE.y;
            cnt++;
        }
        // title
        this.mChara[IMAGE_TITLE].mPos.x = OPTION_TITLE_POSITION.x;
        this.mChara[IMAGE_TITLE].mPos.y = OPTION_TITLE_POSITION.y;
        this.mChara[IMAGE_TITLE].mSize.x = OPTION_TITLE_SIZE.x;
        this.mChara[IMAGE_TITLE].mSize.y = OPTION_TITLE_SIZE.y;
        // view best record button
        this.mChara[IMAGE_BEST_RECORD].mPos.x = BEST_RECORD_BUTTON_POSITION.x;
        this.mChara[IMAGE_BEST_RECORD].mPos.y = BEST_RECORD_BUTTON_POSITION.y;
        this.mChara[IMAGE_BEST_RECORD].mSize.x = BEST_RECORD_BUTTON_SIZE.x;
        this.mChara[IMAGE_BEST_RECORD].mSize.y = BEST_RECORD_BUTTON_SIZE.y;
        // reset button
        this.mChara[IMAGE_RESET_RECORD].mPos.x = RESET_RECORD_BUTTON_POSITION.x;
        this.mChara[IMAGE_RESET_RECORD].mPos.y = RESET_RECORD_BUTTON_POSITION.y;
        this.mChara[IMAGE_RESET_RECORD].mSize.x = RESET_RECORD_BUTTON_SIZE.x;
        this.mChara[IMAGE_RESET_RECORD].mSize.y = RESET_RECORD_BUTTON_SIZE.y;
        // Answer
        // common
        Point screen = GameView.GetScreenSize();
        for (int i = 0; i < 2; i++) {
            this.mAnswer[i].mSize.x = ANSWER_SIZE.x;
            this.mAnswer[i].mSize.y = ANSWER_SIZE.y;
            this.mAnswer[i].mOriginPos.y = Menu.MENU_BUTTON_SIZE.y + this.mAnswer[i].mSize.y*i;
            this.mAnswer[i].mPos.y = screen.y>>1;
        }
        // yes position
        this.mAnswer[0].mPos.x = (screen.x>>1) - (this.mAnswer[0].mSize.x + 30);
        // no position
        this.mAnswer[1].mPos.x = (screen.x>>1) + 30;

        // Each stages
        // common setting
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i].mSize.y = SelectMode.STAGE_BUTTON_SIZE.y;
            this.mStage[i].mPos.x = SelectMode.STAGE_BUTTON_POSITION.x;
            this.mStage[i].mPos.y = SelectMode.STAGE_BUTTON_POSITION.y+((this.mStage[i].mSize.y+20)*i);
            this.mStage[i].mOriginPos.y = this.mStage[i].mSize.y*i;
            this.mStage[i].mExistFlag = true;
            this.mStage[i].mAlpha = STAGE_DEFAULT_ALPHA;
        }
        // off-road
        this.mStage[0].mSize.x = SelectMode.STAGE_BUTTON_SIZE.x;
        // road
        this.mStage[1].mSize.x = SelectMode.ROAD_WIDTH;
        // sea
        this.mStage[2].mSize.x = SelectMode.SEA_WIDTH;
        
        // OK button
        this.mOk.mSize.x = BriefStage.OK_SIZE.x;
        this.mOk.mSize.y = BriefStage.OK_SIZE.y;
        this.mOk.mExistFlag = false;
        this.mOk.mPos.x = screen.x-(BriefStage.OK_SIZE.x+10);
        this.mOk.mPos.y = screen.y-(BriefStage.OK_SIZE.y+20);

        // Initialize menu
        this.mMenu.InitMenuBack(Menu.BACK_SCENE_TITLE);

        // if ( false )		return	CScene.SCENE_ERROR;
        return Scene.SCENE_MAIN;
    }

    // to update
    public int Update() {
        // to transition scene
        // Make the interval time
        if (this.mResponseType == this.mResponseTypeNext) {
            // Get key event
            int keyEvent = GameView.GetKeyEvent();
            // when pressed back-key, back to preview.
            if (keyEvent == KeyEvent.KEYCODE_BACK) {
                if (0 < this.mResponseType) {
                    this.mResponseTypeNext--;
                }
            }

            // Diverge process from the flag that transition to scene.
            switch(this.mResponseType) {
                // Select option menu
                case RESPONSE_NOTHING:
                    // To record view
                    int scene = SCENE_RECORD_VIEW;
                    // when pressed start button, transition to main scene.
                    if (this.mChara[IMAGE_BEST_RECORD].mExistFlag) {
                        if (Collision.CheckTouch(
                                this.mChara[IMAGE_BEST_RECORD].mPos.x, this.mChara[IMAGE_BEST_RECORD].mPos.y,
                                this.mChara[IMAGE_BEST_RECORD].mSize.x, this.mChara[IMAGE_BEST_RECORD].mSize.y,
                                this.mChara[IMAGE_BEST_RECORD].mScale)) {
                            Wipe.CreateWipe(scene, Wipe.TYPE_PENETRATION);
                            // play OK
                            this.mSound[0].PlaySE();
                            return SCENE_RELEASE;
                        }
                    }
                    // Reset button
                    if (this.mChara[IMAGE_RESET_RECORD].mExistFlag) {
                        if (Collision.CheckTouch(
                                this.mChara[IMAGE_RESET_RECORD].mPos.x, this.mChara[IMAGE_RESET_RECORD].mPos.y,
                                this.mChara[IMAGE_RESET_RECORD].mSize.x, this.mChara[IMAGE_RESET_RECORD].mSize.y,
                                this.mChara[IMAGE_RESET_RECORD].mScale)) {
                            // play OK
                            this.mSound[0].PlaySE();
                            // to select the stage to reset the best record
                            this.mResponseTypeNext = RESPONSE_SELECT_STAGE;
                        }
                    }
                    break;
                // select the stage to reset record.
                case RESPONSE_SELECT_STAGE:
                    // kind of stage
                    byte stage[] = {STAGE_BIT_OFF_ROAD, STAGE_BIT_ROAD, STAGE_BIT_SEA};
                    // to select the stage
                    for (int i = 0; i < this.mStage.length; i++) {
                        if (this.mStage[0].mTime == 0 &&
                            MotionEvent.ACTION_DOWN == GameView.GetTouchAction() &&
                            Collision.CheckTouch(
                                this.mStage[i].mPos.x, this.mStage[i].mPos.y,
                                this.mStage[i].mSize.x, this.mStage[i].mSize.y,
                                this.mStage[i].mScale)) {
                            // to make the blank time
                            this.mStage[0].mTime = 10;
                            // substitute stage bit number to the variable
                            if (this.mStage[i].mAlpha == STAGE_DEFAULT_ALPHA) {
                                this.mSelectStage |= stage[i];
                                // change alpha
                                this.mStage[i].mAlpha = STAGE_SELECTED_ALPHA;
                            } else {
                                this.mSelectStage &= ~stage[i];
                                // change alpha
                                this.mStage[i].mAlpha = STAGE_DEFAULT_ALPHA;
                            }
                            // to show OK button
                            boolean accept = false;
                            for (BaseCharacter ch : this.mStage) {
                                if (ch.mAlpha == STAGE_SELECTED_ALPHA) {
                                    accept = true;
                                    break;
                                }
                            }
                            this.mOk.mExistFlag = accept;
                        }
                    }
                    // to make the blank time
                    if (0 < this.mStage[0].mTime) {
                        this.mStage[1].mTime++;
                    }
                    if (this.mStage[0].mTime < this.mStage[1].mTime) {
                        this.mStage[1].mTime = this.mStage[0].mTime = 0;
                    }
                    // accept button to next progress
                    if (this.mOk.mExistFlag) {
                        if (Collision.CheckTouch(
                                this.mOk.mPos.x, this.mOk.mPos.y,
                                this.mOk.mSize.x, this.mOk.mSize.y,
                                this.mOk.mScale)) {
                            this.mOk.mExistFlag = false;
                            this.mStage[1].mTime = this.mStage[0].mTime = 0;
                            // next progress
                            this.mResponseTypeNext = RESPONSE_WAITING;
                            // to show answer buttons
                            for (BaseCharacter an: this.mAnswer) an.mExistFlag = true;
                        }
                    }
                    break;
                // to wait to response to reset the best record or not.
                case RESPONSE_WAITING:
                    // yes is do reset, no is back to option menu
                    int response[] = {RESPONSE_DO_RESET, RESPONSE_RETURN_TO_OPTION_MENU};
                    // to answer, yes or no
                    for (int i = 0; i < 2; i++) {
                        if (GameView.GetTouchAction() == MotionEvent.ACTION_DOWN && this.mAnswer[i].mExistFlag)
                        if (Collision.CheckTouch(
                                this.mAnswer[i].mPos.x, this.mAnswer[i].mPos.y,
                                this.mAnswer[i].mSize.x, this.mAnswer[i].mSize.y,
                                this.mAnswer[i].mScale)) {
                            // play SE
                            this.mSound[i].PlaySE();
                            // substitute type to the flag variable.
                            this.mResponseTypeNext = response[i];
                        }
                    }
                    break;
                // Reset the best record.
                case RESPONSE_DO_RESET:
                    SystemManager systemManager = new SystemManager(this.mContext);
                    // reset the stage's records user selected
                    if ((this.mSelectStage&STAGE_BIT_OFF_ROAD)==STAGE_BIT_OFF_ROAD) {
                        systemManager.resetAllTheBestRecords(STAGE_OFF_ROAD);
                    }
                    if ((this.mSelectStage&STAGE_BIT_ROAD)==STAGE_BIT_ROAD) {
                        systemManager.resetAllTheBestRecords(STAGE_ROAD);
                    }
                    if ((this.mSelectStage&STAGE_BIT_SEA)==STAGE_BIT_SEA) {
                        systemManager.resetAllTheBestRecords(STAGE_SEA);
                    }
                    // substitute flag to
                    this.mResponseTypeNext = RESPONSE_RETURN_TO_OPTION_MENU;
                    // not to show answer buttons
                    for (BaseCharacter an: this.mAnswer) an.mExistFlag = false;
                    // change interval time to show text a while.
                    this.mVariableInterval = 200;
                    break;
                // Back to the selection of option menu.
                case RESPONSE_RETURN_TO_OPTION_MENU:
                    // to show each buttons
                    for (int i = IMAGE_BEST_RECORD; i <= IMAGE_RESET_RECORD; i++)
                        this.mChara[i].mExistFlag = true;
                    // substitute flag to
                    this.mResponseTypeNext = RESPONSE_NOTHING;
                    break;
            }
        } else {
            // count time to make the interval time
            this.mIntervalTime++;
            if (this.mVariableInterval <= this.mIntervalTime) {
                this.mIntervalTime = 0;
                // reset interval time to default
                this.mVariableInterval = INTERVAL_TIME;
                // when back to select option, to show the reset button.
                if (this.mResponseTypeNext == RESPONSE_NOTHING ||
                    this.mResponseTypeNext == RESPONSE_RETURN_TO_OPTION_MENU) {
                    // back to default position-Y
                    this.mChara[IMAGE_RESET_RECORD].mPos.y = RESET_RECORD_BUTTON_POSITION.y;
                    // to show the best record
                    this.mChara[IMAGE_TITLE].mExistFlag = true;
                    this.mChara[IMAGE_BEST_RECORD].mExistFlag = true;
                }
                // if next progress is select stage, to reset each values.
                if (this.mResponseTypeNext == RESPONSE_SELECT_STAGE) {
                    // not to show the best record
                    this.mChara[IMAGE_TITLE].mExistFlag = false;
                    this.mChara[IMAGE_BEST_RECORD].mExistFlag = false;
                    // to change the position-Y
                    this.mChara[IMAGE_RESET_RECORD].mPos.y = OPTION_TITLE_POSITION.y;
                    // not show ok button
                    this.mOk.mExistFlag = false;
                    this.mSelectStage = 0;
                    // reset stage's alpha
                    for (BaseCharacter ch : this.mStage) {
                        if (ch.mAlpha == STAGE_SELECTED_ALPHA) {
                            ch.mAlpha = STAGE_DEFAULT_ALPHA;
                        }
                    }
                }
                // set the response to next
                this.mResponseType = this.mResponseTypeNext;
            }
        }

        // Update Menu
        if (this.mMenu.UpdateMenu()) return SCENE_RELEASE;
        // Play background music
        this.mSound[0].PlayBGM(this.mBGMfileName);

        return Scene.SCENE_MAIN;
    }

    /*
        Draw
     */
    public void Draw() {

        // Title logo and buttons
        for (int i = 0; i < IMAGE_KIND; i++) {
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
            }
        }
        // Select stage
        if (this.mResponseType == RESPONSE_SELECT_STAGE) {
            for (BaseCharacter stage: this.mStage) {
                if (stage.mExistFlag) {
                    this.mImage.DrawAlpha(
                            stage.mPos.x,
                            stage.mPos.y,
                            stage.mSize.x,
                            stage.mSize.y,
                            stage.mOriginPos.x,
                            stage.mOriginPos.y,
                            stage.mAlpha,
                            stage.mBmp
                    );
                }
            }
            // OK button
            if (this.mOk.mExistFlag) {
                this.mImage.DrawScale(
                        this.mOk.mPos.x,
                        this.mOk.mPos.y,
                        this.mOk.mSize.x,
                        this.mOk.mSize.y,
                        this.mOk.mOriginPos.x,
                        this.mOk.mOriginPos.y,
                        this.mOk.mScale,
                        this.mOk.mBmp
                );
            }
            this.mImage.drawText("最高記録を消したいステージを選んでください。",25,200,19, Color.WHITE);
        }
        // Answer
        else if (this.mResponseType == RESPONSE_WAITING) {
            // show text that ask to reset the best record.
            this.mImage.drawText("本当に選択したステージの最高記録を消しますか？",20,300,19, Color.WHITE);
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
        } else if (this.mResponseType == RESPONSE_DO_RESET) {
            // show text
            this.mImage.drawText("選択したステージの最高記録を消しました。",35,300,22,Color.WHITE);
        }
        // Menu
        this.mMenu.DrawMenu();
    }

    // to release
    public int Release() {
        // Release BaseCharacter class.
        for (int i = 0; i < this.mChara.length; i++) {
            // Release BaseCharacter's bitmap object.
            this.mChara[i].ReleaseCharaBmp();
            this.mChara[i] = null;
        }
        // Answer
        for (int i = 0; i < 2; i++) {
            this.mAnswer[i].ReleaseCharaBmp();
            this.mAnswer[i] = null;
        }
        // Stages
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i].ReleaseCharaBmp();
            this.mStage[i] = null;
        }
        // OK
        this.mOk.ReleaseCharaBmp();
        this.mOk = null;
        this.mContext = null;              // activity
        this.mImage = null;                // image object
        for (int i = 0; i < this.mSound.length; i++) {
            this.mSound[i].StopBGM();
            this.mSound[i] = null;
        }
        // Menu
        this.mMenu.ReleaseMenu();

        return Scene.SCENE_END;
    }
}