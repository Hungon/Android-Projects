package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by USER on 3/17/2016.
 */
public class SeaEnemy extends CharacterEx {
    // static variables
    // type of action
    public final static int        ACTION_EMPTY        = -1;
    public final static int        ACTION_CIRCLE       = 0;
    public final static int        ACTION_STRAIGHT     = 1;
    public final static int        ACTION_WAVE         = 2;
    public final static int        ACTION_TOWARD       = 3;
    public final static int        ACTION_AIMING       = 4;
    public final static int        ACTION_TYPE_MAX     = 5;
    // type of animation
    public final static int        ANIMATION_TYPE_PRESENCE = 0;
    public final static int        ANIMATION_TYPE_DEFEATED = 1;
    // filed
    private int             mAngleCount;
    private int             mWaveCount;
    private Point           mTargetPosition;
    private FPoint          mTargetWholeSize;
    private int             mActionType;
    private CharacterEffect mEffect;     // the effect to the enemy
    private int[]           mLikelihoodForItem;

    /*
        Constructor
    */
    public SeaEnemy(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mAngleCount = 0;
        this.mWaveCount = 0;
        this.mTargetPosition = new Point();
        this.mTargetWholeSize = new FPoint();
        this.mActionType = ACTION_EMPTY;
        this.mEffect = new CharacterEffect(context,image);
        this.mLikelihoodForItem = new int[SeaItem.ITEM_KIND];
    }

    /*
        Initialize
    */
    @Override
    public void InitCharacter() {
        this.mAngleCount = 0;
        this.mWaveCount = 0;
        this.mTargetPosition.x = 0;
        this.mTargetPosition.y = 0;
        this.mTargetWholeSize.x = 0.0f;
        this.mTargetWholeSize.y = 0.0f;
        this.mActionType = ACTION_EMPTY;
        // to initialize the effect that is bubbling
        this.mEffect.InitCharacterEffect(CharacterEffect.EFFECT_BUBBLE);
    }
    /*
        Update
    */
    @Override
    public void UpdateCharacter() {
        // update move and animation and so on.
        if (this.mExistFlag) {
            this.mPos.x += this.mMoveX;
            this.mPos.y += this.mMoveY;
            // animation
            this.mAni.UpdateAnimation(this.mOriginPos,false);
            // to update action
            switch(this.mActionType) {
                case ACTION_CIRCLE:
                    this.UpdateActionDrawingCircle();
                    break;
                case ACTION_STRAIGHT:
                    this.UpdateActionMoveStraight();
                    break;
                case ACTION_WAVE:
                    this.UpdateActionMovingWave();
                    break;
                case ACTION_TOWARD:
                    this.UpdateActionTowardTheTarget();
                    break;
                case ACTION_AIMING:
                    this.UpdateActionAimingTheTarget(
                            SeaPlayer.GetPosition(),
                            SeaPlayer.GetWholeSize());
                    break;
                default:
            }
            // to check the overlap own position and camera's position
            if (!StageCamera.CollisionCamera(this)) {
                // not to show the enemy
                this.mExistFlag = false;
                // to reset the type
                this.mType = -1;
                this.mActionType = ACTION_EMPTY;
                this.mBmp = null;
            }
            // update animation
            this.UpdateAnimation();
        }
        // to update the effect that is bubbling
        FPoint size = new FPoint();
        size.x = this.mSize.x*this.mScale;
        size.y = this.mSize.y*this.mScale;
        this.mEffect.UpdateCharacterEffect(
                this.mPos.x+((int)size.x>>1), this.mPos.y+((int)size.y>>1),
                this.mSize.x, this.mSize.y,
                this.mScale, false);
    }
    /*
        Draw
    */
    @Override
    public void DrawCharacter() {
        // get camera position
        Point camera = StageCamera.GetCameraPosition();
        if (camera == null) camera = new Point();
        if (this.mExistFlag && this.mBmp != null) {
            this.mImage.DrawAlphaAndScale(
                    this.mPos.x - camera.x,
                    this.mPos.y - camera.y,
                    this.mSize.x,
                    this.mSize.y,
                    this.mOriginPos.x,
                    this.mOriginPos.y,
                    this.mAlpha,
                    this.mScale,
                    this.mBmp
            );
        }
        // the effect the is bubbling
        this.mEffect.DrawCharacterEffect();
    }
    /*
        Release
    */
    @Override
    public void ReleaseCharacter() {
        this.mImage = null;
        this.mContext = null;
        this.mTargetPosition = null;
        this.mTargetWholeSize = null;
        this.ReleaseCharaBmp();
        this.mEffect.ReleaseCharacterEffect();
        this.mEffect = null;
    }
    /*
        Create the enemy
    */
    public boolean CreateEnemy(String file, Point pos, Point size, float scale, int alpha, float speed, int enemyId, int actionType) {
        if (this.mExistFlag) return false;
        // reset the animation
        this.mAni.ResetEveryAnimationSetting();
        // to load the file
        this.mBmp = this.mImage.LoadImage(this.mContext,file);
        // to set each arguments
        this.mPos.x = pos.x;
        this.mPos.y = pos.y;
        this.mSize.x = size.x;
        this.mSize.y = size.y;
        this.mScale = scale;
        this.mAlpha = alpha;
        this.mOriginPos.x = 0;
        this.mOriginPos.y = 0;
        this.mSpeed = speed;
        this.mType = enemyId;
        if (ACTION_TYPE_MAX <= actionType) actionType = ACTION_EMPTY;
        this.mActionType = actionType;
        this.mExistFlag = true;
        // Except for the action that straight, wave and aiming,
        // to initialize the action
        switch(this.mActionType) {
            case ACTION_CIRCLE:
                this.SetAngleForAction(10);
                break;
            case ACTION_TOWARD:
                this.SetTargetInfoForTowardTheTarget(
                        SeaPlayer.GetPosition(),
                        SeaPlayer.GetWholeSize());
                break;
            default:
        }
        return true;
    }
    /*
        Initialize the animation setting
        except for shoal
    */
    public void InitAnimation(int startX, int startY, int w, int h, int countMax, int frame, int type) {
        // reset setting
        this.mAni.ResetAnimation();
        this.mAni.SetAnimation(
                startX, startY,
                w,h,countMax,frame,type);
    }
    /*
        Update animation
    */
    private void UpdateAnimation() {
        // when the enemy is defeated from player, to set
        if (!this.mAni.UpdateAnimation(this.mOriginPos, false)) {
            if (this.mAni.mType == ANIMATION_TYPE_DEFEATED) {
                // not to show the enemy
                this.mExistFlag = false;
            }
        }
    }

