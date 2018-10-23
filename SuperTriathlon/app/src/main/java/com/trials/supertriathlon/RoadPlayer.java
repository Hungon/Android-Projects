package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Vibrator;
import android.view.MotionEvent;

/**
 * Created by USER on 2/23/2016.
 */
public class RoadPlayer extends RoadRunner implements HasScenes {

    // static variables
    private final static int        STARTING_POSITION_Y = 500;
    // the flag that attempt to attack the competitor
    public final static int        NOT_TARGET = -1;
    // Stamina max
    public final static int       STAMINA_MAX = 3;
    // filed
    private static int              mHp;
    private BaseCharacter           mStatus;
    private static float            mAggregateSpeed;
    private static Point            mPosition;
    private static Rect             mSafetyArea = new Rect();
    private static FPoint           mWholeSize;
    private CharacterEffect         mOwnEffect[];
    private static int              mTarget;
    private static int              mPlayerChain;

    /*
        Constructor
    */
    public RoadPlayer(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        // Effect class
        this.mEffect = new CharacterEffect[EFFECT_MAX ];
        for (int i = 0; i < this.mEffect.length; i++) {
            this.mEffect[i] = new CharacterEffect(context, image);
        }
        // Own effect
        this.mOwnEffect = new CharacterEffect[2];
        for (int i = 0; i < this.mOwnEffect.length; i++) {
            this.mOwnEffect[i] = new CharacterEffect(context, image);
        }
        // Status
        this.mStatus = new BaseCharacter(image);
        // getter
        mPosition = new Point(0,0);
        mAggregateSpeed = RUNNER_SPEED;
        mSafetyArea.left = this.mRect.left;
        mSafetyArea.top = this.mRect.top;
        mSafetyArea.right = this.mRect.right;
        mSafetyArea.bottom = this.mRect.bottom;
        mWholeSize = new FPoint(0.0f,0.0f);
    }
    /*
        Constructor
    */
    public RoadPlayer() {
        // getter
        mPosition = new Point(0,0);
        mAggregateSpeed = RUNNER_SPEED;
        mSafetyArea.left = this.mRect.left;
        mSafetyArea.top = this.mRect.top;
        mSafetyArea.right = this.mRect.right;
        mSafetyArea.bottom = this.mRect.bottom;
        mWholeSize = new FPoint(0.0f,0.0f);
    }

    /*
        Initialize
    */
    public void InitRoadPlayer() {
        // using image
        String imageFile = "roadplayer";
        // load the image
        this.mBmp = this.mImage.LoadImage(this.mContext, imageFile);
        // Stamina
        this.mStatus.LoadCharaImage(this.mContext, "roadstamina");
        // SE
        this.mSe = new Sound(this.mContext);
        this.mSe.CreateSound("jump");

        // Stamina
        mHp = STAMINA_MAX;
        // target
        mTarget = -1;
        // reset chain
        this.mChain = 0;

        Point screen = GameView.GetScreenSize();
        // Image setting
        this.mPos.x = (screen.x-RUNNER_SIZE.x)>>1;
        this.mPos.y = StageCamera.GetCameraWholeArea().bottom-GameView.GetScreenSize().y+STARTING_POSITION_Y;
        this.mExistFlag = true;
        // set position
        mPosition.x = this.mPos.x;
        mPosition.y = this.mPos.y;
        // set camera
        StageCamera.SetCamera(0, this.mPos.y - STARTING_POSITION_Y);
        // Variable scale rate
        this.mScale = (float)this.VariableScaleRateBasedOnCameraAngle(1.0f,2.0f);

        // Set whole size to get that values.
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);

        // Stamina
        this.mStatus.mSize.x = 32;
        this.mStatus.mSize.y = 32;
        this.mStatus.mOriginPos.x = 0;
        this.mStatus.mOriginPos.y = 0;
        this.mStatus.mExistFlag = true;
        //position
        this.mStatus.mPos.x = this.mPos.x-this.mStatus.mSize.x;
        this.mStatus.mPos.y = this.mPos.y+this.mSize.y;


