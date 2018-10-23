package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;


/**
 * Created by USER on 3/10/2016.
 */
public class Sea extends Scene {
    // static variables
    // filed
    private Context     mContext;
    private Image       mImage;
    private SeaPlayer   mPlayer;
    private SeaStage    mStage;
    private MiniMap     mMiniMap;
    private SeaEnemyManager mEnemyManager;
    private SeaItem     mItem;
    private CharacterController mController;
    private StageManager mStageManager;
    private RaceScoreManager mScore;
    private Sound       mSound;
    private Bitmap      mBmp;
    private boolean     mStartingBGM;
    private FPoint      mPlayerMove;

    /*
        Constructor
    */
    public Sea(Context context, Image image) {
        Scene.mPlayingF = true;
        this.mContext = context;
        this.mImage = image;
        // Stage
        this.mStage = new SeaStage(context, image);
        // Swimmer
        this.mPlayer = new SeaPlayer(context, image);
        // Sound
        this.mSound = new Sound(context);
        // Mini map
        this.mMiniMap = new MiniMap(context,image);
        // Enemy manager
        this.mEnemyManager = new SeaEnemyManager(context,image);
        // Item
        this.mItem = new SeaItem(context, image);
        // Controller
        this.mController = new CharacterController(context,image);
        // Stage Manager
        this.mStageManager = new StageManager(context, image);
        // Race score
        this.mScore = new RaceScoreManager(context,image);
        // the sign that starting the BGM
        this.mStartingBGM = false;
        // player's move
        this.mPlayerMove = new FPoint();
    }
    /*
        Initialize
    */
    @Override
    public int Init() {
        // load the file
        this.mBmp = this.mImage.LoadImage(this.mContext,"wipe00");
        // Stage
        this.mStage.InitSeaStage();
        // Swimmer
        this.mPlayer.InitSwimmer();
        // Mini map
        this.mMiniMap.InitMiniMap();
        // Enemy manager
        this.mEnemyManager.InitSeaEnemyManager();
        // Item
        this.mItem.InitItem();
        // Controller
        Point screen = GameView.GetScreenSize();
        this.mController.InitController(screen.x-(CharacterController.CONTROLLER_SIZE_BG.x+20),
                screen.y-(CharacterController.CONTROLLER_SIZE_BG.y+20));
        // Stage manager
        this.mStageManager.InitStageManager();
        // Race score
        this.mScore.InitRaceScoreManager();
        // the sign that starting the BGM
        this.mStartingBGM = false;
        return SCENE_MAIN;
    }
    /*
        Update
    */
    @Override
    public int Update() {
        if (Scene.mPlayingF) {
            // update time record
            if (this.mStageManager.UpdateStageManager()) {
                if (!this.mStartingBGM) this.mStartingBGM = true;
                // Stage
                if (this.mStage.UpdateSeaStage(this.mPlayer)) {
                    return SCENE_RELEASE;
                }
                // Swimmer
                this.mPlayer.UpdateSwimmer(this.mItem, this.mPlayerMove);
                // Controller
                this.mPlayerMove = this.mController.UpdateController();
                // Enemy manager
                this.mEnemyManager.UpdateEnemyManager(this.mPlayer, this.mItem);
                // Item
                this.mItem.UpdateItem();
                // Mini map
                this.mMiniMap.UpdateMiniMap();
                // Race score
                this.mScore.UpdateRaceScoreManager();
            }
            // Play BGM
            if (this.mStartingBGM) this.mSound.PlayBGM("sea");
        }
        return SCENE_MAIN;
    }
    /*
        Draw
    */
    @Override
    public void Draw() {
        // fill color
        Point screen = GameView.GetScreenSize();
        this.mImage.fillRect(0, 0, screen.x, screen.y, Color.BLUE);
        // Item
        this.mItem.DrawItem();
        // Enemies
        this.mEnemyManager.DrawSeaEnemyManager();
        // Swimmer
        this.mPlayer.DrawSwimmer();
        // Lightning
        this.mStage.DrawSeaStage();
        // Mini map
        this.mMiniMap.DrawMiniMap();
        // Controller
        this.mController.DrawController();
        // Time record
        this.mStageManager.DrawStageManager();
        // Race score
        this.mScore.DrawRaceScoreManager();
        // when open the menu, to make a whole screen to black.
        if (!Scene.mPlayingF) this.mImage.DrawAlpha(0, 0, 800, 480, 0, 0, 100, this.mBmp);
    }
    /*
        Release
    */
    public int Release() {
        // Stage
        this.mStage.ReleaseSeaStage();
        // Swimmer
        this.mPlayer.ReleaseSwimmer();
        // Enemy manager
        this.mEnemyManager.ReleaseSeaEnemyManager();
        // Mini map
        this.mMiniMap.ReleaseMiniMap();
        // Item
        this.mItem.ReleaseItem();
        // Controller
        this.mController.ReleaseController();
        // Stage manager
        this.mStageManager.ReleaseStageManager();
        // Race score
        this.mScore.ReleaseRaceScoreManager();
        // Sound
        this.mSound.StopBGM();
        this.mController = null;
        this.mStage = null;
        this.mPlayer = null;
        this.mEnemyManager = null;
        this.mMiniMap = null;
        this.mSound = null;
        this.mBmp = null;
        this.mItem = null;
        this.mStageManager = null;
        this.mScore = null;
        return SCENE_END;
    }
}