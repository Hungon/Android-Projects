package com.trials.supertriathlon;

import android.content.Context;

/**
 * Created by USER on 2/15/2016.
 */
public class Play extends Scene implements HasProperties {

    // Game level
    // filed
    private static int          mStageNumber;
    private static int          mNextStageNumber = STAGE_NOTHING;
    private Scene               mStageScene;
    private static int          mGameLevel = LEVEL_EASY;
    private Menu                mMenu;

    /*
        Constructor
    */
    public Play(Context context, Image image) {
        // set current stage number
        if (mNextStageNumber != STAGE_NOTHING) {
            mStageNumber = mNextStageNumber;
            mNextStageNumber = STAGE_NOTHING;
        }
        // diverge initialize stage from stage number
        switch(mStageNumber) {
            case STAGE_OFF_ROAD:
                this.mStageScene = new Offroad(context, image);
                break;
            case STAGE_ROAD:
                this.mStageScene = new Road(context, image);
                break;
            case STAGE_SEA:
                this.mStageScene = new Sea(context, image);
                break;
            default:        // if variable is null or out of fixed number, back to opening scene anyway.
                this.mStageScene = new Opening(context, image);
                break;
        }
        // Menu
        this.mMenu = new Menu(context,image);
    }


    /*
        Initialize
     */
    public int Init() {

        // Initialize the stage
        this.mStageScene.Init();
        // Menu
        this.mMenu.InitMenuList();

        // if ( false )		return	CScene.SCENE_ERROR;
        return Scene.SCENE_MAIN;
    }

    /*
        Update
    */
    public int Update() {

        // Update the stage
        if (this.mStageScene.Update() == Scene.SCENE_RELEASE) return Scene.SCENE_RELEASE;
        // Menu
        if (this.mMenu.UpdateMenu()) return Scene.SCENE_RELEASE;

        return Scene.SCENE_MAIN;
    }

    /*
        Draw
    */
    public void Draw() {
        // Draw the stage
        this.mStageScene.Draw();
        // Menu
        this.mMenu.DrawMenu();
    }

    /*
        Release
    */
    public int Release() {
        // Reset camera
        StageCamera.ResetCamera();
        // Release the stage
        this.mStageScene.Release();
        this.mStageScene = null;
        // Menu
        this.mMenu.ReleaseMenu();
        this.mMenu = null;
        return Scene.SCENE_END;
    }

    /*************************************************************************************
        Each setter functions
    ***********************************************************************************/
    /*
        Set the stage number to next
    */
    public static void SetStageNumberToNext(int stage) { mNextStageNumber = stage; }
    /*
        Set Game level
    */
    public static void SetGameLevel(int level) { mGameLevel = level; }
    /*************************************************************************************
     Each getter functions
     ***********************************************************************************/
    /*
        Get current stage number
    */
    public static int   GetCurrentStageNumber() { return mStageNumber; }
    /*
        Get stage number to next
    */
    public static int GetNextStageNumber() { return mNextStageNumber; }
    /*
        Get game level
     */
    public static int   GetGameLevel() { return mGameLevel; }
}