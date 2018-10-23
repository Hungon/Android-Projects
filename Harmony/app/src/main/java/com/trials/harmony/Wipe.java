package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 1/31/2016.
 */
public class Wipe implements HasScene {

    // static  variables
    private final static int    ALPHA_ADD = 5;
    private final static int    ALPHA_SUB = 5;
    // type of wipe
    final static int    TYPE_PENETRATION = 0;
    // wipe
    private final static Point  PENETRATION_SIZE = new Point(900, 900);
    // loading image
    private final static Point  LOADING_IMAGE_SIZE = new Point(256, 48);
    // animation setting
    private final static int    ANIMATION_LOADING_IMAGE_FRAME = 20;
    private final static int    ANIMATION_LOADING_IMAGE_COUNT_MAX = 4;
    // the process of wipe
    final static int    DESTROYED   = 0;
    final static int    UPDATE      = 1;
    final static int    RELEASE     = 2;
    // filed
    private Context         mContext;
    private Image           mImage;
    private int             mWipeType;         // direction
    private int             mNextScene;        // scene to next
    private CharacterEx     mWipe;             // wipe own
    private CharacterEx     mLoad;             // load image
    private boolean         mChangeSceneF;
    private int             mFixedStayTime;
    // to guide user with voice
    private Guidance  mGuidance;
    // is guiding
    private boolean         mIsGuiding = false;
    // the process of wipe
    private int             mTransit;
    private static boolean  mCreateWipe = true;

    /*
        Constructor
    */
    public Wipe(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
    }
    /*
        Create wipe and transition to scene selected.
    */
    public void CreateWipe(int nextScene, int wipeType) {
        if (!mCreateWipe) return;
        Point screen = MainView.GetScreenSize();
        // to allot memory
        mWipe = new CharacterEx(this.mContext, this.mImage);
        mWipe.InitCharacterEx(
                "wipeimage",
                0,0,
                PENETRATION_SIZE.x,PENETRATION_SIZE.y,
                1,1.0f,wipeType);
        mLoad = new CharacterEx(this.mContext, this.mImage);
        mLoad.InitCharacterEx(
                "loadingimage",
                (screen.x - LOADING_IMAGE_SIZE.x)>>1,
                (screen.y - LOADING_IMAGE_SIZE.y)>>1,
                LOADING_IMAGE_SIZE.x,LOADING_IMAGE_SIZE.y,
                255,1.0f,0);
        // loading image, not to draw first
        mLoad.mExistFlag = false;
        // animation setting
        mLoad.InitAnimation(
                ANIMATION_LOADING_IMAGE_COUNT_MAX,
                ANIMATION_LOADING_IMAGE_FRAME, 0);
        // to allot the memory for guidance
        this.mGuidance = new Guidance(mContext);
        // to set guidance words
        // when there is difference between the preview scene and the current scene,
        // to set the default sentence.
        String guidance;
        if (SceneManager.GetCurrentScene() != nextScene) {
            guidance = SceneManager.TRANSITION_GUIDANCE[nextScene];
        } else {
            guidance = "now loading";
        }
        this.mGuidance.InitGuidance(guidance);
        mIsGuiding = true;

        // wipe setting
        mWipeType = wipeType;
        mNextScene = nextScene;
        // set scene to next.
        SceneManager.SetNextScene(mNextScene);
        mChangeSceneF = false;
        mWipe.mExistFlag = true;
        mIsGuiding = false;
        // the fixed time that wipe staying while loading the scene.
        mFixedStayTime = 60;
        // wipe setting
        mWipe.mTime = 0;          // to count the time
        mWipe.mAlpha = 1;         // alpha value

        // the process
        this.mTransit = UPDATE;
        // reset setting available to create
        mCreateWipe = false;
    }
    /*
        Update wipe direction.
    */
    public int UpdateWipe() {
        // when only transition to scene, to update wipe process.
        if (this.mTransit == UPDATE) {
            // diverge direction from wipe type.
            switch (mWipeType) {
                case TYPE_PENETRATION:
                    this.mTransit = DirectPenetration();
                    break;
                default:
                    break;
            }
            // update loading image animation
            mLoad.UpdateCharacterEx(false);
            // to guide user with voice to transition the scene
            if (this.mGuidance != null) {
                this.mGuidance.UpdateGuidance();
                // to get the flag that is guiding
                if (mChangeSceneF) mIsGuiding = this.mGuidance.GetSpeaking();
            }
            // when end wipe process, next scene is nothing.
            if (this.mTransit == RELEASE) {
                mWipe.mExistFlag = false;
                mNextScene = SCENE_NOTHING;
            }
        }
        // return value is flag that is transition to scene.
        return this.mTransit;
    }
    /*
        Draw
    */
    public void DrawWipe() {
        if (this.mTransit == UPDATE) {
            // diverge drawing wipe from type.
            switch (mWipeType) {
                case TYPE_PENETRATION:
                    mWipe.DrawCharacterEx();
                    break;
                default:
                    break;

            }
            // Draw loading image
            mLoad.DrawCharacterEx();
        }
    }

    /*
        Release
    */
    public void ReleaseWipe() {
        if (this.mTransit == RELEASE) {
            this.mWipe.ReleaseCharacterEx();
            this.mWipe = null;
            this.mLoad.ReleaseCharacterEx();
            this.mLoad = null;
            this.mGuidance.StopGuidance();
            this.mGuidance.ReleaseGuidance();
            this.mGuidance = null;
            // to set the process
            this.mTransit = DESTROYED;
        }
    }

    /*
        Wipe direction. the type is Penetration.
    */
    private int	DirectPenetration() {            //direction of penetration
        if (mWipe.mTime == 0 && !mChangeSceneF) {
            mWipe.mAlpha += ALPHA_ADD;
            if (mWipe.mAlpha >= 255) {
                mWipe.mAlpha = 255;
                mChangeSceneF = true;  // then to draw next scene.
                return UPDATE;
            }
        }
        if (mChangeSceneF) {
            mLoad.mExistFlag = true;
            // when finish guiding, to count to change the scene
            if (!mIsGuiding) {
                mWipe.mTime++;
                if (mFixedStayTime <= mWipe.mTime) {
                    mLoad.mExistFlag = false;
                    mWipe.mAlpha -= ALPHA_SUB;
                    if (mWipe.mAlpha <= 1) {
                        mWipe.mAlpha = 1;
                        mWipe.mTime = 0;
                        // end direction
                        return RELEASE;
                    }
                }
            }
        }
        return UPDATE;                            //during wipe is directing
    }
    /*
        Get the flag that transition to scene.
    */
    public boolean GetChangeScene() { return mChangeSceneF; }
    /*
        Setting that available to create wipe
    */
    public static void SetAvailableToCreate(boolean create) { mCreateWipe = create; }
}