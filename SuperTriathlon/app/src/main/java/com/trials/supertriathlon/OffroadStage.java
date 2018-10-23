package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by USER on 1/30/2016.
 */
public class OffroadStage extends StageCamera implements HasScenes {

    // static variables
    // Goal line
    private final static Point  GOAL_LINE_SIZE = new Point(320,55);
    private final static Point  GOAL_LINE_POSITION = new Point(
            (GameView.GetScreenSize().x-GOAL_LINE_SIZE.x)>>1,50
    );
    // filed
    private Context         mContext;
    private Image           mImage;
    private BaseCharacter   mStage[] = new BaseCharacter[3];
    private Rect            mWholeSize = new Rect(0,0,0,0);
    private int             mDistance;
    /*
        Constructor
     */
    public OffroadStage(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i] = new BaseCharacter(image);
        }
    }
    /*
        Initialize
     */
    public void InitStage() {
        // using images
        String imageFiles[] = {
                "offroadbg",
                "offroadbg",
                "offroadgoal",
        };
        // load images
        for (int i = 0; i < imageFiles.length; i++) this.mStage[i].LoadCharaImage(this.mContext, imageFiles[i]);

        // select distance based on game level
        int level = Play.GetGameLevel();
        int distance[] = {25000, 30000, 35000};

        // images' setting
        Point screen = GameView.GetScreenSize();    // get screen size
        // background image
        this.mStage[0].mPos.y = distance[level]-(screen.y+50);
        this.mStage[1].mPos.y = this.mStage[0].mPos.y-(screen.y+50);
        this.mStage[1].mExistFlag = this.mStage[0].mExistFlag = true;

        // Goal line
        this.mStage[2].mSize.x = GOAL_LINE_SIZE.x;
        this.mStage[2].mSize.y = GOAL_LINE_SIZE.y;
        // position is global coordination
        this.mStage[2].mPos.x = GOAL_LINE_POSITION.x;
        this.mStage[2].mPos.y = GOAL_LINE_POSITION.y;

        // set whole area in stage.
        this.mWholeSize.left = 0;
        this.mWholeSize.top = 0;
        this.mWholeSize.right = screen.x;
        // this is whole distance
        this.mDistance = this.mWholeSize.bottom = distance[level];
        super.SetCameraArea(this.mWholeSize);
        super.SetCameraWholeArea(this.mWholeSize);
    }
    /*
        Update
     */
    public boolean UpdateStage() {
        Point screen = GameView.GetScreenSize();    // get screen size
        // vertical scrolling
        float playerSpeed = OffroadPlayer.GetAggregateSpeed();
        // subtract speed value from distance in the stage.
        this.mDistance -= playerSpeed;
        Rect area = new Rect(0,0,screen.x,this.mDistance);
        // update camera area
        super.SetCameraArea(area);

        // scroll background image
        int camera = StageCamera.GetCameraPosition().y;
        for (int i = 0; i < 2; i++) {
            if (camera+screen.y <= this.mStage[i].mPos.y) {
                this.mStage[i].mPos.y = camera-1000;
            }
        }

        // when whole distance is less than 800pix, to show goal line.
        if (!this.mStage[2].mExistFlag) {
            if (this.mDistance <= screen.y) {
                this.mStage[2].mExistFlag = true;
            }
        } else {
            // get player's position
            Point playerPos = OffroadPlayer.GetPlayerPosition();
            // get the flag that player is showing the action.
            boolean action = OffroadPlayer.GetShowAction();
            // when player's y-coordination reach to goal line, to transition to result scene.
            if (!action && playerPos.y <= this.mStage[2].mPos.y+this.mStage[2].mSize.y) {
                // create wipe and transition to scene.
                Wipe.CreateWipe(SCENE_RESULT, Wipe.TYPE_PENETRATION);
                return true;
            }
        }
        return false;
    }
    /*
        Draw
     */
    public void DrawStage() {

        // get camera position
        Point camera = StageCamera.GetCameraPosition();

        // BG
        for (BaseCharacter ch: this.mStage) {
            if (ch.mExistFlag) {
                this.mImage.DrawImageFast(ch.mPos.x, ch.mPos.y-camera.y, ch.mBmp);
            }
        }
        // Goal line
        if (this.mStage[2].mExistFlag) {
            this.mImage.DrawImage(
                    this.mStage[2].mPos.x,
                    this.mStage[2].mPos.y-camera.y,
                    this.mStage[2].mSize.x,
                    this.mStage[2].mSize.y,
                    this.mStage[2].mOriginPos.x,
                    this.mStage[2].mOriginPos.y,
                    this.mStage[2].mBmp
            );
        }
    }
    /*
        Release
     */
    public void ReleaseStage() {
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i].ReleaseCharaBmp();
            this.mStage[i] = null;
        }
        this.mContext = null;
        this.mImage = null;
    }
}