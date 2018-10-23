package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by USER on 2/24/2016.
 */
public class RoadObstacles {

    // static variables
    // Kind of obstacle
    public final static int OBSTACLE_TYPE_HURDLE    = 0;
    public final static int OBSTACLE_TYPE_CONE      = 1;

    public final static int[] OBSTACLE_TYPE_BOX = {OBSTACLE_TYPE_HURDLE,OBSTACLE_TYPE_CONE};

    // Bump type
    public enum BUMP_TYPE {
        NOT_BUMP,
        ORDINARY_BUMP,
        HURDLE_RED,
        HURDLE_BLUE,
        HURDLE_GREEN
    }

    // Hurdle
    public final static Point HURDLE_SIZE = new Point(48, 24);
    public final static int HURDLE_SPACE_X = 12;
    public final static int HURDLE_STARTING_POSITION_X = 96;

    // Arrangement type
    private final static int HURDLE_ARRANGEMENT_TYPE_EMPTY = -1;
    private final static int HURDLE_ARRANGEMENT_TYPE_STRAIGHT = 0;
    private final static int HURDLE_ARRANGEMENT_TYPE_RANDOM = 1;

    // Each Hurdle position
    private final static int[] HURDLE_POSITION_BOX = {
            HURDLE_STARTING_POSITION_X,
            HURDLE_STARTING_POSITION_X + (HURDLE_SIZE.x + HURDLE_SPACE_X),
            HURDLE_STARTING_POSITION_X + (HURDLE_SIZE.x + HURDLE_SPACE_X) * 2,
            HURDLE_STARTING_POSITION_X + (HURDLE_SIZE.x + HURDLE_SPACE_X) * 3,
            HURDLE_STARTING_POSITION_X + (HURDLE_SIZE.x + HURDLE_SPACE_X) * 4,
    };
    // placement for random arrangement
    private final static int[][] HURDLE_PLACEMENT = {
            {0, 2, 4},         // left tip, center and right tip
            {1, 2, 3},
            {1, 2, 3, 4},
            {0, 1, 2, 3},
            {0, 1, 3, 4},
    };
    // animation setting
    private final static int HURDLE_ANIMATION_COUNT_MAX = 4;
    private final static int HURDLE_ANIMATION_FRAME = 10;

    // Cone
    public final static Point  CONE_SIZE = new Point(48, 24);
    public final static int    CONE_STARTING_POSITION_X = 96;
    // animation setting
    public final static int    CONE_ANIMATION_COUNT_MAX = 4;
    public final static int    CONE_ANIMATION_FRAME = 5;
    // creation count
    private final static int    CONE_CREATION_COUNT[][] = {
        {1, 2, 3},
        {2, 3, 4},
        {3, 4, 5}
    };

    // Type
    private final static int ANIMATION_EXIST = 0;
    private final static int ANIMATION_BREAK = 1;

    // Obstacle scale setting
    private final static float OBSTACLE_SCALE_MAX = 1.5f;

    // kind of type
    public final static int HURDLE_TYPE_BLACK = 0;
    public final static int HURDLE_TYPE_RED = 1;
    public final static int HURDLE_TYPE_BLUE = 2;
    public final static int HURDLE_TYPE_GREEN = 3;
    public final static int HURDLE_COLOR_KIND = 4;

    // The flag reflect effect to player
    private final static int REFLECT_EFFECT_TO_EMPTY = -1;
    private final static int REFLECT_EFFECT_TO_PLAYER = 0;

    // filed
    private Context mContext;
    private Image mImage;
    private BaseCharacter mObstacles[];
    private Animation mAni[];
    private int mCreationMax;
    // for creation
    private Creation mCreation[];
    // arrangement type
    private int mArrangementType;
    // placement type
    private int mPlacementType;
    // the flag that reflect effect to player.
    private int mReflectEffect[];
    // kind of obstacle to get.
    private static int  mKind;

