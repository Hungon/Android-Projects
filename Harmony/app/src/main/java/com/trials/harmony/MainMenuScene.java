package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by Kohei Moroi on 6/22/2016.
 */
public class MainMenuScene extends Scene implements HasButtons, HasScene {

    // Static variables
    // Filed
    // transition manager
    private TransitionManager mTransitionManager;
    // Select tune
    private MusicSelector mMusicSelector;
    /*
        Constructor
    */
    public MainMenuScene(Context context, Image image) {
        this.mTransitionManager = new TransitionManager(context,image,1);
        this.mMusicSelector = new MusicSelector(context);
    }

    @Override
    public int Init() {
        // transition manager
        this.mTransitionManager.InitManager();
        // Music selector
        this.mMusicSelector.InitSelector();
        return SCENE_MAIN;
    }
    @Override
    public int Update() {
        this.mMusicSelector.UpdateMusic(false,100);
        return this.mTransitionManager.UpdateManager(
                BUTTON_EMPTY,
                RecognitionButton.DISTANCE,
                SCENE_MAIN,60);
    }
    @Override
    public void Draw() {
        // to draw each button to transition
        this.mTransitionManager.DrawManager();
    }
    @Override
    public int Release() {
        // transition manager
        this.mTransitionManager.ReleaseManager();
        this.mTransitionManager = null;
        // When the next scene is play,
        // to release music selector
        this.mMusicSelector.ReleaseSelector();
        this.mMusicSelector = null;
        // Set user's experience
        SystemManager.SetUserExperience(HasSystem.EXPERIENCED_FIRST_IMPRESSION_IN_MAIN_MENU);
        return SCENE_END;
    }
}