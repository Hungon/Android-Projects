package com.trials.supertriathlon;

import android.content.Context;

/**
 * Created by USER on 1/26/2016.
 */
public class SceneManager implements HasScenes {
    // field
    private Scene           mCurrentScene;         // current scene object to manage.
    private Image           mImage;                // image object
    private static int      mNextSceneNum;         // next scene number
    private static int      mSceneNumNow;          // current scene
    private Context         mContext;

    /* constructor
     * to set scene number
    */
    public SceneManager(Context context, Image image, int sceneNum) {
        // get image object.
        this.mImage = image;
        // get activity
        this.mContext = context;
        // set scene number.
        this.SetMainScene(sceneNum);
        // get current scene number
        mSceneNumNow = sceneNum;
        // clean next scene
        SceneManager.mNextSceneNum = SCENE_NOTHING;

        // Initialize wipe
        Wipe.InitWipe(context,image,Wipe.TYPE_PENETRATION);
    }

    // set next scene number.
    public void SetMainScene(int SceneNum) {
        // diverge next scene from SceneNum.
        switch(SceneNum) {
            case SCENE_OPENING :
                this.mCurrentScene = new Opening(this.mContext, this.mImage);
                break;
            case SCENE_SELECT :
                this.mCurrentScene = new SelectMode(this.mContext, this.mImage);
                break;
            case SCENE_BRIEF_STAGE :
                this.mCurrentScene = new BriefStage(this.mContext, this.mImage);
                break;
            case SCENE_CREDIT :
                this.mCurrentScene = new CreditView(this.mContext, this.mImage);
                break;
            case SCENE_PLAY :
                this.mCurrentScene = new Play(this.mContext, this.mImage);
                break;
            case SCENE_RESULT :
                this.mCurrentScene = new Result(this.mContext, this.mImage);
                break;
            case SCENE_OPTION :
                this.mCurrentScene = new Option(this.mContext, this.mImage);
                break;
            case SCENE_RECORD_VIEW :
                this.mCurrentScene = new RecordView(this.mContext, this.mImage);
                break;
            case SCENE_GAME_OVER :
                this.mCurrentScene = new GameOver(this.mContext, this.mImage);
                break;
            default :
                this.mCurrentScene = null;
        }
    }

    // Update to check process in the scene.
    public boolean UpdateScene() {
        // error check
        if (this.mCurrentScene == null) return true;
        // update scene.
        if (this.mCurrentScene.Scene(this.mImage)) {
            // when set next scene, to make next scene.
            if (SceneManager.mNextSceneNum != SCENE_NOTHING) {
                // set current scene
                mSceneNumNow = SceneManager.mNextSceneNum;
                // set next scene.
                this.SetMainScene(mSceneNumNow);
                // Reset next scene.
                SceneManager.SetNextScene(SCENE_NOTHING);
            }
            // when not to set next scene, to finish activity.
            else {
                // release wipe
                Wipe.ReleaseWipe();
                return true;
            }
        }
        return false;
    }
    // get current stage number
    public static int GetCurrentScene() { return mSceneNumNow; }
    // set next scene
    public static void SetNextScene(int scene) { SceneManager.mNextSceneNum = scene; }
}