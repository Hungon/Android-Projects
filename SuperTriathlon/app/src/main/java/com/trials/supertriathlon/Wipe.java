package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 1/31/2016.
 */
public class Wipe extends BaseCharacter implements HasScenes, HasProperties {

    // static  variables
    private final static int    ALPHA_ADD = 5;
    private final static int    ALPHA_SUB = 5;
    // type of wipe
    public final static int    TYPE_PENETRATION = 0;
    // wipe
    private final static Point  PENETRATION_SIZE = new Point(900, 900);
    // loading image
    private final static Point  LOADING_IMAGE_SIZE = new Point(230, 55);
    // notification that inform of to change orientation
    private final static Point  NOTIFICATION_SIZE = new Point(128,48);

    // to show the ad in these scene.
    private final static int    SHOWING_AD_IN_THE_SCENE[] = {
            SCENE_PLAY,
            SCENE_RESULT,
            SCENE_OPTION,
            SCENE_CREDIT,
    };

    // filed
    private static Image           mImage;            // image object
    private static int             mWipeType;         // direction
    private static int             mNextScene;        // scene to next
    private static BaseCharacter   mWipe;             // wipe own
    private static BaseCharacter   mLoad;             // load image
    private static Animation       mLoadAni;
    private static boolean         mChangeSceneF;
    private static BaseCharacter   mNotification;
    private static int             mFixedStayTime;

    /*
        Constructor
    */
    public Wipe() {}

    /*
        Create wipe and transition to scene selected.
    */
    public static void  CreateWipe(int nextScene, int wipeType) {
        // get current stage number
        int stage = Play.GetCurrentStageNumber();
        int nextStage = Play.GetNextStageNumber();
        int level = Play.GetGameLevel();
        // get current scene number
        int scene = SceneManager.GetCurrentScene();
        // get screen size
        Point screen = GameView.GetScreenSize();
        // when not to retry the sea stage
        if (screen.x == 800 && stage == STAGE_SEA && scene == SCENE_PLAY &&
            nextScene != SCENE_PLAY) {              // when from sea stage, to change orientation.
            GameView.NextActivity(GameView.VIEW_ORIENTATION.ORIENTATION_PORTRAIT,nextScene,level);
            return;
        // next to sea stage
        } else if (nextStage == STAGE_SEA && screen.x == 480) {
            if (nextScene == SCENE_BRIEF_STAGE || nextScene == SCENE_PLAY) {
                GameView.NextActivity(GameView.VIEW_ORIENTATION.ORIENTATION_LANDSCAPE, nextScene, level);
                return;
            }
        // back to select stage from the brief stage.
        } else if (nextStage == STAGE_SEA && screen.x == 800 &&
                nextScene == SCENE_SELECT && scene == SCENE_BRIEF_STAGE) {
            GameView.NextActivity(GameView.VIEW_ORIENTATION.ORIENTATION_PORTRAIT,nextScene,level);
            return;
        } else if (nextScene == SCENE_RANKING) {
            GameView.goToRankingView();
            return;
        }

        // wipe setting
        mWipeType = wipeType;
        mNextScene = nextScene;
        // set scene to next.
        SceneManager.SetNextScene(mNextScene);
        mChangeSceneF = false;
        mWipe.mExistFlag = true;
    }