    /*
        Constructor
    */
    public RoadObstacles(Context context, Image image) {
        // get game level
        int level = Play.GetGameLevel();
        // kind of obstacle
        int kind[] = {1,2,2};
        // substitute kind to static variable
        mKind = kind[level];
        // obstacle max
        int max[] = {8,9,11};
        this.mContext = context;
        this.mImage = image;
        // obstacle max
        this.mCreationMax = max[level];
        // allot memory
        this.mObstacles = new BaseCharacter[this.mCreationMax];
        this.mAni = new Animation[this.mCreationMax];
        for (int i = 0; i < this.mCreationMax; i++) {
            this.mObstacles[i] = new BaseCharacter(image);
            this.mAni[i] = new Animation();
        }
        // the flag that reflect effect to player
        this.mReflectEffect = new int[this.mCreationMax];

        // For creation obstacle
        this.mCreation = new Creation[mKind];
        for (int i = 0; i < this.mCreation.length; i++) {
            this.mCreation[i] = new Creation();
        }
    }

    /*
        Initialize
     */
    public void InitObstacles() {
        // get game level
        int level = Play.GetGameLevel();
        // the interval time that create the obstacle.
        int inter[][] = {
            {400,350,300},              // hurdle
            {420,370,320},              // cone
        };
        // reset creation setting and setting.
        for (int i = 0; i < this.mCreation.length; i++) {
            this.mCreation[i].ResetCreation();
            // interval creation based on player's move.
            this.mCreation[i].mFixedInterval = inter[i][level];
        }
        // arrangement type
        this.mArrangementType = HURDLE_ARRANGEMENT_TYPE_EMPTY;
        // the flag the reflect effect to player
        for (int i = 0; i < this.mReflectEffect.length; i++) {
            this.mReflectEffect[i] = REFLECT_EFFECT_TO_EMPTY;
        }
    }

    /*
        Update
    */
    public void UpdateObstacle() {
        int max = 0;
        // get count obstacle
        for (Creation creation :this.mCreation) max += creation.mExistCount;
        int cameraY = StageCamera.GetCameraPosition().y;
        if (GameView.GetScreenSize().y < cameraY) {
            // count to create obstacle
            if (max < this.mCreationMax) {
                // get player's move
                float playerSpeed = RoadPlayer.GetAggregateSpeed();
                for (int i = 0; i < this.mCreation.length; i++) {
                    // add player's move to interval time to creation
                    this.mCreation[i].mIntervalCount += Math.abs(playerSpeed);
                    if (this.mCreation[i].CreationInterval()) {
                        this.CreateObstacles(OBSTACLE_TYPE_BOX[i]);
                    }
                }
            }
        }
        // count that created obstacle
        int obstacleCount[] = new int[this.mCreation.length];
        // check overlap between camera's area and obstacle's position
        for (int i = 0; i < this.mCreationMax; i++) {
            // check exist flag
            if (!this.mObstacles[i].mExistFlag) continue;

            // Update priority to draw.
            // player's position
            int playerPosY = RoadPlayer.GetPosition().y;
            // player's safety area
            double safetyTop = RoadPlayer.GetSafetyArea().top * 0.9;
            if (playerPosY + safetyTop < this.mObstacles[i].mPos.y) {
                // to draw in toward to player
                this.mObstacles[i].mPriority = BaseCharacter.PRIORITY_FORWARD;
            } else {
                // to draw in backward to player
                this.mObstacles[i].mPriority = BaseCharacter.PRIORITY_BACKWARD;
            }

            // when obstacle is out of screen
            if (!StageCamera.CollisionCamera(this.mObstacles[i])) {
                // flag to draw
                this.mObstacles[i].mExistFlag = false;
                continue;
            }
            // count obstacle
            obstacleCount[this.mObstacles[i].mType]++;

            // diverge action from type.
            switch (this.mObstacles[i].mType) {
                case OBSTACLE_TYPE_HURDLE:
                    this.UpdateHurdle(i);
                    break;
                case OBSTACLE_TYPE_CONE:
                    this.UpdateCone(i);
                    break;
            }
            // update variable scale
            this.mObstacles[i].mScale =
                    (float)this.mObstacles[i].VariableScaleRateBasedOnCameraAngle(1.0f, OBSTACLE_SCALE_MAX);
        }
        // update count of obstacle
        for (int i = 0; i < this.mCreation.length; i++)
            this.mCreation[i].mExistCount = obstacleCount[i];

    }

