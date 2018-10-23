package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by Kohei Moroi on 6/26/2016.
 */
public class ResultScene extends Scene implements HasButtons {

    // static variable
    // transition manager
    private TransitionManager mTransitionManager;
    private ScoreManager      mScoreManager;
    private Utility           mUtility;
    /*
        Constructor
    */
    public ResultScene(Context context, Image image) {
        // transition manager
        this.mTransitionManager = new TransitionManager(context,image,1);
        // Score manager
        this.mScoreManager = new ScoreManager(context,image);
        // Utility
        this.mUtility = new Utility();
    }

    @Override
    public int Init() {
        // Initialize each score
        this.mScoreManager.InitScore();
        // initialize transition manager
        this.mTransitionManager.InitManager();
        return SCENE_MAIN;
    }

    @Override
    public int Update() {
        // Score manager
        this.mScoreManager.UpdateScore();
        return this.mTransitionManager.UpdateManager(
                BUTTON_EMPTY,
                RecognitionButton.DISTANCE,
                SCENE_MAIN,60);
    }

    @Override
    public void Draw() {
        // Each score
        this.mScoreManager.DrawScore();
        // to draw each button to transition
        this.mTransitionManager.DrawManager();
    }

    @Override
    public int Release() {
        // Score manager
        this.mScoreManager.ReleaseScore();
        this.mScoreManager = null;
        // transition manager
        this.mTransitionManager.ReleaseManager();
        this.mTransitionManager = null;
        // Utility
        this.mUtility.ReleaseUtility();
        this.mUtility = null;
        // set user's experience
        SystemManager.SetUserExperience(HasSystem.EXPERIENCED_FIRST_IMPRESSION_IN_RESULT);
        return SCENE_END;
    }
}