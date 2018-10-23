package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by USER on 2/29/2016.
 */
public class RoadCompetitor extends RoadRunner {
    // static variables
    // The interval to do attack action
    private final static int[]    ACTION_ATTACK_INTERVAL_TIME = {900,800,700};
    private final static int      FINISH_ACTION_INTERVAL_TIME_ANYWAY = 1500;
    private final static int      TRACE_DISTANCE[] = {250,300,350};
    // the action to hurdle
    private final static byte     ACTION_BOX[][] = {
        {ACTION_JUMP, ACTION_LINE_MOVE},     // to hurdle
        {ACTION_LINE_MOVE, ACTION_JUMP},     // to cone
    };
    // filed
    private float                 mDistanceCount;
    private float                 mSelectedDistance;
    private int                   mAttackIntervalCount;
    private int                   mAttackFixedTime;
    private int                   mFinishActionCount;
    private float                 mAggregateSpeed;
    private Rect                  mSafetyArea = new Rect();
    private FPoint                mWholeSize;
    private int                   mLikelihood[][];
    private int                   mTraceDistance;

    /*
        Constructor
    */
    public RoadCompetitor(Context context, Image image) {
        // get kind of obstacle.
        int kind = RoadObstacles.GetKind();
        this.mContext = context;
        this.mImage = image;
        // Effect class
        this.mEffect = new CharacterEffect[EFFECT_MAX ];
        for (int i = 0; i < this.mEffect.length; i++) {
            this.mEffect[i] = new CharacterEffect(context, image);
        }
        // getter
        this.mAggregateSpeed = RUNNER_SPEED;
        this.mSafetyArea.left = this.mRect.left;
        this.mSafetyArea.top = this.mRect.top;
        this.mSafetyArea.right = this.mRect.right;
        this.mSafetyArea.bottom = this.mRect.bottom;
        this.mWholeSize = new FPoint();
        // the count that distance
        this.mDistanceCount = 0.0f;
        this.mSelectedDistance = 0.0f;
        // the interval to do attack action
        this.mAttackIntervalCount = 0;
        // fixed time to attack
        this.mAttackFixedTime = 0;
        // the count that finish action anyway.
        this.mFinishActionCount = 0;
        // the distance that trace to player.
        this.mTraceDistance = 0;
        // Likelihood
        this.mLikelihood = new int[kind][ACTION_BOX[0].length];
    }

    /*
        Initialize
    */
    public void InitRoadCompetitor(int count) {
        int level = Play.GetGameLevel();
        // likelihood to the obstacle
        // Hurdle   jump, evade,    Line = game level, Row = kind oof obstacle.
        int hurdle[][] = {
                {40,30},
                {45,35},
                {80,15},
        };
        for (int i = 0; i < this.mLikelihood[0].length; i++) {
            this.mLikelihood[0][i] = hurdle[level][i];
        }
        // current game level is more than normal,
        if (Play.LEVEL_EASY < level) {
            // Cone     evade, jump
            int cone[][] = {
                    {50, 5},
                    {60, 10},
                    {80, 15},
            };
            for (int i = 0; i < this.mLikelihood[0].length; i++) {
                this.mLikelihood[1][i] = cone[level][i];
            }
        }

        // using image
        this.mBmp = this.mImage.LoadImage(this.mContext, "roadcompetitor");
        // SE
        this.mSe = new Sound(this.mContext);
        this.mSe.CreateSound("jump");

        // Fixed time to attack
        this.mAttackFixedTime = ACTION_ATTACK_INTERVAL_TIME[level];
        // the distance that trace to player.
        this.mTraceDistance = TRACE_DISTANCE[level];

        // image setting
        // get player info
        float playerW = RoadPlayer.GetWholeSize().x;
        Point playerPos = RoadPlayer.GetPosition();
        this.mPos.y = playerPos.y;
        // position-X
        float pos[] = {
                playerPos.x+playerW+10,
                playerPos.x-playerW-10,
                playerPos.x+(playerW+10)*2,
                playerPos.x-(playerW+10)*2,
        };
        this.mPos.x = (int)pos[count];
        // exist flag
        this.mExistFlag = true;
        // Variable scale rate
        this.mScale = (float)this.VariableScaleRateBasedOnCameraAngle(1.0f,2.0f);
        // Set whole size to get that values.
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);

