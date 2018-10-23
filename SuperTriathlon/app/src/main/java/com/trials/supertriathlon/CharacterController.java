package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 3/19/2016.
 */
public class CharacterController {
    // static variables
    public final static Point      CONTROLLER_SIZE_BG = new Point(112,112);
    private final static Point      CONTROLLER_SIZE_STICK = new Point(44,44);
    // filed
    private Context         mContext;
    private Image           mImage;
    private BaseCharacter   mCon[];
    /*
        Constructor
    */
    public CharacterController(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mCon = new BaseCharacter[2];
        for (int i = 0; i < this.mCon.length; i++) {
            this.mCon[i] = new BaseCharacter(image);
        }
    }
    /*
        Initialize
    */
    public void InitController(int x, int y) {
        for (BaseCharacter c: this.mCon) c.LoadCharaImage(this.mContext,"controller");
        // back image
        this.mCon[0].mPos.x = x;
        this.mCon[0].mPos.y = y;
        this.mCon[0].mSize.x = CONTROLLER_SIZE_BG.x;
        this.mCon[0].mSize.y = CONTROLLER_SIZE_BG.y;
        this.mCon[0].mExistFlag = true;
        this.mCon[0].mAlpha = 100;
        this.mCon[0].mScale = 1.2f;
        // to make the blank time
        this.mCon[0].mTime = 5;
        // stick
        this.mCon[1].mOriginPos.y = CONTROLLER_SIZE_BG.y;
        this.mCon[1].mPos.x = x+((CONTROLLER_SIZE_BG.x-CONTROLLER_SIZE_STICK.x)>>1);
        this.mCon[1].mPos.y = y+((CONTROLLER_SIZE_BG.y-CONTROLLER_SIZE_STICK.y)>>1);
        this.mCon[1].mSize.x = CONTROLLER_SIZE_STICK.x;
        this.mCon[1].mSize.y = CONTROLLER_SIZE_STICK.y;
        this.mCon[1].mExistFlag = true;
        this.mCon[1].mAlpha = 150;
        this.mCon[1].mScale = 1.5f;
        this.mCon[1].mTime = 0;
    }
    /*
        Update
    */
    public FPoint UpdateController() {
        FPoint values = new FPoint();
        // update stick
        if (this.mCon[1].mExistFlag) {
            // get touch index
            int index = GameView.GetTouchIndex();
            if (index == 0) {
                if (Collision.CheckTouch(this.mCon[0].mPos.x, this.mCon[0].mPos.y,
                    this.mCon[0].mSize.x, this.mCon[0].mSize.y, this.mCon[0].mScale)) {
                    // get touched position.
                    Point touch = GameView.GetTouchedPosition(index);
                    // substitute the touched position to stick's position.
                    this.mCon[1].mPos.x = touch.x - ((int) (this.mCon[1].mSize.x * this.mCon[1].mScale) >> 1);
                    this.mCon[1].mPos.y = touch.y - ((int) (this.mCon[1].mSize.y * this.mCon[1].mScale) >> 1);
                    // stick's center position
                    double stickX = this.mCon[1].mPos.x + ((int) (this.mCon[1].mSize.x * this.mCon[1].mScale) >> 1);
                    double stickY = this.mCon[1].mPos.y + ((int) (this.mCon[1].mSize.y * this.mCon[1].mScale) >> 1);
                    // back image
                    double bgX = this.mCon[0].mPos.x + ((int) (this.mCon[0].mSize.x * this.mCon[0].mScale) >> 1);
                    double bgY = this.mCon[0].mPos.y + ((int) (this.mCon[0].mSize.y * this.mCon[0].mScale) >> 1);
                    // when both position are same, to return
                    if (stickX == bgX && stickY == bgY) return new FPoint();
                    // calculate triangle area.
                    // bottom
                    double bottom = stickX - bgX;
                    // height
                    double height = stickY - bgY;
                    // oblique side.
                    double oblique = Math.sqrt((bottom * bottom) + (height * height));
                    // calculate values
                    values.x = (float) (bottom / (float) (oblique));
                    values.y = (float) (height / (float) (oblique));
                    // reset count time
                    this.mCon[1].mTime = 0;
                } else {
                    // make the blank time
                    if (this.mCon[1].mTime < this.mCon[0].mTime) {
                        this.mCon[1].mTime++;
                    } else {
                        this.mCon[1].mTime = this.mCon[0].mTime;
                    }
                    if (this.mCon[0].mTime <= this.mCon[1].mTime) {
                        this.mCon[1].mTime = 0;
                        this.mCon[1].mPos.x = this.mCon[0].mPos.x + ((CONTROLLER_SIZE_BG.x - CONTROLLER_SIZE_STICK.x) >> 1);
                        this.mCon[1].mPos.y = this.mCon[0].mPos.y + ((CONTROLLER_SIZE_BG.y - CONTROLLER_SIZE_STICK.y) >> 1);
                    }
                }
            }
            // to constrain the position
            this.ConstrainPosition();
        }
        return values;
    }
    /*
        Draw
    */
    public void DrawController() {
        for (BaseCharacter c: this.mCon) {
            if (c.mExistFlag) {
                this.mImage.DrawAlphaAndScale(
                        c.mPos.x,c.mPos.y,
                        c.mSize.x,c.mSize.y,
                        c.mOriginPos.x,c.mOriginPos.y,
                        c.mAlpha,c.mScale,
                        c.mBmp
                );
            }
        }
    }
    /*
        Release
    */
    public void ReleaseController() {
        this.mContext = null;
        this.mImage = null;
        for (int i = 0; i < this.mCon.length; i++) {
            this.mCon[i].ReleaseCharaBmp();
            this.mCon[i] = null;
        }
    }
    /*
        Constrain the position
    */
    private void ConstrainPosition() {
        // to constrain the stick's position within the back image.
        // whole size of stick
        FPoint size = new FPoint(this.mCon[1].mSize.x*this.mCon[1].mScale,
                this.mCon[1].mSize.y*this.mCon[1].mScale);
        // left tip
        if (this.mCon[1].mPos.x < this.mCon[0].mPos.x-((int)size.x>>1)) {
            this.mCon[1].mPos.x = this.mCon[0].mPos.x-((int)size.x>>1);
            // top tip
        } else if (this.mCon[1].mPos.y < this.mCon[0].mPos.y-((int)size.y>>1)) {
            this.mCon[1].mPos.y = this.mCon[0].mPos.y-((int)size.y>>1);
            // right tip
        } else if (this.mCon[0].mPos.x+(this.mCon[0].mSize.x*this.mCon[0].mScale) <
                this.mCon[1].mPos.x+((int)size.x>>1)) {
            this.mCon[1].mPos.x =
                    this.mCon[0].mPos.x+(int)(this.mCon[0].mSize.x*this.mCon[0].mScale) -
                            ((int)size.x>>1);
            // bottom tip
        } else if (this.mCon[0].mPos.y+(int)(this.mCon[0].mSize.y*this.mCon[0].mScale) <
                this.mCon[1].mPos.y+((int)size.y>>1)) {
            this.mCon[1].mPos.y =
                    this.mCon[0].mPos.y+(int)(this.mCon[0].mSize.y*this.mCon[0].mScale) -
                            ((int)size.y>>1);
        }
    }
}