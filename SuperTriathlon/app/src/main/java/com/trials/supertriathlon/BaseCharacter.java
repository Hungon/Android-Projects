package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

/**
 * Created by USER on 1/28/2016.
 */
public class BaseCharacter {

    // static variables
    public final static int PRIORITY_BACKWARD = 0;
    public final static int PRIORITY_FORWARD = 1;

    // field
    public Point mPos;                // position
    public Point mSize;               // size
    public float mMoveX;              // move value
    public float mMoveY;              // move value
    public float mSpeed;              // variable speed
    public float mPreviewSpeed;       // preview speed
    public int mTime;               // time
    public boolean mExistFlag;          // exist flag
    public Point mOriginPos;          // origin position
    public int mAlpha;              // alpha value
    public float mScale;              // scale rate
    public int mType;               // kind of type
    public Rect mRect;
    public int mPriority;
    public int mAngle;
    public Point mPreviewPos;
    public Bitmap mBmp;                // bitmap object to draw the character image.
    private Image mImage;              // image object


    /*
        Constructor
     */
    public BaseCharacter() {
        this.mPos = new Point(0, 0);
        this.mSize = new Point(0, 0);
        this.mMoveX = 0.0f;
        this.mMoveY = 0.0f;
        this.mSpeed = 0.0f;
        this.mPreviewSpeed = 0.0f;
        this.mTime = 0;
        this.mExistFlag = false;
        this.mOriginPos = new Point(0, 0);
        this.mAlpha = 255;
        this.mScale = 1.0f;
        this.mType = 0;
        this.mRect = new Rect(0, 0, 0, 0);
        this.mPreviewPos = new Point(0, 0);
        this.mPriority = PRIORITY_BACKWARD;
        this.mAngle = 0;
    }

    public BaseCharacter(Image image) {
        this.mPos = new Point(0, 0);
        this.mSize = new Point(0, 0);
        this.mMoveX = 0.0f;
        this.mMoveY = 0.0f;
        this.mSpeed = 0.0f;
        this.mPreviewSpeed = 0.0f;
        this.mTime = 0;
        this.mExistFlag = false;
        this.mOriginPos = new Point(0, 0);
        this.mAlpha = 255;
        this.mScale = 1.0f;
        this.mImage = image;
        this.mType = 0;
        this.mRect = new Rect(0, 0, 0, 0);
        this.mPreviewPos = new Point(0, 0);
        this.mPriority = PRIORITY_BACKWARD;
        this.mAngle = 0;
    }

    public BaseCharacter(BaseCharacter src) {
        this.mPos = src.mPos;
        this.mSize = src.mSize;
        this.mMoveX = src.mMoveX;
        this.mMoveY = src.mMoveY;
        this.mSpeed = src.mSpeed;
        this.mPreviewSpeed = src.mPreviewSpeed;
        this.mTime = src.mTime;
        this.mExistFlag = src.mExistFlag;
        this.mOriginPos = src.mOriginPos;
        this.mAlpha = src.mAlpha;
        this.mScale = src.mScale;
        this.mType = src.mType;
        this.mRect = src.mRect;
        this.mPreviewPos = src.mPreviewPos;
        this.mPriority = src.mPriority;
        this.mAngle = src.mAngle;
    }

    /*
        Copy Constructor
     */
    public BaseCharacter CopyCharacter() {
        return new BaseCharacter(this);
    }

    /*
        Load image file.
    */
    public void LoadCharaImage(Context context, String fileName) {
        this.mBmp = this.mImage.LoadImage(context, fileName);
    }

    /*
        Release process
     */
    public void ReleaseCharaBmp() {
        if (this.mBmp != null) this.mBmp = null;
    }

    /*
        Avoid object a while.
     */
    public boolean AvoidObject(BaseCharacter ch, int time) {
        if (ch.mExistFlag) return false;
        // first, get current speed and reset current speed.
        if (this.mTime <= 0) ch.mPreviewSpeed = ch.mSpeed;
        // count time to avoid
        ch.mTime++;
        ch.mSpeed = 0.0f;
        if (ch.mTime > time) {
            ch.mExistFlag = true;
            ch.mTime = 0;
            ch.mSpeed = ch.mPreviewSpeed;
            return false;
        }
        return true;
    }

