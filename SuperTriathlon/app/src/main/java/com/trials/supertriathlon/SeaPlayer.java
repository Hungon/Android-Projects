package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Vibrator;
import android.view.MotionEvent;

/**
 * Created by USER on 3/10/2016.
 */
public class SeaPlayer extends BaseCharacter {
    // static variables
    // image setting
    private final static Point      SWIMMER_POSITION = new Point(100,200);
    public final static Point       SWIMMER_SIZE = new Point(104,40);
    private final static float      SWIMMER_DEFAULT_SPEED = 3.0f;
    // the difference setting that is doing the attack action
    private final static int        SWIMMER_ATTACK_HEIGHT = 96;
    // animation setting
    // Normal
    public final static int        ANIMATION_NORMAL_COUNT_MAX = 3;
    private final static int        ANIMATION_NORMAL_FRAME = 20;
    // wave is same the setting with normal
    // circle
    private final static int        ANIMATION_CIRCLE_COUNT_MAX  = 69;
    private final static int        ANIMATION_CIRCLE_FRAME      = 5;
    // bump
    private final static int        ANIMATION_BUMP_COUNT_MAX    = 5;
    private final static int        ANIMATION_BUMP_FRAME        = 5;
    private final static int        CHARGE_MAX                  = 5;
    // type of animation
    private final static int        ANIMATION_SWIM      = 0;
    private final static int        ANIMATION_WAVE      = 1;
    private final static int        ANIMATION_CIRCLE    = 2;
    private final static int        ANIMATION_BUMP      = 3;
    private final static int        ANIMATION_MAX       = 4;
    // type of attack
    public final static byte       ACTION_SWIM         = 0x00;
    public final static byte       ACTION_WAVE         = 0x01;
    public final static byte       ACTION_CIRCLE       = 0x02;
    public final static byte       ACTION_BUMP         = 0x04;
    public final static int        ACTION_MAX          = 5;
    // the difference safety area
    private final static Rect       SAFETY_AREA_DEFAULT         = new Rect(7,10,7,10);
    private final static Rect       SAFETY_AREA_WHEN_ATTACK     = new Rect(20,15,20,15);
    // item box
    public final static Point      ITEM_BOX_SIZE = new Point(64,64);
    public final static Point      ITEM_BOX_POSITION = new Point(10,185);
    // the limit time to show the item's effect
    // absolute, speed up, slow
    private final static int[]      ITEM_EFFECT_LIMIT_TIME = {1000,1000,500};
    // attack button
    public final static Point      ATTACK_BUTTON_SIZE = new Point(64,64);
    public final static Point      ATTACK_BUTTON_POSITION = new Point(10,275);
    // the image that is available to attack
    public final static Point      ATTACK_IMAGE_SIZE = new Point(64,54);
    public final static Point      ATTACK_IMAGE_POSITION = new Point(10,355);
    // animation setting
    public final static int        ANIMATION_ATTACK_IMAGE_COUNT_MAX  = 4;
    private final static int        ANIMATION_ATTACK_IMAGE_FRAME      = 10;
    // the attack charge point.
    // when player defeats the enemy, to increase the point
    private final static int        ATTACK_CHARGE_WHEN_DEFEAT = 8;
    // when is attacked from the enemy.
    private final static int        ATTACK_CHARGE_WHEN_IS_ATTACKED = 4;

    // filed
    private Context             mContext;
    private Image               mImage;
    private Animation           mAni[];
    private BaseCharacter       mAttack[];
    private byte                mActionType;
    private CharacterEffect     mEffect[];
    private int                 mItemEffect;
    private int                 mItemStock;
    private int                 mItemEffectTime;
    private BaseCharacter       mItemBox[];
    private int                 mBumpCount;
    private float               mBonusSpeed;
    private Sound               mSe;
    private static float            mAggregateSpeed;
    private static Point            mPosition;
    private static Rect             mSafetyArea;
    private static FPoint           mWholeSize;
    private static int              mChain;

    /*
        Constructor
    */
    public SeaPlayer(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mAni = new Animation[2];
        for (int i = 0; i < this.mAni.length; i++) {
            this.mAni[i] = new Animation();
        }
        // allot memory that is available to attack
        this.mAttack = new BaseCharacter[2];
        for (int i = 0; i < this.mAttack.length; i++) {
            this.mAttack[i] = new BaseCharacter(image);
        }
        // the effect
        this.mEffect = new CharacterEffect[1];
        for (int i = 0; i < this.mEffect.length; i++) {
            this.mEffect[i] = new CharacterEffect(context,image);
        }
        // item box
        this.mItemBox = new BaseCharacter[2];
        for (int i = 0; i < this.mItemBox.length; i++) {
            this.mItemBox[i] = new BaseCharacter(image);
        }
        // SE
        this.mSe = new Sound(context);
        // getter
        mPosition = new Point(0,0);
        mAggregateSpeed = SWIMMER_DEFAULT_SPEED;
        mSafetyArea = new Rect();
        mWholeSize = new FPoint(0.0f,0.0f);
        mChain = 0;
    }

