package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by USER on 3/13/2016.
 */
public class SeaStage extends StageCamera implements HasScenes {
    // static variables
    // light image
    public final static Point      LIGHTNING_SIZE = new Point(48,128);
    // animation setting
    public final static int        ANIMATION_LIGHTNING_FRAME       = 10;
    public final static int        ANIMATION_LIGHTNING_COUNT_MAX   = 3;
    // goal
    private final static Point      GOAL_SIZE                   = new Point(96,360);
    // animation setting
    private final static int        ANIMATION_GOAL_FRAME        = 10;
    private final static int        ANIMATION_GOAL_COUNT_MAX    = 4;
    // Distance
    private final static int[]      SEA_DISTANCE = {40000,50000,60000};

    // filed
    private Context         mContext;
    private Image           mImage;
    private Rect            mStageArea;
    private BaseCharacter   mLight[];
    private Animation       mAni[];
    private BaseCharacter   mGoal;
    private Animation       mGoalAni;
    private Creation        mCreation;
    private Sound           mSe;
    /*
        Constructor
    */
    public SeaStage(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mStageArea = new Rect();
        this.mLight = new BaseCharacter[2];
        this.mAni = new Animation[2];
        for (int i = 0; i < this.mLight.length; i++) {
            this.mLight[i] = new BaseCharacter(image);
            this.mAni[i] = new Animation();
        }
        this.mCreation = new Creation();
        this.mGoal = new BaseCharacter(image);
        this.mGoalAni = new Animation();
        this.mSe = new Sound(context);
    }

