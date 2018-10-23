package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Created by USER on 3/17/2016.
 */
public class CharacterEx extends BaseCharacter {
    // static variables
    enum SMOOTH_MOVE {
        DIRECTION_VERTICAL,DIRECTION_HORIZONTAL
    }
    // filed
    private Animation   mAni;
    private double      mMillisecond;
    public PointF       mWholeSize;
    private Point       mTerminatePos;
    /*
        Constructor
    */
    public CharacterEx() {
        // allot memory
        this.mAni = new Animation();
        this.mMillisecond = 0;
        this.mWholeSize = new PointF();
        this.mTerminatePos = new Point();
    }
    /*
        Constructor
    */
    public CharacterEx(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mMillisecond = 0;
        // allot memory
        this.mAni = new Animation();
        this.mWholeSize = new PointF();
        this.mTerminatePos = new Point();
    }
    /*
        Initialize the image setting and animation setting
    */
    public void InitCharacterEx(
            String fileName,
            int x, int y,
            int w, int h,
            int alpha, float scale, int type)
    {
        // loading the file
        this.LoadCharaImage(fileName);
        this.mPos.x = x;
        this.mPos.y = y;
        this.mSize.x = w;
        this.mSize.y = h;
        this.mAlpha = alpha;
        this.mScale = scale;
        this.mType = type;
        this.mExistFlag = true;
        this.mMillisecond = 0;
    }
    /*
        Initialize the image setting and animation setting
    */
    public void InitCharacterEx(
            String fileName,
            int x, int y,
            int w, int h,
            int srcX, int srcY,
            int alpha, float scale, int type)
    {
        // loading the file
        this.LoadCharaImage(fileName);
        this.mPos.x = x;
        this.mPos.y = y;
        this.mSize.x = w;
        this.mSize.y = h;
        this.mOriginPos.x = srcX;
        this.mOriginPos.y = srcY;
        this.mAlpha = alpha;
        this.mScale = scale;
        this.mType = type;
        this.mExistFlag = true;
        this.mMillisecond = 0;
    }
    /*
        Set animation
    */
    public void InitAnimation(int countMax, int frame,  int type) {
        this.mAni.SetAnimation(
                0,0,
                this.mSize.x,this.mSize.y,
                countMax,frame,type);
    }
    /*
        Update animation
    */
    public void UpdateCharacterEx(boolean animationReverse) {
        if (this.mExistFlag) {
            this.mAni.UpdateAnimation(this.mOriginPos,animationReverse);
        }
    }
    /*
        Draw
    */
    public void DrawCharacterEx() {
        if (this.mExistFlag) {
            this.mImage.DrawAlphaAndScale(
                    this.mPos.x,
                    this.mPos.y,
                    this.mSize.x,
                    this.mSize.y,
                    this.mOriginPos.x,
                    this.mOriginPos.y,
                    this.mAlpha,
                    this.mScale,
                    this.mBmp
            );
        }
    }
    /*
        Release
    */
    public void ReleaseCharacterEx() {
        this.ReleaseBaseChara();
        this.mAni = null;
        this.mWholeSize = null;
    }
    /*
        Reset the count
    */
    public void ResetMillisecond() { this.mMillisecond = 0; }
    /*
        Character's move
        return values are move's variables
    */
    public PointF CharacterMoveToTouchedPositionEx() {
        // get touched position
        Point touchPos = new Point(MainView.GetTouchedPosition().x, MainView.GetTouchedPosition().y);
        // get camera's position
        Point cameraPos = StageCamera.GetCameraPosition();
        // when camera doesn't exist, to initialize position to 0.
        if (cameraPos == null) cameraPos = new Point(0, 0);
        // calculate player's center position.
        Point pCenter = new Point(
                (this.mPos.x - cameraPos.x) + ((this.mSize.x * (int) (this.mScale))>>1),
                (this.mPos.y - cameraPos.y) + ((this.mSize.y * (int) (this.mScale))>>1)
        );
        // error check
        if (pCenter.x == touchPos.x || pCenter.y == touchPos.y) return new PointF();
        // get action
        int action = MainView.GetTouchAction();
        // if touch action is Action-Up, to stop character's move.
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) return new PointF();

