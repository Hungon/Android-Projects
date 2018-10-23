package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Created by USER on 2/20/2016.
 */
public class CharacterEffect extends Animation {

    // static variables
    // Effect 1 Perspiration
    private final static Point PERSPIRATION_SIZE = new Point(32,32);
    // animation setting
    private final static int          PERSPIRATION_COUNT_MAX = 3;
    private final static int          PERSPIRATION_FRAME = 5;
    // Effect 2 Circle that scale down little by little
    private final static Point  CIRCLE_SIZE = new Point(144,144);
    // animation setting
    private final static int    CIRCLE_COUNT_MAX    = 0;
    private final static int    CIRCLE_FRAME        = 0;
    // kind of type
    public final static int    CIRCLE_TYPE_BLUE    = 0;
    public final static int    CIRCLE_TYPE_RED     = 1;
    public final static int    CIRCLE_TYPE_GREEN   = 2;
    // Effect 3 ball
    public final static Point  BALL_SIZE = new Point(32,32);
    // animation setting
    private final static int    BALL_COUNT_MAX    = 4;
    private final static int    BALL_FRAME        = 5;
    // kind of type
    public final static int    BALL_TYPE_BLUE    = 0;
    public final static int    BALL_TYPE_RED     = 1;
    // Effect 4 attention
    private final static Point  ATTENTION_SIZE = new Point(16,32);
    // animation setting
    private final static int    ATTENTION_COUNT_MAX    = 3;
    private final static int    ATTENTION_FRAME        = 5;
    // Effect 5 bump
    private final static Point  BUMP_SIZE = new Point(32,32);
    // animation setting
    private final static int    BUMP_COUNT_MAX    = 4;
    private final static int    BUMP_FRAME        = 3;
    // Effect 6 target
    private final static Point  TARGET_SIZE = new Point(42,42);
    public final static float   TARGET_SCALE = 1.5f;
    // animation setting
    private final static int    TARGET_COUNT_MAX    = 0;
    private final static int    TARGET_FRAME        = 0;
    // Effect 7 finger action01 that tap-action
    private final static Point  FINGER_TAP_SIZE = new Point(32,48);
    // animation setting
    private final static int    FINGER_TAP_COUNT_MAX    = 3;
    private final static int    FINGER_TAP_FRAME        = 7;
    // Effect 8 that is bubbling
    private final static Point  BUBBLE_SIZE = new Point(32,32);
    // animation setting
    private final static int    BUBBLE_COUNT_MAX    = 5;
    private final static int    BUBBLE_FRAME        = 7;

    // for set animation
    private final static int[][]      SET_ANIMATION = {
        {PERSPIRATION_SIZE.x,PERSPIRATION_SIZE.y,
        PERSPIRATION_COUNT_MAX,PERSPIRATION_FRAME},     // perspiration
        {CIRCLE_SIZE.x,CIRCLE_SIZE.y,
        CIRCLE_COUNT_MAX,CIRCLE_FRAME},                 // circle
        {BALL_SIZE.x,BALL_SIZE.y,
        BALL_COUNT_MAX,BALL_FRAME},                     // ball
        {ATTENTION_SIZE.x,ATTENTION_SIZE.y,
        ATTENTION_COUNT_MAX,ATTENTION_FRAME},           // attention
        {BUMP_SIZE.x,BUMP_SIZE.y,
        BUMP_COUNT_MAX,BUMP_FRAME},                     // bump
        {TARGET_SIZE.x,TARGET_SIZE.y,
        TARGET_COUNT_MAX,TARGET_FRAME},                 // target
        {FINGER_TAP_SIZE.x,FINGER_TAP_SIZE.y,
        FINGER_TAP_COUNT_MAX,FINGER_TAP_FRAME},         // finger
        {BUBBLE_SIZE.x,BUBBLE_SIZE.y,
        BUBBLE_COUNT_MAX,BUBBLE_FRAME},                 // bubble
    };

