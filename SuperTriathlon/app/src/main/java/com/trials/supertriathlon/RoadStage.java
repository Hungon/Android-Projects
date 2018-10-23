package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by USER on 2/23/2016.
 */
public class RoadStage extends StageCamera implements HasScenes {
    // static variables
    // BG
    private final static Point      STAGE_BG_SIZE = new Point(480,900);
    // road-line
    private final static Point      STAGE_LINE_SIZE = new Point(15,100);
    private final static int        STAGE_LINE_SPACE_Y = 75;
    private final static float      STAGE_LINE_DEFAULT_SCALE = 1.0f;
    private final static float      STAGE_LINE_MAX_SCALE     = 1.5f;
    // Side BG
    private final static Point      STAGE_SIDE_BG_SIZE = new Point(100,1800);
    // Goal
    private final static Point      GOAL_SIZE = new Point(370,200);

    // Distance
    private final static int[]      ROAD_DISTANCE = {20000,25000,30000};

    // filed
    private Context         mContext;
    private Image           mImage;
    private BaseCharacter   mStage[] = new BaseCharacter[6];
    private Rect            mStageArea;
    private BaseCharacter   mSideBg[] = new BaseCharacter[2];
    
    /*
        Constructor
    */
    public RoadStage(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i] = new BaseCharacter(image);
        }
        // side bg
        for (int i = 0; i < this.mSideBg.length; i++) {
            this.mSideBg[i] = new BaseCharacter(image);
        }
    }
    /*
        Initialize
     */
    public void InitStage() {
        // get game level
        int level = Play.GetGameLevel();
        // using image files
        String imageFiles[] = {
                "roadbg",
                "roadline",
                "roadline",
                "roadline",
                "roadline",
                "roadgoal"
        };
        // side bg
        String sideImages[] = {"roadleftsidebg", "roadrightsidebg"};
        // load images
        for (int i = 0; i < this.mStage.length; i++)  this.mStage[i].LoadCharaImage(this.mContext, imageFiles[i]);
        // side bg
        for (int i = 0; i < this.mSideBg.length; i++) this.mSideBg[i].LoadCharaImage(this.mContext, sideImages[i]);

        // distance
        Point screen = GameView.GetScreenSize();
        this.mStageArea = new Rect(0,0,screen.x, ROAD_DISTANCE[level]);
        // Set camera
        super.SetCameraArea(this.mStageArea);
        super.SetCameraWholeArea(this.mStageArea);

        //  image setting
        // BG
        this.mStage[0].mSize.x = STAGE_BG_SIZE.x;
        this.mStage[0].mSize.y = STAGE_BG_SIZE.y;
        this.mStage[0].mExistFlag = true;
        
        // road line
        int cameraY = this.mStageArea.bottom-screen.y;
        for (int i = 1; i < this.mStage.length-1; i++) {
            this.mStage[i].mSize.x = STAGE_LINE_SIZE.x;
            this.mStage[i].mSize.y = STAGE_LINE_SIZE.y;
            this.mStage[i].mPos.x = (screen.x-STAGE_LINE_SIZE.x)>>1;
            this.mStage[i].mExistFlag = true;
            // variable scale
            this.mStage[i].mScale = (float)this.mStage[i].VariableScaleRateBasedOnCameraAngle(
                    STAGE_LINE_MAX_SCALE,STAGE_LINE_DEFAULT_SCALE);
            // size
            float lineH = this.mStage[i].mSize.y*this.mStage[i].mScale;
            this.mStage[i].mPos.y = cameraY + (int)(lineH+STAGE_LINE_SPACE_Y)*(i-1);
        }
        // Goal
        this.mStage[5].mSize.x = GOAL_SIZE.x;
        this.mStage[5].mSize.y = GOAL_SIZE.y;
        this.mStage[5].mPos.x = (screen.x-this.mStage[5].mSize.x)/2;

        // Side BG
        for (BaseCharacter ch: this.mSideBg) {
            ch.mSize.x = STAGE_SIDE_BG_SIZE.x;
            ch.mSize.y = STAGE_SIDE_BG_SIZE.y;
            ch.mExistFlag = true;
            ch.mPos.y = -1*STAGE_BG_SIZE.y;
        }
        // right side bg position
        this.mSideBg[1].mPos.x = screen.x-this.mSideBg[1].mSize.x;
    }
    
    /*
        Update
     */
    public boolean UpdateStage() {
        // get player speed
        float playerSpeed = RoadPlayer.GetAggregateSpeed();
        // subtract speed value from distance in the stage.
        this.mStageArea.bottom -= playerSpeed;
        // update camera area
        super.SetCameraArea(this.mStageArea);

        // Goal line
        Point screen = GameView.GetScreenSize();
        if (!this.mStage[5].mExistFlag) {
            if (this.mStageArea.bottom <= screen.y+100) this.mStage[5].mExistFlag = true;
        } else {
            // get player's position
            int posY = RoadPlayer.GetPosition().y;
            this.mStage[5].mScale = (float)this.mStage[5].VariableScaleRateBasedOnCameraAngle(1.0f,1.3f);
            float size = this.mStage[5].mSize.y*this.mStage[5].mScale;
            if (posY <= this.mStage[5].mPos.y+size) {
                Wipe.CreateWipe(SCENE_RESULT,Wipe.TYPE_PENETRATION);
                return true;
            }
        }


        // Scroll road-line
        for (int i = 1; i < this.mStage.length-1; i++) {
            // update scale
            this.mStage[i].mScale = (float)this.mStage[i].VariableScaleRateBasedOnCameraAngle(
                    STAGE_LINE_MAX_SCALE,STAGE_LINE_DEFAULT_SCALE);
            // when road-line is out of screen, to rearrangement.
            int cameraY = StageCamera.GetCameraPosition().y;     // camera position
            if (cameraY+screen.y+100 <= this.mStage[i].mPos.y) {
                this.mStage[i].mPos.y = cameraY-this.mStage[i].mSize.y;
                this.mStage[i].mScale = STAGE_LINE_DEFAULT_SCALE;
            }
        }

        // Scroll side BG
        for (BaseCharacter ch: this.mSideBg) {
            ch.mPos.y += playerSpeed;
            // rearrangement
            if (-50 <= ch.mPos.y) {
                ch.mPos.y = -1*this.mStage[0].mSize.y;
            }
        }
        return false;
    }
    
    /*
        Draw
     */
    public void DrawStage() {
        // Side BG
        for (BaseCharacter ch: this.mSideBg) {
            if (ch.mExistFlag) {
                mImage.DrawScale(
                        ch.mPos.x,
                        ch.mPos.y,
                        ch.mSize.x,
                        ch.mSize.y,
                        ch.mOriginPos.x,
                        ch.mOriginPos.y,
                        ch.mScale,
                        ch.mBmp
                );
            }
        }
        // BG
        if (this.mStage[0].mExistFlag) {
            this.mImage.DrawScale(
                    this.mStage[0].mPos.x,
                    this.mStage[0].mPos.y-50,
                    this.mStage[0].mSize.x,
                    this.mStage[0].mSize.y,
                    this.mStage[0].mOriginPos.x,
                    this.mStage[0].mOriginPos.y,
                    this.mStage[0].mScale,
                    this.mStage[0].mBmp
            );
        }
        // road-line
        Point camera = StageCamera.GetCameraPosition();
        for (int i = 1; i < this.mStage.length; i++) {
            if (this.mStage[i].mExistFlag) {
                this.mImage.DrawScale(
                        this.mStage[i].mPos.x-camera.x,
                        this.mStage[i].mPos.y-camera.y,
                        this.mStage[i].mSize.x,
                        this.mStage[i].mSize.y,
                        this.mStage[i].mOriginPos.x,
                        this.mStage[i].mOriginPos.y,
                        this.mStage[i].mScale,
                        this.mStage[i].mBmp
                );
            }
        }
    }
    
    /*
        Release
     */
    public void ReleaseStage() {
        this.mContext = null;
        this.mImage = null;
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i].ReleaseCharaBmp();
            this.mStage[i] = null;
        }
        this.mStageArea = null;
        // Side bg
        for (int i = 0; i < this.mSideBg.length; i++) {
            this.mSideBg[i].ReleaseCharaBmp();
            this.mSideBg[i] = null;
        }
    }
}