        // calculate triangle area.
        // bottom
        float bottom = touchPos.x - pCenter.x;
        // height
        float height = touchPos.y - pCenter.y;
        // oblique side.
        double oblique = Math.sqrt((double) (bottom * bottom) + (double) (height * height));
        // calculate move and to return that values
        return new PointF((bottom / (float) (oblique)),(height / (float) (oblique)));
    }
    /*
        To move to the specified position
    */
    public void ToMoveToSpecifiedPosition(Point pos) {
        // get camera's position
        Point cameraPos = StageCamera.GetCameraPosition();
        // when camera doesn't exist, to initialize position to 0.
        if (cameraPos == null) cameraPos = new Point(0, 0);
        // calculate player's center position.
        Point pCenter = new Point(
                (this.mPos.x - cameraPos.x) + ((this.mSize.x * (int) (this.mScale))>>1),
                (this.mPos.y - cameraPos.y) + ((this.mSize.y * (int) (this.mScale))>>1)
        );
        // calculate triangle area.
        // bottom
        float bottom = pos.x - pCenter.x;
        // height
        float height = pos.y - pCenter.y;
        // oblique side.
        double oblique = Math.sqrt((double) (bottom * bottom) + (double) (height * height));
        // calculate move and to return that values
        this.mMove.x = (bottom / (float) (oblique)) * Math.abs(this.mSpeed);
        this.mMove.y = (height / (float) (oblique)) * Math.abs(this.mSpeed);
        // to add move to position
        this.mPos.x += this.mMove.x;
        this.mPos.y += this.mMove.y;
    }
    /*
        Constrain available to move area
    */
    public void ConstrainMove() {
        // get camera position
        Point camera = StageCamera.GetCameraPosition();
        // when camera doesn't exist, to initialize position to 0.
        if (camera == null) camera = new Point(0, 0);
        // get screen size
        Point screen = MainView.GetScreenSize();
        // player's total size
        float w = this.mSize.x * this.mScale;
        float h = this.mSize.y * this.mScale;
        // left side
        if (this.mPos.x <= camera.x) this.mPos.x = camera.x;
        // top tip
        if (this.mPos.y <= camera.y) this.mPos.y = camera.y;
        // right side
        if (camera.x + screen.x <= this.mPos.x + w)
            this.mPos.x = camera.x + screen.x - (int) w;
        // bottom tip
        if (camera.y + screen.y <= this.mPos.y + h)
            this.mPos.y = camera.y + screen.y - (int) h;
    }
    /*
        to draw the image in gradually increase or decrease transparent value.
        when the current alpha value reached to limit,
        to return false.
        I mean that false which is process up to the end.
    */
    public boolean VariableAlpha(int variable, int interval) {
        if (!this.mExistFlag) return false;
        // to increase the count for alpha
        this.mCount++;
        if (interval <= 0) interval = 1;
        // when already ends up to limit,
        // to return false.
        if (0 < variable) {
            if (255 <= this.mAlpha) return false;
        } else {
            if (this.mAlpha <= 0) return false;
        }
        if (this.mCount % interval == 0) {
            this.mAlpha += variable;
            // to limit the alpha
            if (255 <= this.mAlpha) {
                this.mAlpha = 255;
            } else if (this.mAlpha <= 0) {
                this.mAlpha = 0;
            }
        }
        this.mCount %= interval;
        return true;
    }
    public boolean VariableAlpha(int variable, int interval, int maxAlpha, int minAlpha) {
        if (!this.mExistFlag) return false;
        // to increase the count for alpha
        this.mCount++;
        if (interval <= 0) interval = 1;
        // to do either increase process or decrease process
        if (0 < variable) {
            if (255 <= this.mAlpha) return false;
        } else {
            if (this.mAlpha <= 0) return false;
        }
        if (this.mCount % interval == 0) {
            this.mAlpha += variable;
            // to limit the alpha
            if (maxAlpha <= this.mAlpha) {
                this.mAlpha = maxAlpha;
            } else if (this.mAlpha <= minAlpha) {
                this.mAlpha = minAlpha;
            }
        }
        this.mCount %= interval;
        return true;
    }
    public boolean VariableAlphaWhenUpToZeroNoExistence(int variable, int interval) {
        if (!this.mExistFlag) return false;
        // to increase the count for alpha
        this.mCount++;
        if (interval <= 0) interval = 1;
        // when already ends up to limit,
        // to return false.
        if (0 < variable) {
            if (255 <= this.mAlpha) return false;
        } else {
            if (this.mAlpha <= 0) return false;
        }
        if (this.mCount % interval == 0) {
            this.mAlpha += variable;
            // to limit the alpha
            if (255 <= this.mAlpha) {
                this.mAlpha = 255;
            } else if (this.mAlpha <= 0) {
                this.mAlpha = 0;
                this.mExistFlag = false;
            }
        }
        this.mCount %= interval;
        return true;
    }
    /*
        Variable scale rate
    */
    public boolean VariableScale(float add, float maxValue) {
        if (!this.mExistFlag) return false;
        if (0 < add) {
            if (maxValue <= this.mScale) {
                this.mScale = maxValue;
                return false;
            }
        } else {
            if (this.mScale <= 0) {
                this.mScale = 0;
                return false;
            }
        }
        this.mScale += add;
        float difference = this.mScale - maxValue;
        this.mScale = (Math.abs(difference) == 0)?maxValue:this.mScale;
        return true;
    }
    public boolean VariableScaleWhenReachedTheFixedValueNoExistence(float add, float maxValue) {
        if (!this.mExistFlag) return false;
        if (0 < add) {
            if (maxValue <= this.mScale) {
                this.mScale = maxValue;
                return false;
            }
        } else {
            if (this.mScale <= 0) {
                this.mExistFlag = false;
                this.mScale = 0;
                return false;
            }
        }
        this.mScale += add;
        float difference = this.mScale - maxValue;
        this.mScale = (Math.abs(difference) == 0)?maxValue:this.mScale;
        return true;
    }
    public boolean VariableSmoothing(SMOOTH_MOVE direction) {
        if (!this.mExistFlag) return false;
        Point differentPos = new Point(this.mTerminatePos.x-this.mPos.x,this.mTerminatePos.y-this.mPos.y);
        if (direction == SMOOTH_MOVE.DIRECTION_HORIZONTAL) {
            if (0 < Math.abs(differentPos.x)) {
                this.mPos.x += this.mMove.x;
            } else if (Math.abs(differentPos.x) <= Math.abs(this.mMove.x)) {
                this.mPos.x = this.mTerminatePos.x;
                return true;
            }
        } else if (direction == SMOOTH_MOVE.DIRECTION_VERTICAL) {
            if (0 < Math.abs(differentPos.y)) {
                this.mPos.y += this.mMove.y;
            } else if (Math.abs(differentPos.y) <= Math.abs(this.mMove.y)) {
                this.mPos.y = this.mTerminatePos.y;
                return true;
            }
        }
        return false;
    }
    //************************************************************************************************
    // Update bezier drawing
    //************************************************************************************************
    public void UpdateBezier(Point[] bezierPos){
        int max = bezierPos.length-1;
        // to count up the millisecond for update bezier
        this.mMillisecond += 0.01;
        // each starting position
        // coordinate-X
        float sx = bezierPos[0].x * (float)(Math.pow((1 - this.mMillisecond) , max));
        float ex = bezierPos[max].x * (float)(Math.pow(this.mMillisecond , max));
        // coordinate-Y
        float sy = bezierPos[0].y * (float)(Math.pow((1 - this.mMillisecond) , max));
        float ey = bezierPos[max].y * (float)(Math.pow(this.mMillisecond , max));
        // to substitute each position
        this.mPos.x = (int)sx;
        this.mPos.y = (int)sy;
        // to calculate the center of position in order to max
        for(int i = 1; i < max; i++){
            // coordinate-X
            this.mPos.x += bezierPos[i].x * max * (float)(Math.pow((1 - this.mMillisecond) , (max - i)))
                    * (float)(Math.pow(this.mMillisecond , i));
            // coordinate-Y
            this.mPos.y += bezierPos[i].y * max * (float)(Math.pow((1 - this.mMillisecond) , (max - i)))
                    * (float)(Math.pow(this.mMillisecond , i));
        }
        // Eventually, to substitute each end position
        this.mPos.x += ex;		// X
        this.mPos.y += ey;		// Y
    }
    /*
        Rotate image
    */
    public Point RotateImage(int startX,int startY, Point size, float scale, int length, int angle) {
        // size
        float w = size.x * scale;
        float h = size.y * scale;
        // calculate center position
        int cx = startX + ((int) w >> 1);
        int cy = startY + ((int) h >> 1);
        // rotate position
        double nextX = Math.cos(angle * 3.14 / 180.0) * length + cx - ((int) w >> 1);
        double nextY = Math.sin(angle * 3.14 / 180.0) * length + cy - ((int) h >> 1);
        Point pos = new Point();
        pos.x = (int) nextX;
        pos.y = (int) nextY;
        return pos;
    }

    /**********************************************************
     Each setter functions
     *********************************************************/
    /*
        Set position
    */
    public void SetPosition(Point pos) {
        this.mPos.x = pos.x;
        this.mPos.y = pos.y;
    }
    /*
        Set size
    */
    public void SetSize(Point size) {
        this.mSize.x = size.x;
        this.mSize.y = size.y;
    }
    /*
        Set origin position of image
    */
    public void SetOriginPosition(Point origin) {
        this.mOriginPos.x = origin.x;
        this.mOriginPos.y = origin.y;
    }
    /*
        Set Type
    */
    public void SetType(int type) { this.mType = type; }

    public void setTerminatePos(Point pos) {
        this.mTerminatePos.x = pos.x;
        this.mTerminatePos.y = pos.y;
    }

    public void setMove(PointF move) {
        this.mMove.x = move.x;
        this.mMove.y = move.y;
    }

    /**********************************************************
     Each getter functions
     *********************************************************/
    /*
        Get position
    */
    public Point GetPosition() { return this.mPos; }
    /*
        Get Alpha
    */
    public int GetAlpha() { return this.mAlpha; }
    /*
        Get type
    */
    public int GetType() { return this.mType; }
    /*
        Get size
    */
    public Point GetSize() { return this.mSize; }
    // get scale
    public float GetScale() { return this.mScale; }
}