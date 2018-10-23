package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by USER on 2/1/2016.
 */
public class Offroad extends Scene {

    // static variables

    // filed
    private Image               mImage;
    private Sound               mSound;
    private Context             mContext;
    // using BGM file
    private String              mBGMFile;
    // each classes in off-road
    private OffroadPlayer       mPlayer;
    private StageManager        mStageManager;
    private OffroadStage        mStage;
    private OffroadObstacles    mObstacles;
    private RaceScoreManager    mScore;
    private MiniMap             mMiniMap;
    private ActionCommand       mActionCommand;
    private Bitmap              mBmp;
    // make slow scene while action command is creating.
    private int                 mSlowTime;
    private boolean             mStartingBGM;

    /*
        Constructor
     */
    public  Offroad(Context activity, Image image) {
        // the flag that playing race.
        Scene.mPlayingF = true;
        this.mContext = activity;
        this.mImage = image;
        // player class
        this.mPlayer = new OffroadPlayer(activity, image);
        // stage manager
        this.mStageManager = new StageManager(activity, image);
        // stage
        this.mStage = new OffroadStage(activity, image);
        // obstacles class
        this.mObstacles = new OffroadObstacles(activity, image);
        // Score
        this.mScore = new RaceScoreManager(activity, image);
        // Mini Map
        this.mMiniMap = new MiniMap(activity,image);
        // Action command
        this.mActionCommand = new ActionCommand(activity,image);
        // Sound
        this.mSound = new Sound(activity);
    }

    /*
        Initialize
     */
    public int  Init() {
        // load the file
        this.mBmp = this.mImage.LoadImage(this.mContext,"wipe00");
        // using BGM
        this.mBGMFile = "offroad";
        this.mSound.CreateSound(this.mBGMFile);

        // the slow time
        this.mSlowTime = 0;
        this.mStartingBGM = false;

        // Stage Manager
        this.mStageManager.InitStageManager();
        // Stage
        this.mStage.InitStage();
        // Obstacles
        this.mObstacles.InitObstacles();
        // Score
        this.mScore.InitRaceScoreManager();
        // Initialize player
        this.mPlayer.InitPlayer(this.mObstacles);
        // Action command
        this.mActionCommand.InitActionCommand();
        // Mini Map
        this.mMiniMap.InitMiniMap();

        return SCENE_MAIN;
    }
    /*
        Update
     */
    public int  Update(){

        // if the flag that playing race is true, to execute process
        if (Scene.mPlayingF) {
            // when player is showing jump action, create command action
            boolean creation = false;
            if (OffroadPlayer.GetActionType() == OffroadPlayer.JUMP_ACTION) creation = true;
            // Action command
            if (this.mActionCommand.UpdateActionCommand(creation)){
                this.mSlowTime++;
            } else {
                this.mSlowTime = 0;
            }
            // except for action command, make the direction that slow down scene.
            if (this.mSlowTime % 3 == 0) {
                // Stage Manager
                if (this.mStageManager.UpdateStageManager()) {
                    if (!this.mStartingBGM) this.mStartingBGM = true;
                    // Update player
                    this.mPlayer.UpdatePlayer();
                    // Score
                    this.mScore.UpdateRaceScoreManager();
                    // Stage
                    if (this.mStage.UpdateStage()) return SCENE_RELEASE;
                    // Obstacles
                    this.mObstacles.UpdateObstacles();
                    // Mini map
                    this.mMiniMap.UpdateMiniMap();
                }
            }
        }
        // play BGM
        if (this.mStartingBGM) this.mSound.PlayBGM(this.mBGMFile);

        return SCENE_MAIN;
    }

    /*
        Draw
     */
    public void Draw() {
        // Stage
        this.mStage.DrawStage();
        // Obstacles
        this.mObstacles.DrawObstacles();
        // Draw player
        this.mPlayer.DrawPlayer();
        // Score
        this.mScore.DrawRaceScoreManager();
        // Stage Manager
        this.mStageManager.DrawStageManager();
        // Mini map
        this.mMiniMap.DrawMiniMap();
        // Action command
        this.mActionCommand.DrawActionCommand();
        // when open the menu, to make a whole screen to black.
        if (!Scene.mPlayingF) this.mImage.DrawAlpha(0, 0, 480, 800, 0, 0, 100, this.mBmp);
    }
    /*
        Release
     */
    public int Release() {
        // Release player
        this.mPlayer.ReleasePlayer();
        // Stage Manager
        this.mStageManager.ReleaseStageManager();
        // Stage
        this.mStage.ReleaseStage();
        // Obstacles
        this.mObstacles.ReleaseObstacles();
        // Score
        this.mScore.ReleaseRaceScoreManager();
        // Mini map
        this.mMiniMap.ReleaseMiniMap();
        // Action command
        this.mActionCommand.ReleaseActionCommand();
        // BGM
        this.mSound.StopBGM();

        // each classes
        this.mPlayer = null;
        this.mStageManager = null;
        this.mStage = null;
        this.mObstacles = null;
        this.mScore = null;
        this.mMiniMap = null;
        this.mSound = null;
        this.mActionCommand = null;
        this.mBmp = null;
        this.mContext = null;
        this.mImage = null;

        return SCENE_END;
    }
}