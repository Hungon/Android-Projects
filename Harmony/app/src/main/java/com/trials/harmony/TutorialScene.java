package com.trials.harmony;

import android.content.Context;

/**
 * Created by Kohei Moroi on 7/8/2016.
 */
public class TutorialScene extends Scene implements HasButtons, HasSystem, FileManager {
    // static variable
    // Filed
    // transition manager
    private TransitionManager mTransitionManager;
    private MusicSelector mMusic;
    // Constructor
    public TutorialScene(Context context, Image  image) {
        // reset mode number
        this.mTransitionManager = new TransitionManager(context,image,1);
        this.mMusic = new MusicSelector(context);
    }

    @Override
    public int Init() {
        // transition manager
        this.mTransitionManager.InitManager();
        this.mMusic.InitSelector();
        return SCENE_MAIN;
    }
    @Override
    public int Update() {
        this.mMusic.UpdateMusic(false,100);
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
        this.mMusic.ReleaseSelector();
        this.mMusic = null;
        // transition manager
        this.mTransitionManager.ReleaseManager();
        this.mTransitionManager = null;
        // Set user's experience
        SystemManager.SetUserExperience(EXPERIENCE_DONE_TUTORIAL);
        return SCENE_END;
    }
}