package com.trials.harmony;

import android.content.Context;

/**
 * Created by Kohei Moroi on 8/4/2016.
 */
public class TaskButton extends CharacterEx implements HasButtons {
    public int mAddAngle;
    public int mJustAngle;
    public int mMusicId;
    private int mAddAlpha;
    private Sound mSound;
    private boolean mAvailableToPlay;
    public TaskButton(Context context, Image image) {
        super(context,image);
        this.mSound = new Sound(context);
        this.mMusicId = BUTTON_EMPTY;
    }
    public void InitTask(String file,int x, int y, int w, int h, int srcX, int srcY, int alpha, float scale) {
        this.InitCharacterEx(file,x,y,w,h,srcX,srcY,alpha,scale,this.mType);
        this.mAddAngle = 0;
        this.mJustAngle = 360;
        // using se
        this.mSound.CreateSound("click");
        this.mAvailableToPlay = true;
        this.mAddAlpha = 0;
    }
    // when the button's current angle just reached to 270,
    // to return own type
    public int UpdateRotate(int rotateX, int rotateY, int length) {
        int type = BUTTON_EMPTY;
        if (this.mExistFlag) {
            // when the current angle is 270, to clearly draw
            if (265 <= this.mAngle && this.mAngle <= 275) {
                this.mAddAlpha = 2;
                if (this.mAvailableToPlay) this.mSound.PlaySE();       // to play SE
                this.mAvailableToPlay = false;
            } else {
                this.mAddAlpha = -2;
                this.mAvailableToPlay = true;
            }
            int difference = 270-this.mAngle;
            type = (Math.abs(difference)<=Math.abs(this.mAddAngle))?this.mType:BUTTON_EMPTY;
            this.mPos = this.RotateImage(rotateX, rotateY, this.mSize, this.mScale, length, this.mAngle);
            this.mAngle+=this.mAddAngle;
        }
        return type;
    }
    public void UpdateAlpha() {
        this.VariableAlpha(this.mAddAlpha,2);
        this.mAlpha = (this.mAlpha < 100) ? 100 : this.mAlpha;
    }
    public void DrawTask() { this.DrawCharacterEx(); }
    public void ReleaseTask() {
        this.ReleaseCharacterEx();
        this.mSound.StopSE();
        this.mSound = null;
    }
    // set angle
    public void SetAngle(int angle, int rotateX, int rotateY, int length) {
        this.mAngle = angle;
        this.mPos = this.RotateImage(rotateX, rotateY, this.mSize, this.mScale, length, this.mAngle);
    }
    // get the current music id that reached to 270 degree
    public int GetMusicId() { return (this.mAngle == 270)?this.mMusicId:BUTTON_EMPTY; }
}