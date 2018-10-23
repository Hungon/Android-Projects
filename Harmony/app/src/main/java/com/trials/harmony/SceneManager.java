package com.trials.harmony;

import android.content.Context;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 1/26/2016.
 */
public class SceneManager implements HasScene, HasButtons {
    // static variables
    // the guidance words
    public final static String[] TRANSITION_GUIDANCE = {
            "to transition to opening",
            "to transition to prologue",
            "to transition to play",
            "to transition to main menu",
            "to transition to result",
            "to transition to tutorial",
            "to transition to credit view",
    };
    // field
    private Scene           mCurrentScene;         // current scene object to manage.
    private Image           mImage;                // image object
    private static int      mNextSceneNum;         // next scene number
    private static int      mSceneNumNow;          // current scene
    private static int      mPreviewScene;
    private Context         mContext;
    // wipe class
    private Wipe            mWipe;
    private MusicSelector   mMusic;
    private boolean mAvailableToPlayTheMusic;

    /* constructor
     * to set scene number
    */
    public SceneManager(Context context, Image image, int sceneNum) {
        // To load system variables
        SystemManager system = new SystemManager(context);
        system.LoadSystemVariables();
        // get image object.
        this.mImage = image;
        // get activity
        this.mContext = context;
        // to set next scene
        mPreviewScene = mSceneNumNow = mNextSceneNum = sceneNum;
        this.SetMainScene(mNextSceneNum);
        mNextSceneNum = SceneManager.SCENE_NOTHING;
        // to allot the memory
        this.mWipe = new Wipe(context,image);
        Wipe.SetAvailableToCreate(true);
        // to initialize the wipe
        this.mWipe.CreateWipe(sceneNum,Wipe.TYPE_PENETRATION);
    }

    // set next scene number.
    private void SetMainScene(int sceneNum) {
        // when next scene is play, to release the music class
        if (mSceneNumNow == SCENE_PLAY || mSceneNumNow == SCENE_MAIN_MENU ||
                mSceneNumNow == SCENE_TUTORIAL) {
            if (this.mMusic != null) {
                this.mMusic.ReleaseSelector();
                this.mMusic = null;
            }
            this.mAvailableToPlayTheMusic = false;
        } else {
            if (this.mMusic == null) {
                // clear cache
                this.mMusic = new MusicSelector(this.mContext);
                this.mAvailableToPlayTheMusic = true;
                // Set tune that recognized in Recognition Mode
                int id = SystemManager.getMusicId();
                id = (id == MusicSelector.TUNE_ELEMENT_EMPTY)?0:id;
                MusicSelector.SetCurrentElementToPlayMusic(id);
            }
        }
        // diverge next scene from SceneNum.
        switch(sceneNum) {
            case SCENE_OPENING:
                this.mCurrentScene = new Opening(this.mContext, this.mImage);
                break;
            case SCENE_PROLOGUE:
                this.mCurrentScene = new PrologueScene(this.mContext,this.mImage);
                break;
            case SCENE_PLAY:
                this.mCurrentScene = new PlayScene(this.mContext,this.mImage);
                break;
            case SCENE_MAIN_MENU:
                this.mCurrentScene = new MainMenuScene(this.mContext,this.mImage);
                break;
            case SCENE_RESULT:
                this.mCurrentScene = new ResultScene(this.mContext,this.mImage);
                break;
            case SCENE_TUTORIAL:
                this.mCurrentScene = new TutorialScene(this.mContext,this.mImage);
                break;
            case SCENE_CREDIT_VIEW:
                this.mCurrentScene = new CreditView(this.mContext,this.mImage);
                break;
            default :
                this.mCurrentScene = null;
        }
    }

    // Update to check process in the scene.
    public final boolean UpdateSceneManager() {
        // error check
        if (this.mCurrentScene == null) return true;
        // update scene.
        // when scene executed release process,
        // to return true.
        if (this.mCurrentScene.UpdateScene(this.mContext,this.mImage,this.mWipe)) {
            // when set next scene, to make next scene.
            if (mNextSceneNum != SCENE_NOTHING) {
                // set preview scene
                mPreviewScene = mSceneNumNow;
                // set current scene
                mSceneNumNow = mNextSceneNum;
                // set next scene.
                this.SetMainScene(mSceneNumNow);
                // Reset next scene.
                SceneManager.SetNextScene(SCENE_NOTHING);
            }
            // when not to set next scene, to finish activity.
            else {
                this.mCurrentScene = null;
                this.mWipe = null;
                return true;
            }
        } else {
            // to play the music
            if (this.mAvailableToPlayTheMusic) this.mMusic.ToPlayTheMusic(true);
        }
        return false;
    }
    /*****************************************
        Each getter functions
    ***************************************/
    /*
        get current stage number
    */
    @Contract(pure = true)
    public static int GetCurrentScene() { return mSceneNumNow; }
    /*
        get next scene
    */
    @Contract(pure = true)
    public static int GetNextScene() { return mNextSceneNum; }
    /*
        Get preview scene
    */
    @Contract(pure = true)
    public static int GetPreviewScene() { return mPreviewScene; }
    /*****************************************
        Each setter functions
     ***************************************/
    /*
        set next scene
    */
    public static void SetNextScene(int scene) { mNextSceneNum = scene; }
}