        // Set effect
        // kind of effect
        int effectKind[] = {
                CharacterEffect.EFFECT_CIRCLE,
                CharacterEffect.EFFECT_BALL,
                CharacterEffect.EFFECT_BALL,
                CharacterEffect.EFFECT_BUMP
        };
        for (int i = 0; i < effectKind.length; i++) {
            this.mEffect[i].InitCharacterEffect(effectKind[i]);
        }
        // Own effect that is attention
        int ownEffect[] = {
            CharacterEffect.EFFECT_ATTENTION,
            CharacterEffect.EFFECT_TARGET,
        };
        for (int i = 0; i < this.mOwnEffect.length; i++) {
            this.mOwnEffect[i].InitCharacterEffect(ownEffect[i]);
        }
    }
    /*
        Initialize the player to explain
    */
    public void InitToExplain() {
        // using image
        String imageFile = "roadplayer";
        // load the image
        this.mBmp = this.mImage.LoadImage(this.mContext, imageFile);
        // Stamina
        this.mStatus.LoadCharaImage(this.mContext, "roadstamina");
        // SE
        this.mSe = new Sound(this.mContext);
        this.mSe.CreateSound("jump");

        // Stamina
        mHp = STAMINA_MAX;
        // target
        mTarget = -1;

        Point screen = GameView.GetScreenSize();
        // Image setting
        this.mPos.x = (screen.x-RUNNER_SIZE.x)>>1;
        this.mPos.y = STARTING_POSITION_Y;
        // set position to get
        mPosition.x = this.mPos.x;
        mPosition.y = this.mPos.y;
        this.mExistFlag = true;
        // Set whole size to get that values.
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);

        // Stamina
        this.mStatus.mSize.x = 32;
        this.mStatus.mSize.y = 32;
        this.mStatus.mOriginPos.x = 0;
        this.mStatus.mOriginPos.y = 0;
        this.mStatus.mExistFlag = true;
        //position
        this.mStatus.mPos.x = this.mPos.x-this.mStatus.mSize.x;
        this.mStatus.mPos.y = this.mPos.y+this.mSize.y;


        // Own effect that is attention
        int ownEffect[] = {
                CharacterEffect.EFFECT_ATTENTION,
                CharacterEffect.EFFECT_TARGET,
        };
        for (int i = 0; i < this.mOwnEffect.length; i++) {
            this.mOwnEffect[i].InitCharacterEffect(ownEffect[i]);
        }
    }
    /*
        Update
    */
    @Override
    public boolean UpdateRunner(RoadObstacles obstacles) {

        // update safety area
        SetSafetyArea(this.mRect);
        // bump type
        RoadObstacles.BUMP_TYPE bumpType = RoadObstacles.BUMP_TYPE.NOT_BUMP;
        if (this.mExistFlag) {
            // collision with obstacle and get bump type to do an action.
            bumpType = obstacles.CollisionObstacles(this, mSafetyArea, this.mActionType, this.mEffectType);
        }

        // Update stamina
        if (this.mStatus.mExistFlag) {
            //position
            this.mStatus.mPos.x = this.mPos.x-this.mStatus.mSize.x;
            this.mStatus.mPos.y = this.mPos.y+this.mSize.y;
            if (bumpType == RoadObstacles.BUMP_TYPE.ORDINARY_BUMP) {
                mHp--;
                // to transition to game-over scene.
                if (mHp <= 0) {
                    Wipe.CreateWipe(SCENE_GAME_OVER,Wipe.TYPE_PENETRATION);
                    return true;            // player isn't existing.
                }
            } else if (bumpType == RoadObstacles.BUMP_TYPE.HURDLE_GREEN) {
                mHp++;
                if (STAMINA_MAX < mHp) mHp = STAMINA_MAX;
            }
        }

        // Each action
        if (this.mActionType == ACTION_RUN) {
            // update move process
            if (GameView.GetTouchAction() == MotionEvent.ACTION_UP ||
                GameView.GetTouchAction() == MotionEvent.ACTION_CANCEL) {
                // reset move
                this.mMoveY = 0.0f;
                this.mMoveX = 0.0f;
            } else {
                // when doing action, not to move.
                this.CharacterMoveToTouchedPositionEx();
                // update move
                this.mPos.x += this.mMoveX*(RUNNER_SPEED+this.mEffectSpeed);
                this.mPos.y += this.mMoveY*(RUNNER_SPEED+this.mEffectSpeed);
            }
            // Jump action
            this.InitJumpAction();
            // Attack action
            if (mTarget == NOT_TARGET) this.InitAttackAction();
        // Update attack action
        } else if (this.mActionType == ACTION_PREPARE_ATTACK) {
            this.UpdateAttackAction();
            // update move
            this.mPos.x += this.mMoveX;
            this.mPos.y += this.mMoveY;
            // cancel attack action
            this.CancelAttackAction();
        }

        // Update animation
        this.UpdateRunnerAnimation();

        // when current animation type is running, reset the action type
        this.FinishActionType();

        // Initialize the effect
        if (bumpType != RoadObstacles.BUMP_TYPE.NOT_BUMP) this.InitRunnerEffect(bumpType);
        // Update effect
        this.UpdateRunnerEffect();

        // Update own effect
        // when competitor attempt to attack player, to show attention image.
        byte attention[] = RoadCompetitorManager.GetCurrentActionType();
        boolean attentionF = false;
        // position
        Point pos = new Point();
        // size
        Point size = new Point();
        // scale
        float scale = 0.0f;
        // check the flag that attention to attack
        for (byte flag:attention) if (flag == ACTION_PREPARE_ATTACK) attentionF = true;
        for (int i = 0; i < this.mOwnEffect.length; i++) {
            // create the effect that attention to attack from competitor
            if (i == 0) {
                if (!attentionF) {
                    this.mOwnEffect[0].NotToShowTheEffect();
                } else {
                    // drawing setting
                    pos.x = this.mPos.x;
                    pos.y = this.mPos.y;
                    size.x = this.mSize.x;
                    size.y = this.mSize.y;
                    scale = this.mScale;
                    this.mOwnEffect[0].ToShowTheEffect();
                }
                // the effect that attempt to attack to competitor
            } else if (i == 1) {
                // when prepare to attack to the competitor
                if (this.mActionType == ACTION_PREPARE_ATTACK) {
                    // get competitor's position
                    Point comPos[] = RoadCompetitorManager.GetPosition();
                    // whole size
                    FPoint comSize[] = RoadCompetitorManager.GetWholeSize();
                    // set
                    pos.x = comPos[mTarget].x;
                    pos.y = comPos[mTarget].y;
                    size.x = (int) comSize[mTarget].x;
                    size.y = (int) comSize[mTarget].y;
                    scale = 1.0f;
                    // to show the effect
                    if (this.mOwnEffect[1].mType == CharacterEffect.EFFECT_NOTHING) {
                        this.mOwnEffect[1].CreateEffect(pos.x,pos.y,CharacterEffect.TARGET_SCALE,255,0);
                    }
                } else {
                    this.mOwnEffect[1].NotToShowTheEffect();
                }
            }
            // Update the effect to render animation.
            this.mOwnEffect[i].UpdateCharacterEffect(pos.x, pos.y, size.x, size.y, scale, false);
        }

        // Update avoid time
        this.AvoidObject(this, 200);

        // update variable scale rate
        this.mScale = (float)this.VariableScaleRateBasedOnCameraAngle(1.0f,2.0f);
        // Update whole size
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);

        // update coordinate-Y based on aggregate seed.
        mAggregateSpeed = this.mSpeed+this.mEffectSpeed;
        // update position
        mPosition.x = this.mPos.x;
        mPosition.y = this.mPos.y;
        this.mPos.y -= mAggregateSpeed;
        // set camera
        int localY = this.mPos.y - StageCamera.GetCameraPosition().y;
        StageCamera.SetCamera(0, this.mPos.y - localY);
        // constrain available to move area
        this.ConstrainMove();

        // Update animation speed
        if (this.mAni.mType == ANIMATION_RUN) {
            this.mAni.mFrame = this.VariableAnimationFrame(mAggregateSpeed+this.mMoveY);
        }
        // Update area that not to overlap
        this.UpdateOverlapArea(OVERLAP_AREA);
        // update chain
        mPlayerChain = this.mChain;
        return false;       // false is player is existing.
    }
    /*
        Update to explain the stage.
    */
    public void UpdateToExplain() {

        // Update stamina
        if (this.mStatus.mExistFlag) {
            //position
            this.mStatus.mPos.x = this.mPos.x-this.mStatus.mSize.x;
            this.mStatus.mPos.y = this.mPos.y+this.mSize.y;
        }

        // Each action
        if (this.mActionType == ACTION_RUN) {
            // update move process
            if (GameView.GetTouchAction() == MotionEvent.ACTION_UP ||
                GameView.GetTouchAction() == MotionEvent.ACTION_CANCEL) {
                // reset move
                this.mMoveY = 0.0f;
                this.mMoveX = 0.0f;
            } else {
                // when doing action, not to move.
                this.CharacterMoveToTouchedPosition(RUNNER_SPEED);
            }
            // Jump action
            this.InitJumpAction();
            // Attack action
            if (mTarget == NOT_TARGET) this.InitAttackAction();
            // Update attack action
        } else if (this.mActionType == ACTION_PREPARE_ATTACK) {
            this.UpdateAttackAction();
            // update move
            this.mPos.x += this.mMoveX;
            this.mPos.y += this.mMoveY;
            // cancel attack action
            this.CancelAttackAction();
        }

        // Update animation
        this.UpdateRunnerAnimation();

        // when current animation type is running, reset the action type
        this.FinishActionType();

        // Update own effect
        // when competitor attempt to attack player, to show attention image.
        byte attention[] = RoadCompetitorManager.GetCurrentActionType();
        boolean attentionF = false;
        // position
        Point pos = new Point();
        // size
        Point size = new Point();
        // scale
        float scale = 0.0f;
        // check the flag that attention to attack
        for (byte flag:attention) if (flag == ACTION_PREPARE_ATTACK) attentionF = true;
        for (int i = 0; i < this.mOwnEffect.length; i++) {
            // create the effect that attention to attack from competitor
            if (i == 0) {
                if (!attentionF) {
                    this.mOwnEffect[0].NotToShowTheEffect();
                } else {
                    // drawing setting
                    pos.x = this.mPos.x;
                    pos.y = this.mPos.y;
                    size.x = this.mSize.x;
                    size.y = this.mSize.y;
                    scale = this.mScale;
                    this.mOwnEffect[0].ToShowTheEffect();
                }
                // the effect that attempt to attack to competitor
            } else if (i == 1) {
                // when prepare to attack to the competitor
                if (this.mActionType == ACTION_PREPARE_ATTACK) {
                    // get competitor's position
                    Point comPos[] = RoadCompetitorManager.GetPosition();
                    // whole size
                    FPoint comSize[] = RoadCompetitorManager.GetWholeSize();
                    // set
                    pos.x = comPos[mTarget].x;
                    pos.y = comPos[mTarget].y;
                    size.x = (int) comSize[mTarget].x;
                    size.y = (int) comSize[mTarget].y;
                    scale = 1.0f;
                    // to show the effect
                    if (this.mOwnEffect[1].mType == CharacterEffect.EFFECT_NOTHING) {
                        this.mOwnEffect[1].CreateEffect(pos.x,pos.y,CharacterEffect.TARGET_SCALE,255,0);
                    }
                } else {
                    this.mOwnEffect[1].NotToShowTheEffect();
                }
            }
            // Update the effect to render animation.
            this.mOwnEffect[i].UpdateCharacterEffect(pos.x, pos.y, size.x, size.y, scale, false);
        }
        // update position
        mPosition.x = this.mPos.x;
        mPosition.y = this.mPos.y;
        // Update whole size
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);
         // constrain available to move area
        this.ConstrainMove();
    }

    /*
        Draw
    */
    public void DrawRoadPlayer() {
        // camera position
        Point camera = StageCamera.GetCameraPosition();
        if (camera == null) camera = new Point(0,0);
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
        // Effect that increase own potential
        for (CharacterEffect effect: this.mEffect) {
            effect.DrawCharacterEffect();
        }
        // target effect is in the play-scene.
        // to show the effect that attention to attack
        this.mOwnEffect[0].DrawCharacterEffect();

        // Stamina
        int stamina = mHp;
        for (int i = 0; i < STAMINA_MAX; i++) {
            if (this.mStatus.mExistFlag) {
                if ((stamina-1) < i) {
                    this.mStatus.mOriginPos.x = this.mStatus.mSize.x;
                } else {
                    this.mStatus.mOriginPos.x = 0;
                }
                this.mImage.DrawImage(
                        this.mStatus.mPos.x-camera.x+(this.mStatus.mSize.x*i),
                        this.mStatus.mPos.y-camera.y,
                        this.mStatus.mSize.x,
                        this.mStatus.mSize.y,
                        this.mStatus.mOriginPos.x,
                        this.mStatus.mOriginPos.y,
                        this.mStatus.mBmp
                );
            }
        }
    }
    /*
        Draw to explain
    */
    public void DrawToExplain() {
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
        // Stamina
        int stamina = mHp;
        for (int i = 0; i < STAMINA_MAX; i++) {
            if (this.mStatus.mExistFlag) {
                if ((stamina-1) < i) {
                    this.mStatus.mOriginPos.x = this.mStatus.mSize.x;
                } else {
                    this.mStatus.mOriginPos.x = 0;
                }
                this.mImage.DrawImage(
                        this.mStatus.mPos.x+(this.mStatus.mSize.x*i),
                        this.mStatus.mPos.y,
                        this.mStatus.mSize.x,
                        this.mStatus.mSize.y,
                        this.mStatus.mOriginPos.x,
                        this.mStatus.mOriginPos.y,
                        this.mStatus.mBmp
                );
            }
        }
        // to show the effect that attention to attack
        for (CharacterEffect effect:this.mOwnEffect) {
            effect.DrawCharacterEffect();
        }
    }

    /*
        Release
    */
    public void ReleaseRoadPlayer() {
        // Stamina
        this.mStatus.ReleaseCharaBmp();
        this.mStatus = null;
        this.ReleaseRunner();
        // own effect
        for (int i = 0; i < this.mOwnEffect.length; i++) {
            if (this.mOwnEffect[i] != null) {
                this.mOwnEffect[i].ReleaseCharacterEffect();
                this.mOwnEffect[i] = null;
            }
        }
    }
    /**********************************************************************************************
        Each action functions
     *********************************************************************************************/
    /*
        Initialize jump action
    */
    private void InitJumpAction() {
        int touchAction = GameView.GetTouchAction();
        Point camera = StageCamera.GetCameraPosition();
        if (camera == null) camera = new Point();
        if (touchAction == MotionEvent.ACTION_DOWN &&
                Collision.CheckTouch(
                        this.mPos.x - camera.x, this.mPos.y - camera.y,
                        this.mSize.x, this.mSize.y,
                        this.mScale)) {
            // set action type
            this.mActionType |= ACTION_JUMP;
            // set animation setting
            this.InitJumpAnimation();
        }
    }
    /*
        To check the event that touched the competitor
        and to initialize attack action
    */
    private void InitAttackAction() {
        // check the event that touch the competitor
        int target = RoadCompetitorManager.GetTouchedEvent();
        // set the action
        if (target != NOT_TARGET && target != mTarget) {
            mTarget = target;
            this.mActionType |= ACTION_PREPARE_ATTACK;
            // vibrate process
            Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1);
        }
    }
    /*
        Update attack action
    */
    private void UpdateAttackAction() {
        // get camera's position
        Point cameraPos = StageCamera.GetCameraPosition();
        if (cameraPos == null) cameraPos = new Point();

        // calculate center position in local.
        Point center = new Point(
                this.mPos.x + ((int)mWholeSize.x>>1),
                (this.mPos.y - cameraPos.y) + ((int)mWholeSize.y>>1)
        );
        // get competitor's position
        Point pos[] = RoadCompetitorManager.GetPosition();
        // whole size
        FPoint size[] = RoadCompetitorManager.GetWholeSize();
        // get competitor's center position in local
        float x = (pos[mTarget].x-cameraPos.x)+((int)size[mTarget].x>>1);
        float y = (pos[mTarget].y-cameraPos.y)+((int)size[mTarget].y>>1);

        // calculate triangle area.
        // bottom
        float bottom = x - center.x;
        // height
        float height = y - center.y;
        // oblique side.
        double oblique = Math.sqrt((double) (bottom * bottom) +
                (double) (height * height));
        // calculate move
        this.mMoveX = (bottom / (float) (oblique)) * (Math.abs(this.mSpeed)+this.mEffectSpeed);
        this.mMoveY = (height / (float) (oblique)) * (Math.abs(this.mSpeed)+this.mEffectSpeed);


        // To attack process
        // when current position nears player's position, to do animation
        Point difference = new Point(
                Math.abs(center.x-(int)x),
                Math.abs(center.y-(int)y));

        // when competitor through player, not to move to forward or backward.
        if (difference.y < (int)mWholeSize.y>>3) this.mMoveY = 0.0f;

        // check difference to position
        if (difference.x < (int)mWholeSize.x && difference.y < (int)mWholeSize.y>>3) {
            // select direction to attack
            // player's position is right side from competitor.
            if (center.x < x) {
                this.InitAttackToRightAnimation();
                // set direction
                this.mAttackDirection = ATTACK_RIGHT;
            } else { // left side
                // animation setting
                this.InitAttackToLeftAnimation();
                this.mAttackDirection = ATTACK_LEFT;
            }
            // reset move
            this.mMoveX = 0.0f;
            // reset action type
            this.mActionType &= ~ACTION_PREPARE_ATTACK;
            // reset target
            mTarget = NOT_TARGET;
        }
    }
    /*
        Cancel attack action
    */
    private void CancelAttackAction() {
        // check the event that touch the competitor
        int target = RoadCompetitorManager.GetTouchedEvent();
        if (target == -1 && mTarget != -1 && GameView.GetTouchAction() == MotionEvent.ACTION_DOWN) {
            // reset move
            this.mMoveX = 0.0f;
            this.mMoveY = 0.0f;
            // reset action type
            this.mActionType &= ~ACTION_PREPARE_ATTACK;
            // reset target
            mTarget = NOT_TARGET;
        }
    }

    /*
        Check to finish action type
    */
    private void FinishActionType() {
        // when the flag that exist is false, reset action
        if (!this.mExistFlag) {
            this.mActionType &= ~this.mActionType;
            // reset target
            mTarget = NOT_TARGET;
        }
        // when current action type is prepare attack, not to reset action type
        if (this.mActionType == ACTION_PREPARE_ATTACK) return;
        // when current animation is running, reset the action
        if (this.mAni.mType == ANIMATION_RUN) {
            this.mActionType &= ~this.mActionType;
            // reset target
            mTarget = NOT_TARGET;
        }
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
        Set stamina
    */
    public static void SetStamina(int hp) {
        if (hp < 0 || STAMINA_MAX < hp) return;
        mHp = hp;
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
        Get current effect type
    */
    public byte GetCurrentEffectType() { return this.mEffectType; }
    /*
        Get the current effect that attempt to attack to competitor
    */
    public CharacterEffect GetTargetEffect() { return this.mOwnEffect[1]; }
    /*
        Get current animation count
    */
    public int GetAnimationCount() { return this.mAni.mCount; }
    /*
        Get the target that attempt to attack to the competitor.
    */
    public static int GetTarget() { return mTarget; }
    /*
        Get chain
    */
    public static int GetChain() { return mPlayerChain; }
}