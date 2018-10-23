package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 1/26/2016.
 */
public class Opening extends Scene implements HasScenes, HasButtons {

    // static variables
    // background image
    private final static Point BG_SIZE = new Point(480, 800);
    // title logo
    private final static Point  TITLE_LOGO_SIZE = new Point(460, 60);            // size
    private final static Point  TITLE_LOGO_POSITION =
            new Point((GameView.GetScreenSize().x - TITLE_LOGO_SIZE.x)>>1, 50);     // position
    private final static int    SCALE_UP_DURATION = 50;                         // duration to scale up
    
    // buttons' setting
    private final static int    STARTING_BUTTON_POSITION_Y = 330;
    private final static int    BUTTON_SPACE_Y = 35;
    
    // filed
    private Image       mImage;                              // image object
    private Context     mContext;                           // activity
    private BaseCharacter mBg[];     // BG
    private BaseCharacter mButtons[];// buttons
    private Sound       mSound;                             // sound object to play SE or background music
    private String      mBGMfileName;


    /*
        Constructor
    */
    public Opening(Context context, Image image) {
        // get image object
        this.mImage = image;
        // get activity
        this.mContext = context;
        this.mBg = new BaseCharacter[2];
        for (int i = 0; i < this.mBg.length; i++) {
            this.mBg[i] = new BaseCharacter(this.mImage);
        }
        this.mButtons = new BaseCharacter[4];
        for (int i=0;i<this.mButtons.length;i++) {
            this.mButtons[i] = new BaseCharacter(this.mImage);
        }
        // set sound
        this.mSound = new Sound(this.mContext);
    }


    /*
        Initialize
     */
    public int Init() {

        // using image files' name.
        String bgFile[] = {
                "opbg",             // background image
                "optitlelogo",      // title logo
        };
        // load image file.
        for (int i = 0; i < this.mBg.length; i++) {
            this.mBg[i].LoadCharaImage(this.mContext, bgFile[i]);
        }
        // using BGM
        this.mBGMfileName = "opening";
        // using SE
        String seFiles[] = {"click"};
        // create SE
        for(int i = 0; i < seFiles.length; i++) this.mSound.CreateSound(seFiles[i]);
        // background image
        this.mBg[0].mSize = BG_SIZE;
        // title logo
        // position
        this.mBg[1].mPos = TITLE_LOGO_POSITION;
        // size
        this.mBg[1].mSize = TITLE_LOGO_SIZE;
        this.mBg[1].mScale = 0.0f;             // scale rate
        // drawing flag
        this.mBg[1].mExistFlag = true;

        // Each buttons setting
        Point screen = GameView.GetScreenSize();
        for (int i = 0; i < this.mButtons.length; i++) {
            this.mButtons[i].mPos.x = (screen.x-BUTTONS_SIZE[i].x)>>1;
            this.mButtons[i].mPos.y = STARTING_BUTTON_POSITION_Y+((BUTTONS_SIZE[i].y+BUTTON_SPACE_Y)*i);
            this.mButtons[i].mSize.x = BUTTONS_SIZE[i].x;
            this.mButtons[i].mSize.y = BUTTONS_SIZE[i].y;
            this.mButtons[i].mExistFlag = true;
            this.mButtons[i].mScale = 1.0f;
            this.mButtons[i].mOriginPos.y = i*BUTTONS_SIZE[i].y;
            this.mButtons[i].LoadCharaImage(this.mContext,BUTTONS_FILE);
        }
        // if ( false )		return	CScene.SCENE_ERROR;
        return Scene.SCENE_MAIN;
    }

    /*
        Update
     */
    public int Update() {
        // to transition scene
        // if ( false )	return	CScene.SCENE_RELEASE;
        // if ( false )			return	CScene.SCENE_ERROR;

        // Title logo is gradually increased scale rate.
        if (SCALE_UP_DURATION <= this.mBg[1].mTime && this.mBg[1].mScale < 1.0f) {
            this.mBg[1].mScale += 0.1f;
        } else if (1.0f <= this.mBg[1].mScale){
            this.mBg[1].mScale = 1.0f;
        }
        // count time
        if (this.mBg[1].mTime < SCALE_UP_DURATION) this.mBg[1].mTime++;

        // scene buttons
        int scene[] = {
                SCENE_SELECT,
                SCENE_OPTION,
                SCENE_CREDIT,
                SCENE_RANKING
        };
        // when pressed start button, transition to main scene.
        for (int i = 0; i < this.mButtons.length; i++) {
            if (Collision.CheckTouch(
                    this.mButtons[i].mPos.x, this.mButtons[i].mPos.y,
                    this.mButtons[i].mSize.x, this.mButtons[i].mSize.y,
                    this.mButtons[i].mScale)) {
                Wipe.CreateWipe(scene[i], Wipe.TYPE_PENETRATION);
                // play SE
                this.mSound.PlaySE();
                return SCENE_RELEASE;
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
        // BG and title
        this.mImage.DrawImageFast(
                this.mBg[0].mPos.x,
                this.mBg[0].mPos.y,
                this.mBg[0].mBmp
        );
        if (this.mBg[1].mExistFlag) {
            this.mImage.DrawScale(
                    this.mBg[1].mPos.x,
                    this.mBg[1].mPos.y,
                    this.mBg[1].mSize.x,
                    this.mBg[1].mSize.y,
                    this.mBg[1].mOriginPos.x,
                    this.mBg[1].mOriginPos.y,
                    this.mBg[1].mScale,
                    this.mBg[1].mBmp

            );
        }
        // each button
        for (BaseCharacter bt: this.mButtons) {
            if (bt.mExistFlag) {
                this.mImage.DrawScale(
                        bt.mPos.x,
                        bt.mPos.y,
                        bt.mSize.x,
                        bt.mSize.y,
                        bt.mOriginPos.x,
                        bt.mOriginPos.y,
                        bt.mScale,
                        bt.mBmp
                );
            }
        }
    }

    /*
        Release
     */
    public int Release() {
        // BG and title
        for (int i = 0; i < this.mBg.length; i++) {
            this.mBg[i].ReleaseCharaBmp();
            this.mBg[i] = null;
        }
        // each button
        for (int i = 0; i < this.mButtons.length; i++) {
            this.mButtons[i].ReleaseCharaBmp();
            this.mButtons[i] = null;
        }
        this.mContext = null;         // activity
        this.mImage = null;            // image object
        this.mSound.StopBGM();
        return Scene.SCENE_END;
    }
}