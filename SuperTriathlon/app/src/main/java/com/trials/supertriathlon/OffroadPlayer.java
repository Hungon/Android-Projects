package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * Created by USER on 2/1/2016.
 */
public class OffroadPlayer extends BaseCharacter {

    // static variables
    // the flag that show action
    public final static int    NOT_ACTION      = -1;
    public final static int    AVOID_ACTION    = 0;
    public final static int    JUMP_ACTION     = 1;
    public final static int    SLOW_ACTION     = 2;
    public final static int    SHOW_ACTION     = 3;
    public final static int    FINISH_ACTION   = 4;

    // player setting
    public final static Point PLAYER_SIZE = new Point(34, 64);      // size
    public final static float PLAYER_DEFAULT_SPEED = 5.0f;
    private final static float PLAYER_MAX_SCALE = 3.0f;
    //set animation's value
    // kind of animation type
    public final static byte ANIMATION_TYPE_NORMAL			    = 0x00;
    public final static byte ANIMATION_TYPE_RIGHT_ROTATE		= 0x01;
    public final static byte ANIMATION_TYPE_LEFT_ROTATE		    = 0x02;
    public final static byte ANIMATION_TYPE_RIGHT_QUICK_ROTATE  = 0x03;
    public final static byte ANIMATION_TYPE_LEFT_QUICK_ROTATE	= 0x04;
    public final static byte ANIMATION_TYPE_RIGHT_HALF_ROTATE   = 0x05;
    public final static byte ANIMATION_TYPE_LEFT_HALF_ROTATE	= 0x06;
    public final static byte ANIMATION_TYPE_KIND                = 7;

    //for normal style
    public final static int PLAYER_ANIMATION_TYPE_NORMAL_CNT_MAX   = 2;			// max count
    public final static int PLAYER_ANIMATION_TYPE_NORMAL_FRAME     = 10;		// frame speed
    //for quick rotate
    public final static int PLAYER_ANIMATION_TYPE_QUICK_ROTATE_CNT_MAX = 5;     // count max
    // for half rotate
    public final static int PLAYER_ANIMATION_TYPE_HALF_ROTATE_CNT_MAX = 9;
    //for once rotate
    public final static int PLAYER_ANIMATION_ROTATE_IMAGE_WIDTH    = 64;		// change width
    public final static int PLAYER_ANIMATION_TYPE_ROTATE_CNT_MAX   = 19;	    // count max
    public final static int PLAYER_ANIMATION_TYPE_ROTATE_FRAME     = 2;		    // frame speed


    // filed
    private Context             mContext;
    private Image               mImage;
    private Animation           mAni = new Animation();
    private OffroadObstacles    mObstacles;            // obstacle class
    private boolean             mReserveActionF;
    // for perspiration animation
    private CharacterEffect         mEffect;
    private Sound                   mSe;
    // each getter
    private static Point        mPlayerPosition;
    private static float        mAggregateSpeed;
    private static boolean      mShowF;                // the flag that show technical action.
    private static int          mActionType;
    private static byte         mShowAction;

    /*
        Constructor
     */
    public OffroadPlayer(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        this.mEffect = new CharacterEffect(activity, image);
        // SE
        this.mSe = new Sound(activity);
        // Each getter
        mPlayerPosition = new Point(0,0);
        // aggregate speed
        mAggregateSpeed = this.mSpeed;
        // the flag that show action.
        mShowF = false;
        // action type
        mActionType = NOT_ACTION;
        // animation type
        mShowAction = ANIMATION_TYPE_NORMAL;
    }
    /*
        Constructor
     */
    public OffroadPlayer() {
        // Each getter
        mPlayerPosition = new Point(0,0);
        // aggregate speed
        mAggregateSpeed = this.mSpeed;
        // the flag that show action.
        mShowF = false;
        // action type
        mActionType = NOT_ACTION;
        // animation type
        mShowAction = ANIMATION_TYPE_NORMAL;
    }

