package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/*
    Default setting for runners.
 */
/**
 * Created by USER on 2/23/2016.
 */
abstract class RoadRunner extends BaseCharacter {
    // static variables
    // Effect type
    protected final static byte         EFFECT_NOTHING  = 0x00;
    public final static byte            EFFECT_ABSOLUTE = 0x01;
    protected final static byte         EFFECT_SPEED_UP = 0x02;
    protected final static byte         EFFECT_HEALING  = 0x04;
    public final static byte            EFFECT_ABSOLUTE_AND_SPEED_UP = 0x03;
    // to notice the rest of time that not to show absolute effect.
    private final static int            EFFECT_NOTICE_TIME = 200;
    // Action type
    public final static byte            ACTION_RUN                      = 0x00;
    public final static byte            ACTION_JUMP                     = 0x01;
    public final static byte            ACTION_PREPARE_ATTACK           = 0x02;
    public final static byte            ACTION_LINE_MOVE                = 0x04;     // competitor only
    // Attack direction
    protected final static byte            NOT_ATTACK   = -1;
    protected final static byte            ATTACK_LEFT  = 0x00;
    protected final static byte            ATTACK_RIGHT = 0x01;
    // Image setting
    public final static Point           RUNNER_SIZE = new Point(32,96);
    public final static float           RUNNER_SPEED = 3.0f;

    // Animation setting
    // Action type
    public final static int            ANIMATION_RUN              = 0;
    public final static int            ANIMATION_JUMP             = 1;
    public final static int            ANIMATION_ATTACK_TO_LEFT   = 2;
    public final static int            ANIMATION_ATTACK_TO_RIGHT  = 3;

    // Normal
    public final static int             ANIMATION_NORMAL_COUNT_MAX  = 4;
    public final static int             ANIMATION_NORMAL_FRAME      = 10;
    // Jump
    public final static int             ANIMATION_JUMP_COUNT_MAX  = 18;
    public final static int             ANIMATION_JUMP_FRAME      = 2;
    // Attack
    public final static int             ANIMATION_ATTACK_COUNT_MAX  = 6;
    public final static int             ANIMATION_ATTACK_FRAME      = 5;
    public final static int             ANIMATION_ATTACK_WIDTH      = 48;

    // Safety area
    protected final static Rect            SAFETY_AREA = new Rect(3,20,3,20);
    
    // Overlap area
    protected final static Rect            OVERLAP_AREA = new Rect(14,30,14,30);

    // Detect area
    private final static Point          DETECT_AREA = new Point(48,70);

    // Absolute limit time
    private final static int          ABSOLUTE_LIMIT_TIME = 1000;

    // limit speed
    private final static float        LIMIT_EFFECT_SPEED = 2.0f;

    // Effect max
    protected final static int        EFFECT_MAX = 4;

    // Screen margin
    protected final static Rect      SCREEN_MARGIN = new Rect(40,-150,0,150);

    // filed
    protected   Image               mImage;
    protected   Context             mContext;
    protected   Animation           mAni;
    protected   int                 mAbsoluteTime;
    protected byte                  mEffectType;
    protected byte                  mActionType;
    protected CharacterEffect       mEffect[];
    protected Point                 mDetectArea;
    public byte                     mAttackDirection;
    public Rect                     mOverlapArea;
    private BaseCharacter           mAttackArea;
    protected float                 mEffectSpeed;
    protected Sound                 mSe;
    // for score
    protected int                   mChain;