    /*
        Draw in backward to player
     */
    public void DrawObstaclesBackward() {
        // get camera's position
        Point camera = StageCamera.GetCameraPosition();
        for (BaseCharacter ch : this.mObstacles) {
            if (ch.mExistFlag &&
                    ch.mBmp != null &&
                    ch.mPriority == BaseCharacter.PRIORITY_BACKWARD) {
                // draw each obstacles
                this.mImage.DrawScale(
                        ch.mPos.x,
                        ch.mPos.y - camera.y,
                        ch.mSize.x,
                        ch.mSize.y,
                        ch.mOriginPos.x,
                        ch.mOriginPos.y,
                        ch.mScale,
                        ch.mBmp
                );
            }
        }
    }

    /*
        Draw in forward to player
    */
    public void DrawObstaclesForward() {
        // get camera's position
        Point camera = StageCamera.GetCameraPosition();
        for (BaseCharacter ch : this.mObstacles) {
            if (ch.mExistFlag &&
                    ch.mBmp != null &&
                    ch.mPriority == BaseCharacter.PRIORITY_FORWARD) {
                // draw each obstacles
                this.mImage.DrawScale(
                        ch.mPos.x,
                        ch.mPos.y - camera.y,
                        ch.mSize.x,
                        ch.mSize.y,
                        ch.mOriginPos.x,
                        ch.mOriginPos.y,
                        ch.mScale,
                        ch.mBmp
                );
            }
        }
    }


    /*
        Release
    */
    public void ReleaseObstacle() {
        for (int i = 0; i < this.mObstacles.length; i++) {
            this.mObstacles[i].ReleaseCharaBmp();
            this.mObstacles[i] = null;
            this.mAni = null;
        }
        for (int i = 0; i < this.mCreation.length; i++) {
            this.mCreation[i] = null;
        }
        this.mContext = null;
        this.mImage = null;
    }

