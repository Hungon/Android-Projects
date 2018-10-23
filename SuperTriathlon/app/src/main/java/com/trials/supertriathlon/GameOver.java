package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * Created by USER on 3/23/2016.
 */
public class GameOver extends Scene {
    // static variables
    // the message that ask player about back to the race.
    private final static String     MESSAGE = "諦めますか？\\e";
    // question's position
    private final static Point      QUESTION_POSITION = new Point(180,200);
    // image setting
    // Never
    private final static Point      NEVER_SIZE = new Point(96,32);
    // Yes
    private final static Point      YES_SIZE = new Point(64,32);
    // animation setting
    private final static int        ANIMATION_ANSWER_FRAME      = 15;
    private final static int        ANIMATION_ANSWER_COUNT_MAX  = 2;
    // the interval time
    private final static int        INTERVAL_TIME = 100;
    // filed
    private Context         mContext;
    private Image           mImage;
    private Talk            mTalk;
    private BaseCharacter   mAnswer[];
    private Animation       mAni[];
    private Sound           mSe;
    private int             mNextScene;
    /*
        Constructor
    */
    public GameOver(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        // allot memory
        this.mAnswer = new BaseCharacter[2];
        this.mAni = new Animation[2];
        for (int i = 0; i < this.mAnswer.length; i++) {
            this.mAnswer[i] = new BaseCharacter(image);
            this.mAni[i] = new Animation();
        }
        this.mTalk = new Talk(context,image,24, Color.WHITE);
        this.mSe = new Sound(context);
        this.mNextScene = SceneManager.SCENE_NOTHING;
    }

    /*
        Initialize
    */
    @Override
    public int Init() {
        // loading image file
        for (BaseCharacter a:this.mAnswer) a.LoadCharaImage(this.mContext,"answer2");
        // loading the SE
        this.mSe.CreateSound("enter");

        // next scene
        this.mNextScene = SceneManager.SCENE_NOTHING;

        Point screen = GameView.GetScreenSize();
        // image setting
        // Never
        this.mAnswer[0].mSize.x = NEVER_SIZE.x;
        this.mAnswer[0].mSize.y = NEVER_SIZE.y;
        this.mAnswer[0].mPos.y = QUESTION_POSITION.y+100;
        this.mAnswer[0].mPos.x = (screen.x-NEVER_SIZE.x)>>1;
        this.mAnswer[0].mExistFlag  = true;
        this.mAnswer[0].mScale = 1.5f;
        // scene to next
        this.mAnswer[0].mType = SceneManager.SCENE_PLAY;
        // to count the time to make the interval
        this.mAnswer[0].mTime = 0;
        // Yes
        this.mAnswer[1].mSize.x = YES_SIZE.x;
        this.mAnswer[1].mSize.y = YES_SIZE.y;
        this.mAnswer[1].mPos.y = this.mAnswer[0].mPos.y+NEVER_SIZE.y+100;
        this.mAnswer[1].mPos.x = this.mAnswer[0].mPos.x;
        this.mAnswer[1].mOriginPos.y = NEVER_SIZE.y;
        this.mAnswer[1].mExistFlag  = true;
        this.mAnswer[1].mScale = 1.0f;
        // scene to next
        this.mAnswer[1].mType = SceneManager.SCENE_SELECT;
        // animation setting
        for (int i = 0; i < this.mAni.length;i ++) {
            this.mAni[i].SetAnimation(
                    0,this.mAnswer[i].mOriginPos.y,
                    this.mAnswer[i].mSize.x,
                    this.mAnswer[i].mSize.y,
                    ANIMATION_ANSWER_COUNT_MAX,
                    ANIMATION_ANSWER_FRAME,0);
        }

        // create talk
        // the position that show the message
        try {
            this.mTalk.CreateTalkEx(MESSAGE,QUESTION_POSITION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SCENE_MAIN;
    }
    /*
        Update
    */
    @Override
    public int Update() {
        // when touch the image, to transition to next scene.
        for (int i = 0; i < this.mAnswer.length; i++) {
            if (this.mAnswer[i].mExistFlag) {
                int touch = GameView.GetTouchAction();
                if (touch == MotionEvent.ACTION_DOWN &&
                    Collision.CheckTouch(
                        this.mAnswer[i].mPos.x, this.mAnswer[i].mPos.y,
                        this.mAnswer[i].mSize.x, this.mAnswer[i].mSize.y,
                        this.mAnswer[i].mScale)) {
                    // get the next scene
                    this.mNextScene = this.mAnswer[i].mType;
                    // when next scene is play-scene, to play the SE
                    if (this.mAnswer[i].mType == SceneManager.SCENE_PLAY) {
                        this.mSe.PlaySE();
                    }
                }
                // update animation
                this.mAni[i].UpdateAnimation(this.mAnswer[i].mOriginPos,true);
            }
        }

        // when substituted the next scene, to transition to the scene after interval.
        if (this.mNextScene != SceneManager.SCENE_NOTHING) {
            if (this.mNextScene == SceneManager.SCENE_PLAY) {
                // to count the time to make the interval.
                this.mAnswer[0].mTime++;
                if (INTERVAL_TIME < this.mAnswer[0].mTime) {
                    this.mAnswer[0].mTime = 0;
                    Wipe.CreateWipe(this.mNextScene, Wipe.TYPE_PENETRATION);
                    return SCENE_RELEASE;
                }
            } else if (this.mNextScene == SceneManager.SCENE_SELECT) {
                Wipe.CreateWipe(this.mNextScene, Wipe.TYPE_PENETRATION);
                return SCENE_RELEASE;
            }
        }
        // update talk
        this.mTalk.UpdateTalk();
        return SCENE_MAIN;
    }
    /*
        Draw
    */
    @Override
    public void Draw() {
        // talk
        if (this.mNextScene == SceneManager.SCENE_NOTHING) {
            this.mTalk.DrawTalk(0, 0);
            // each response image
            for (BaseCharacter a : this.mAnswer) {
                if (!a.mExistFlag) continue;
                this.mImage.DrawScale(
                        a.mPos.x, a.mPos.y,
                        a.mSize.x, a.mSize.y,
                        a.mOriginPos.x, a.mOriginPos.y,
                        a.mScale,
                        a.mBmp
                );
            }
        }
        // if the next scene is play-scene, to show the message.
        else if (this.mNextScene == SceneManager.SCENE_PLAY) {
            this.mImage.drawText("Good luck!",150,200,28,Color.YELLOW);
        }
    }
    /*
        Release
    */
    @Override
    public int Release() {
        for (int i = 0; i < this.mAnswer.length; i++) {
            this.mAnswer[i].ReleaseCharaBmp();
            this.mAnswer[i] = null;
            this.mAni[i] = null;
        }
        this.mSe = null;
        this.mTalk = null;
        return SCENE_END;
    }
}