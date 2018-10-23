package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * Created by USER on 1/30/2016.
 */
public class Menu implements HasScenes {
    // static variables
    // buttons
    // to-title button
    private final static Point TOTITLE_BUTTON_SIZE = new Point(80, 80);          // size
    // Menu
    public final static Point MENU_BUTTON_SIZE = new Point(80, 80);          // size
    // Retry
    public final static int RETRY_BUTTON_WIDTH = 90;        // size
    // To-title
    public final static int TO_TITLE_BUTTON_WIDTH = 100;    // size

    // Menu list type
    public enum MENU_TYPE {
        MENU_TYPE_LIST,
        MENU_TYPE_BACK,
    }
    // back scene list
    public static int BACK_SCENE_TITLE  = SCENE_OPENING;
    public static int BACK_SCENE_OPTION = SCENE_OPTION;
    public static int BACK_SCENE_SELECT = SCENE_SELECT;


    // transition to scene in menu list.
    private enum MENU_SCENE {
        MENU_SCENE_NOTHING,
        MENU_SCENE_INIT_LIST,
        MENU_SCENE_UPDATE_LIST,
        MENU_SCENE_RESPONSE,
        MENU_SCENE_TRANSITION,
    }
    // default interval time
    private final static int    INTERVAL_DEFAULT_TIME = 40;
    // filed
    private Context             mContext;
    private Image               mImage;
    private BaseCharacter       mMenu[];
    private MENU_TYPE           mMenuType;
    private MENU_SCENE          mMenuScene;
    private MENU_SCENE          mMenuSceneNext;
    private int                 mIntervalTime;
    private int                 mTransitionScene;
    private Sound               mSound[];
    
    /*
        Constructor
     */
    public Menu(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
    }

    /*
        Initialize menu list
     */
    public void InitMenuList() {

        // type
        this.mMenuType = MENU_TYPE.MENU_TYPE_LIST;

        // The flag that transition to scene in menu
        this.mMenuScene = this.mMenuSceneNext = MENU_SCENE.MENU_SCENE_NOTHING;
        // Interval time
        this.mIntervalTime = 0;
        // Transition to the scene
        this.mTransitionScene = SCENE_NOTHING;

        // Allot memory to each buttons
        this.mMenu = new BaseCharacter[5];
        for (int i = 0; i < this.mMenu.length; i++) this.mMenu[i] = new BaseCharacter(this.mImage);
        // sound
        this.mSound = new Sound[2];
        for (int i = 0; i < this.mSound.length; i++) this.mSound[i] = new Sound(this.mContext);

        // using images
        String imageFiles[] = {
                "menu",         // menu list
        };
        // load image file.
        for (int i = 0; i < this.mMenu.length; i++) this.mMenu[i].LoadCharaImage(mContext, imageFiles[0]);

        // using sound effect
        String se[] = {
            "click",
            "cancel"
        };
        // create SE
        for (int i = 0; i < this.mSound.length; i++) this.mSound[i].CreateSound(se[i]);

        // Menu button
        Point screen = GameView.GetScreenSize();
        Point menuPos = new Point();
        // change menu position by current screen size
        if (screen.x == 480) {  // when portrait
            menuPos.x = 10;
            menuPos.y = 700;
        } else if (screen.x == 800) {   // when landscape
            menuPos.x = 10;
            menuPos.y = 10;
        }
        this.mMenu[0].mSize.x = TOTITLE_BUTTON_SIZE.x;
        this.mMenu[0].mSize.y = TOTITLE_BUTTON_SIZE.y;
        this.mMenu[0].mPos.x = menuPos.x;
        this.mMenu[0].mPos.y = menuPos.y;
        this.mMenu[0].mExistFlag = true;
        // Answer buttons
        // common setting
        for (int i = 1; i < this.mMenu.length; i++) {
            this.mMenu[i].mSize.y = Option.ANSWER_SIZE.y;
            this.mMenu[i].mOriginPos.y = MENU_BUTTON_SIZE.y + Option.ANSWER_SIZE.y*(i-1);
        }
        // yes position
        this.mMenu[1].mSize.x = Option.ANSWER_SIZE.x;
        this.mMenu[1].mPos.x = (screen.x / 2) - (this.mMenu[1].mSize.x + 30);
        this.mMenu[1].mPos.y = screen.y / 2;
        // no position
        this.mMenu[2].mSize.x = Option.ANSWER_SIZE.x;
        this.mMenu[2].mPos.x = (screen.x / 2) + 30;
        this.mMenu[2].mPos.y = screen.y / 2;
        // Retry button
        // width
        this.mMenu[3].mSize.x = RETRY_BUTTON_WIDTH;
        // position
        this.mMenu[3].mPos.x = (screen.x - TO_TITLE_BUTTON_WIDTH)>>1;
        this.mMenu[3].mPos.y = (screen.y>>1)-this.mMenu[3].mSize.y-20;
        // To-title button
        // width
        this.mMenu[4].mSize.x = TO_TITLE_BUTTON_WIDTH;
        // position
        this.mMenu[4].mPos.x = (screen.x - TO_TITLE_BUTTON_WIDTH)>>1;
        this.mMenu[4].mPos.y = this.mMenu[3].mPos.y+this.mMenu[3].mSize.y+20;
    }