    /*
        Create obstacles
    */
    private void CreateObstacles(int type) {
        // loop to max.
        for (int i = 0; i < this.mCreationMax; i++) {
            if (this.mObstacles[i].mExistFlag) continue;

            // common setting
            this.mObstacles[i].mType = type;
            this.mObstacles[i].mExistFlag = true;
            // origin position
            this.mObstacles[i].mOriginPos.x = 0;
            this.mObstacles[i].mOriginPos.y = 0;
            this.mObstacles[i].mMoveX = 0.0f;
            this.mObstacles[i].mMoveY = 0.0f;
            // scale
            this.mObstacles[i].mScale = 1.0f;
            // Reset Animation setting
            this.mAni[i].ResetAnimation();

            // the first, drawing in backward to player.
            this.mObstacles[i].mPriority = BaseCharacter.PRIORITY_BACKWARD;

            // reset the flag that reflect effect to player
            this.mReflectEffect[i] = REFLECT_EFFECT_TO_EMPTY;

            // safety area that not to collision between obstacle and player.
            this.mObstacles[i].mRect.left = 0;
            this.mObstacles[i].mRect.top = 0;
            this.mObstacles[i].mRect.right = 0;
            this.mObstacles[i].mRect.bottom = 0;
            // diverge initialization from type.
            switch (this.mObstacles[i].mType) {
                case OBSTACLE_TYPE_HURDLE:
                    // limit to create
                    if (this.InitHurdle(i)) {
                        // reset arrangement type
                        this.mArrangementType = HURDLE_ARRANGEMENT_TYPE_EMPTY;
                        return;
                    }
                    break;
                case OBSTACLE_TYPE_CONE:
                    if (this.InitCone(i)) {
                        // reset arrangement type
                        this.mArrangementType = HURDLE_ARRANGEMENT_TYPE_EMPTY;
                        return;
                    }
            }
        }
    }
    /*************************************************************************
        Each initialize
    ***********************************************************************/
    /*
        Initialize hurdle
    */
    private boolean InitHurdle(int element) {
        // load file
        this.mObstacles[element].LoadCharaImage(this.mContext, "roadhurdle");

        // set arrangement type when variable is empty.
        if (this.mArrangementType == HURDLE_ARRANGEMENT_TYPE_EMPTY) {
            int type[] = {HURDLE_ARRANGEMENT_TYPE_STRAIGHT, HURDLE_ARRANGEMENT_TYPE_RANDOM};
            // substitute type to variable
            this.mArrangementType = type[MyRandom.GetRandom(type.length)];
            // when type is random line, set creation max that random value.
            if (this.mArrangementType == HURDLE_ARRANGEMENT_TYPE_RANDOM) {
                // set placement type
                this.mPlacementType = MyRandom.GetRandom(HURDLE_PLACEMENT.length);
                this.mCreation[0].mCreatedCountMax = MyRandom.GetRandom(HURDLE_PLACEMENT[this.mPlacementType].length);
                // type is straight line
            } else if (this.mArrangementType == HURDLE_ARRANGEMENT_TYPE_STRAIGHT) {
                this.mCreation[0].mCreatedCountMax = 5;
            }
        }

        // color type
        int color = MyRandom.GetRandom(HURDLE_COLOR_KIND);
        // image setting
        this.mObstacles[element].mSize.x = HURDLE_SIZE.x;
        this.mObstacles[element].mSize.y = HURDLE_SIZE.y;
        this.mObstacles[element].mOriginPos.y = color * this.mObstacles[element].mSize.y;
        // Animation setting
        this.mAni[element].SetAnimation(
                this.mObstacles[element].mOriginPos.x,
                this.mObstacles[element].mOriginPos.y,
                this.mObstacles[element].mSize.x,
                this.mObstacles[element].mSize.y,
                HURDLE_ANIMATION_COUNT_MAX,
                HURDLE_ANIMATION_FRAME,
                ANIMATION_EXIST);

        // diverge arrangement from type.
        switch (this.mArrangementType) {
            case HURDLE_ARRANGEMENT_TYPE_STRAIGHT:
                // straight line
                this.InitHurdleStraightLine(element);
                break;
            case HURDLE_ARRANGEMENT_TYPE_RANDOM:
                // random line
                this.InitHurdleRandomLine(element);
                break;
        }

        // Get starting position-X
        this.mObstacles[element].mPreviewPos.x = this.mObstacles[element].mPos.x;

        // check count
        return this.mCreation[0].CheckCreatedCount(this.mCreation[0].mCreatedCountMax);
    }

    /*
        Initialize arrangement of hurdle to straight line
     */
    private void InitHurdleStraightLine(int element) {
        // position
        this.mObstacles[element].mPos.x = HURDLE_POSITION_BOX[this.mCreation[0].mCreatedCount];
        this.mObstacles[element].mPos.y = StageCamera.GetCameraPosition().y - this.mObstacles[element].mSize.y * 3;
    }

    /*
        Initialize arrangement of hurdle to random line.
     */
    private void InitHurdleRandomLine(int element) {
        // position
        if (this.mCreation[0].mCreatedCount < HURDLE_PLACEMENT[this.mPlacementType].length) {
            this.mObstacles[element].mPos.x = HURDLE_POSITION_BOX[HURDLE_PLACEMENT[this.mPlacementType][this.mCreation[0].mCreatedCount]];
        } else {
            this.mObstacles[element].mPos.x = HURDLE_POSITION_BOX[MyRandom.GetRandom(HURDLE_POSITION_BOX.length)];
        }
        this.mObstacles[element].mPos.y = StageCamera.GetCameraPosition().y - this.mObstacles[element].mSize.y * 3;
    }