    /*
        Constructor
    */
    protected RoadRunner() {
         this.mAni = new Animation();
        // image setting
        this.mSize.x = RUNNER_SIZE.x;
        this.mSize.y = RUNNER_SIZE.y;
        this.mSpeed  = RUNNER_SPEED;
        // safety area
        this.mRect.left = SAFETY_AREA.left;
        this.mRect.top  = SAFETY_AREA.top;
        this.mRect.right = SAFETY_AREA.right;
        this.mRect.bottom = SAFETY_AREA.bottom;
        // absolute time
        this.mAbsoluteTime = 0;
        // effect type
        this.mEffectType = EFFECT_NOTHING;
        // action type
        this.mActionType = ACTION_RUN;
        // detect area
        this.mDetectArea = new Point(DETECT_AREA.x,DETECT_AREA.y);
        // attack direction
        this.mAttackDirection = NOT_ATTACK;
        // the effect speed
        this.mEffectSpeed = 0.0f;

        // animation setting
        this.mAni.SetAnimation(
                this.mOriginPos.x, this.mOriginPos.y,
                RUNNER_SIZE.x, RUNNER_SIZE.y,
                ANIMATION_NORMAL_COUNT_MAX,
                ANIMATION_NORMAL_FRAME,
                ACTION_RUN);
        // overlap area
        mOverlapArea = new Rect(0,0,0,0);
        // Attack area
        this.mAttackArea = new BaseCharacter();
        this.mAttackArea.mSize.x = 10;
        this.mAttackArea.mSize.y = 20;
        // chain value that jump the hurdle of consecutive.
        this.mChain = 0;
    }

    /*
        Release
    */
    protected void ReleaseRunner() {
        if (this.mSe != null) this.mSe = null;
        this.mContext = null;
        this.mImage = null;
        this.mAni = null;
        this.ReleaseCharaBmp();
        // Effect
        for (int i = 0; i < this.mEffect.length; i++) {
            this.mEffect[i].ReleaseCharacterEffect();
            this.mEffect[i] = null;
        }
        // Attack area
        this.mAttackArea = null;
    }

    /*
        Update overlap area
    */
    protected void UpdateOverlapArea(Rect over) {
        // safety area
        float Safety[] = {
                over.left*this.mScale,over.top*this.mScale,
                over.right*this.mScale,over.bottom*this.mScale,
        };
        this.mOverlapArea.left = (int)Safety[0];
        this.mOverlapArea.top = (int)Safety[1];
        this.mOverlapArea.right = (int)Safety[2];
        this.mOverlapArea.bottom = (int)Safety[3];
    }

    /********************************************************************
        Each animation functions
     *******************************************************************/
    /*
        Initialize animation for jump
     */
    protected void InitJumpAnimation() {
        // reset animation setting
        this.mAni.ResetAnimation();
        // set type
        this.mAni.mType = ANIMATION_JUMP;
        this.mAni.mFrame = ANIMATION_JUMP_FRAME;
        this.mAni.mCountMax = ANIMATION_JUMP_COUNT_MAX;
        this.mAni.mDirection = ANIMATION_JUMP;
        // normal size
        this.mAni.mSize.x = this.mSize.x = RUNNER_SIZE.x;
        // play sound
        this.mSe.PlaySE();
    }
    /*
        Initialize attack to left animation
     */
    protected void InitAttackToLeftAnimation() {
        // reset animation 
        this.mAni.ResetAnimation();
        // set type
        this.mAni.mType = ANIMATION_ATTACK_TO_LEFT;
        this.mAni.mFrame = ANIMATION_ATTACK_FRAME;
        this.mAni.mCountMax = ANIMATION_ATTACK_COUNT_MAX;
        this.mAni.mDirection = ANIMATION_ATTACK_TO_LEFT;
        // width
        this.mAni.mSize.x = this.mSize.x = ANIMATION_ATTACK_WIDTH;
    }
    /*
        Initialize attack to right animation
    */
    protected void InitAttackToRightAnimation() {
        // reset animation 
        this.mAni.ResetAnimation();
        // set type
        this.mAni.mType = ANIMATION_ATTACK_TO_RIGHT;
        this.mAni.mFrame = ANIMATION_ATTACK_FRAME;
        this.mAni.mCountMax = ANIMATION_ATTACK_COUNT_MAX;
        this.mAni.mDirection = ANIMATION_ATTACK_TO_RIGHT;
        // width
        this.mAni.mSize.x = this.mSize.x = ANIMATION_ATTACK_WIDTH;
    }