    /*
        Initialize back button
    */
    public void InitMenuBack(int nextScene) {

        // back scene list
        int sceneList[] = {
                SCENE_OPENING,
                SCENE_OPTION,
                SCENE_SELECT
        };
        int origin = 0;
        for (int scene: sceneList) {
            if (nextScene == scene) break;
            origin++;
        }

        // type
        this.mMenuType = MENU_TYPE.MENU_TYPE_BACK;
        // Transition to the scene
        this.mTransitionScene = nextScene;

        // Allot memory to each buttons
        this.mMenu = new BaseCharacter[1];
        for (int i = 0; i < this.mMenu.length; i++) this.mMenu[i] = new BaseCharacter(this.mImage);
        // sound
        this.mSound = new Sound[2];
        for (int i = 0; i < this.mSound.length; i++) this.mSound[i] = new Sound(this.mContext);

        // using images
        String imageFiles[] = {
                "backmenu",
        };
        // load image file.
        this.mMenu[0].LoadCharaImage(mContext, imageFiles[0]);

        // using sound effect
        String se = "click";
        // create SE
        this.mSound[0].CreateSound(se);

        // Back button
        Point screen = GameView.GetScreenSize();
        Point menuPos = new Point();
        // change menu position by current screen size
        if (screen.x == 480) {  // when portrait
            menuPos.x = 10;
            menuPos.y = 700;
        } else if (screen.x == 800) {   // when landscape
            menuPos.x = 10;
            menuPos.y = 10;
        }
        this.mMenu[0].mSize.x = TOTITLE_BUTTON_SIZE.x;
        this.mMenu[0].mSize.y = TOTITLE_BUTTON_SIZE.y;
        this.mMenu[0].mPos.x = menuPos.x;
        this.mMenu[0].mPos.y = menuPos.y;
        this.mMenu[0].mExistFlag = true;
        this.mMenu[0].mOriginPos.y = this.mMenu[0].mSize.y*origin;
    }

    /*
        Update
     */
    public boolean UpdateMenu() {

        // Diverge process from the menu type.
        if(this.mMenuType.equals(MENU_TYPE.MENU_TYPE_LIST)) {
            if (this.mMenuScene.equals(this.mMenuSceneNext)) {
                // To diverge scene process from the flag.
                switch (this.mMenuSceneNext) {
                    // Play the race
                    case MENU_SCENE_NOTHING:
                        // when pressed the button, to show list buttons.
                        if (GameView.GetTouchAction() == MotionEvent.ACTION_DOWN && this.mMenu[0].mExistFlag) {
                            if (Collision.CheckTouch(
                                    this.mMenu[0].mPos.x, this.mMenu[0].mPos.y,
                                    this.mMenu[0].mSize.x, this.mMenu[0].mSize.y,
                                    this.mMenu[0].mScale)) {
                                // change image to Back
                                this.mMenu[0].mOriginPos.y = 240;
                                // Set scene
                                this.mMenuScene = MENU_SCENE.MENU_SCENE_INIT_LIST;
                                // to stop the race
                                Scene.SetAvailableToPlay(false);
                                // Play se
                                this.mSound[0].PlaySE();
                            }
                        }
                        break;
                    // Initialize list view
                    case MENU_SCENE_INIT_LIST:
                        this.InitListView();
                        break;
                    // Update list view
                    case MENU_SCENE_UPDATE_LIST:
                        this.UpdateListView();
                        break;
                    // Response process
                    case MENU_SCENE_RESPONSE:
                        this.UpdateResponse();
                        break;
                    // Transition to the scene
                    case MENU_SCENE_TRANSITION:
                        // create wipe and transition to scene.
                        Wipe.CreateWipe(this.mTransitionScene, Wipe.TYPE_PENETRATION);
                        return true;
                }
            } else {
                // make the interval time a while.
                // count time
                this.mIntervalTime++;
                // when reach the set time, substitute the next scene to m_MenuSceneNext.
                if (INTERVAL_DEFAULT_TIME <= this.mIntervalTime) {
                    // reset time
                    this.mIntervalTime = 0;
                    // set the scene
                    this.mMenuSceneNext = this.mMenuScene;
                }
            }
            // when pressed the button, back to race anyway.
            if (!this.mMenuSceneNext.equals(MENU_SCENE.MENU_SCENE_NOTHING)) {
                if (GameView.GetTouchAction() == MotionEvent.ACTION_DOWN && this.mMenu[0].mExistFlag) {
                    if (Collision.CheckTouch(
                            this.mMenu[0].mPos.x, this.mMenu[0].mPos.y,
                            this.mMenu[0].mSize.x, this.mMenu[0].mSize.y,
                            this.mMenu[0].mScale)) {
                        // change the image to Menu
                        this.mMenu[0].mOriginPos.y = 0;
                        // reset time
                        this.mIntervalTime = 0;
                        // Set scene
                        this.mMenuScene = MENU_SCENE.MENU_SCENE_NOTHING;
                        // Not to show menu buttons
                        for (int i = 1; i < 5; i++) this.mMenu[i].mExistFlag = false;
                        // to restart the race
                        Scene.SetAvailableToPlay(true);
                        // Play se
                        this.mSound[1].PlaySE();
                    }
                }
            } // the here, end to process to menu list view.

        } else if (this.mMenuType.equals(MENU_TYPE.MENU_TYPE_BACK)) {
            // when pressed the button, transition to main scene.
            if (GameView.GetTouchAction() == MotionEvent.ACTION_DOWN && this.mMenu[0].mExistFlag) {
                if (Collision.CheckTouch(
                        this.mMenu[0].mPos.x, this.mMenu[0].mPos.y,
                        this.mMenu[0].mSize.x, this.mMenu[0].mSize.y,
                        this.mMenu[0].mScale)) {
                    // create wipe and transition to scene.
                    Wipe.CreateWipe(this.mTransitionScene, Wipe.TYPE_PENETRATION);
                    // Play se
                    this.mSound[0].PlaySE();
                    return true;
                }
            }
        }
        return false;
    }

