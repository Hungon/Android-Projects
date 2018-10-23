package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 1/30/2016.
 */
public class StageManager {

    // static variables
    // start sign
    private final static Point  START_SIGN_SIZE = new Point(112, 80);
    // animation setting
    private final static int    START_SIGN_COUNT_MAX = 4;
    private final static int    START_SIGN_FRAME     = 60;
    // filed
    private Context         mContext;
    private Image           mImage;
    private BaseCharacter   mStage[] = new BaseCharacter[1];
    private Animation       mAni;
    private boolean         mStartF;
    private Time            mTime;         // time class
    private Point           mTimePos = new Point();
    private Sound           mSe[];

    /*
        Constructor
     */
    public StageManager(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i] = new BaseCharacter(image);
        }
        this.mAni = new Animation();
        this.mTime = new Time(activity, image);
        this.mSe = new Sound[2];
        for (int i = 0; i < this.mSe.length; i++) {
            this.mSe[i] = new Sound(activity);
        }
    }
    /*
        Initialize
     */
    public void InitStageManager() {
        // using images
        String imageFiles[] = {
                "startsign",
        };
        // load images
        for (int i = 0; i < imageFiles.length; i++) this.mStage[i].LoadCharaImage(this.mContext, imageFiles[i]);
        // loading the SE
        String SE[] = {"countdown","startsign"};
        for (int i = 0; i < this.mSe.length; i++) this.mSe[i].CreateSound(SE[i]);

        // the flag that start race.
        this.mStartF = false;

        // get current screen size
        Point screen = GameView.GetScreenSize();
        // images' setting
        // start sign
        this.mStage[0].mSize.x = START_SIGN_SIZE.x;
        this.mStage[0].mSize.y = START_SIGN_SIZE.y;
        this.mStage[0].mPos.x = (screen.x-START_SIGN_SIZE.x)>>1;
        this.mStage[0].mPos.y = (screen.y-START_SIGN_SIZE.y)>>1;
        this.mStage[0].mExistFlag = true;

        // initialize time class
        this.mTime.InitTime();
        // when current orientation is portrait,
        if (screen.x == 480) {
            // change time record position
            this.mTimePos.x = 0;
            this.mTimePos.y = 30;
            // change start sign image
            this.mStage[0].mOriginPos.y = 0;
        } else if (screen.x == 800) {
            // change time record position
            this.mTimePos.x = 100;
            this.mTimePos.y = 0;
            // change start sign image
            this.mStage[0].mOriginPos.y = START_SIGN_SIZE.y;
        }
        // animation setting
        this.mAni.SetAnimation(
                this.mStage[0].mOriginPos.x, this.mStage[0].mOriginPos.y,
                this.mStage[0].mSize.x, this.mStage[0].mSize.y,
                START_SIGN_COUNT_MAX, START_SIGN_FRAME, 0);
    }
    /*
        Update
     */
    public boolean UpdateStageManager() {

        // to start count down.
        if (!this.mStartF) {
            if (!this.mAni.UpdateAnimation(this.mStage[0].mOriginPos, false)) {
                this.mStage[0].mExistFlag = false;
                this.mStartF = true;
            }
            int ele[] = {0,0,0,1};
            int time[] = {1,0,0,0};
            for (int i = 0; i < 4; i++) {
                // play the sound
                if (this.mAni.mCount == i && this.mAni.mTime == time[i]) {
                    this.mSe[ele[i]].PlaySE();
                    break;
                }
            }
        }

        // update time to record
        this.mTime.UpdateTime(this.mStartF);

        // when time's count is starting, to update play.
        return this.mStartF;
    }
    /*
        Draw
     */
    public void DrawStageManager() {
        // start sign
        if (this.mStage[0].mExistFlag) {
            this.mImage.DrawImage
                    (this.mStage[0].mPos.x,
                    this.mStage[0].mPos.y,
                    this.mStage[0].mSize.x,
                    this.mStage[0].mSize.y,
                    this.mStage[0].mOriginPos.x,
                    this.mStage[0].mOriginPos.y,
                    this.mStage[0].mBmp);
        }

        // time
        this.mTime.DrawTime(this.mTimePos.x,this.mTimePos.y);
    }
    /*
        Release
     */
    public void ReleaseStageManager() {
        for (int i = 0; i < this.mStage.length; i++) {
            this.mStage[i].ReleaseCharaBmp();
            this.mStage[i] = null;
        }
        for (int i = 0; i < this.mSe.length; i++) {
            this.mSe[i].StopBGM();
            this.mSe[i] = null;
        }
        this.mAni = null;
        this.mContext = null;
        this.mImage = null;
        // time class
        this.mTime.ReleaseTime();
    }
}