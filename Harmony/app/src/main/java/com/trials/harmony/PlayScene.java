package com.trials.harmony;

import android.content.Context;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 5/9/2016.
 */
public class PlayScene extends Scene implements HasButtons, FileManager, HasSystem {
    // static variables
    // filed
    private RecognitionCharacter        mRecognitionChara;
    private TransitionManager           mTransitionManager;
    private FirstStage                  mStage;
    private PlayManager         mPlayManager;       // to up cast each manager
    // score colour
    private ScoreManager        mScoreManager;
    private MusicSelector       mMusic;
    // the current play mode
    private int mCurrentMode;
    /*
        Constructor
    */
    public PlayScene(Context context,Image image) {
        this.mCurrentMode = SystemManager.GetPlayMode();
        int level = SystemManager.GetGameLevel();
        // count max to create colour and the count that available to recognise
        int max[] = {2,3,4};
        int idMax = 0;
        // to diverge the allocation from the current mode.
        if (this.mCurrentMode == MODE_SOUND) {
            // Colour manager
            this.mPlayManager = new ColourManager(context, image, max[level]);
            idMax = max[level];
        } else if (this.mCurrentMode == MODE_SENTENCE) {
            max[level]++;
            this.mPlayManager = new SentenceManager(context,image,max[level]);
            idMax = max[level];
        } else if (this.mCurrentMode == MODE_ASSOCIATION_IN_EMOTIONS ||
                this.mCurrentMode == MODE_ASSOCIATION_IN_FRUITS ||
                this.mCurrentMode == MODE_ASSOCIATION_IN_ALL) {
            idMax = 2;
            this.mPlayManager = new AssociationManager(context,image,idMax);
        }
        // Stage
        this.mStage = new FirstStage(context,image);
        // Recognition character
        this.mRecognitionChara = new RecognitionCharacter(context,image);
        // Transition manager
        this.mTransitionManager = new TransitionManager(context,image,idMax);
        // Score manager
        this.mScoreManager = new ScoreManager(context,image,max[level]);
        this.mMusic = new MusicSelector(context);
    }
    /*
        Initialize
    */
    @Override
    public int Init() {
        // Recognition character
        this.mRecognitionChara.InitChara();
        // Transition manager
        this.mTransitionManager.InitManager();
        this.mPlayManager.initManager();
        // Stage
        this.mStage.InitStage();
        // Score manager
        this.mScoreManager.InitScore();
        // music
        this.mMusic.InitSelector();
        MusicSelector.SetAvailableToPlay(true);
        // Set tune that recognized in Recognition Mode
        int id = SystemManager.getMusicId();
        id = (id== MusicSelector.TUNE_ELEMENT_EMPTY)?0:id;
        MusicSelector.SetCurrentElementToPlayMusic(id);
        return SCENE_MAIN;
    }
    /*
        Update
    */
    @Override
    public int Update() {
        int buttonType = BUTTON_EMPTY;
        int scene = SCENE_MAIN;
        // when user is opening task table, not to update below
        if (!RecognitionButtonManager.GetIsOpeningTask()) {
            // Stage: when current distance reached to 0, to return Scene_Release.
            scene = this.mStage.UpdateStage();
            // Recognition character
            this.mRecognitionChara.UpdateChara();
            // Score manager
            this.mScoreManager.UpdateScore();

            //  to diverge the update from the current mode.
            buttonType = this.mPlayManager.updateManager();

            if (!GuidanceManager.GetIsGuiding() && !this.mMusic.GetAvailableToPlay()) {
                MusicSelector.SetAvailableToPlay(true);
                this.mMusic.RestartTheMusic();
            }
            this.mMusic.UpdateMusic(false,100);
        } else {
            if (this.mMusic.GetAvailableToPlay()) {
                MusicSelector.SetAvailableToPlay(false);
                this.mMusic.StopTheMusicTemporary();
            }
        }
        // Transition manager
        return this.mTransitionManager.UpdateManager(
                buttonType,
                RecognitionButton.DISTANCE,
                scene,
                60);
    }
    /*
        Draw
    */
    @Override
    public void Draw() {
        // Stage images
        this.mStage.DrawStage();
        this.mPlayManager.drawManager();
        // To draw each button
        this.mTransitionManager.DrawManager();
        // Recognition character
        this.mRecognitionChara.DrawChara();
        // Score
        this.mScoreManager.DrawScore();
    }
    /*
        Release
    */
    @Override
    public int Release() {
        // Stage
        this.mStage.ReleaseStage();
        this.mStage = null;
        // Transition manager
        this.mTransitionManager.ReleaseManager();
        this.mTransitionManager = null;
        // Recognition character
        this.mRecognitionChara.ReleaseChara();
        this.mRecognitionChara = null;
        this.mPlayManager.releaseManager();
        this.mPlayManager = null;
        // Score manager
        this.mScoreManager.ReleaseScore();
        this.mScoreManager = null;
        // music
        this.mMusic.ReleaseSelector();
        this.mMusic = null;
        return SCENE_END;
    }
}