    /*
        Initialize
    */
    public void InitSwimmer() {
        // load the files
        // own
        this.mBmp = this.mImage.LoadImage(this.mContext,"swimmer");
        // attack
        String attackImages[] = {"attackbutton","attackimage"};
        for (int i = 0; i < this.mAttack.length; i++) {
            this.mAttack[i].LoadCharaImage(this.mContext, attackImages[i]);
        }
        // item box
        String itemImages[] = {"itembox","seaitems"};
        // SE
        this.mSe.CreateSound("attack");
        for (int i = 0; i < this.mItemBox.length; i++) this.mItemBox[i].LoadCharaImage(this.mContext, itemImages[i]);
        // the flag that is doing the action
        this.mActionType = ACTION_SWIM;
        // the item effect
        this.mItemStock = this.mItemEffect = SeaItem.ITEM_EMPTY;
        // to count the effect time
        this.mItemEffectTime = 0;
        // the bonus speed that change by item effect.
        this.mBonusSpeed = 0.0f;

        // image setting
        // own
        this.mSize.x = SWIMMER_SIZE.x;
        this.mSize.y = SWIMMER_SIZE.y;
        this.mPos.x = SWIMMER_POSITION.x;
        this.mPos.y = SWIMMER_POSITION.y;
        this.mSpeed = SWIMMER_DEFAULT_SPEED;
        this.mExistFlag = true;
        // set safety area
        this.SetSafetyArea(SAFETY_AREA_DEFAULT);
        // attack button
        this.mAttack[0].mPos.x = ATTACK_BUTTON_POSITION.x;
        this.mAttack[0].mPos.y = ATTACK_BUTTON_POSITION.y;
        this.mAttack[0].mSize.x = ATTACK_BUTTON_SIZE.x;
        this.mAttack[0].mSize.y = ATTACK_BUTTON_SIZE.y;
        this.mAttack[0].mExistFlag = true;
        // the image that is available to attack
        // the position is in local position
        this.mAttack[1].mPos.x = ATTACK_IMAGE_POSITION.x;
        this.mAttack[1].mPos.y = ATTACK_IMAGE_POSITION.y;
        this.mAttack[1].mSize.x = 0;
        this.mAttack[1].mSize.y = ATTACK_IMAGE_SIZE.y;
        this.mAttack[1].mScale = 1.2f;
        this.mAttack[1].mExistFlag = true;

        // item box
        this.mItemBox[0].mPos.x = ITEM_BOX_POSITION.x;
        this.mItemBox[0].mPos.y = ITEM_BOX_POSITION.y;
        this.mItemBox[0].mSize.x = ITEM_BOX_SIZE.x;
        this.mItemBox[0].mSize.y = ITEM_BOX_SIZE.y;
        this.mItemBox[0].mExistFlag = true;
        this.mItemBox[0].mAlpha = 100;
        // the item
        this.mItemBox[1].mPos.x = ITEM_BOX_POSITION.x+((ITEM_BOX_SIZE.x-SeaItem.ITEM_SIZE.x)>>1);
        this.mItemBox[1].mPos.y = ITEM_BOX_POSITION.y+((ITEM_BOX_SIZE.y-SeaItem.ITEM_SIZE.y)>>1);
        this.mItemBox[1].mSize.x = SeaItem.ITEM_SIZE.x;
        this.mItemBox[1].mSize.y = SeaItem.ITEM_SIZE.y;

        // set the effect
        this.mEffect[0].InitCharacterEffect(CharacterEffect.EFFECT_BUMP);

        // set position to get
        mPosition.x = this.mPos.x;
        mPosition.y = this.mPos.y;
        // Set whole size to get that values.
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);
        // reset chain
        this.mBumpCount = mChain = 0;