    /***********************************************************
        Each enemy's actions to setting
    *********************************************************/
    /*
        Initialize angle to action that move the staying with moving circle
    */
    private void SetAngleForAction(int angle) {
        // substitute angle to variable
        this.mAngle = angle;
    }
    /*
        Initialize target's position and 
        whole size to set moving to target's preview position.
    */
    private void SetTargetInfoForTowardTheTarget(Point pos, FPoint wholeSize) {
        this.mTargetPosition.x = pos.x;
        this.mTargetPosition.y = pos.y;
        this.mTargetWholeSize.x = wholeSize.x;
        this.mTargetWholeSize.y = wholeSize.y;
    }
    /***********************************************************
     Each enemy's actions to update
     *********************************************************/
    /*
        The action-1 that drawing circle
    */
    private void UpdateActionDrawingCircle() {
        // to increase angle count
        this.mAngleCount++;
        // update action that staying with do drawing circle
        this.mMoveX = (float)Math.cos(this.mAngleCount * 3.14 / this.mAngle)*this.mSpeed;
        this.mMoveY = (float)Math.sin(this.mAngleCount*3.14/this.mAngle)*this.mSpeed;
        // limit count
        this.mAngleCount %= 1000;
    }
    /*
        The action-2 that move toward straight
    */
    private void UpdateActionMoveStraight() {
        this.mMoveX = this.mSpeed*-1;
        this.mMoveY = 0.0f;
    }
    /*
        The action-3 that moving like the wave
    */
    private void UpdateActionMovingWave() {
        // to increase wave count
        this.mWaveCount += 2;
        // update move
        this.mMoveX = this.mSpeed*-1;
        this.mMoveY = (float)Math.sin(this.mWaveCount*3.14/180.0f)*this.mSpeed;
        // limit count
        this.mWaveCount %= 100000;
    }
    /*
        The action-4 that aiming the target based on target's preview position.
    */
    private void UpdateActionTowardTheTarget() {
        float ownSizeX = this.mSize.x*this.mScale;
        float ownSizeY = this.mSize.y*this.mScale;
        // when over through the target, go away.
        if (this.mPos.x+((int)(ownSizeX)>>1) <= this.mTargetPosition.x+this.mTargetWholeSize.x) {
            this.mMoveX = -5.0f;
            this.mMoveY = 5.0f;
            return;
        }
        // own center position
        double ownPosX = this.mPos.x+((int)ownSizeX>>1);
        double ownPosY = this.mPos.y+((int)ownSizeY>>1);
        // target's center position
        double targetPosX = this.mTargetPosition.x+((int)this.mTargetWholeSize.x>>1);
        double targetPosY = this.mTargetPosition.y+((int)this.mTargetWholeSize.y>>1);
        // calculate triangle area.
        // bottom
        double bottom = targetPosX - ownPosX;
        // height
        double height = targetPosY - ownPosY;
        // oblique side.
        double oblique = Math.sqrt((bottom * bottom)+(height * height));
        // calculate move
        this.mMoveX = (float)(bottom / oblique) * Math.abs(this.mSpeed);
        this.mMoveY = (float)(height / oblique) * Math.abs(this.mSpeed);

    }
    /*
        The action-5 that aiming the target.
    */
    private void UpdateActionAimingTheTarget(Point targetPos, FPoint targetSize) {
        float ownSizeX = this.mSize.x*this.mScale;
        float ownSizeY = this.mSize.y*this.mScale;
        // when over through the target, go away.
        if (this.mPos.x+((int)(ownSizeX)>>1) <= targetPos.x+targetSize.x) {
            this.mMoveY = this.mMoveX = -5.0f;
            return;
        }
        // own center position
        double ownPosX = this.mPos.x+((int)ownSizeX>>1);
        double ownPosY = this.mPos.y+((int)ownSizeY>>1);
        // target's center position
        double targetPosX = targetPos.x+((int)targetSize.x>>1);
        double targetPosY = targetPos.y+((int)targetSize.y>>1);
        // calculate triangle area.
        // bottom
        double bottom = targetPosX - ownPosX;
        // height
        double height = targetPosY - ownPosY;
        // oblique side.
        double oblique = Math.sqrt((bottom * bottom)+(height * height));
        // calculate move
        this.mMoveX = (float)(bottom / oblique) * Math.abs(this.mSpeed);
        this.mMoveY = (float)(height / oblique) * Math.abs(this.mSpeed);
    }
    /*
        Collision between the enemy and player.
    */
    public boolean CollisionEnemy(SeaPlayer ch, SeaItem item) {
        if (this.mExistFlag &&
            this.mAni.mType == ANIMATION_TYPE_PRESENCE &&
            ch.GetExist()) {
            // get player's safety area
            Rect pSafety = SeaPlayer.GetSafetyArea();
            // to check the overlap between the enemy and player.
            if (Collision.CollisionCharacter(ch, this,pSafety,this.mRect)) {
                // when player is swimming, to substitute the flag of presence to player's variable.
                byte playerAction = ch.GetActionType();
                // get player's item effect
                int playerEffect = ch.GetItemEffect();
                if (playerAction == SeaPlayer.ACTION_SWIM &&
                    playerEffect != SeaItem.ITEM_ABSOLUTE) {
                    // to substitute the flag that player is attacked from the enemy.
                    ch.IsAttacked(true);
                } else if (playerAction == SeaPlayer.ACTION_WAVE ||
                        playerAction == SeaPlayer.ACTION_CIRCLE ||
                        playerAction == SeaPlayer.ACTION_BUMP ||
                        playerEffect == SeaItem.ITEM_ABSOLUTE) { // when the enemy is attacked from the player,

                    // next to animation that is defeated from player.
                    this.mAni.mType = ANIMATION_TYPE_DEFEATED;
                    this.mAni.ResetAnimation();
                    // change the origin position-Y
                    this.mAni.mStartPic.y = this.mSize.y;

                    // to shoe the effect that is bubbling
                    this.mEffect.ToShowTheEffect();
                    // to substitute the flag that defeat the enemy to player's variable
                    ch.ToDefeatTheEnemy(true);
                    // to create the item based on enemy's type
                    item.CreateItem(this.mPos.x,this.mPos.y,this.mLikelihoodForItem);
                }
            }
        }
        return false;
    }
    /*************************************************************
        Each setter functions
    ***********************************************************/
    /*
        The likelihood to create the item
    */
    public void SetLikelihood(int[] likelihood) {
       this.mLikelihoodForItem = likelihood;
    }
    /*
        The safety area
    */
    public void SetSafetyArea(Rect safety) {
        safety.left *= this.mScale;
        safety.top *= this.mScale;
        safety.right *= this.mScale;
        safety.bottom *= this.mScale;
        this.mRect = safety;
    }
    /*************************************************************
     Each getter functions
     ***********************************************************/
    /*
        To get the presence
    */
    public boolean GetExist() { return this.mExistFlag; }
}