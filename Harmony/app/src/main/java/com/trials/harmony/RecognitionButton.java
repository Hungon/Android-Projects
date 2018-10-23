package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Vibrator;
import android.view.MotionEvent;

/**
 * Created by USER on 4/18/2016.
 */
public class RecognitionButton implements HasButtons {
    
    // static variables
    public final static int    DISTANCE = -1;
    public final static int    ABOVE   = 0;
    public final static int    RIGHT   = 1;
    public final static int    BENEATH = 2;
    public final static int    LEFT    = 3;
    // the fixed time that vibration process
    private final static int  VIBRATE_FIXED_INTERVAL_TIME = 300;
    // filed
    private Context         mContext;
    private CharacterEx     mButton;
    private int             mVibrateIntervalCount;
    // the flag that as is pressed the button.
    private boolean         mAsPressesButton;
    // the count that is pressed button
    private int             mWasPressedCount;
    // the direction from finger toward a button.
    private int             mDirection;
    // SE
    private Sound   mSound;
    // the task to change that to transition to next scene or to call recognizer.
    private int mChangeableType;

    /*
        Constructor    
    */
    public RecognitionButton(Context context, Image image) {
        this.mContext = context;
        // allot the memory
        this.mButton = new CharacterEx(context,image);
    }
    /*
        Initialize
    */
    public void InitRecognitionButton(
            String imageFile,String seFile, Point pos,
            Point size, Point src, int alpha, float scale, int type)
    {
        // to set the image
        this.mButton.InitCharacterEx(
                imageFile,
                pos.x,pos.y,
                size.x,size.y,
                src.x, src.y,
                alpha,scale,type);
        // the count make the blank time to execute the vibrate.
        this.mVibrateIntervalCount = 0;
        this.mAsPressesButton = false;
        this.mDirection = DISTANCE;
        this.mWasPressedCount = 0;
        // to set the SE
        if (!seFile.equals("")) {
            this.mSound = new Sound(this.mContext);
            this.mSound.CreateSound(seFile);
        }
        // to reset the type that changeable to process.
        mChangeableType = BUTTON_EMPTY;
    }
    /*
        Update
    */
    public int IsPressedTheButton() {
        if (MainView.GetTouchAction() == MotionEvent.ACTION_CANCEL) return Scene.SCENE_MAIN;
        if (this.mButton.mExistFlag) {
            if (Collision.CheckTouch(
                    this.mButton.mPos.x,
                    this.mButton.mPos.y,
                    this.mButton.mSize.x,
                    this.mButton.mSize.y,
                    this.mButton.mScale
            )) {
                // to count the press time
                this.mWasPressedCount++;
                // to make the blank time to execute vibrate process
                if (this.mVibrateIntervalCount == 0) {
                    // when touch the button, to vibrate process
                    Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1);
                    // as pressed the button
                    this.mAsPressesButton = true;
                    // play SE
                    if (this.mSound != null) this.mSound.PlaySE();
                }
                // to increase count
                this.mVibrateIntervalCount++;
                // when pressed down the button, to call recognizer to speak
                if (VIBRATE_FIXED_INTERVAL_TIME < this.mVibrateIntervalCount) {
                    // reset the count
                    this.mVibrateIntervalCount = 0;
                    // task dives into next hence to get the type
                    this.mChangeableType = this.mButton.mType;
                }
            } else {    // when touch away from the button, to reset the count
                this.mVibrateIntervalCount = 0;
                // is get away the finger from the button
                this.mAsPressesButton = false;
                this.mChangeableType = BUTTON_EMPTY;
            }
        }
        return Scene.SCENE_MAIN;
    }
    /*
        as touched the button and then to process
    */
    public boolean IsTouchedButton(CharacterEx ch) {
        if (Collision.CollisionCharacter(this.mButton,ch)) {
            // to count the press time
            this.mWasPressedCount++;
            // to make the blank time to execute vibrate process
            if (this.mVibrateIntervalCount == 0) {
                // when touch the button, to vibrate process
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1);
                // as pressed the button
                this.mAsPressesButton = true;
            }
            // to increase count
            this.mVibrateIntervalCount++;
            // when pressed down the button, to call recognizer to speak
            if (VIBRATE_FIXED_INTERVAL_TIME < this.mVibrateIntervalCount) {
                // reset the count
                this.mVibrateIntervalCount = 0;
            }
            return true;
        } else {    // when touch away from the button, to reset the count
            this.mVibrateIntervalCount = 0;
            // is get away the finger from the button
            this.mAsPressesButton = false;
        }
        return false;
    }
    /*
        Draw the button to recognize the speech
    */
    public void DrawRecognitionButton() {
        this.mButton.DrawCharacterEx();
    }
    /*
        Release
    */
    public void ReleaseRecognitionButton() {
        this.mButton.ReleaseCharacterEx();
        this.mButton = null;
        if (this.mSound != null) {
            this.mSound.StopSE();
            this.mSound = null;
        }
    }
    /*
        To seek the button
        return value is direction.
    */
    public int ToSeekTheButton(Point area) {
        // the direction to return.
        this.mDirection = DISTANCE;
        int action = MainView.GetTouchAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) return this.mDirection;
        // when to draw the button and isn't pressed the button
        // to check the button's position toward finger's position.
        if (this.mButton.mExistFlag && !this.mAsPressesButton) {
            // whole size
            PointF wholeSize = new PointF(
                    this.mButton.mSize.x * this.mButton.mScale,
                    this.mButton.mSize.y * this.mButton.mScale);
            PointF startPos = new PointF(
                    this.mButton.mPos.x-area.x,
                    this.mButton.mPos.y-area.y);
            // to check to touch the button as twice size.
            if (startPos.x < 0) startPos.x = 0;
            if (startPos.y < 0) startPos.y = 0;
            if (Collision.CheckTouch(
                    (int)startPos.x, (int)startPos.y,
                    this.mButton.mSize.x+(area.x<<1),
                    this.mButton.mSize.y+(area.y<<1),
                    this.mButton.mScale
            )) {
                // finger's position
                Point fingerPos = MainView.GetTouchedPosition();
                // button't center position
                Point centerPos = new Point(
                        this.mButton.mPos.x + ((int) wholeSize.x >> 1),
                        this.mButton.mPos.y + ((int) wholeSize.y >> 1));
                // to check the finger's position toward the button's position.
                // to calculate the difference
                PointF difference = new PointF(
                        fingerPos.x-centerPos.x,
                        fingerPos.y-centerPos.y);
                // when to seek to right or left
                if (Math.abs(difference.x) <= (int)wholeSize.x>>1) difference.x = 999999;
                if (Math.abs(difference.y) <= (int)wholeSize.y>>1) difference.y = 999999;
                if (Math.abs(difference.x) < Math.abs(difference.y)) {
                    if (fingerPos.x < this.mButton.mPos.x) {
                        this.mDirection = LEFT;
                    } else if (this.mButton.mPos.x+wholeSize.x < fingerPos.x) {
                        this.mDirection = RIGHT;
                    }
                // when to seek to above or beneath
                } else if (Math.abs(difference.y) < Math.abs(difference.x)) {
                    if (fingerPos.y < this.mButton.mPos.y) {
                        this.mDirection = ABOVE;
                    } else if (this.mButton.mPos.y+wholeSize.y < fingerPos.y) {
                        this.mDirection = BENEATH;
                    }
                }
            }
        }
        return this.mDirection;
    }
    /*
        Variable scale
    */
    public boolean VariableScaleWhenUpToZeroNoExistence(float variable, float max) { return this.mButton.VariableScaleWhenReachedTheFixedValueNoExistence(variable,max); }
    /***************************************************
        Each setter functions
     ***************************************************/
    /*
        Set position
    */
    public void SetPosition(int x, int y) {
        this.mButton.mPos.x = x;
        this.mButton.mPos.y = y;
    }
    /*
        Set alpha
    */
    public void SetAlpha(int alpha) { this.mButton.mAlpha = alpha; }
    /*
        Set exist
    */
    public void SetExist(boolean exist) { this.mButton.mExistFlag = exist; }
    /*
        Set type
    */
    public void SetType(int type) { this.mButton.mType = type; }
    /*
        Set origin position
    */
    public void SetOriginPosition(int x, int y) {
        this.mButton.mOriginPos.x = x;
        this.mButton.mOriginPos.y = y;
    }
    /***************************************************
        Each getter functions
    ***************************************************/
    /*
        Get position
    */
    public Point GetPosition() { return this.mButton.mPos; }
    /*
        Is pressed the button
    */
    public boolean AsPressesTheButton() { return this.mAsPressesButton; }
    /*
        Get count that was pressed button
    */
    public int GetCountWasPressedButton() { return this.mWasPressedCount; }
    /*
        Get the type
    */
    public int GetType() { return this.mButton.mType; }
    /*
        Get the direction
    */
    public int GetDirection() { return this.mDirection; }
    /*
        Get exist
    */
    public boolean GetExist() { return this.mButton.mExistFlag; }
    // Get scale
    public float GetScale() { return this.mButton.mScale; }
    /*
        Get whole size
    */
    public PointF GetWholeSize() {
        return new PointF(
                this.mButton.mSize.x*this.mButton.mScale,
                this.mButton.mSize.y*this.mButton.mScale);
    }
    /*
        Get Alpha
    */
    public int GetAlpha() { return this.mButton.mAlpha; }
    /*
        Get task to changeable execution
        return value is the type of button
    */
    public int GetChangeableType() { return this.mChangeableType; }

}