    // kind
    public final static int        EFFECT_NOTHING       = -1;
    public final static int        EFFECT_PERSPIRATION  = 0;
    public final static int        EFFECT_CIRCLE        = 1;
    public final static int        EFFECT_BALL          = 2;
    public final static int        EFFECT_ATTENTION     = 3;
    public final static int        EFFECT_BUMP          = 4;
    public final static int        EFFECT_TARGET        = 5;
    public final static int        EFFECT_FINGER_TAP    = 6;
    public final static int        EFFECT_BUBBLE        = 7;

    // filed
    private Context             mContext;
    private Image               mImage;
    private Bitmap              mBmp;
    private float               mScale;
    private int                 mAlpha;
    private Point               mPos;
    private int                 mCurrentType;
    private int                 mAngle;
    private int                 mFlutterCount;
    private int                 mFlutterInterval;

    /*
        Constructor
    */
    public CharacterEffect(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mPos = new Point();
        this.mScale = 1.0f;
        this.mAlpha = 255;
        this.mAngle = 0;
        this.mFlutterCount = 0;
        this.mFlutterInterval = 2;
    }

    /*
        Initialize
    */
    public void InitCharacterEffect(int type) {
        // using image files
        String imageFiles[] = {
                "perspiration",
                "roadeffectcircles",
                "roadeffectballs",
                "attention",
                "bump",
                "target",
                "fingeraction01",
                "bubble"
        };
        // load image
        this.mBmp = this.mImage.LoadImage(this.mContext, imageFiles[type]);

        // reset
        this.mScale = 1.0f;
        this.mAlpha = 255;
        this.mAngle = 0;
        this.mFlutterCount = 0;
        this.mFlutterInterval = 2;

        // setting
        this.SetAnimation(
                this.mOriginPos.x,
                this.mOriginPos.y,
                SET_ANIMATION[type][0],
                SET_ANIMATION[type][1],
                SET_ANIMATION[type][2],
                SET_ANIMATION[type][3],
                EFFECT_NOTHING);
        // set current type
        this.mCurrentType = type;
    }
    /*
        Update
    */
    public boolean UpdateCharacterEffect(float x, float y, int w, int h, float scale, boolean reverseF) {
        // Update position
        this.mPos.x = (int)x;
        this.mPos.y = (int)y;
        boolean ret = false;
        // Diverge effect direction from type.
        if (this.mType != EFFECT_NOTHING) {
            switch(this.mCurrentType) {
                case EFFECT_PERSPIRATION:
                    // Update animation
                    ret = this.UpdateAnimation(this.mOriginPos, reverseF);
                    break;
                case EFFECT_CIRCLE:         // direction that scale down little by little
                    float resizeW = w*1.5f;
                    this.mPos.x = (int)(x-resizeW);
                    this.mPos.y = (int)y-(this.mSize.y>>2);
                    this.mScale -= 0.1f;
                    // When current scale is less than 0, not to show the effect.
                    if (this.mScale <= 0.0f) this.NotToShowTheEffect();
                    // Update effect
                    ret = this.UpdateAnimation(this.mOriginPos,reverseF);
                    break;
                case EFFECT_BALL:
                    // position
                    this.mPos.x = (int)(x-(this.mSize.x>>1))+(this.mSize.x*this.mDirection);
                    this.mPos.y = (int)y;
                    // count angle
                    this.mAngle++;
                    // if blue ball, to increase the rotate speed.
                    if (this.mDirection == BALL_TYPE_BLUE) this.mAngle++;
                    // do rotate
                    this.mPos = BaseCharacter.RotateCharacter(
                            this.mPos.x,this.mPos.y,
                            this.mSize,this.mScale,
                            this.mSize.x>>2,this.mAngle);
                    // limit angle
                    this.mAngle %= 360;
                    // Update animation
                    ret = this.UpdateAnimation(this.mOriginPos, reverseF);
                    break;
                case EFFECT_ATTENTION:
                    float adjustH = this.mSize.y*scale;
                    this.mPos.x = (int)x+(this.mSize.x>>1);
                    this.mPos.y = (int)(y-adjustH);
                    // Update animation
                    ret = this.UpdateAnimation(this.mOriginPos, reverseF);
                    break;
                case EFFECT_BUMP:
                    ret = true;
                    // Update animation
                    if (!this.UpdateAnimation(this.mOriginPos, reverseF)) {
                        this.NotToShowTheEffect();
                        ret = false;
                    }
                    break;
                case EFFECT_TARGET:         // direction that scale down little by little
                    this.mPos.x = (int)x-(this.mSize.x>>2);
                    this.mPos.y = (int)y+(this.mSize.y>>2);
                    if (this.mScale <= TARGET_SCALE && 0.1f < this.mScale) {
                        this.mScale -= 0.1f;
                    }
                    if (this.mScale <= 0.1f){
                        this.mScale = TARGET_SCALE+0.1f;
                    }
                    break;
                case EFFECT_FINGER_TAP:
                    ret = this.UpdateAnimation(this.mOriginPos, reverseF);
                    break;
                case EFFECT_BUBBLE:
                    ret = true;
                    FPoint pos = new FPoint();
                    pos.x = (int)(w*scale)>>1;
                    pos.y = (int)(h*scale)>>1;
                    // update animation
                    // Update animation
                    if (!this.UpdateAnimation(this.mOriginPos, reverseF)) {
                        this.NotToShowTheEffect();
                        ret = false;
                    }
                default:
                    break;
            }
        }
        return ret;
    }

