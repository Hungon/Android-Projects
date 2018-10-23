package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by USER on 2/23/2016.
 */
public class Road extends Scene {
    // static variables.
    // filed
    private Context         mContext;
    private Image           mImage;
    private Bitmap          mBmp;
    private RoadPlayer      mPlayer;
    private RoadStage       mStage;
    private RoadObstacles   mObstacles;
    private RoadCompetitorManager mComManager;
    private StageManager    mStageManager;
    private MiniMap         mMiniMap;
    private RaceScoreManager mScore;
    private Sound            mSound;
    private String           mBGMFile;
    private boolean          mStartBGM;

    /*
        Constructor
     */
    public Road(Context context, Image image) {
        // the flag that playing race.
        Scene.mPlayingF = true;
        this.mContext = context;
        this.mImage = image;
        this.mPlayer = new RoadPlayer(context, image);
        this.mStage = new RoadStage(context, image);
        this.mObstacles = new RoadObstacles(context, image);
        this.mComManager = new RoadCompetitorManager(context, image);
        this.mStageManager = new StageManager(context, image);
        this.mMiniMap = new MiniMap(context, image);
        this.mScore = new RaceScoreManager(context, image);
        this.mSound = new Sound(context);
    }

    /*
        Initialize
     */
    public int Init() {
        // using BGM file
        this.mBGMFile = "road";
        // the flag that start BGM
        this.mStartBGM = false;
        // load the file
        this.mBmp = this.mImage.LoadImage(this.mContext,"wipe00");
        // stage
        this.mStage.InitStage();
        // player
        this.mPlayer.InitRoadPlayer();
        // competitor
        this.mComManager.InitCompetitorManager();
        // obstacles
        this.mObstacles.InitObstacles();
        // Stage manager
        this.mStageManager.InitStageManager();
        // Mini map
        this.mMiniMap.InitMiniMap();
        // Score
        this.mScore.InitRaceScoreManager();

        return SCENE_MAIN;
    }
    /*
        Update
    */
    public int Update() {
        // when open the menu, to stop the race.
        if (Scene.mPlayingF) {
            // Update time
            if (this.mStageManager.UpdateStageManager()) {
                if (!this.mStartBGM) this.mStartBGM = true;
                // competitor
                this.mComManager.UpdateCompetitorManager(this.mObstacles, this.mPlayer);
                // player
                if (this.mPlayer.UpdateRunner(this.mObstacles)) return SCENE_RELEASE;
                // stage
                if (this.mStage.UpdateStage()) return SCENE_RELEASE;
                // obstacles
                this.mObstacles.UpdateObstacle();
                // Mini map
                this.mMiniMap.UpdateMiniMap();
                // Score
                this.mScore.UpdateRaceScoreManager();
            }
        }
        // play BGM
        if (this.mStartBGM) this.mSound.PlayBGM(this.mBGMFile);
        return SCENE_MAIN;
    }

    /*
        Draw
    */
    public void Draw() {
        // stage
        this.mStage.DrawStage();
        // Obstacles to draw to backward
        this.mObstacles.DrawObstaclesBackward();
        // competitor to draw to backward
        this.mComManager.DrawCompetitorBackward();
        // Player
        this.mPlayer.DrawRoadPlayer();
        // Obstacles to draw to forward
        this.mObstacles.DrawObstaclesForward();
        // competitor to draw to forward
        this.mComManager.DrawCompetitorForward();

        // draw the effect that attempt to attack to competitor
        this.mPlayer.GetTargetEffect().DrawCharacterEffect();

        // Score
        this.mScore.DrawRaceScoreManager();
        // Time record
        this.mStageManager.DrawStageManager();

        // Mini map
        this.mMiniMap.DrawMiniMap();

        // when open the menu, to make a whole screen to black.
        if (!Scene.mPlayingF) this.mImage.DrawAlpha(0, 0, 480, 800, 0, 0, 100, this.mBmp);
    }

    /*
        Release
    */
    public int Release() {
        this.mBmp = null;
        this.mContext = null;
        this.mImage = null;
        // stage
        this.mStage.ReleaseStage();
        // obstacles
        this.mObstacles.ReleaseObstacle();
        // player
        this.mPlayer.ReleaseRoadPlayer();
        // competitor
        this.mComManager.ReleaseCompetitorManager();
        // stage manager
        this.mStageManager.ReleaseStageManager();
        // Mini map
        this.mMiniMap.ReleaseMiniMap();
        // Score
        this.mScore.ReleaseRaceScoreManager();
        // BGM
        this.mSound.StopBGM();
        // Each classes
        this.mStage = null;
        this.mObstacles = null;
        this.mPlayer = null;
        this.mComManager = null;
        this.mStageManager = null;
        this.mMiniMap = null;
        this.mScore = null;
        this.mSound = null;
        return SCENE_END;
    }
}