    /*
        Character's move
    */
    public void CharacterMoveToTouchedPosition(float speed) {
        // get touched position
        Point touchPos = new Point(GameView.GetTouchedPosition().x, GameView.GetTouchedPosition().y);
        // get camera's position
        Point cameraPos = StageCamera.GetCameraPosition();
        // get action
        int action = GameView.GetTouchAction();
        // if touch action is Action-Up, to stop character's move.
        if (action == MotionEvent.ACTION_UP) speed = 0;

        // when camera doesn't exist, to initialize position to 0.
        if (cameraPos == null) cameraPos = new Point(0, 0);

        // calculate player's center position.
        Point pCenter = new Point(
                (this.mPos.x - cameraPos.x) + ((this.mSize.x * (int) (this.mScale)) / 2),
                (this.mPos.y - cameraPos.y) + ((this.mSize.y * (int) (this.mScale)) / 2)
        );
        // error check
        if (pCenter.x == touchPos.x || pCenter.y == touchPos.y) return;

        // calculate triangle area.
        // bottom
        float bottom = touchPos.x - pCenter.x;
        // height
        float height = touchPos.y - pCenter.y;
        // oblique side.
        double oblique = Math.sqrt((double) (bottom * bottom) +
                (double) (height * height));
        // calculate move
        this.mMoveX = (bottom / (float) (oblique)) * Math.abs(speed);
        this.mMoveY = (height / (float) (oblique)) * Math.abs(speed);

        // add move to player's position
        this.mPos.x += this.mMoveX;
        this.mPos.y += this.mMoveY;
    }

    /*
        Character's move
    */
    public void CharacterMoveToTouchedPositionEx() {
        // get touched position
        Point touchPos = new Point(GameView.GetTouchedPosition().x, GameView.GetTouchedPosition().y);
        // get camera's position
        Point cameraPos = StageCamera.GetCameraPosition();

        // when camera doesn't exist, to initialize position to 0.
        if (cameraPos == null) cameraPos = new Point(0, 0);

        // calculate player's center position.
        Point pCenter = new Point(
                (this.mPos.x - cameraPos.x) + ((this.mSize.x * (int) (this.mScale)) / 2),
                (this.mPos.y - cameraPos.y) + ((this.mSize.y * (int) (this.mScale)) / 2)
        );
        // error check
        if (pCenter.x == touchPos.x || pCenter.y == touchPos.y) return;

        // calculate triangle area.
        // bottom
        float bottom = touchPos.x - pCenter.x;
        // height
        float height = touchPos.y - pCenter.y;
        // oblique side.
        double oblique = Math.sqrt((double) (bottom * bottom) +
                (double) (height * height));
        // calculate move
        this.mMoveX = (bottom / (float) (oblique));
        this.mMoveY = (height / (float) (oblique));
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
        Point screen = GameView.GetScreenSize();

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
        Constrain available to move area
    */
    public void ConstrainMove(Rect margin) {
        // get camera position
        Point camera = StageCamera.GetCameraPosition();

        if (margin == null) margin = new Rect(0,0,0,0);
        // when camera don't exist, to initialize position to 0.
        if (camera == null) camera = new Point(0, 0);

        // get screen size
        Point screen = GameView.GetScreenSize();

        // player's total size
        float w = this.mSize.x * this.mScale;
        float h = this.mSize.y * this.mScale;
        // left side
        if (this.mPos.x <= camera.x+margin.left) this.mPos.x = camera.x+margin.left;
        // top tip
        if (this.mPos.y <= camera.y+margin.top) this.mPos.y = camera.y+margin.top;
        // right side
        if (camera.x + screen.x + margin.right <= this.mPos.x + w)
            this.mPos.x = camera.x + screen.x + margin.right - (int) w;
        // bottom tip
        if (camera.y + screen.y + margin.bottom <= this.mPos.y + h)
            this.mPos.y = camera.y + screen.y + margin.bottom - (int) h;
    }


    /*
        Variable size based on camera angele
    */
    public double VariableScaleRateBasedOnCameraAngle(float defaultScale, float maxScale) {
        // get camera position
        Point camera = StageCamera.GetCameraPosition();
        // when camera don't exist, to initialize position to 0.
        if (camera == null) camera = new Point(0, 0);

        // get screen size
        Point screen = GameView.GetScreenSize();

        // local position
        double localPosY = this.mPos.y - camera.y;
        // calculate position rate to screen height.
        double localRateY = localPosY / screen.y;

        // add scale rate
        float size = this.mSize.y * this.mScale;
        if (camera.y <= this.mPos.y + size) {
            return defaultScale + (maxScale * localRateY);
        } else if (defaultScale + maxScale <= this.mScale) {
            return defaultScale + maxScale;
        }
        return defaultScale;
    }

    /*
        Rotate action
    */
    public static Point RotateCharacter(int startX,int startY, Point size, float scale, int length, int angle) {
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

}