package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 2/15/2016.
 */
public class MiniMap {
    // static variables
    // filed
    private Context         mContext;
    private Image           mImage;
    private BaseCharacter   mChara[] = new BaseCharacter[2];
    private Animation       mAni;
    private int             mCurrentStageNum;

    /*
        Constructor
     */
    public MiniMap(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        // base character
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i] = new BaseCharacter(image);
        }
        this.mAni = new Animation();
    }

    /*
        Initialize
     */
    public void InitMiniMap() {
        // get current stage number
        this.mCurrentStageNum = Play.GetCurrentStageNumber();
        // using image files
        String imageFiles[] = {"offroadplayer","roadplayer","seaplayer"};
        String mapFiles[] = {"minimapbg","minimapbg","seaminimapbg"};
        // load image files
        this.mChara[0].LoadCharaImage(this.mContext, mapFiles[this.mCurrentStageNum]);
        // player's image
        this.mChara[1].LoadCharaImage(this.mContext, imageFiles[this.mCurrentStageNum]);

        // difference setting
        // size
        int playerW[] = {OffroadPlayer.PLAYER_SIZE.x,RoadPlayer.RUNNER_SIZE.x,SeaPlayer.SWIMMER_SIZE.x};
        int playerH[] = {OffroadPlayer.PLAYER_SIZE.y,RoadPlayer.RUNNER_SIZE.y,SeaPlayer.SWIMMER_SIZE.y};

        // Set images
        Point screen = GameView.GetScreenSize();
        // mini-map position
        Point pos = new Point();
        // size
        Point size = new Point();
        if (this.mCurrentStageNum == Play.STAGE_OFF_ROAD || this.mCurrentStageNum == Play.STAGE_ROAD) {
            size.x = 40;
            size.y = 700;
            pos.x = (screen.x-size.x)-10;
            pos.y = (screen.y-size.y)>>1;
        } else if (this.mCurrentStageNum == Play.STAGE_SEA) {
            size.x = 630;
            size.y = 40;
            pos.x = 20;
            pos.y = (screen.y-size.y)-10;
        }
        // mini map
        this.mChara[0].mSize.x = size.x;
        this.mChara[0].mSize.y = size.y;
        this.mChara[0].mPos.x = pos.x;
        this.mChara[0].mPos.y = pos.y;
        this.mChara[0].mExistFlag = true;
        this.mChara[0].mAlpha = 70;
        // player
        this.mChara[1].mSize.x = playerW[this.mCurrentStageNum];
        this.mChara[1].mSize.y = playerH[this.mCurrentStageNum];
        this.mChara[1].mScale = 0.5f;
        this.mChara[1].mExistFlag = true;
        // player's position
        Point pos1 = new Point();
        // player's whole size
        float h = this.mChara[1].mSize.y*this.mChara[1].mScale;
        float w = this.mChara[1].mSize.x*this.mChara[1].mScale;
        if (this.mCurrentStageNum == Play.STAGE_OFF_ROAD || this.mCurrentStageNum == Play.STAGE_ROAD) {
            pos1.y = this.mChara[0].mPos.y+this.mChara[0].mSize.y - (int)h;
            pos1.x = this.mChara[0].mPos.x+(this.mChara[0].mSize.x-(int)w)/5;
        } else if (this.mCurrentStageNum == Play.STAGE_SEA) {
            pos1.y = this.mChara[0].mPos.y+(this.mChara[0].mSize.y-(int)h)/5;
            pos1.x = this.mChara[0].mPos.x;
        }
        // substitute each values
        this.mChara[1].mPos.x = pos1.x;
        this.mChara[1].mPos.y = pos1.y;

        // animation setting
        // count max
        int countMax[] = {
                OffroadPlayer.PLAYER_ANIMATION_TYPE_NORMAL_CNT_MAX,
                RoadPlayer.ANIMATION_NORMAL_COUNT_MAX,
                SeaPlayer.ANIMATION_NORMAL_COUNT_MAX};
        this.mAni.SetAnimation(
                this.mChara[1].mOriginPos.x,
                this.mChara[1].mOriginPos.y,
                this.mChara[1].mSize.x,
                this.mChara[1].mSize.y,
                countMax[this.mCurrentStageNum],15,0);
    }

    /*
        Update
     */
    public void UpdateMiniMap() {
        // diverge update from current stage number.
        switch(this.mCurrentStageNum) {
            case Play.STAGE_OFF_ROAD:
                UpdateVerticalMiniMap();
                break;
            case Play.STAGE_ROAD:
                UpdateVerticalMiniMap();
                break;
            case Play.STAGE_SEA:
                UpdateHorizontalMiniMap();
                break;
        }
        // update animation
        this.mAni.UpdateAnimation(this.mChara[1].mOriginPos, false);
    }
    /*
        Update for mini-player move to horizontal.
     */
    private void UpdateVerticalMiniMap() {
        float playerPos = 0.0f;
        // get player's position-Y
        switch(this.mCurrentStageNum) {
            case Play.STAGE_OFF_ROAD:
                playerPos = OffroadPlayer.GetPlayerPosition().y;
                break;
            case Play.STAGE_ROAD:
                playerPos = RoadPlayer.GetPosition().y;
                break;
        }
        float playerH = this.mChara[1].mSize.y*this.mChara[1].mScale;
        float stageDistance = StageCamera.GetCameraWholeArea().bottom;
        float progress = (playerPos / stageDistance)*this.mChara[0].mSize.y;
        // update player's position
        this.mChara[1].mPos.y = (int)progress+this.mChara[0].mPos.y-((int)playerH>>1);
    }
    /*
        Update for mini-player move to horizontal.
    */
    private void UpdateHorizontalMiniMap() {
        float playerPos = SeaPlayer.GetPosition().x;
        float playerW = this.mChara[1].mSize.x*this.mChara[1].mScale;
        float stageDistance = StageCamera.GetCameraWholeArea().right;
        float progress = (playerPos / stageDistance)*this.mChara[0].mSize.x;
        // update player's position
        this.mChara[1].mPos.x = (int)progress+this.mChara[0].mPos.x-((int)playerW>>1);
    }

    /*
        Draw
     */
    public void DrawMiniMap() {
        for (BaseCharacter ch: this.mChara) {
            if (ch.mExistFlag) {
                this.mImage.DrawAlphaAndScale(
                        ch.mPos.x,
                        ch.mPos.y,
                        ch.mSize.x,
                        ch.mSize.y,
                        ch.mOriginPos.x,
                        ch.mOriginPos.y,
                        ch.mAlpha,
                        ch.mScale,
                        ch.mBmp
                );
            }
        }
    }
    /*
        Release
     */
    public void ReleaseMiniMap() {
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i].ReleaseCharaBmp();
            this.mChara[i] = null;
        }
        this.mContext = null;
        this.mImage = null;
        this.mAni = null;
    }
}