    /*
        Initialize
     */
    public void InitPlayer(OffroadObstacles obstacles) {
        // using images
        String imageFiles[] = {
                "offroadplayer",        // player
        };
        // load image file.
        this.mBmp = this.mImage.LoadImage(this.mContext, imageFiles[0]);
        // using SE
        this.mSe.CreateSound("jump");

        Point screen = GameView.GetScreenSize();
        // player setting
        // get stage whole distance.
        int StageHeight = OffroadStage.GetCameraArea().bottom;
        this.mSize.x = PLAYER_SIZE.x;
        this.mSize.y = PLAYER_SIZE.y;
        this.mPos.x = (screen.x-PLAYER_SIZE.x)>>1;
        this.mPos.y = StageHeight - 600;
        this.mExistFlag = true;
        this.mScale = 1.0f;
        this.mSpeed = PLAYER_DEFAULT_SPEED;
        // aggregate speed
        OffroadPlayer.mAggregateSpeed = this.mSpeed;
        // the flag that show action.
        this.mReserveActionF = mShowF = false;
        // action type
        mActionType = NOT_ACTION;
        // the type that show action
        this.mAni.mType = ANIMATION_TYPE_NORMAL;
        // animation type
        mShowAction = ANIMATION_TYPE_NORMAL;

        // animation setting
        this.mAni.SetAnimation(
                this.mOriginPos.x, this.mOriginPos.y,
                this.mSize.x, this.mSize.y,
                PLAYER_ANIMATION_TYPE_NORMAL_CNT_MAX,
                PLAYER_ANIMATION_TYPE_NORMAL_FRAME,
                ANIMATION_TYPE_NORMAL);
        // get obstacle class
        this.mObstacles = obstacles;

        // set camera
        StageCamera.SetCamera(0, this.mPos.y - 600);

        // set effect animation
        this.mEffect.InitCharacterEffect(CharacterEffect.EFFECT_PERSPIRATION);

        // set safety area
        // safety area
        this.mRect.left    = 7;
        this.mRect.top     = 7;
        this.mRect.right   = 7;
        this.mRect.bottom  = 32;
    }

    /*
        Update
     */
    public void UpdatePlayer() {

        // update move process
        if (GameView.GetTouchAction() == MotionEvent.ACTION_UP ||
            GameView.GetTouchAction() == MotionEvent.ACTION_CANCEL) {
            // reset move
            this.mMoveY = 0.0f;
            this.mMoveX = 0.0f;
        } else {
            // when doing action, not to move.
            if (mActionType != JUMP_ACTION) this.CharacterMoveToTouchedPosition(PLAYER_DEFAULT_SPEED);
        }

        if (!mShowF) {
            // to check overlap between player and obstacles.
            if (this.mExistFlag && mActionType == NOT_ACTION) {
                mActionType = this.mObstacles.CollisionObstacles(this);       // get obstacle's type

                // when type isn't bog, back to default speed
                this.UpdateBumpToBog();

                // diverge action from obstacle's type.
                switch (mActionType) {
                    case AVOID_ACTION:
                        this.InitBumpToRock();
                        break;
                    case JUMP_ACTION:
                        this.InitBumpToJump();
                        break;
                    case SLOW_ACTION:               // more than normal level
                        this.InitBumpToBog();
                        break;
                }
            }
            // diverge action from the type that bumped to object.
            if (mActionType != NOT_ACTION) {
                switch (mActionType) {
                    case AVOID_ACTION:
                        this.UpdateBumpToRock();
                        break;
                    case JUMP_ACTION:
                        this.UpdateBumpToJump();
                        break;
                }
            }
        // show action
        } else {
            if (mActionType == SHOW_ACTION && mShowAction == ANIMATION_TYPE_NORMAL) {
                // get the count that current success.
                int count = ActionCommand.GetSuccessCount();
                // Initialize action
                this.InitAnimationAction(count);
            }
            // finish action
            if (mActionType == FINISH_ACTION) {
                if (1.0f < this.mScale) {
                    this.mScale -= 0.1f;
                } else if (this.mScale <= 1.0f) {
                    this.mScale = 1.0f;
                    // the flag that show action
                    this.mReserveActionF = mShowF = false;
                    mActionType = NOT_ACTION;
                    mShowAction = ANIMATION_TYPE_NORMAL;
                }
            }
        }

        // update animation
        if (this.mExistFlag && this.mScale == 1.0f || mShowF) this.UpdateAnimationAction();

        // update position
        mPlayerPosition.x = this.mPos.x;
        mPlayerPosition.y = this.mPos.y;
        // update aggregate speed
        OffroadPlayer.mAggregateSpeed = this.mSpeed;
        // update coordinate-Y based on aggregate seed.
        this.mPos.y -= OffroadPlayer.mAggregateSpeed;

        // set camera
        int localY = this.mPos.y - StageCamera.GetCameraPosition().y;
        StageCamera.SetCamera(0, this.mPos.y - localY);
        // constrain available to move area
        this.ConstrainMove();
    }
    /*
        Draw
     */
    public void DrawPlayer() {

        // get camera's position
        Point cameraPos = StageCamera.GetCameraPosition();

        // player
        if (this.mTime % 2 == 0) {
            this.mImage.DrawScale(
                    this.mPos.x,
                    this.mPos.y - cameraPos.y,
                    this.mSize.x,
                    this.mSize.y,
                    this.mOriginPos.x,
                    this.mOriginPos.y,
                    this.mScale,
                    this.mBmp
            );
        }
        // effect
        this.mEffect.DrawCharacterEffect();
    }
    /*
        Release
     */
    public void ReleasePlayer() {
        // Release BaseCharacter class.
        this.ReleaseCharaBmp();
        this.mContext = null;         // activity
        this.mImage = null;            // image object
        this.mAni = null;              // animation class
        this.mObstacles = null;        // obstacle class
        this.mEffect.ReleaseCharacterEffect(); // effect
        this.mEffect = null;
        this.mSe = null;
    }
    /*
        Initialization that bump to object that is rock
    */
    private void InitBumpToRock() {
        this.mExistFlag = false;
    }
    /*
        Update to process that bumped to rock
     */
    private void UpdateBumpToRock() {
        if (!this.mExistFlag) {
            if (!this.AvoidObject(this,200)) {
                mActionType = NOT_ACTION;
            }
        }
    }