    /*
        Initialize cone
    */
    private boolean InitCone(int element) {
        // load file
        this.mObstacles[element].LoadCharaImage(this.mContext, "roadcone");

        // set arrangement type when variable is empty.
        if (this.mArrangementType == HURDLE_ARRANGEMENT_TYPE_EMPTY) {
            int level = 0;
            this.mCreation[1].mCreatedCountMax = CONE_CREATION_COUNT[level][MyRandom.GetRandom(3)];
        }
            // position
        this.mObstacles[element].mPos.x = HURDLE_POSITION_BOX[MyRandom.GetRandom(HURDLE_POSITION_BOX.length)];
        this.mObstacles[element].mPos.y = StageCamera.GetCameraPosition().y - this.mObstacles[element].mSize.y * 3;
        // move setting
        float root[] = {2.0f,-2.0f};
        this.mObstacles[element].mMoveX = root[MyRandom.GetRandom(2)];
        this.mObstacles[element].mMoveY = 2.0f;
        
        // image setting
        this.mObstacles[element].mSize.x = CONE_SIZE.x;
        this.mObstacles[element].mSize.y = CONE_SIZE.y;
        this.mObstacles[element].mOriginPos.y = 0;
        // Animation setting
        this.mAni[element].SetAnimation(
                this.mObstacles[element].mOriginPos.x,
                this.mObstacles[element].mOriginPos.y,
                this.mObstacles[element].mSize.x,
                this.mObstacles[element].mSize.y,
                CONE_ANIMATION_COUNT_MAX,
                CONE_ANIMATION_FRAME,
                ANIMATION_EXIST);

        // check count
        return this.mCreation[1].CheckCreatedCount(this.mCreation[1].mCreatedCountMax);
    }

    /*************************************************************************
     Each Update
     ***********************************************************************/
    /*
        Update hurdle
    */
    private void UpdateHurdle(int element) {
        // local position
        double localY = this.mObstacles[element].mPos.y - StageCamera.GetCameraPosition().y;
        // position rate
        double localRateY = localY / GameView.GetScreenSize().y;
        // variable position
        double variableX = HURDLE_STARTING_POSITION_X * localRateY;
        // Update position-X
        // from left to right
        // first
        if (this.mObstacles[element].mPreviewPos.x == HURDLE_POSITION_BOX[0]) {
            this.mObstacles[element].mPos.x =
                    this.mObstacles[element].mPreviewPos.x - (int) variableX;
        } // second
        else if (this.mObstacles[element].mPreviewPos.x == HURDLE_POSITION_BOX[1]) {
            this.mObstacles[element].mPos.x =
                    this.mObstacles[element].mPreviewPos.x - (int) variableX / 2;
        } // fourth
        else if (this.mObstacles[element].mPreviewPos.x == HURDLE_POSITION_BOX[3]) {
            this.mObstacles[element].mPos.x =
                    this.mObstacles[element].mPreviewPos.x + (int) variableX / 2;
        } // fifth
        else if (this.mObstacles[element].mPreviewPos.x == HURDLE_POSITION_BOX[4]) {
            this.mObstacles[element].mPos.x =
                    this.mObstacles[element].mPreviewPos.x + (int) variableX;
        }
        // Update animation
        if (this.mAni[element].mType == ANIMATION_BREAK) {
            if (!this.mAni[element].UpdateAnimation(this.mObstacles[element].mOriginPos, false)) {
                this.mObstacles[element].mExistFlag = false;
            }
        }
    }
    /*
        Update cone
    */
    private void UpdateCone(int element) {
        // move
        this.mObstacles[element].mPos.x += this.mObstacles[element].mMoveX;
        this.mObstacles[element].mPos.y += this.mObstacles[element].mMoveY;
        // Update animation
        this.mAni[element].UpdateAnimation(this.mObstacles[element].mOriginPos, false);
    }

