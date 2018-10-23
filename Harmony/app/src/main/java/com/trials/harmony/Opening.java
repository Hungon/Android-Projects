package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by USER on 1/26/2016.
 */
public class Opening extends Scene implements HasButtons, HasSystem, FileManager {

    // static variables
    // filed
    // transition manager
    private Image mImage;
    private TransitionManager mTransitionManager;

    /*********************************************************
        Constructor
    *********************************************************/
    public Opening(Context context, Image image) {
        this.mImage = image;
        // allot memory
        // transition manager
        this.mTransitionManager = new TransitionManager(context,image,1);
    }
    /*********************************************************
        Initialize
    *********************************************************/
    public int Init() {
        // initialize transition manager
        this.mTransitionManager.InitManager();
        return Scene.SCENE_MAIN;
    }
    /***********************************************************
        Update
    *********************************************************/
    public int Update() {
        return this.mTransitionManager.UpdateManager(
                BUTTON_EMPTY,
                RecognitionButton.DISTANCE,
                SceneManager.SCENE_NOTHING,60);

    }
    /*********************************************************
        Draw
    *********************************************************/
    public void Draw() {
        this.mImage.fillRect(0,0,480,800,Color.CYAN);
        // to draw each button to transition
        this.mTransitionManager.DrawManager();
    }
    /*********************************************************
        Release
    *********************************************************/
    public int Release() {
        // transition manager
        this.mTransitionManager.ReleaseManager();
        this.mTransitionManager = null;
        // Set user's experience
        SystemManager.SetUserExperience(EXPERIENCED_FIRST_IMPRESSION_IN_OPENING);
        return SCENE_END;
    }
}