    /*
        Initialize
    */
    public void InitSeaStage() {
        // game level
        int level = Play.GetGameLevel();
        // loading the file
        for (BaseCharacter light: this.mLight) light.LoadCharaImage(this.mContext,"sealight");
        this.mGoal.LoadCharaImage(this.mContext, "seagoal");
        // loading SE
        this.mSe.CreateSound("charge");
        // image setting
        // light
        for (BaseCharacter l: this.mLight) {
            l.mSize.x = LIGHTNING_SIZE.x;
            l.mSize.y = LIGHTNING_SIZE.y;
            l.mRect.left = 18;
            l.mRect.top = 30;
            l.mRect.right = 18;
            l.mRect.bottom = 30;
        }
        // animation setting
        for (Animation ani:this.mAni) {
            ani.SetAnimation(
                    this.mLight[0].mOriginPos.x,
                    this.mLight[0].mOriginPos.y,
                    this.mLight[0].mSize.x,
                    this.mLight[0].mSize.y,
                    ANIMATION_LIGHTNING_COUNT_MAX,
                    ANIMATION_LIGHTNING_FRAME,0);
        }

        // goal
        Point screen = GameView.GetScreenSize();
        this.mGoal.mPos.y = (screen.y-GOAL_SIZE.y)>>1;
        this.mGoal.mPos.x = SEA_DISTANCE[level]-(GOAL_SIZE.x+100);
        this.mGoal.mSize.x = GOAL_SIZE.x;
        this.mGoal.mSize.y = GOAL_SIZE.y;
        // animation setting
        this.mGoalAni.SetAnimation(
                this.mGoal.mOriginPos.x,
                this.mGoal.mOriginPos.y,
                this.mGoal.mSize.x,
                this.mGoal.mSize.y,
                ANIMATION_GOAL_COUNT_MAX,
                ANIMATION_GOAL_FRAME, 0);

        // set fixed time to create the light
        this.mCreation.mFixedInterval = 300;

        // distance
        this.mStageArea = new Rect(0,0,SEA_DISTANCE[level],screen.y);
        // Set camera
        super.SetCameraArea(this.mStageArea);
        super.SetCameraWholeArea(this.mStageArea);

    }
    /*
        Update
    */
    public boolean UpdateSeaStage(SeaPlayer player) {
        // get player speed
        float playerSpeed = SeaPlayer.GetAggregateSpeed();
        // add speed value to left tip in the stage.
        this.mStageArea.left += playerSpeed;
        // update camera area
        super.SetCameraArea(this.mStageArea);

        // create the light
        this.mCreation.mIntervalCount++;
        if (this.mCreation.CreationInterval()) {
            this.CreateLightning();
        }

        // update light
        for (int i = 0; i < this.mLight.length; i++) {
            if (!this.mLight[i].mExistFlag) continue;
            if (!StageCamera.CollisionCamera(this.mLight[i])) {
                this.mLight[i].mExistFlag = false;
            }
            // to check the overlap between the light and player
            if (Collision.CollisionCharacter(
                    player,this.mLight[i],
                    SeaPlayer.GetSafetyArea(),
                    this.mLight[i].mRect)) {
                // add bonus point to the total point score.
                float score = RaceScore.GetTotalPoint()*0.1f;
                // to limit the score
                if (2000 < score) score = 2000;
                RaceScore.UpdateTotalPoint((int)score);
                // not to show the light
                this.mLight[i].mExistFlag = false;
                // to change the attack image
                player.ThroughTheLight(true);
                // play SE
                this.mSe.PlaySE();
            }
            // update animation
            this.mAni[i].UpdateAnimation(this.mLight[i].mOriginPos,true);
        }

        // update goal
        if (!this.mGoal.mExistFlag) {
            Point screen = GameView.GetScreenSize();
            if (StageCamera.GetCameraWholeArea().right - (screen.x + 200) < this.mStageArea.left) {
                // to show the goal
                this.mGoal.mExistFlag = true;
            }
        } else {
            // when player reaches to goal, to transition to result scene.
            Point pos = SeaPlayer.GetPosition();
            if (this.mGoal.mPos.x < pos.x) {
                Wipe.CreateWipe(SCENE_RESULT,Wipe.TYPE_PENETRATION);
                return true;
            }
            // update animation
            this.mGoalAni.UpdateAnimation(this.mGoal.mOriginPos,false);
        }
        return false;
    }
    /*
        Draw
     */
    public void DrawSeaStage() {
        // light
        Point camera = StageCamera.GetCameraPosition();
        if (camera == null) camera = new Point();
        for (BaseCharacter l:this.mLight) {
            if (l.mExistFlag) {
                this.mImage.DrawAlphaAndScale(
                        l.mPos.x-camera.x,l.mPos.y-camera.y,
                        l.mSize.x,l.mSize.y,
                        l.mOriginPos.x,l.mOriginPos.y,
                        l.mAlpha,l.mScale,
                        l.mBmp
                );
            }
        }
        // goal
        if (this.mGoal.mExistFlag) {
            this.mImage.DrawImage(
                    this.mGoal.mPos.x-camera.x,
                    this.mGoal.mPos.y-camera.y,
                    this.mGoal.mSize.x,
                    this.mGoal.mSize.y,
                    this.mGoal.mOriginPos.x,
                    this.mGoal.mOriginPos.y,
                    this.mGoal.mBmp
            );
        }
    }
    /*
        Release
     */
    public void ReleaseSeaStage() {
        this.mContext = null;
        this.mImage = null;
        this.mStageArea = null;
        // light
        for (int i = 0; i < this.mLight.length; i++) {
            this.mLight[i].ReleaseCharaBmp();
            this.mLight[i] = null;
            this.mAni[i] = null;
        }
        // goal
        this.mGoal.ReleaseCharaBmp();
        this.mGoal = null;
        this.mCreation = null;
        // SE
        this.mSe = null;
    }
    /*
        Create light
    */
    public void CreateLightning() {
        for (BaseCharacter l:this.mLight) {
            if (l.mExistFlag) continue;
            // global camera
            Point camera = StageCamera.GetCameraPosition();
            // screen size
            Point screen = GameView.GetScreenSize();
            // whole height
            float h = l.mSize.y*l.mScale;
            l.mPos.x = (camera.x+screen.x)+(MyRandom.GetRandom(100));
            l.mPos.y = MyRandom.GetRandom(screen.y-(int)h);
            l.mExistFlag = true;
            break;
        }
    }
}