        // animation setting
        // own
        this.mAni[0].SetAnimation(
                this.mOriginPos.x, this.mOriginPos.y,
                this.mSize.x, this.mSize.y,
                ANIMATION_NORMAL_COUNT_MAX,
                ANIMATION_NORMAL_FRAME,
                ANIMATION_SWIM);
        // attack image
        this.mAni[1].SetAnimation(
                this.mAttack[1].mOriginPos.x, this.mAttack[1].mOriginPos.y,
                ATTACK_IMAGE_SIZE.x,ATTACK_IMAGE_SIZE.y,
                ANIMATION_ATTACK_IMAGE_COUNT_MAX,
                ANIMATION_ATTACK_IMAGE_FRAME,0
        );
    }
    /*
        Initialize the brief stage
    */
    public void InitForTheBrief() {
        // load the files
        // own
        this.mBmp = this.mImage.LoadImage(this.mContext,"swimmer");
        // attack
        String attackImages[] = {"attackbutton","attackimage"};
        for (int i = 0; i < this.mAttack.length; i++) {
            this.mAttack[i].LoadCharaImage(this.mContext, attackImages[i]);
        }
        // item box
        String itemImages[] = {"itembox","seaitems"};
        for (int i = 0; i < this.mItemBox.length; i++) this.mItemBox[i].LoadCharaImage(this.mContext, itemImages[i]);
        // the flag that is doing the action
        this.mActionType = ACTION_SWIM;
        // the item effect
        this.mItemStock = this.mItemEffect = SeaItem.ITEM_EMPTY;
        // to count the effect time
        this.mItemEffectTime = 0;
        // the bonus speed that change by item effect.
        this.mBonusSpeed = 0.0f;

        // image setting
        // own
        this.mSize.x = SWIMMER_SIZE.x;
        this.mSize.y = SWIMMER_SIZE.y;
        this.mPos.x = SWIMMER_POSITION.x;
        this.mPos.y = SWIMMER_POSITION.y;
        this.mSpeed = SWIMMER_DEFAULT_SPEED;
        this.mExistFlag = true;
        // set safety area
        this.SetSafetyArea(SAFETY_AREA_DEFAULT);
        // attack button
        this.mAttack[0].mPos.x = ATTACK_BUTTON_POSITION.x;
        this.mAttack[0].mPos.y = ATTACK_BUTTON_POSITION.y;
        this.mAttack[0].mSize.x = ATTACK_BUTTON_SIZE.x;
        this.mAttack[0].mSize.y = ATTACK_BUTTON_SIZE.y;
        this.mAttack[0].mExistFlag = true;
        // the image that is available to attack
        // the position is in local position
        this.mAttack[1].mPos.x = ATTACK_IMAGE_POSITION.x;
        this.mAttack[1].mPos.y = ATTACK_IMAGE_POSITION.y;
        this.mAttack[1].mSize.x = 0;
        this.mAttack[1].mSize.y = ATTACK_IMAGE_SIZE.y;
        this.mAttack[1].mScale = 1.2f;
        this.mAttack[1].mExistFlag = true;

        // item box
        this.mItemBox[0].mPos.x = ITEM_BOX_POSITION.x;
        this.mItemBox[0].mPos.y = ITEM_BOX_POSITION.y;
        this.mItemBox[0].mSize.x = ITEM_BOX_SIZE.x;
        this.mItemBox[0].mSize.y = ITEM_BOX_SIZE.y;
        this.mItemBox[0].mExistFlag = true;
        this.mItemBox[0].mAlpha = 100;
        // the item
        this.mItemBox[1].mPos.x = ITEM_BOX_POSITION.x+((ITEM_BOX_SIZE.x-SeaItem.ITEM_SIZE.x)>>1);
        this.mItemBox[1].mPos.y = ITEM_BOX_POSITION.y+((ITEM_BOX_SIZE.y-SeaItem.ITEM_SIZE.y)>>1);
        this.mItemBox[1].mSize.x = SeaItem.ITEM_SIZE.x;
        this.mItemBox[1].mSize.y = SeaItem.ITEM_SIZE.y;

        // set position to get
        mPosition.x = this.mPos.x;
        mPosition.y = this.mPos.y;
        // Set whole size to get that values.
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);

        // animation setting
        // own
        this.mAni[0].SetAnimation(
                this.mOriginPos.x, this.mOriginPos.y,
                this.mSize.x, this.mSize.y,
                ANIMATION_NORMAL_COUNT_MAX,
                ANIMATION_NORMAL_FRAME,
                ANIMATION_SWIM);
        // attack image
        this.mAni[1].SetAnimation(
                this.mAttack[1].mOriginPos.x, this.mAttack[1].mOriginPos.y,
                ATTACK_IMAGE_SIZE.x,ATTACK_IMAGE_SIZE.y,
                ANIMATION_ATTACK_IMAGE_COUNT_MAX,
                ANIMATION_ATTACK_IMAGE_FRAME,0
        );
    }
    /*
        Update
    */
    public void UpdateSwimmer(SeaItem item, FPoint playerMove) {

        // get move
        this.mMoveX = playerMove.x*(SWIMMER_DEFAULT_SPEED+this.mBonusSpeed);
        this.mMoveY = playerMove.y*(SWIMMER_DEFAULT_SPEED+this.mBonusSpeed);

        // Attack process
        if (this.mActionType == ACTION_SWIM) {
            // type of attack
            int type = -1;
            // touch action
            int action = GameView.GetTouchAction();
            // attack process
            if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
                if (Collision.CheckTouch(
                        this.mAttack[0].mPos.x, this.mAttack[0].mPos.y,
                        this.mAttack[0].mSize.x, this.mAttack[0].mSize.y,
                        this.mAttack[0].mScale)) {
                    type = 0;           // normal attack
                    this.mMoveX = 0;
                    this.mMoveY = 0;
                    // when attack image reached to default size, to initialize the attack
                } else if (ATTACK_IMAGE_SIZE.x <= this.mAttack[1].mSize.x) {
                    if (Collision.CheckTouch(
                            this.mAttack[1].mPos.x, this.mAttack[1].mPos.y,
                            this.mAttack[1].mSize.x, this.mAttack[1].mSize.y,
                            this.mAttack[1].mScale)) {
                        this.mMoveX = 0;
                        this.mMoveY = 0;
                        // to diverge the action from own origin position
                        if (this.mAttack[1].mOriginPos.y == 0) {
                            type = 1;       // circle
                            this.mAttack[1].mSize.x = 0;
                        } else {
                            type = 2;       // bump
                            this.mAttack[1].mSize.x = 0;
                            this.mAttack[1].mOriginPos.x = this.mAttack[1].mOriginPos.y = 0;
                            this.mAni[1].mDirection = 0;
                        }
                    }
                }
            }
            // to initialize the action based on current type.
            if (type != -1) this.InitAction(type);
        }

        // to check the overlap between the player and the item.
        int itemType = item.CollisionItem(this);
        // to diverge the process from the type of item
        switch(itemType) {
            case SeaItem.ITEM_ABSOLUTE:
            case SeaItem.ITEM_SPEED_UP:
                // to set the image to the item-box
                this.mItemBox[1].mOriginPos.y = itemType*SeaItem.ITEM_SIZE.y;
                this.mItemBox[1].mExistFlag = true;
                this.mItemStock = itemType;            // to stock the item type
                break;
            case SeaItem.ITEM_SLOW:
                // to set the effect.
                this.mItemEffect = SeaItem.ITEM_SLOW;
                // to set showing time
                this.mItemEffectTime = ITEM_EFFECT_LIMIT_TIME[this.mItemEffect];
                // to decrease the speed
                this.mBonusSpeed = SeaItem.ITEM_EFFECT_SPEED_DOWN;
                break;
            case SeaItem.ITEM_SUPER:
                // to reset animation setting
                this.mAni[1].ResetAnimation();
                // to fill the attack mater to max
                this.mAttack[1].mSize.x = ATTACK_IMAGE_SIZE.x;
                this.mAni[1].mDirection = 1;
                // vibrate process
                Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1);
                break;
        }

        // to use the stock item
        this.UseItem();

        // to check to finish the item effect
        this.CheckFinishTheItemEffect();

        // add move to position
        this.mPos.x += this.mMoveX;
        this.mPos.y += this.mMoveY;

        // avoid process
        this.AvoidObject(this,200);

        // when current animation is normal, update animation speed
        if (this.mAni[0].mType == ANIMATION_SWIM) {
            this.mAni[0].mFrame = this.VariableAnimationFrame(mAggregateSpeed);
        }
        // update animation
        this.UpdateSwimmerAnimation();

        // to check the end action
        this.FinishTheAction();

        // update animation to attack charge
        if (this.mAni[1].mDirection == 1) {
            this.mAni[1].UpdateAnimation(this.mAttack[1].mOriginPos,false);
        }

        // update the effect
        this.mEffect[0].UpdateCharacterEffect(
                this.mPos.x,this.mPos.y,
                this.mSize.x,this.mSize.y,
                this.mScale,false);

        // update coordinate-Y based on aggregate seed.
        mAggregateSpeed = this.mSpeed+this.mBonusSpeed;
        // update position
        mPosition.x = this.mPos.x;
        mPosition.y = this.mPos.y;
        this.mPos.x += mAggregateSpeed;
        // set camera
        int localX = this.mPos.x - StageCamera.GetCameraPosition().x;
        StageCamera.SetCamera(this.mPos.x - localX, 0);
        // constrain available to move area
        this.ConstrainMove();
    }
    /*
        Update to explain the brief stage
    */
    public void UpdateForTheBrief(FPoint playerMove, int itemType) {

        // get move
        this.mMoveX = playerMove.x * SWIMMER_DEFAULT_SPEED;
        this.mMoveY = playerMove.y * SWIMMER_DEFAULT_SPEED;

        // when the character is existing, to process
        if (this.mExistFlag) {
            if (this.mActionType == ACTION_SWIM) {
                // type of attack
                int type = -1;
                // touch action
                int action = GameView.GetTouchAction();
                // attack process
                if (action == MotionEvent.ACTION_DOWN ||
                    action == MotionEvent.ACTION_POINTER_DOWN) {
                    if (Collision.CheckTouch(
                            this.mAttack[0].mPos.x, this.mAttack[0].mPos.y,
                            this.mAttack[0].mSize.x, this.mAttack[0].mSize.y,
                            this.mAttack[0].mScale)) {
                        type = 0;           // normal attack
                        this.mMoveX = 0;
                        this.mMoveY = 0;
                        // when attack image reached to default size, to initialize the attack
                    } else if (ATTACK_IMAGE_SIZE.x <= this.mAttack[1].mSize.x) {
                        if (Collision.CheckTouch(
                                this.mAttack[1].mPos.x, this.mAttack[1].mPos.y,
                                this.mAttack[1].mSize.x, this.mAttack[1].mSize.y,
                                this.mAttack[1].mScale)) {
                            this.mMoveX = 0;
                            this.mMoveY = 0;
                            // to diverge the action from own origin position
                            if (this.mAttack[1].mOriginPos.y == 0) {
                                type = 1;       // circle
                                this.mAttack[1].mSize.x = 0;
                            } else {
                                type = 2;       // bump
                                this.mAttack[1].mSize.x = 0;
                                this.mAttack[1].mOriginPos.x = this.mAttack[1].mOriginPos.y = 0;
                                this.mAni[1].mDirection = 0;
                            }
                        }
                    }
                }
                // to initialize the action based on current type.
                if (type != -1) this.InitAction(type);
            }
            // to diverge the process from the type of item
            switch (itemType) {
                case SeaItem.ITEM_ABSOLUTE:
                case SeaItem.ITEM_SPEED_UP:
                    // to set the image to the item-box
                    this.mItemBox[1].mOriginPos.y = itemType * SeaItem.ITEM_SIZE.y;
                    this.mItemBox[1].mExistFlag = true;
                    this.mItemStock = itemType;            // to stock the item type
                    break;
                case SeaItem.ITEM_SLOW:
                    // to set the effect.
                    this.mItemEffect = SeaItem.ITEM_SLOW;
                    // to set showing time
                    this.mItemEffectTime = ITEM_EFFECT_LIMIT_TIME[this.mItemEffect];
                    // to decrease the speed
                    this.mBonusSpeed = SeaItem.ITEM_EFFECT_SPEED_DOWN;
                    break;
                case SeaItem.ITEM_SUPER:
                    // to reset animation setting
                    this.mAni[1].ResetAnimation();
                    // to fill the attack mater to max
                    this.mAttack[1].mSize.x = ATTACK_IMAGE_SIZE.x;
                    this.mAni[1].mDirection = 1;
                    // vibrate process
                    Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1);
                    break;
            }
        }
        // to use the stock item
        this.UseItem();
        // to check to finish the item effect
        this.CheckFinishTheItemEffect();

        // add move to position
        this.mPos.x += this.mMoveX;
        this.mPos.y += this.mMoveY;

        // update animation
        this.UpdateSwimmerAnimation();

        // to check the end action
        this.FinishTheAction();

        // update animation to attack charge
        if (this.mAni[1].mDirection == 1) {
            this.mAni[1].UpdateAnimation(this.mAttack[1].mOriginPos,false);
        }
        // update position
        mPosition.x = this.mPos.x;
        mPosition.y = this.mPos.y;
        // constrain available to move area
        this.ConstrainMove();
    }

    /*
        Draw
    */
    public void DrawSwimmer() {
        // camera position
        Point camera = StageCamera.GetCameraPosition();
        if (camera == null) camera = new Point();
        if (this.mTime % 2 == 0) {
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
        // attack button to normal attack
        if (this.mAttack[0].mExistFlag) {
            this.mImage.DrawImage(
                    this.mAttack[0].mPos.x,
                    this.mAttack[0].mPos.y,
                    this.mAttack[0].mSize.x,
                    this.mAttack[0].mSize.y,
                    this.mAttack[0].mOriginPos.x,
                    this.mAttack[0].mOriginPos.y,
                    this.mAttack[0].mBmp

            );
        }
        // the image that is available to attack
        for (int i = 0; i < 2; i++) {
            if (this.mAttack[1].mExistFlag) {
                // the difference alpha to distinguish the image
                // that is available to attack or not.
                int alpha[] = {100, 255};
                int size[] = {ATTACK_IMAGE_SIZE.x,this.mAttack[1].mSize.x};
                this.mImage.DrawAlphaAndScale(
                        this.mAttack[1].mPos.x,
                        this.mAttack[1].mPos.y,
                        size[i],
                        this.mAttack[1].mSize.y,
                        this.mAttack[1].mOriginPos.x,
                        this.mAttack[1].mOriginPos.y,
                        alpha[i],
                        this.mAttack[1].mScale,
                        this.mAttack[1].mBmp
                );
            }
        }
        // the effect that is attacked from the enemy.
        this.mEffect[0].DrawCharacterEffect();

        // item
        for (BaseCharacter i: this.mItemBox) {
            if (i.mExistFlag) {
                this.mImage.DrawAlphaAndScale(
                        i.mPos.x,i.mPos.y,
                        i.mSize.x,i.mSize.y,
                        i.mOriginPos.x,i.mOriginPos.y,
                        i.mAlpha,i.mScale,
                        i.mBmp
                );
            }
        }
        // rest of time that showing the effect item
        if (this.mItemEffect != SeaItem.ITEM_EMPTY) {
            this.mImage.drawText(""+this.mItemEffectTime,this.mPos.x-camera.x,this.mPos.y+(int)mWholeSize.y+10,18, Color.WHITE);
        }
    }
    /*
        Draw to explain the stage
    */
    public void DrawForTheBrief() {
        if (this.mTime % 2 == 0) {
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
        // attack button to normal attack
        if (this.mAttack[0].mExistFlag) {
            this.mImage.DrawImage(
                    this.mAttack[0].mPos.x,
                    this.mAttack[0].mPos.y,
                    this.mAttack[0].mSize.x,
                    this.mAttack[0].mSize.y,
                    this.mAttack[0].mOriginPos.x,
                    this.mAttack[0].mOriginPos.y,
                    this.mAttack[0].mBmp

            );
        }
        // the image that is available to attack
        for (int i = 0; i < 2; i++) {
            if (this.mAttack[1].mExistFlag) {
                // the difference alpha to distinguish the image
                // that is available to attack or not.
                int alpha[] = {100, 255};
                int size[] = {ATTACK_IMAGE_SIZE.x,this.mAttack[1].mSize.x};
                this.mImage.DrawAlphaAndScale(
                        this.mAttack[1].mPos.x,
                        this.mAttack[1].mPos.y,
                        size[i],
                        this.mAttack[1].mSize.y,
                        this.mAttack[1].mOriginPos.x,
                        this.mAttack[1].mOriginPos.y,
                        alpha[i],
                        this.mAttack[1].mScale,
                        this.mAttack[1].mBmp
                );
            }
        }
        // item
        for (BaseCharacter i: this.mItemBox) {
            if (i.mExistFlag) {
                this.mImage.DrawAlphaAndScale(
                        i.mPos.x,i.mPos.y,
                        i.mSize.x,i.mSize.y,
                        i.mOriginPos.x,i.mOriginPos.y,
                        i.mAlpha,i.mScale,
                        i.mBmp
                );
            }
        }
        // rest of time that showing the effect item
        if (this.mItemEffect != SeaItem.ITEM_EMPTY) {
            this.mImage.drawText(""+this.mItemEffectTime,this.mPos.x,this.mPos.y+(int)mWholeSize.y+10,18, Color.WHITE);
        }
    }

    /*
        Release
    */
    public void ReleaseSwimmer() {
        // release bitmap class
        this.ReleaseCharaBmp();
        this.mContext = null;
        this.mImage = null;
        // SE
        if (this.mSe != null) this.mSe = null;
        // animation class
        for (int i = 0; i < this.mAni.length; i++) {
            this.mAni[i] = null;
        }
        // the effect
        if (this.mEffect != null) {
            this.mEffect[0].ReleaseCharacterEffect();
            this.mEffect[0] = null;
        }
        for (int i = 0; i < this.mAttack.length; i++) {
            this.mAttack[i].ReleaseCharaBmp();
            this.mAttack[i] = null;
        }
        // item
        for (int i = 0; i < this.mItemBox.length; i++) {
            this.mItemBox[i].ReleaseCharaBmp();
            this.mItemBox[i] = null;
        }
    }
    /******************************************************************
        Each definition that is action
    ******************************************************************/
    /*
        Initialize the action
    */
    private void InitAction(int type) {
        // the type of action
        byte action[] = {
                ACTION_WAVE,ACTION_CIRCLE,ACTION_BUMP
        };
        // in the time, when current action is swimming,
        // to substitute the action type to variable.
        if (this.mActionType == ACTION_SWIM) {
            this.mActionType |= action[type];
            // the setting to animate the image
            switch(type) {
                case 0:         // wave
                    this.InitAnimationWave();
                    break;
                case 1:         // circle
                    this.InitAnimationCircle();
                    break;
                case 2:         // bump
                    this.InitAnimationBump();
                    break;
                default:
            }
        }
    }
    /*
        To finish the action anyway.
    */
    private void FinishTheAction() {
        // when current action is swimming, back to the normal type.
        if (this.mAni[0].mType == ANIMATION_SWIM) {
            this.mActionType &= ~this.mActionType;
            // set safety area
            this.SetSafetyArea(SAFETY_AREA_DEFAULT);
        }
    }

    /*****************************************************************
        Each initialization the animation
    ***************************************************************/
    /*
        Initialize the action that is attack like the wave.
    */
    private void InitAnimationWave() {
        // to reset animation
        this.mAni[0].ResetAnimation();
        this.mAni[0].mSize.y = this.mSize.y = SWIMMER_SIZE.y;
        this.mAni[0].mType = ANIMATION_WAVE;
        this.mAni[0].mCountMax = ANIMATION_NORMAL_COUNT_MAX;
        this.mAni[0].mFrame = ANIMATION_NORMAL_FRAME;
        this.mAni[0].mStartPic.y = (SWIMMER_SIZE.y<<2);
    }
    /*
        Initialize the action that is moving the as the circle
    */
    private void InitAnimationCircle() {
        // set safety area
        this.SetSafetyArea(SAFETY_AREA_WHEN_ATTACK);
        // to reset animation
        this.mAni[0].ResetAnimation();
        this.mAni[0].mType = ANIMATION_CIRCLE;
        this.mAni[0].mCountMax = ANIMATION_CIRCLE_COUNT_MAX;
        this.mAni[0].mFrame = ANIMATION_CIRCLE_FRAME;
        // the difference setting that is own height
        this.mAni[0].mSize.y = this.mSize.y = SWIMMER_ATTACK_HEIGHT;
        this.mAni[0].mStartPic.y = (SWIMMER_SIZE.y<<3);
    }
    /*
        Initialize the action that bump toward.
    */
    private void InitAnimationBump() {
        // set safety area
        this.SetSafetyArea(SAFETY_AREA_WHEN_ATTACK);
        // to reset animation
        this.mAni[0].ResetAnimation();
        this.mAni[0].mType = ANIMATION_BUMP;
        this.mAni[0].mCountMax = ANIMATION_BUMP_COUNT_MAX;
        this.mAni[0].mFrame = ANIMATION_BUMP_FRAME;
        // the difference setting that is own height
        this.mAni[0].mSize.y = this.mSize.y = SWIMMER_ATTACK_HEIGHT;
        this.mAni[0].mStartPic.y = (SWIMMER_SIZE.y<<3)+(SWIMMER_ATTACK_HEIGHT<<2);
        // to increase speed
        this.mPreviewSpeed = this.mSpeed;
        this.mSpeed = ((int)SWIMMER_DEFAULT_SPEED<<2);
    }

    /*
        Update animation
    */
    private boolean UpdateSwimmerAnimation() {
        // animation type
        int aniType[] = {
                ANIMATION_SWIM,              // 0
                ANIMATION_WAVE,              // 1
                ANIMATION_CIRCLE,            // 2
                ANIMATION_BUMP,              // 3
        };
        // the item effects
        int itemEffect[] = {
                SeaItem.ITEM_ABSOLUTE,
                SeaItem.ITEM_SPEED_UP,
                SeaItem.ITEM_SLOW
        };
        // loop to type of item effect
        for (int effect: itemEffect) {
            if (this.mItemEffect == SeaItem.ITEM_EMPTY) {
                // to back the normal animation
                this.mAni[0].mDirection = 0;
                break;
            } else if (this.mItemEffect == effect) {
                this.mAni[0].mDirection = effect+1;
                break;
            }
        }
        // loop to kind of action
        for (int kind : aniType) {
            if (kind == this.mAni[0].mType) {
                // when except for bump,
                if (kind == ANIMATION_SWIM ||
                    kind == ANIMATION_WAVE ||
                    kind == ANIMATION_CIRCLE) {
                    // update animation
                    if (!this.mAni[0].UpdateAnimation(this.mOriginPos, false)) {
                        // reset current type
                        this.mAni[0].mType = ANIMATION_SWIM;
                        // animation setting
                        this.mAni[0].mCountMax = ANIMATION_NORMAL_COUNT_MAX;
                        this.mAni[0].mFrame = ANIMATION_NORMAL_FRAME;
                        // back to normal width
                        this.mAni[0].mSize.y = this.mSize.y = SWIMMER_SIZE.y;
                        this.mAni[0].mStartPic.y = 0;
                        return false;
                    }
                } else if (kind == ANIMATION_BUMP) {
                    // update animation
                    if (!this.mAni[0].UpdateAnimation(this.mOriginPos, true)) {
                        // count end to animation
                        this.mBumpCount++;
                        // when counted value reached to count max,
                        // to reset
                        if (CHARGE_MAX < this.mBumpCount) {
                            // reset count
                            this.mBumpCount = 0;
                            // back to the preview speed
                            this.mSpeed = this.mPreviewSpeed;
                            // reset current type
                            this.mAni[0].mType = ANIMATION_SWIM;
                            // animation setting
                            this.mAni[0].mCountMax = ANIMATION_NORMAL_COUNT_MAX;
                            this.mAni[0].mFrame = ANIMATION_NORMAL_FRAME;
                            // back to normal width
                            this.mAni[0].mSize.y = this.mSize.y = SWIMMER_SIZE.y;
                            this.mAni[0].mStartPic.y = 0;
                            return false;
                        }
                    }
                }
            }
        }
        // true is doing the animation
        return true;
    }

    /*
        To use the item
    */
    private void UseItem() {
        // to use the item as touch the item box
        if (this.mItemBox[1].mExistFlag) {
            // touch action
            int action = GameView.GetTouchAction();
            // attack process
            if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
                if (Collision.CheckTouch(
                        this.mItemBox[1].mPos.x,
                        this.mItemBox[1].mPos.y,
                        this.mItemBox[1].mSize.x,
                        this.mItemBox[1].mSize.y,
                        this.mItemBox[1].mScale)) {
                    // to show the effect
                    this.mItemEffect = this.mItemStock;
                    this.mItemStock = SeaItem.ITEM_EMPTY;
                    // not to show the item
                    this.mItemBox[1].mExistFlag = false;
                    // to set showing time
                    this.mItemEffectTime = ITEM_EFFECT_LIMIT_TIME[this.mItemEffect];
                    // reset the bonus speed
                    this.mBonusSpeed = 0.0f;
                    // diverge the effect from the item
                    if (this.mItemEffect == SeaItem.ITEM_ABSOLUTE) {
                        // when while avoiding the enemy, to reset
                        if (!this.mExistFlag) {
                            this.mExistFlag = true;
                            this.mTime = 0;
                            this.mSpeed = this.mPreviewSpeed;
                        }
                    } else if (this.mItemEffect == SeaItem.ITEM_SPEED_UP) {
                        // to increase the speed
                        this.mBonusSpeed = SeaItem.ITEM_EFFECT_SPEED_UP;
                    }
                }
            }
        }
    }
    /*
        To check the end time to show the item effect.
    */
    private void CheckFinishTheItemEffect() {
        if (this.mItemEffect == SeaItem.ITEM_EMPTY) return;
        // count time
        this.mItemEffectTime--;
        // when reached to limit the time, to end time
        if (this.mItemEffectTime < 0) {
            this.mItemEffectTime = 0;
            this.mItemEffect = SeaItem.ITEM_EMPTY;
            // reset the bonus speed
            this.mBonusSpeed = 0.0f;
        }
    }
    /*
        Variable animation speed.
        return value is animation frame
    */
    protected int VariableAnimationFrame(float speed) {
        int frame = ANIMATION_NORMAL_FRAME;     // 10
        for (int i = 0; i < 4; i++) {
            if ((i*0.5f)+SWIMMER_DEFAULT_SPEED < Math.abs(speed)) {
                frame -= (i+1);
            } else if (Math.abs(speed) < SWIMMER_DEFAULT_SPEED-(i*0.5f)) {
                frame += (i+1);
                break;
            }
        }
        return frame;
    }
    /**********************************************************************************************
     Setter functions
     *********************************************************************************************/
    /*
        Whole size
     */
    private void SetWholeSize(int w, int h, float scale) {
        mWholeSize.x = w*scale;
        mWholeSize.y = h*scale;
    }
    /*
        Safety area
    */
    private void SetSafetyArea(Rect safety) {
        // safety area
        float Safety[] = {
                safety.left*this.mScale,safety.top*this.mScale,
                safety.right*this.mScale,safety.bottom*this.mScale,
        };
        mSafetyArea.left = (int)Safety[0];
        mSafetyArea.top = (int)Safety[1];
        mSafetyArea.right = (int)Safety[2];
        mSafetyArea.bottom = (int)Safety[3];
    }
    /*
        Set the flag that is attacked from the enemy
    */
    public void IsAttacked(boolean attacked) {
        if (!attacked || ATTACK_IMAGE_SIZE.x <= this.mAttack[1].mSize.x) return;
        this.mExistFlag = false;
        this.mEffect[0].ToShowTheEffect();
        // to increase the point that available to attack.
        if (this.mAttack[1].mSize.x < ATTACK_IMAGE_SIZE.x) this.mAttack[1].mSize.x += ATTACK_CHARGE_WHEN_IS_ATTACKED;
        if (ATTACK_IMAGE_SIZE.x <= this.mAttack[1].mSize.x) {
            this.mAttack[1].mSize.x = ATTACK_IMAGE_SIZE.x;
            // vibrate process
            Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1);
        }
        // reset the chain
        mChain = 0;
    }
    /*
        Set the flag that defeat the enemy
    */
    public void ToDefeatTheEnemy(boolean defeat) {
        // play SE
        this.mSe.PlaySE();
        if (!defeat || ATTACK_IMAGE_SIZE.x <= this.mAttack[1].mSize.x) return;
        // to increase the point that available to attack.
        if (this.mAttack[1].mSize.x < ATTACK_IMAGE_SIZE.x) this.mAttack[1].mSize.x += ATTACK_CHARGE_WHEN_DEFEAT;
        if (ATTACK_IMAGE_SIZE.x <= this.mAttack[1].mSize.x) {
            this.mAttack[1].mSize.x = ATTACK_IMAGE_SIZE.x;
            // vibrate process
            Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1);
        }
        // count the chain
        mChain++;
    }
    /*
        To increase the attack gauge
    */
    public void ToIncreaseTheAttackGauge(boolean c) {
        if (!c || ATTACK_IMAGE_SIZE.x <= this.mAttack[1].mSize.x) return;
        if (this.mAttack[1].mSize.x < ATTACK_IMAGE_SIZE.x) this.mAttack[1].mSize.x += 5;
        if (ATTACK_IMAGE_SIZE.x <= this.mAttack[1].mSize.x) {
            this.mAttack[1].mSize.x = ATTACK_IMAGE_SIZE.x;
            // vibrate process
            Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1);
        }
    }
    /*
        When through the light
    */
    public void ThroughTheLight(boolean through) {
        if (!through) return;
        this.mAttack[1].mOriginPos.y = ATTACK_IMAGE_SIZE.y;
    }
    /**********************************************************************************************
     Getter functions
     **********************************************************************************************/
    /*
        get aggregate speed
    */
    public static float GetAggregateSpeed() { return mAggregateSpeed; }
    /*
        get position
    */
    public static Point GetPosition() { return mPosition; }
    /*
        Get safety area
     */
    public static Rect GetSafetyArea() { return mSafetyArea; }
    /*
        Get whole size
    */
    public static FPoint GetWholeSize() { return mWholeSize; }
    /*
        Get current action type
    */
    public byte GetActionType() { return this.mActionType; }
    /*
        Get the flag of presence
    */
    public boolean  GetExist() { return this.mExistFlag; }
    /*
        Get item effect
    */
    public int GetItemEffect() { return this.mItemEffect; }
    /*
        Get chain
    */
    public static int GetChain() { return mChain; }
}