    /*
        Initialize that bump to object that is jump point.
    */
    private void InitBumpToJump() {
        // play the SE
        this.mSe.PlaySE();
    }
    /*
        Update to process tha bumped to jump point.
    */
    private void UpdateBumpToJump() {
        // Update command, reserve to show action for make the time that create action command.
        if (!ActionCommand.GetCreationFlag() && this.mReserveActionF) {
            mShowF = true;
            mActionType = SHOW_ACTION;
        } else {
            // add scale rate to current scale rate.
            if (this.mScale < PLAYER_MAX_SCALE) {
                this.mScale += 0.1f;
            } else if (PLAYER_MAX_SCALE <= this.mScale) {
                this.mScale = PLAYER_MAX_SCALE;
                this.mReserveActionF = true;
            }
        }
    }

    /*
        Initialize slow action
     */
    private void InitBumpToBog() {
        // to be slow while touch bog.
        if (this.mSpeed != ((int)PLAYER_DEFAULT_SPEED >> 1)) this.mSpeed = ((int)PLAYER_DEFAULT_SPEED >> 1);
        mActionType = NOT_ACTION;
        // reset animation setting for effect
        if (this.mEffect.mType != CharacterEffect.EFFECT_PERSPIRATION) this.mEffect.ResetAnimation();
        // set type
        this.mEffect.ToShowTheEffect();
    }
    /*
        Update slow action
     */
    private void UpdateBumpToBog() {
        // when not to bump to bog, to back to default speed and not show the effect.
        if (mActionType != SLOW_ACTION){
            this.mSpeed = PLAYER_DEFAULT_SPEED;
            // not show effect
            this.mEffect.NotToShowTheEffect();
        } else {
            // update effect animation
            this.mEffect.UpdateCharacterEffect(
                    this.mPos.x,
                    this.mPos.y,
                    this.mSize.x,
                    this.mSize.y,
                    this.mScale,false);
        }
    }

    /*
        Initialize action type
    */
    private void InitAnimationAction(int count) {
        // not to do action, error check
        if (count <= 0 || 4 <= count) {
            mActionType = FINISH_ACTION;
            return;
        }
        // kind of animation
        byte actionType[] = {
                ANIMATION_TYPE_RIGHT_QUICK_ROTATE,
                ANIMATION_TYPE_LEFT_QUICK_ROTATE,
                ANIMATION_TYPE_RIGHT_HALF_ROTATE,
                ANIMATION_TYPE_LEFT_HALF_ROTATE,
                ANIMATION_TYPE_RIGHT_ROTATE,
                ANIMATION_TYPE_LEFT_ROTATE,
        };
        // count max to animation
        int countMax[] = {
                PLAYER_ANIMATION_TYPE_QUICK_ROTATE_CNT_MAX,
                PLAYER_ANIMATION_TYPE_QUICK_ROTATE_CNT_MAX,
                PLAYER_ANIMATION_TYPE_HALF_ROTATE_CNT_MAX,
                PLAYER_ANIMATION_TYPE_HALF_ROTATE_CNT_MAX,
                PLAYER_ANIMATION_TYPE_ROTATE_CNT_MAX,
                PLAYER_ANIMATION_TYPE_ROTATE_CNT_MAX,
        };

        if (count == 1) {
            byte action[] = {ANIMATION_TYPE_LEFT_QUICK_ROTATE, ANIMATION_TYPE_RIGHT_QUICK_ROTATE};
            mShowAction = action[MyRandom.GetRandom(2)];
        }
        if (count == 2){
            byte action[] = {ANIMATION_TYPE_LEFT_HALF_ROTATE, ANIMATION_TYPE_RIGHT_HALF_ROTATE};
            mShowAction = action[MyRandom.GetRandom(2)];
        }
        if (count == 3) {
            byte action[] = {ANIMATION_TYPE_LEFT_ROTATE, ANIMATION_TYPE_RIGHT_ROTATE};
            mShowAction = action[MyRandom.GetRandom(2)];
        }
        
        // loop to animation max that show action
        for (int i = 0; i < ANIMATION_TYPE_KIND - 1; i++) {
            if (mShowAction == actionType[i]) {
                // change width to rotate action
                this.mSize.x = PLAYER_ANIMATION_ROTATE_IMAGE_WIDTH;
                // set animation
                this.mAni.SetAnimation(
                        0,0,
                        this.mSize.x,this.mSize.y,
                        countMax[i],PLAYER_ANIMATION_TYPE_ROTATE_FRAME,
                        mShowAction);
                this.mAni.mTime = 0;
                this.mAni.mCount = 0;
                break;
            }
        }
    }