    /*
        Create effect
    */
    public void CreateEffect(float x, float y, int kind) {
        // Reset animation setting
        this.ResetAnimation();
        // set
        this.mPos.x = (int)x;
        this.mPos.y = (int)y;
        this.mDirection = kind;
        this.mScale = 1.0f;
        this.mAlpha = 255;
        // set type
        this.mType = this.mCurrentType;
    }

    /*
        Create effect
    */
    public void CreateEffect(float x, float y, float scale, int alpha, int kind) {
        // Reset animation setting
        this.ResetAnimation();
        // set
        this.mPos.x = (int)x;
        this.mPos.y = (int)y;
        this.mDirection = kind;
        this.mScale = scale;
        this.mAlpha = alpha;
        // set type
        this.mType = this.mCurrentType;
    }


    /*
        Draw
    */
    public void DrawCharacterEffect() {

        // camera
        Point camera = StageCamera.GetCameraPosition();
        if (camera == null) camera = new Point(0,0);
        if (this.mType != EFFECT_NOTHING && this.mFlutterCount % this.mFlutterInterval == 0) {
            this.mImage.DrawAlphaAndScale(
                    this.mPos.x-camera.x,
                    this.mPos.y-camera.y,
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
    public void ReleaseCharacterEffect() {
        this.mContext = null;
        this.mImage = null;
        this.mBmp = null;
    }
    /*
        To show the effect
    */
    public void ToShowTheEffect() { this.mType = this.mCurrentType; }
    /*
        Not to show the effect
     */
    public void NotToShowTheEffect() { this.mType = EFFECT_NOTHING; }
    /*
        start to flutter effect
    */
    public void StartToFlutterEffect(int time) {
        if (this.mFlutterCount < time) {
            this.mFlutterCount++;
        } else if (time <= this.mFlutterCount){
            this.mFlutterCount = time*2;
        }
        this.mFlutterCount %= 10000;
    }
    /*
        finish to flutter effect
    */
    public void ResetFlutterCount() { this.mFlutterCount = 0; }
    /*
        Set interval that flutter the effect
     */
    public void SetIntervalToFlutter(int interval) { this.mFlutterInterval = interval; }
}