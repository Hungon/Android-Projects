package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 2/1/2016.
 */
public class CreditView extends Scene {

    // static variables
    // BG
    private Point           CREDIT_BG_SIZE          = new Point(640,7400);
    private float           CREDIT_BG_SCROLL_SPEED  = 2.0f;
    // credit size
    private Point           CREDIT_SIZE             = new Point(400,96);
    private int             CREDIT_STARTING_POINT_X = 700;
    private float           CREDIT_DEFAULT_MOVEX    = 5.0f;
    private int             CREDIT_SPACEY           = 700;
    private int             CREDIT_END_POINT_X      = (GameView.GetScreenSize().x - CREDIT_SIZE.x)>>1;
    // kind
    private int CREDIT_MATERIAL_PROVIDERS					= 0;
    private int CREDIT_BGM_PROVIDERS						= 1;
    private int CREDIT_SOUND_EFFECT_PROVIDERS				= 2;
    private int CREDIT_BGM_PROVIDER_TAM						= 3;
    private int CREDIT_BGM_AND_SOUND_EFFECT_PROVIDER_CIRCUIT= 4;
    private int CREDIT_SOUND_EFFECT_PROVIDER_ONJIN			= 5;
    private int CREDIT_TANKS_PROVIDERS						= 6;
    private int CREDIT_PRESENTED_OWN_NAME					= 7;
    private int CREDIT_TANKS_PLAYERS						= 8;
    private int CREDIT_KIND									= 9;
    private int CREDIT_MAX                                  = 10;

    // filed
    private Context         mContext;
    private Image           mImage;
    private BaseCharacter   mBg;
    private BaseCharacter   mCredit[] = new BaseCharacter[CREDIT_MAX];
    private Sound           mSound;
    private String          mBGMFile;
    private Menu            mMenu;

    /*
        Constructor
     */
    public CreditView(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        // background image
        this.mBg = new BaseCharacter(image);
        // credit images
        for (int i = 0; i < CREDIT_MAX; i++) {
            this.mCredit[i] = new BaseCharacter(image);
        }
        // to BGM
        this.mSound = new Sound(this.mContext);
        // menu class
        this.mMenu = new Menu(activity, image);
    }

    /*
        Initialize
     */
    public int Init() {
        // load image files
        // BG
        this.mBg.LoadCharaImage(this.mContext, "creditbg");
        // credit
        for (int i = 0; i < CREDIT_MAX; i++) {
            this.mCredit[i].LoadCharaImage(this.mContext, "credit");
        }

        // using BGM
        this.mBGMFile = "credit";

        // setting to draw
        // BG
        // get screen size
        Point screen = GameView.GetScreenSize();
        this.mBg.mPos.y = screen.y - CREDIT_BG_SIZE.y;
        this.mBg.mSize = CREDIT_BG_SIZE;
        this.mBg.mExistFlag = true;
        this.mBg.mMoveY = CREDIT_BG_SCROLL_SPEED;
        // credit images
        for (int i = 0; i < CREDIT_MAX; i++) {
            this.mCredit[i].mSize = CREDIT_SIZE;
            this.mCredit[i].mPos.x = CREDIT_STARTING_POINT_X;
            this.mCredit[i].mMoveX = CREDIT_DEFAULT_MOVEX;
            this.mCredit[i].mExistFlag = false;
        }

        // Menu
        this.mMenu.InitMenuBack(Menu.BACK_SCENE_TITLE);

        return SCENE_MAIN;
    }
    /*
        Update
     */
    public int Update() {
        // using credit images
        int creditTbl[] = {
                CREDIT_MATERIAL_PROVIDERS					,
                CREDIT_BGM_PROVIDERS						,
                CREDIT_BGM_PROVIDER_TAM						,
                CREDIT_BGM_AND_SOUND_EFFECT_PROVIDER_CIRCUIT,
                CREDIT_SOUND_EFFECT_PROVIDERS				,
                CREDIT_BGM_AND_SOUND_EFFECT_PROVIDER_CIRCUIT,
                CREDIT_SOUND_EFFECT_PROVIDER_ONJIN			,
                CREDIT_TANKS_PROVIDERS						,
                CREDIT_PRESENTED_OWN_NAME					,
                CREDIT_TANKS_PLAYERS						,
        };

        // scroll
        // get screen size
        Point screen = GameView.GetScreenSize();
        for (int i = 0; i < CREDIT_MAX; i++) {
            if (this.mCredit[i].mExistFlag) continue;
            if (i * CREDIT_SPACEY <
                (this.mBg.mSize.y - screen.y) - Math.abs(this.mBg.mPos.y)) {
                // set position to credit
                this.mCredit[i].mPos.y = this.mBg.mPos.y + CREDIT_SPACEY;
                // origin position
                this.mCredit[i].mOriginPos.y = creditTbl[i] * CREDIT_SIZE.y;
                this.mCredit[i].mExistFlag = true;
            }
        }
        // scroll to down
        this.mBg.mPos.y += this.mBg.mMoveY;
        // when reach to bottom tip, end process
        if (0 < this.mBg.mPos.y) {
            this.mBg.mPos.y = 0;
            this.mBg.mMoveY = 0.0f;
        }
        // update credit move
        for (int i = 0; i < CREDIT_MAX; i++) {
            if (this.mCredit[i].mExistFlag) {
                this.mCredit[i].mPos.x -= this.mCredit[i].mMoveX;
                // end position to draw
                if (this.mCredit[i].mPos.x < CREDIT_END_POINT_X) {
                    this.mCredit[i].mPos.x = CREDIT_END_POINT_X;
                }
            }
        }

        // update menu
        if (this.mMenu.UpdateMenu()) return SCENE_RELEASE;

        // play BGM
        this.mSound.PlayBGM(this.mBGMFile);

        return SCENE_MAIN;
    }
    /*
        Draw
     */
    public void Draw() {
        // BG
        if (this.mBg.mExistFlag) {
            this.mImage.DrawImageFast(
                this.mBg.mPos.x,
                this.mBg.mPos.y,
                this.mBg.mBmp);
        }
        // credit
        for (int i = 0; i < CREDIT_MAX; i++) {
            if (this.mCredit[i].mExistFlag) {
                this.mImage.DrawScale(
                        this.mCredit[i].mPos.x,
                        this.mCredit[i].mPos.y - this.mBg.mPos.y,
                        this.mCredit[i].mSize.x,
                        this.mCredit[i].mSize.y,
                        this.mCredit[i].mOriginPos.x,
                        this.mCredit[i].mOriginPos.y,
                        this.mCredit[i].mScale,
                        this.mCredit[i].mBmp
                );
            }
        }
        // menu
        this.mMenu.DrawMenu();
    }
    /*
        Release
    */
    public int Release() {
        this.mContext = null;
        this.mImage = null;
        // Base character
        // for BG
        this.mBg.ReleaseCharaBmp();
        this.mBg = null;
        // credit
        for (int i = 0; i < CREDIT_MAX; i++) {
            this.mCredit[i].ReleaseCharaBmp();
            this.mCredit[i] = null;
        }
        // BGM
        this.mSound.StopBGM();
        this.mSound = null;
        // Menu
        this.mMenu.ReleaseMenu();

        return SCENE_END;
    }
}