    /*
        Update animation
    */
    protected boolean UpdateRunnerAnimation() {
        // animation type
        int aniType[] = {
                ANIMATION_RUN,              // 0
                ANIMATION_JUMP,             // 1
                ANIMATION_ATTACK_TO_LEFT,   // 2
                ANIMATION_ATTACK_TO_RIGHT,  // 3
        };
        // loop to kind of action
        for (int kind: aniType) {
            if (kind == this.mAni.mType) {
                // update animation
                if (!this.mAni.UpdateAnimation(this.mOriginPos, false)) {
                    // reset current type
                    this.mAni.mDirection = this.mAni.mType = ANIMATION_RUN;
                    // animation setting
                    this.mAni.mCountMax = ANIMATION_NORMAL_COUNT_MAX;
                    this.mAni.mFrame = ANIMATION_NORMAL_FRAME;
                    // back to normal width
                    this.mAni.mSize.x = this.mSize.x = RUNNER_SIZE.x;
                    // when attack animation, to reset direction
                    if (kind == ANIMATION_ATTACK_TO_LEFT || kind == ANIMATION_ATTACK_TO_RIGHT) {
                        this.mAttackDirection = NOT_ATTACK;
                    }
                    return false;
                }
            }
        }
        // true is doing the animation
        return true;
    }
    /*
        Variable animation speed.
        return value is animation frame
    */
    protected int VariableAnimationFrame(float speed) {
        int frame = ANIMATION_NORMAL_FRAME;     // 10
        for (int i = 0; i < 4; i++) {
            if (i+RUNNER_SPEED < Math.abs(speed)) {
                frame -= i;
            } else if (Math.abs(speed) <= RUNNER_SPEED) {
                frame = ANIMATION_NORMAL_FRAME;
                break;
            }
        }
        return frame;
    }
    /***********************************************************************************************
        Each effect functions
    **********************************************************************************************/
    /*
        Initialize the effect
     */
    protected void InitRunnerEffect(RoadObstacles.BUMP_TYPE bumpType) {
        // diverge action from bump type.
        // effect type
        int effectType[] = new int[EFFECT_MAX];
        for (int i = 0; i < effectType.length; i++) effectType[i] = -1;
        switch (bumpType) {
            case ORDINARY_BUMP:
                this.mExistFlag = false;       // avoid obstacle a while.
                this.RepealSpeedUp();           // repeal effect of speed up.
                this.mEffect[2].NotToShowTheEffect();
                // the effect that bump to the obstacle
                if (this.mEffect[3].mType == CharacterEffect.EFFECT_NOTHING) effectType[3] = 0;
                // reset chain
                this.mChain = 0;
                break;
            case HURDLE_RED:        // type is absolute
                this.SetAbsolute();
                // set effect
                if (this.mEffect[0].mType == CharacterEffect.EFFECT_NOTHING) effectType[0] = CharacterEffect.CIRCLE_TYPE_RED;
                if (this.mEffect[1].mType == CharacterEffect.EFFECT_NOTHING) effectType[1] = CharacterEffect.BALL_TYPE_RED;
                // increase chain
                this.mChain++;
                break;
            case HURDLE_BLUE:       // type is speed up
                this.SetSpeedUp();
                // set effect
                if (this.mEffect[0].mType == CharacterEffect.EFFECT_NOTHING) effectType[0] = CharacterEffect.CIRCLE_TYPE_BLUE;
                if (this.mEffect[2].mType == CharacterEffect.EFFECT_NOTHING) effectType[2] = CharacterEffect.BALL_TYPE_BLUE;
                // increase chain
                this.mChain++;
                break;
            case HURDLE_GREEN:
                // set effect
                if (this.mEffect[0].mType == CharacterEffect.EFFECT_NOTHING) effectType[0] = CharacterEffect.CIRCLE_TYPE_GREEN;
                // increase chain
                this.mChain++;
                break;
            default:
                break;
        }
        // Create the effect
        for (int i = 0; i < effectType.length; i++) {
            if (effectType[i] == -1) continue;
            this.mEffect[i].CreateEffect(this.mPos.x,this.mPos.y,effectType[i]);
        }
    }
    /*
        Update effect
    */
    protected void UpdateRunnerEffect() {
        // Update the effect
        for (CharacterEffect effect: this.mEffect) {
            if (effect.mType != CharacterEffect.EFFECT_NOTHING) {
                // Update the effect
                effect.UpdateCharacterEffect(
                        this.mPos.x,this.mPos.y,
                        this.mSize.x,this.mSize.y,this.mScale,false);
            }
        }
        // Update absolute
        if (!this.UpdateAbsolute()) this.mEffect[1].NotToShowTheEffect();
    }