    /*
        Collision between obstacle and player
    */
    public BUMP_TYPE CollisionObstacles(BaseCharacter ch, Rect safetyArea, byte actionType, byte effectType) {
        // bump type
        BUMP_TYPE bumpType = BUMP_TYPE.NOT_BUMP;
        for (int i = 0; i < this.mCreationMax; i++) {
            if (!this.mObstacles[i].mExistFlag ||
                this.mAni[i].mType == ANIMATION_BREAK)
                continue;
            // check overlap between obstacle and player.
            if (Collision.CollisionCharacter(this.mObstacles[i], ch, this.mObstacles[i].mRect, safetyArea)) {
                bumpType = this.CheckBumpType(i, actionType, effectType);   // return value is bump type.
            }
        }
        return bumpType;
    }

    /*
        Is broadened collision area
    */
    public int IsBroadenedCollisionArea(BaseCharacter searcher, Point detectArea) {
        for (BaseCharacter ob: this.mObstacles) {
            if (!ob.mExistFlag) continue;
            // Check overlap between searcher's detect area and obstacle
            if (Collision.BroadenCollisionAreaToForward(searcher, ob, detectArea)) {
                return ob.mType;
            }
        }
        return -1;
    }

    /*
        Check bump type
    */
    private BUMP_TYPE CheckBumpType(int element, byte actionType, byte effectType) {
        // if obstacle is hurdle
        if (this.mObstacles[element].mType == OBSTACLE_TYPE_HURDLE) {
            int hurdle[] = {
                    HURDLE_TYPE_BLACK, HURDLE_TYPE_RED,
                    HURDLE_TYPE_BLUE, HURDLE_TYPE_GREEN};
            BUMP_TYPE bump[] = {
                    BUMP_TYPE.NOT_BUMP, BUMP_TYPE.HURDLE_RED,
                    BUMP_TYPE.HURDLE_BLUE, BUMP_TYPE.HURDLE_GREEN};
            // In the hurdle
            for (int kind : hurdle) {
                if (this.mObstacles[element].mOriginPos.y == this.mObstacles[element].mSize.y * kind) {
                    // if action type is jump, not to bump the hurdle
                    if (actionType == RoadRunner.ACTION_JUMP) {
                        // set the flag that reflect effect to player
                        if (this.mReflectEffect[element] == REFLECT_EFFECT_TO_EMPTY) {
                            this.mReflectEffect[element] = REFLECT_EFFECT_TO_PLAYER;
                        } else {
                            return BUMP_TYPE.NOT_BUMP;
                        }
                        return bump[kind];
                        // if effect type is absolute or both absolute and speed up, not to bump the hurdle
                    } else if (effectType == RoadRunner.EFFECT_ABSOLUTE ||
                            effectType == RoadRunner.EFFECT_ABSOLUTE_AND_SPEED_UP) {
                        // set animation
                        this.mAni[element].mType = ANIMATION_BREAK;
                        return BUMP_TYPE.NOT_BUMP;
                    } else {
                        return BUMP_TYPE.ORDINARY_BUMP;
                    }
                }
            }
        // In the cone,
        } else if (this.mObstacles[element].mType == OBSTACLE_TYPE_CONE) {
            // if action type is jump, not to bump the hurdle
            if (actionType == RoadRunner.ACTION_JUMP) {
                return BUMP_TYPE.NOT_BUMP;
            // if effect type is absolute or both absolute and speed up, not to bump the cone
            } else if (effectType == RoadRunner.EFFECT_ABSOLUTE ||
                    effectType == RoadRunner.EFFECT_ABSOLUTE_AND_SPEED_UP) {
                // set animation
                this.mAni[element].mType = ANIMATION_BREAK;
                this.mObstacles[element].mExistFlag = false;
                return BUMP_TYPE.NOT_BUMP;
            } else {
                return BUMP_TYPE.ORDINARY_BUMP;
            }
        }
        return BUMP_TYPE.NOT_BUMP;
    }
    /********************************************************
        Each getter functions
     ******************************************************/
    /*
        Get current kind
     */
    public static int GetKind() { return mKind; }
}