    /*
        Update animation
     */
    private void UpdateAnimationAction() {
        // kind of animation
        byte animationType[] = {
                ANIMATION_TYPE_NORMAL,
                ANIMATION_TYPE_RIGHT_ROTATE,
                ANIMATION_TYPE_LEFT_ROTATE,
                ANIMATION_TYPE_RIGHT_QUICK_ROTATE,
                ANIMATION_TYPE_LEFT_QUICK_ROTATE,
                ANIMATION_TYPE_RIGHT_HALF_ROTATE,
                ANIMATION_TYPE_LEFT_HALF_ROTATE,
        };
        // treat origin position to action type
        int originPos[] = {
                ANIMATION_TYPE_NORMAL,
                ANIMATION_TYPE_RIGHT_ROTATE,
                ANIMATION_TYPE_LEFT_ROTATE,
                ANIMATION_TYPE_RIGHT_ROTATE,
                ANIMATION_TYPE_LEFT_ROTATE,
                ANIMATION_TYPE_RIGHT_ROTATE,
                ANIMATION_TYPE_LEFT_ROTATE,
        };

        // update animation based on action type.
        for (int i = 0; i < ANIMATION_TYPE_KIND; i++) {
            if (this.mAni.mType == animationType[i]) {
                // substitute origin element to variable direction.
                this.mAni.mDirection = originPos[i];
                // normal animation
                if (this.mAni.mType == ANIMATION_TYPE_NORMAL) {
                    if (!this.mAni.UpdateAnimation(this.mOriginPos, false)) {
                        // reset type
                        this.mAni.mType &= ~animationType[i];
                        break;
                    }
                }
                // once rotate
                else if (this.mAni.mType == ANIMATION_TYPE_RIGHT_ROTATE ||
                        this.mAni.mType == ANIMATION_TYPE_LEFT_ROTATE) {
                    if (!this.mAni.UpdateAnimation(this.mOriginPos, false)) {
                        this.ResetAnimationAction();
                        break;
                    }
                }
                // quick rotate animation
                else if (this.mAni.mType == ANIMATION_TYPE_RIGHT_QUICK_ROTATE ||
                        this.mAni.mType == ANIMATION_TYPE_LEFT_QUICK_ROTATE) {
                    // subtraction animation
                    if (!this.mAni.UpdateAnimation(this.mOriginPos, true)) {
                        this.ResetAnimationAction();
                        break;
                    }
                }
                // half rotate animation
                else if (this.mAni.mType == ANIMATION_TYPE_RIGHT_HALF_ROTATE ||
                        this.mAni.mType == ANIMATION_TYPE_LEFT_HALF_ROTATE) {
                    // subtraction animation
                    if (!this.mAni.UpdateAnimation(this.mOriginPos, true)) {
                        this.ResetAnimationAction();
                        break;
                    }
                }
            }
        }
    }

    /*
        Reset animation setting
     */
    private void ResetAnimationAction() {
        // back to normal width
        this.mSize.x = PLAYER_SIZE.x;
        // set animation
        this.mAni.SetAnimation(
                0,0,
                this.mSize.x,this.mSize.y,
                PLAYER_ANIMATION_TYPE_NORMAL_CNT_MAX,PLAYER_ANIMATION_TYPE_NORMAL_FRAME,
                ANIMATION_TYPE_NORMAL);
        this.mAni.mTime = 0;
        this.mAni.mCount = 0;
        this.mOriginPos.y = 0;         // for change the image immediately
        // action to next
        mActionType = FINISH_ACTION;
    }

    /*
        Getter functions
     */
    /*
        Get position
     */
    public static Point GetPlayerPosition() { return mPlayerPosition; }
    /*
        Get aggregate speed
     */
    public static float GetAggregateSpeed() { return mAggregateSpeed; }
    /*
        Get current action type
     */
    public static int GetActionType() { return mActionType; }
    /*
        Get the flag tha show action
     */
    public static boolean GetShowAction() { return mShowF; }
}