    /*
        Effect 1 Absolute
     */
    private void SetAbsolute() {
        // limit time
        this.mAbsoluteTime = ABSOLUTE_LIMIT_TIME;
        // set effect type
        this.mEffectType |= EFFECT_ABSOLUTE;
        // reset flutter count
        this.mEffect[1].ResetFlutterCount();
    }
    /*
        Effect 2 Speed up
     */
    private void SetSpeedUp() {
        // add speed
        this.mEffectSpeed += 0.2f;
        // limit
        if (LIMIT_EFFECT_SPEED <= this.mSpeed) this.mEffectSpeed = LIMIT_EFFECT_SPEED;
        // set type
        this.mEffectType |= EFFECT_SPEED_UP;
    }
    /*
        to Repeal effect
     */
    /*
        Effect 1 Absolute
     */
    private boolean UpdateAbsolute() {
        boolean ret = false;
        // if type is absolute or absolute and speed up.
        if (this.mEffectType == EFFECT_ABSOLUTE ||
            this.mEffectType == EFFECT_ABSOLUTE_AND_SPEED_UP) {
            // count time
            this.mAbsoluteTime--;
            ret = true;
        }
        // if rest of time is around limit time, to flutter the ball effect.
        if (this.mAbsoluteTime <= EFFECT_NOTICE_TIME) {
            this.mEffect[1].StartToFlutterEffect(EFFECT_NOTICE_TIME);
            // set interval to flutter the effect
            if (this.mAbsoluteTime <= EFFECT_NOTICE_TIME>>1) {
                this.mEffect[1].SetIntervalToFlutter(2);
            } else {
                this.mEffect[1].SetIntervalToFlutter(4);
            }
        }
        // reached to limit time
        if (this.mAbsoluteTime <= 0) {
            // reset type
            this.mEffectType &= ~EFFECT_ABSOLUTE;
            this.mEffect[1].ResetFlutterCount();
            ret = false;
        }
        return ret;
    }
    /*
        Effect 2 Speed up
     */
    private void RepealSpeedUp() {
        // reset type
        this.mEffectType &= ~EFFECT_SPEED_UP;
        // back to default speed
        this.mEffectSpeed = 0.0f;
    }
    /**********************************************************************************************
     *  Check collision between character's attack area and an other.
     *********************************************************************************************/
    protected boolean CheckAttackCollision(BaseCharacter attacker, byte direction, BaseCharacter target) {
        // when attacker doesn't attack, to return
        if (direction == NOT_ATTACK) return false;
        // attack area
        this.mAttackArea.mScale = attacker.mScale;
        this.mAttackArea.mSize.x = 10;
        this.mAttackArea.mSize.y = 20;
        float w = this.mAttackArea.mSize.x*this.mAttackArea.mScale;
        // make attack area based on attacker's position.
        // target's size
        FPoint attackerSize = new FPoint(attacker.mSize.x*attacker.mScale,attacker.mSize.y*attacker.mScale);
        // position-Y
        this.mAttackArea.mPos.y = attacker.mPos.y+((int)attackerSize.y>>4);
        // left side
        if (direction == ATTACK_LEFT) {
            this.mAttackArea.mPos.x = attacker.mPos.x;
        // right side
        } else if (direction == ATTACK_RIGHT) {
            this.mAttackArea.mPos.x = attacker.mPos.x+(int)attackerSize.x-((int)w<<1);
        }
        // check collision between attack area and character's rectangular.
        if(Collision.CollisionCharacter(this.mAttackArea,target)) {
            target.mExistFlag = false;
            return true;
        }
        return false;
    }
    /*
        When runner is attacked from other, to show the effect.
    */
    public void IsAttacked(boolean attacked) {
        if (!attacked) return;
        // create the effect that is attacked from competitor.
        this.mEffect[3].CreateEffect(this.mPos.x,this.mPos.y,0);
    }

    /**********************
     Abstract functions
     *********************/
    /*
        Update
    */
    protected abstract boolean UpdateRunner(RoadObstacles obstacle);
}