    /*
        Draw
    */
    public void DrawMenu() {

        // Each menu buttons
        for (int i = 0; i < this.mMenu.length; i++) {
            if (this.mMenu[i].mExistFlag) {
                this.mImage.DrawScale(
                        this.mMenu[i].mPos.x,
                        this.mMenu[i].mPos.y,
                        this.mMenu[i].mSize.x,
                        this.mMenu[i].mSize.y,
                        this.mMenu[i].mOriginPos.x,
                        this.mMenu[i].mOriginPos.y,
                        this.mMenu[i].mScale,
                        this.mMenu[i].mBmp
                );
            }
        }
    }
    /*
        Release
     */
    public void ReleaseMenu() {
        // Release BaseCharacter class.
        for (int i = 0; i < this.mMenu.length; i++) {
            // Release BaseCharacter's bitmap object.
            this.mMenu[i].ReleaseCharaBmp();
            this.mMenu[i] = null;
        }
        // Release BaseCharacter class.
        this.mContext = null;         // activity
        this.mImage = null;            // image object
        // Sound
        for (int i = 0; i < this.mSound.length; i++) this.mSound[i] = null;
    }
    /*
        Initialize list view
     */
    private void InitListView() {
        // Show list buttons that retry and to-title.
        for (int i = 3; i <= 4; i++) this.mMenu[i].mExistFlag = true;
        // substitute the flag that next to scene
        this.mMenuScene = MENU_SCENE.MENU_SCENE_UPDATE_LIST;
    }
    
    /*
        Update list view
     */
    private void UpdateListView() {
        // the scene
        int scene[] = {SCENE_PLAY,SCENE_OPENING};
        // to diverge process from pressed the button.
        for (int i = 3; i <= 4; i++) {              // Retry or To-title
            if (this.mMenu[i].mExistFlag) {
                if (Collision.CheckTouch(
                        this.mMenu[i].mPos.x, this.mMenu[i].mPos.y,
                        this.mMenu[i].mSize.x, this.mMenu[i].mSize.y,
                        this.mMenu[i].mScale)) {
                    // set scene to transition
                    this.mTransitionScene = scene[i-3];
                    // To show response
                    this.mMenuScene = MENU_SCENE.MENU_SCENE_RESPONSE;
                    // not to show each buttons
                    for (int j = 3; j <= 4; j++) this.mMenu[j].mExistFlag = false;
                    // Play se
                    this.mSound[0].PlaySE();
                    return;
                }
            }
        }
    }
    /*
        to Reply yes or no to transition to the scene.
     */
    private void UpdateResponse() {
        // to show buttons
        if (!this.mMenu[1].mExistFlag) {
            for (int i = 1; i <= 2; i++) {
                this.mMenu[i].mExistFlag = true;
            }
        }
        // Each scene
        MENU_SCENE scene[] = {MENU_SCENE.MENU_SCENE_TRANSITION,MENU_SCENE.MENU_SCENE_INIT_LIST};
        // to diverge process from pressed the button.
        for (int i = 1; i <= 2; i++) {              // yes or no
            if (GameView.GetTouchAction() == MotionEvent.ACTION_DOWN && this.mMenu[i].mExistFlag) {
                if (Collision.CheckTouch(
                        this.mMenu[i].mPos.x, this.mMenu[i].mPos.y,
                        this.mMenu[i].mSize.x, this.mMenu[i].mSize.y,
                        this.mMenu[i].mScale)) {
                    // Set scene to next
                    this.mMenuScene = scene[i-1];
                    // Not to show buttons
                    for (int j = 1; j <= 2; j++) this.mMenu[j].mExistFlag = false;
                    // Play se
                    this.mSound[i-1].PlaySE();
                    return;
                }
            }
        }
    }
}