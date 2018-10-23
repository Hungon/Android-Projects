package com.trials.harmony;

import android.content.Context;

/**
 * Created by Kohei Moroi on 8/2/2016.
 */
public class CreditView extends Scene implements HasButtons, HasScene {

    private TransitionManager mTransitionManager;
    private CreditViewer mCreditViewer;

    public CreditView(Context context, Image image) {
        this.mTransitionManager = new TransitionManager(context,image,1);
        this.mCreditViewer = new CreditViewer(context,image);
    }
    @Override
    public int Init() {
        this.mTransitionManager.InitManager();
        this.mCreditViewer.InitCreditViewer();
        return SCENE_MAIN;
    }

    @Override
    public int Update() {
        int scene = SCENE_MAIN;
        this.mCreditViewer.UpdateCreditViewer();
        return this.mTransitionManager.UpdateManager(
                BUTTON_EMPTY,
                RecognitionButton.DISTANCE,
                scene,60);
    }

    @Override
    public void Draw() {
        this.mCreditViewer.DrawCreditViewer();
        this.mTransitionManager.DrawManager();
    }

    @Override
    public int Release() {
        this.mCreditViewer.ReleaseCreditViewer();
        this.mCreditViewer = null;
        this.mTransitionManager.ReleaseManager();
        this.mTransitionManager = null;
        return SCENE_END;
    }
}