    /*
        Initialize wipe setting.
    */
    public static void InitWipe(Context activity, Image image, int type) {
        // Allot memory
        try {
            if (mWipe == null) {
                mWipe = new BaseCharacter(image);
                mWipe.LoadCharaImage(activity, "wipe00");
            }
            if (mLoad == null) {
                mLoad = new BaseCharacter(image);
                mLoad.LoadCharaImage(activity, "wipeimage00");
            }
            if (mLoadAni == null) mLoadAni = new Animation();
            if (mNotification == null) {
                mNotification = new BaseCharacter(image);
                mNotification.LoadCharaImage(activity, "notification");
            }
        } catch (Exception e) {
        }
        // set each arguments
        mImage = image;
        // the fixed time that wipe staying while loading the scene.
        mFixedStayTime = 60;

        // setting
        // wipe
        mWipe.mSize.x = PENETRATION_SIZE.x;
        mWipe.mSize.y = PENETRATION_SIZE.y;
        mWipe.mTime = 0;                      // stay time
        mWipeType = type;
        mWipe.mExistFlag = true;
        mWipe.mAlpha = 1;
        mChangeSceneF = false;
        mNextScene = SceneManager.GetCurrentScene();
        // loading image
        Point screen = GameView.GetScreenSize();
        mLoad.mSize.x = LOADING_IMAGE_SIZE.x;
        mLoad.mSize.y = LOADING_IMAGE_SIZE.y;
        mLoad.mPos.x = (screen.x - Wipe.LOADING_IMAGE_SIZE.x)>>1;
        mLoad.mPos.y = (screen.y - Wipe.LOADING_IMAGE_SIZE.y)>>1;
        mLoad.mExistFlag = false;
        // animation setting
        mLoadAni.SetAnimation(0,0,mLoad.mSize.x,mLoad.mSize.y,2,5,0);
        // notification 
        mNotification.mSize.x = NOTIFICATION_SIZE.x;
        mNotification.mSize.y = NOTIFICATION_SIZE.y;
        mNotification.mPos.x = (screen.x-NOTIFICATION_SIZE.x)>>1;
        mNotification.mPos.y = mLoad.mPos.y+mLoad.mSize.y+20;
        // get the flag that change the orientation.
        if (GameView.GetChangeOrientation()) {
            // when current orientation is landscape,
            if (screen.x == 800) {
                mNotification.mOriginPos.y = 0;
                mNotification.mExistFlag = true;
                // to reset the flag
                GameView.SetChangeOrientation(false);
            // when portrait
            } else if (screen.x == 480) {
                mNotification.mOriginPos.y = NOTIFICATION_SIZE.y;
                mNotification.mExistFlag = true;
                // to reset the flag
                GameView.SetChangeOrientation(false);
            }
            // the fixed time that staying licence
            mFixedStayTime = 120;
        }
    }
    /*
        Update wipe direction.
    */
    public static boolean UpdateWipe() {
        // end direction flag
        boolean direction = false;

        // when only transition to scene, to update wipe process.
        if (mNextScene != SCENE_NOTHING && mWipe.mExistFlag) {
            // diverge direction from wipe type.
            switch (mWipeType) {
                case TYPE_PENETRATION:
                    direction = DirectPenetration();
                    break;
                default:
                    break;
            }
            // update loading image animation
            if (mLoad.mExistFlag)
                mLoadAni.UpdateAnimation(mLoad.mOriginPos, true);

            // when end wipe process, next scene is nothing.
            if (!mWipe.mExistFlag) {
                // to set the flag that once only showing ad.
                GameView.SetOnlyOnceShowingAd(false);
                mNextScene = SCENE_NOTHING;
            }
        }
        // return value is flag that is transition to scene.
        return direction;
    }

    /*
        Draw
    */
    public static void DrawWipe() {

        if (mWipe.mExistFlag) {
            // diverge drawing wipe from type.
            switch(mWipeType) {
                case TYPE_PENETRATION :
                    mImage.DrawAlpha(
                            mWipe.mPos.x-50,
                            mWipe.mPos.y-50,
                            mWipe.mSize.x,
                            mWipe.mSize.y,
                            mWipe.mOriginPos.x,
                            mWipe.mOriginPos.y,
                            mWipe.mAlpha,
                            mWipe.mBmp
                    );
                    break;
                default :
                    break;

            }
            // Draw loading image
            if(mLoad.mExistFlag) {
                mImage.DrawImage(
                        mLoad.mPos.x,
                        mLoad.mPos.y,
                        mLoad.mSize.x,
                        mLoad.mSize.y,
                        mLoad.mOriginPos.x,
                        mLoad.mOriginPos.y,
                        mLoad.mBmp
                );
            }
            // when drawing the wipe, to show the advertisements in the scene
            for (int scene: SHOWING_AD_IN_THE_SCENE) {
                if (mNextScene == scene) {
                    GameView.ShowAd();
                    break;
                }
            }
        }

        // Notification
        if (mNotification.mExistFlag) {
            mImage.DrawImage(
                    mNotification.mPos.x,
                    mNotification.mPos.y,
                    mNotification.mSize.x,
                    mNotification.mSize.y,
                    mNotification.mOriginPos.x,
                    mNotification.mOriginPos.y,
                    mNotification.mBmp
            );
        }
    }

    /*
        Release
    */
    public static void ReleaseWipe() {
        mNextScene = 999;
    }

    /*
        Wipe direction. the type is Penetration.
    */
    private static boolean	DirectPenetration() {            //direction of penetration

        if (mWipe.mTime == 0 && !mChangeSceneF) {
            mWipe.mAlpha += ALPHA_ADD;
            if (mWipe.mAlpha >= 255) {
                mWipe.mAlpha = 255;
                return mChangeSceneF = true;  // then to draw next scene.
            }
        }
        if (mChangeSceneF) {
            mLoad.mExistFlag = true;
            mWipe.mTime++;
            if (mWipe.mTime >= mFixedStayTime) {
                mNotification.mExistFlag = mLoad.mExistFlag = false;
                mWipe.mAlpha -= ALPHA_SUB;
                if (mWipe.mAlpha <= 1) {
                    mWipe.mAlpha = 1;
                    mWipe.mTime = 0;
                    // end direction
                    return mWipe.mExistFlag = false;
                }
            }
        }
        return true;                            //during wipe is directing
    }
    /*
        Get the flag that transition to scene.
    */
    public static boolean GetChangeScene() { return mChangeSceneF; }
}