        // Set effect
        // kind of effect
        int effectKind[] = {
                CharacterEffect.EFFECT_CIRCLE,
                CharacterEffect.EFFECT_BALL,
                CharacterEffect.EFFECT_BALL,
                CharacterEffect.EFFECT_BUMP,
        };
        for (int i = 0; i < effectKind.length; i++) {
            this.mEffect[i].InitCharacterEffect(effectKind[i]);
        }
    }
    /*
        Initialize to explain the stage
    */
    public void InitToExplain(int count) {
        // using image
        this.mBmp = this.mImage.LoadImage(this.mContext, "roadcompetitor");

        // image setting
        // get player info
        float playerW = RoadPlayer.GetWholeSize().x;
        Point playerPos = RoadPlayer.GetPosition();
        this.mPos.y = playerPos.y;
        // position-X
        float pos[] = {
                playerPos.x+playerW+10,
                playerPos.x-playerW-10,
                playerPos.x+(playerW+10)*2,
                playerPos.x-(playerW+10)*2,
        };
        this.mPos.x = (int)pos[count];
        // exist flag
        this.mExistFlag = false;
        // Set whole size to get that values.
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);
    }

    /*
        Update
    */
    @Override
    public boolean UpdateRunner(RoadObstacles obstacles) {

        // update safety area
        SetSafetyArea(this.mRect);

        RoadObstacles.BUMP_TYPE bumpType = RoadObstacles.BUMP_TYPE.NOT_BUMP;
        // collision with obstacle and get bump type to do an action.
        if (this.mExistFlag) bumpType = obstacles.CollisionObstacles(this, this.mSafetyArea, this.mActionType, this.mEffectType);

        // to attempt to find the obstacle
        int obstacleType = obstacles.IsBroadenedCollisionArea(this, this.mDetectArea);
        // cancel attack action by the type that find the obstacle.
        this.CancelAttackAction(obstacleType);

        // Initialize action to the obstacle
        // when not to do action that run action
        if (this.mActionType == ACTION_RUN) {
            // if found the obstacle
            if (obstacleType != -1) {
                // decide action type
                this.InitActionToObstacle(obstacleType);
                // diverge action setting from the action type
                switch (this.mActionType) {
                    case ACTION_JUMP:
                        // set animation setting
                        this.InitJumpAnimation();
                        break;
                    case ACTION_LINE_MOVE:
                        this.InitLineMoveAction();
                        break;
                    default:
                }
                // if not find obstacle,
            } else {
                // count interval by player's aggregate speed.
                this.mAttackIntervalCount += Math.abs(RoadPlayer.GetAggregateSpeed());
                // when count is more than fixed time, to initialize attack action to player.
                if (this.mAttackFixedTime < this.mAttackIntervalCount) {
                    // reset count
                    this.mAttackIntervalCount = 0;
                    this.InitActionToPlayer();          // Initialize the action
                }
            }
        } else {        // while doing the action
            // update action
            switch(this.mActionType) {
                case ACTION_JUMP:
                    break;
                case ACTION_PREPARE_ATTACK:
                    this.UpdateAttackAction();
                    break;
                case ACTION_LINE_MOVE:
                    this.UpdateEvadeAction();
                    break;
                default:
            }
        }

        // Update animation
        this.UpdateRunnerAnimation();

        // Finish action
        this.FinishActionType();

        // Initialize the effect
        if (bumpType != RoadObstacles.BUMP_TYPE.NOT_BUMP) this.InitRunnerEffect(bumpType);
        // Update effect
        this.UpdateRunnerEffect();

        // Update avoid time
        this.AvoidObject(this, 200);

        // update variable scale rate
        this.mScale = (float)this.VariableScaleRateBasedOnCameraAngle(1.0f,1.8f);
        // Update whole size
        this.SetWholeSize(this.mSize.x, this.mSize.y, this.mScale);

        // update coordinate-Y based on aggregate seed.
        this.mAggregateSpeed = this.mSpeed+this.mEffectSpeed;
        this.mPos.y += this.mMoveY;
        // add move to player's position
        this.mPos.x += this.mMoveX;
        this.mPos.y -= this.mAggregateSpeed;

        // constrain available to move area
        this.ConstrainMove(SCREEN_MARGIN);

        // Update priority to draw.
        // player's position
        int playerPosY = RoadPlayer.GetPosition().y;
        if (playerPosY < this.mPos.y) {
            // to draw in toward to player
            this.mPriority = BaseCharacter.PRIORITY_FORWARD;
        } else {
            // to draw in backward to player
            this.mPriority = BaseCharacter.PRIORITY_BACKWARD;
        }

        // Update animation speed
        if (this.mAni.mType == ANIMATION_RUN) {
            this.mAni.mFrame = this.VariableAnimationFrame(this.mAggregateSpeed+this.mMoveY);
        }

        // Update area that not to overlap
        this.UpdateOverlapArea(OVERLAP_AREA);
        return false;       // false is player is existing.
    }
    /*
        Update to explain the stage
    */
    public void UpdateToExplain() {
        if (this.mExistFlag) {
            // Update animation
            this.UpdateRunnerAnimation();
        }
    }

    /*
        Draw
    */
    public void DrawRoadCompetitor() {
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
        // Effect
        for (CharacterEffect effect: this.mEffect) {
            effect.DrawCharacterEffect();
        }
    }
    /*
        Draw to explain the stage
    */
    public void DrawToExplain() {
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
    public void ReleaseRoadCompetitor() {
        this.ReleaseRunner();
    }

    /*
        Finish action type
    */
    private void FinishActionType() {
        // when the flag that exist is false, reset action
        if (!this.mExistFlag) {
            this.mActionType &= ~this.mActionType;
            this.mMoveX = 0.0f;
            this.mMoveY = 0.0f;
        }
        // when current action type is prepare attack, not to reset action type
        if (this.mActionType == ACTION_PREPARE_ATTACK) return;
        // when current animation is running, reset the action
        if (this.mAni.mType == ANIMATION_RUN) {
            this.mActionType &= ~this.mActionType;
        }
        // when current action is running more than fixed time, to reset action type
        // rethink the action.
        if (this.mAni.mType == ANIMATION_RUN) {
            this.mFinishActionCount++;
            if (FINISH_ACTION_INTERVAL_TIME_ANYWAY < this.mFinishActionCount) {
                this.mFinishActionCount = 0;
                this.mActionType &= ~this.mActionType;
                this.mMoveX = 0.0f;
                this.mMoveY = 0.0f;
            }
        }
    }


    /*
        Initialize action to an obstacle
    */
    private void InitActionToObstacle(int obstacleType) {

        // loop to kind of obstacle
        for (int i = 0; i < RoadObstacles.OBSTACLE_TYPE_BOX.length; i++) {
            // diverge action from the obstacle
            if (this.mActionType == ACTION_RUN &&
                obstacleType == RoadObstacles.OBSTACLE_TYPE_BOX[i]) {
                // loop to kind of action
                for (int j = 0; j < ACTION_BOX[i].length; j++) {
                    // decide to do action by random.
                    if (MyRandom.GetLikelihood(this.mLikelihood[i][j])) {
                        this.mActionType |= ACTION_BOX[i][j];
                        return;
                    }
                }
            }
        }
    }
    /*
        Initialize the action to player
    */
    private void InitActionToPlayer() {
        // set action
        this.mActionType |= ACTION_PREPARE_ATTACK;
        // get camera's position
        Point cameraPos = StageCamera.GetCameraPosition();
        // calculate center position in local.
        Point center = new Point(
                this.mPos.x + ((int)this.mWholeSize.x>>1),
                (this.mPos.y - cameraPos.y) + ((int)this.mWholeSize.y>>1)
        );
        // get player's center position in local
        this.mPreviewPos.x = (RoadPlayer.GetPosition().x-cameraPos.x)+((int)RoadPlayer.GetWholeSize().x>>1);
        this.mPreviewPos.y = (RoadPlayer.GetPosition().y-cameraPos.y)+((int)RoadPlayer.GetWholeSize().y>>1);

        // reset count
        this.mDistanceCount = 0;

        // calculate triangle area.
        // bottom
        float bottom = this.mPreviewPos.x - center.x;
        // height
        float height = this.mPreviewPos.y - center.y;
        // oblique side.
        double oblique = Math.sqrt((double) (bottom * bottom) +
                (double) (height * height));
        // calculate move
        this.mMoveX = (bottom / (float) (oblique)) * (Math.abs(this.mSpeed)+this.mEffectSpeed);
        this.mMoveY = (height / (float) (oblique)) * (Math.abs(this.mSpeed)+this.mEffectSpeed);
    }
    /*********************************************************************************
        Each action settings
     *********************************************************************************/
    /*
        Evade action
     */
    private void InitLineMoveAction() {
        // reset count
        this.mDistanceCount = 0;
        // select root
        int root = MyRandom.GetRandom(2);
        Point screen = GameView.GetScreenSize();
        // when current position is left tip, move to right side
        if (this.mPos.x < this.mWholeSize.x) {
            root = 0;
        // when right side, move to left side
        } else if (this.mPos.x < screen.x &&
                screen.x-(int)this.mWholeSize.x < this.mPos.x) {
            root = 1;
        }
        // distance that move to the position
        int distance[] = {(int)this.mWholeSize.x<<1, (int)this.mWholeSize.x<<2};
        // whole distance that move to the position this time.
        this.mSelectedDistance = Math.abs(distance[MyRandom.GetRandom(distance.length)]);
        // substitute move to variable
        float move[] = {3.0f,-3.0f};
        this.mMoveX = move[root];
    }

    /*********************************************************************************
     Each action updates
     *********************************************************************************/
    /*
        Update attack
    */
    private void UpdateAttackAction() {
        // get camera's position
        Point cameraPos = StageCamera.GetCameraPosition();
        // calculate center position in local.
        Point center = new Point(
                this.mPos.x + ((int)this.mWholeSize.x>>1),
                (this.mPos.y - cameraPos.y) + ((int)this.mWholeSize.y>>1)
        );
        // when current position nears player's position, to do animation
        Point difference = new Point(
                Math.abs(center.x-this.mPreviewPos.x),
                Math.abs(center.y-this.mPreviewPos.y));

        // when competitor through player, not to move to forward or backward.
        if (difference.y < (int)this.mWholeSize.y>>3) this.mMoveY = 0.0f;

        // check difference to position
        if (difference.x < (int)this.mWholeSize.x && difference.y < (int)this.mWholeSize.y>>3) {
            // select direction to attack
            // get player's center position in local
            Point playerPos = new Point((RoadPlayer.GetPosition().x-cameraPos.x)+((int)RoadPlayer.GetWholeSize().x>>1),
            (RoadPlayer.GetPosition().y-cameraPos.y)+((int)RoadPlayer.GetWholeSize().y>>1));
            // player's position is right side from competitor.
            if (center.x < playerPos.x) {
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
        }

        // count distance
        this.mDistanceCount += Math.abs(this.mMoveY);
        // when counted distance is over fixed count, to reset the action.
        if (this.mTraceDistance < this.mDistanceCount) {
            this.mActionType &= ~ACTION_PREPARE_ATTACK;
            this.mMoveX = 0.0f;
            this.mMoveY = 0.0f;
        }
    }
    /*
        Cancel attack action
    */
    private void CancelAttackAction(int obstacleType) {
        // if find the obstacle, to reset the attack action anyway.
        if (obstacleType != -1) {
            this.mActionType &= ~ACTION_PREPARE_ATTACK;
            this.mMoveX = 0.0f;
            this.mMoveY = 0.0f;
            this.mFinishActionCount = 0;
        }
    }

    /*
        Update evade action
    */
    private void UpdateEvadeAction() {
        // when reach to selected position-X, not to move.
        if (this.mSelectedDistance <= this.mDistanceCount) {
            // reset move
            this.mMoveX = 0.0f;
            // reset action
            this.mActionType &= ~ACTION_LINE_MOVE;
        } else { // update move
            // count distance
            this.mDistanceCount += Math.abs(this.mMoveX);
        }
        Point screen = GameView.GetScreenSize();
        // if current position is left-tip or right-tip, reverse to move.
        if (this.mPos.x < this.mWholeSize.x) {
            this.mMoveX = 3;
            // when right side, move to left side
        } else if (this.mPos.x < screen.x &&
                screen.x-(int)this.mWholeSize.x < this.mPos.x) {
            this.mMoveX = -3;
        }
    }

    /*
        Check overlap with player
    */
    public byte NotOverlapWithPlayer(BaseCharacter player, Rect safetyArea) {
        return Collision.NotOverlapCharacter(player,this,safetyArea);
    }

    /**********************************************************************************************
     Setter functions
     *********************************************************************************************/
    /*
        Whole size
     */
    private void SetWholeSize(int w, int h, float scale) {
        this.mWholeSize.x = w*scale;
        this.mWholeSize.y = h*scale;
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
        this.mSafetyArea.left = (int)Safety[0];
        this.mSafetyArea.top = (int)Safety[1];
        this.mSafetyArea.right = (int)Safety[2];
        this.mSafetyArea.bottom = (int)Safety[3];
    }
    /*
        Set the flag that exist own presence.
    */
    public void SetExist(boolean exist) { this.mExistFlag = exist; }

    /**********************************************************************************************
        Getter functions
    **********************************************************************************************/
    /*
        get aggregate speed
     */
    public float GetAggregateSpeed() { return this.mAggregateSpeed; }
    /*
        get position
    */
    public Point GetPosition() { return this.mPos; }
    /*
        Get safety area
     */
    public Rect GetSafetyArea() { return this.mSafetyArea; }
    /*
        Get whole size
     */
    public FPoint GetWholeSize() { return this.mWholeSize; }
    /*
        Get current animation count
     */
    public int GetAnimationCount() { return this.mAni.mCount; }
    /*
        Get current action type
    */
    public byte GetCurrentActionType() { return this.mActionType; }
    /*
        Get current effect type
    */
    public byte GetCurrentEffectType() { return this.mEffectType; }
}