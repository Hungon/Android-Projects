package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;

/**
 * Created by USER on 4/22/2016.
 */
public class PrologueScene extends Scene implements HasButtons, HasSystem {
    // static variables
    // filed
    private Image mImage;
    // transition manager
    private TransitionManager    mTransitionManager;
    private PlayManager mPlayManager;
    private Utility              mUtility;
    // the current modes which are sound and sentence.
    private int mCurrentMode;
    /*
        Constructor
    */
    public PrologueScene(Context context,Image image) {
        // get the current mode
        this.mCurrentMode = SystemManager.GetPlayMode();
        // transition manager
        this.mTransitionManager = new TransitionManager(context,image,1);

        // to diverge the allocation from the current mode.
        if (this.mCurrentMode == MODE_SOUND) {
            // colour manager
            this.mPlayManager = new ColourManager(context, image, 1);
        } else if (this.mCurrentMode == MODE_SENTENCE) {
            this.mPlayManager = new SentenceManager(context,image,3);
        } else if (this.mCurrentMode == MODE_ASSOCIATION_IN_EMOTIONS ||
                this.mCurrentMode == MODE_ASSOCIATION_IN_FRUITS ||
                this.mCurrentMode == MODE_ASSOCIATION_IN_ALL) {
            this.mPlayManager = new AssociationManager(context,image,2);
        }
        this.mUtility = new Utility();
        this.mImage = image;
    }
    /*
        Initialize
    */
    @Override
    public int Init() {
        this.mPlayManager.initManager();
        this.mTransitionManager.InitManager();
        return SCENE_MAIN;
    }
    /*
        Update
    */
    @Override
    public int Update() {
        int buttonType;
        int direction = RecognitionButton.DISTANCE;
        // to diverge the update from the current mode.
        if (this.mCurrentMode == MODE_SOUND) {
            this.mPlayManager.updateManager();
            buttonType = ColourManager.getCurrentButtonType()[0];
            direction = ColourManager.GetDirectionToNotice();
        } else {
            buttonType = this.mPlayManager.updateManager();
        }
        return this.mTransitionManager.UpdateManager(
                buttonType,
                direction,
                SceneManager.SCENE_NOTHING,60);
    }
    /*
        Draw
    */
    @Override
    public void Draw() {
        // filling with cyan
        this.mImage.fillRect(0,0,480,800, Color.CYAN);
        this.mPlayManager.drawManager();
        // to draw the recognition button
        this.mTransitionManager.DrawManager();
    }

    /*
        Release
    */
    @Override
    public int Release() {
        // transition manager
        this.mTransitionManager.ReleaseManager();
        this.mTransitionManager = null;
        this.mPlayManager.releaseManager();
        this.mPlayManager = null;
        // to diverge the release from the current mode.
        if (this.mCurrentMode == MODE_SOUND) {
            SystemManager.SetUserExperience(EXPERIENCE_DONE_PROLOGUE_SOUND_MODE);
        } else if (this.mCurrentMode == MODE_SENTENCE) {
            SystemManager.SetUserExperience(EXPERIENCE_DONE_PROLOGUE_SENTENCE_MODE);
        } else if (this.mCurrentMode == MODE_ASSOCIATION ||
                this.mCurrentMode == MODE_ASSOCIATION_IN_FRUITS ||
                this.mCurrentMode == MODE_ASSOCIATION_IN_EMOTIONS){
            SystemManager.SetUserExperience(HasSystem.EXPERIENCE_DONE_PROLOGUE_ASSOCIATION_MODE);
        }
        this.mUtility.ReleaseUtility();
        this.mUtility = null;
        return SCENE_END;
    }
}