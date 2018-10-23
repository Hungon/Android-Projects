package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;

/**
 * Created by USER on 1/30/2016.
 */
public class OffroadObstacles {
    // static variables
    // Obstacle max
    private final static int[] OBSTACLE_MAX = {8, 12, 16};
    private final static int[][] OBSTACLE_CREATION_INTERVAL = {
            {300, 250, 200},            // rock
            {400, 350, 300},            // jump point
            {0, 400, 350},              // bog
    };
    // kind
    private final static int[] OBSTACLE_APPEAR_KIND = {2,3,3};
    
    // rock
    public final static Point      OBSTACLE_ROCK_SIZE = new Point(64,64);
    // animation
    public final static int        OBSTACLE_ROCK_ANIMATION_COUNT_MAX = 4;
    public final static int        OBSTACLE_ROCK_ANIMATION_FRAME     = 10;
    private final static int[]     OBSTACLE_ROCK_MAX = {5,6,7};
    private final static float[]   OBSTACLE_ROCK_SPEED = {1.0f,1.5f,2.0f};

    // jump point
    public final static Point      OBSTACLE_JUMP_POINT_SIZE = new Point(110, 64);
    private final static int[]     OBSTACLE_JUMP_POINT_MAX = {1,1,1};
    
    // Bog
    public final static Point      OBSTACLE_BOG_SIZE = new Point(100,100);
    private final static int[]     OBSTACLE_BOG_MAX = {0,3,4};

    // kind of type
    public final static int        OBSTACLE_ROCK = 0;
    public final static int        OBSTACLE_JUMP = 1;
    public final static int        OBSTACLE_BOG  = 2;
    public final static int        OBSTACLE_KIND = 3;
    public final static int[]      OBSTACLE_KIND_BOX = {OBSTACLE_ROCK, OBSTACLE_JUMP, OBSTACLE_BOG};

    // filed
    private Context         mContext;
    private Image           mImage;
    private BaseCharacter   mObstacles[];
    private Animation       mAni[];
    private int             mObstacleMax;      // create to limit
    private int             mObstaclesCount[];
    private int             mCreatedCount[];
    private int             mCreateInterval[];
    private int             mCreateTime[];
    private Point           mJumpPosition;

    /*
        Constructor
     */
    public OffroadObstacles(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        int level = Play.GetGameLevel();
        // allot memory
        this.mObstacles = new BaseCharacter[OBSTACLE_MAX[level]];
        this.mAni = new Animation[OBSTACLE_MAX[level]];
        // based on kind
        this.mCreatedCount = new int[OBSTACLE_APPEAR_KIND[level]];
        this.mObstaclesCount = new int[OBSTACLE_APPEAR_KIND[level]];
        this.mCreateTime = new int[OBSTACLE_APPEAR_KIND[level]];
        this.mCreateInterval = new int[OBSTACLE_APPEAR_KIND[level]];
        // obstacles drawing setting
        for (int i = 0; i < this.mObstacles.length; i++) {
            this.mObstacles[i] = new BaseCharacter(image);
            this.mAni[i] = new Animation();
        }
        // preview jump position
        this.mJumpPosition = new Point(1,1);
    }
    /*
        Initialize
     */
    public void InitObstacles() {
        
        // get game level
        int level = Play.GetGameLevel();
        // obstacle max
        this.mObstacleMax = OBSTACLE_MAX[level];

        // the count that each obstacle
        for (int i = 0; i < OBSTACLE_APPEAR_KIND[level]; i++) {
            this.mObstaclesCount[i] = 0;       // the count that is appearing
            this.mCreatedCount[i] = 0;         // the count that created
            this.mCreateInterval[i] = OBSTACLE_CREATION_INTERVAL[i][level];
            // to count that create obstacle
            this.mCreateTime[i] = 0;
        }
    }
    /*
        Update
     */
    public void UpdateObstacles() {

        int max = 0;
        // get count obstacle
        for (int appear: this.mObstaclesCount) max += appear;
        // count to create obstacle
        if (max < this.mObstacleMax) {
            for (int i = 0; i < this.mObstaclesCount.length; i++) {
                this.mCreateTime[i]++;
                if (this.mCreateInterval[i] < this.mCreateTime[i]) {
                    this.mCreateTime[i] = 0;

                    // In jump point, when player near by goal line, not to create jump point.
                    int cameraPos = StageCamera.GetCameraPosition().y;
                    if (OBSTACLE_KIND_BOX[i] == OBSTACLE_JUMP) {
                        Point screen = GameView.GetScreenSize();    // get screen size
                        if (cameraPos <= screen.y) continue;
                    }
                    // create obstacle
                    this.CreateObstacles(OBSTACLE_KIND_BOX[i]);
                    // reset count that create obstacle
                    this.mCreatedCount[OBSTACLE_KIND_BOX[i]] = 0;
                }
            }
        }
        // count that created obstacle
        int obstacleCount[] = new int[OBSTACLE_KIND];
        // check overlap between camera's area and obstacle's position
        for (int i = 0; i < this.mObstacleMax; i++) {
            // check exist flag
            if (!this.mObstacles[i].mExistFlag) continue;

            // if object is jump, update preview position
            if (this.mObstacles[i].mType == OBSTACLE_JUMP) {
                // get jump position
                this.mJumpPosition.x = this.mObstacles[i].mPos.x;
                this.mJumpPosition.y = this.mObstacles[i].mPos.y;
            }

            // when obstacle is out of screen
            if (!StageCamera.CollisionCamera(this.mObstacles[i])) {
                // flag to draw
                this.mObstacles[i].mExistFlag = false;

                // if object is jump, reset jump preview position
                if (this.mObstacles[i].mType == OBSTACLE_JUMP) {
                    this.mJumpPosition.x = -1;
                    this.mJumpPosition.y = -1;
                }

                continue;
            }
            // count obstacle
            obstacleCount[this.mObstacles[i].mType]++;

            // diverge action from type.
            switch(this.mObstacles[i].mType) {
                case OBSTACLE_ROCK :
                    this.UpdateRock(i);
                    break;
                case OBSTACLE_JUMP :
                    this.UpdateJump(i);
                    break;
                case OBSTACLE_BOG:          // game level is more than normal
                    UpdateBog(i);
                    break;
            }
        }
        // update count of obstacle
        for (int i = 0; i < this.mObstaclesCount.length; i++) this.mObstaclesCount[i] = obstacleCount[i];
    }
    /*
        Draw
     */
    public void DrawObstacles() {
        // get camera's position
        Point camera = StageCamera.GetCameraPosition();

        // if game level is easy, start index is 1.
        int start = 0;
        if (Play.GetGameLevel() == Play.LEVEL_EASY) start = 1;
        // update element arrangement
        int[] priority = {OBSTACLE_BOG, OBSTACLE_JUMP, OBSTACLE_ROCK};
        int[] replace = this.ReplaceMaterials(priority, start);
        // bog -> jump -> rock
        for (int kind: replace) {
            if (this.mObstacles[kind].mExistFlag &&
                this.mObstacles[kind].mBmp != null) {
                // draw each obstacles
                this.mImage.DrawScale(
                        this.mObstacles[kind].mPos.x,
                        this.mObstacles[kind].mPos.y - camera.y,
                        this.mObstacles[kind].mSize.x,
                        this.mObstacles[kind].mSize.y,
                        this.mObstacles[kind].mOriginPos.x,
                        this.mObstacles[kind].mOriginPos.y,
                        this.mObstacles[kind].mScale,
                        this.mObstacles[kind].mBmp
                );
            }
        }
    }
    /*
        Release
     */
    public void ReleaseObstacles() {
        for (int i = 0; i < this.mObstacles.length; i++) {
            this.mObstacles[i].ReleaseCharaBmp();
            this.mObstacles[i] = null;
            this.mAni = null;
        }
        this.mContext = null;
        this.mImage = null;
        // creation
        this.mObstaclesCount = null;
        this.mCreatedCount = null;
        this.mCreateInterval = null;
        this.mCreateTime = null;
    }

    /*
        Create obstacles
     */
    private void CreateObstacles(int type) {
        // loop to max.
        for (int i = 0; i < this.mObstacleMax; i++) {
            if (this.mObstacles[i].mExistFlag) continue;
            
            // common setting
            this.mObstacles[i].mType = type;
            this.mObstacles[i].mExistFlag = true;
            // origin position
            this.mObstacles[i].mOriginPos.x = 0;
            this.mObstacles[i].mOriginPos.y = 0;

            // safety area that not to collision between obstacle and player.
            this.mObstacles[i].mRect.left      = 0;
            this.mObstacles[i].mRect.top       = 0;
            this.mObstacles[i].mRect.right     = 0;
            this.mObstacles[i].mRect.bottom    = 0;

            int level = Play.GetGameLevel();
            // diverge initialization from type.
            switch(this.mObstacles[i].mType) {
                case OBSTACLE_ROCK :
                    // limit to create
                    if (OBSTACLE_ROCK_MAX[level] <= this.mObstaclesCount[OBSTACLE_ROCK]) return;
                    if (this.InitRock(i)) return;
                    break;
                case OBSTACLE_JUMP :
                    // limit to create
                    if (OBSTACLE_JUMP_POINT_MAX[level] <= this.mObstaclesCount[OBSTACLE_JUMP]) return;
                    if (this.InitJump(i)) return;
                    break;
                case OBSTACLE_BOG:          // game level is more than normal
                    // limit to create
                    if (OBSTACLE_BOG_MAX[level] <= this.mObstaclesCount[OBSTACLE_BOG]) return;
                    if (this.InitBog(i)) return;
                    break;
            }
        }
    }

    /*
        Initialize rock
    */
    private boolean InitRock(int material) {

        // using images for obstacle
        String imageFile = "offroadstone";      // rock
        // load images
        this.mObstacles[material].LoadCharaImage(this.mContext, imageFile);


        // get camera's position
        Point camera = StageCamera.GetCameraPosition();
        // direction
        int direction[] = {-1, 1};
        // position
        Point pos = new Point(MyRandom.GetRandom(9), MyRandom.GetRandom(5));

        // setting
        // size
        this.mObstacles[material].mSize.x = OBSTACLE_ROCK_SIZE.x;
        this.mObstacles[material].mSize.y = OBSTACLE_ROCK_SIZE.y;
        this.mObstacles[material].mScale = 1.0f;
        float w = this.mObstacles[material].mSize.x * (int) this.mObstacles[material].mScale;
        float h = this.mObstacles[material].mSize.y * (int) this.mObstacles[material].mScale;
        // position
        this.mObstacles[material].mPos.x = camera.x + ((int) w * pos.x);
        this.mObstacles[material].mPos.y = camera.y - ((int) h * pos.y);
        // move
        this.mObstacles[material].mMoveX = direction[MyRandom.GetRandom(2)];
        this.mObstacles[material].mMoveY = 1.0f;
        // speed
        int level = Play.GetGameLevel();
        this.mObstacles[material].mSpeed = OBSTACLE_ROCK_SPEED[level];
        // safety area
        this.mObstacles[material].mRect.left      = 4;
        this.mObstacles[material].mRect.top       = 4;
        this.mObstacles[material].mRect.right     = 4;
        this.mObstacles[material].mRect.bottom    = 4;

        // animation
        this.mAni[material].SetAnimation(
                0, 0,
                this.mObstacles[material].mSize.x,
                this.mObstacles[material].mSize.y,
                OBSTACLE_ROCK_ANIMATION_COUNT_MAX,
                OBSTACLE_ROCK_ANIMATION_FRAME,
                OBSTACLE_ROCK
        );

        // count
        this.mCreatedCount[OBSTACLE_ROCK]++;
        // check count
        return this.CheckCreationCount(OBSTACLE_ROCK);
    }

    /*
        Update rock
     */
    private void UpdateRock(int material) {
        // update animation
        this.mAni[material].UpdateAnimation(this.mObstacles[material].mOriginPos, false);
        // add move
        this.mObstacles[material].mPos.x += this.mObstacles[material].mMoveX*this.mObstacles[material].mSpeed;
        this.mObstacles[material].mPos.y += this.mObstacles[material].mMoveY*this.mObstacles[material].mSpeed;
    }


    /*
        Initialize jump point
    */
    private boolean InitJump(int material) {

        // using images for obstacle
        String imageFile = "offroadjump";      // jump point
        // load images
        this.mObstacles[material].LoadCharaImage(this.mContext, imageFile);

        // get camera's position
        Point camera = StageCamera.GetCameraPosition();

        // setting
        // size
        this.mObstacles[material].mSize.x   = OBSTACLE_JUMP_POINT_SIZE.x;
        this.mObstacles[material].mSize.y   = OBSTACLE_JUMP_POINT_SIZE.y;
        this.mObstacles[material].mScale  = 1.0f;
        float w = this.mObstacles[material].mSize.x*(int)this.mObstacles[material].mScale+10;
        float h = this.mObstacles[material].mSize.y*(int)this.mObstacles[material].mScale;
        // position
        this.mObstacles[material].mPos.x  = ((int)w*MyRandom.GetRandom(4));
        this.mObstacles[material].mPos.y  = (camera.y-(int)h) - ((int)h*MyRandom.GetRandom(4));

        // safety area
        this.mObstacles[material].mRect.right     = 7;
        this.mObstacles[material].mRect.left      = 7;
        this.mObstacles[material].mRect.top       = 5;
        this.mObstacles[material].mRect.bottom    = 55;

        // count
        this.mCreatedCount[OBSTACLE_JUMP]++;
        // check count
        return this.CheckCreationCount(OBSTACLE_JUMP);
    }
    /*
        Update jump point
     */
    private void UpdateJump(int material) {
    }

    /*
        Initialize bog point
    */
    private boolean InitBog(int material) {

        // using images for obstacle
        String imageFile = "offroadbog";      // bog point
        // load images
        this.mObstacles[material].LoadCharaImage(this.mContext, imageFile);

        // get camera's position
        Point camera = StageCamera.GetCameraPosition();

        // setting
        // size
        this.mObstacles[material].mSize.x   = OBSTACLE_BOG_SIZE.x;
        this.mObstacles[material].mSize.y   = OBSTACLE_BOG_SIZE.y;
        this.mObstacles[material].mScale  = 1.0f;
        float w = this.mObstacles[material].mSize.x*(int)this.mObstacles[material].mScale+36;
        float h = this.mObstacles[material].mSize.y*(int)this.mObstacles[material].mScale;
        // position
        this.mObstacles[material].mPos.x  = ((int)w*MyRandom.GetRandom(4));
        this.mObstacles[material].mPos.y  = (camera.y-(int)h) - ((int)h*MyRandom.GetRandom(3));

        // check overlap between preview position and current position
        BaseCharacter jump = new BaseCharacter();
        // jump setting
        jump.mSize.x = OBSTACLE_JUMP_POINT_SIZE.x;
        jump.mSize.y = OBSTACLE_JUMP_POINT_SIZE.y;
        jump.mPos.x = this.mJumpPosition.x;
        jump.mPos.y = this.mJumpPosition.y;
        // check overlap
        Point preview = this.mObstacles[material].mPos = this.CheckOverlapPosition(this.mObstacles[material],jump);
        // if return value is difference, set bog's position to backward than jump.
        if (preview.x != this.mObstacles[material].mPos.x) {
            preview.y = (jump.mPos.y-(int)h*2)*((int)h*MyRandom.GetRandom(3));
        }
        // set position
        this.mObstacles[material].mPos = preview;

        // safety area
        this.mObstacles[material].mRect.left      = 20;
        this.mObstacles[material].mRect.top       = 10;
        this.mObstacles[material].mRect.right     = 30;
        this.mObstacles[material].mRect.bottom    = 15;


        // count
        this.mCreatedCount[OBSTACLE_BOG]++;
        // check count
        return this.CheckCreationCount(OBSTACLE_BOG);
    }
    /*
        Update bog point
     */
    private void UpdateBog(int material) {
    }


    /*
        Check overlap between player and obstacles.
     */
    public int CollisionObstacles(BaseCharacter ch) {

        // update element arrangement
        int[] obstacle = {OBSTACLE_JUMP, OBSTACLE_ROCK, OBSTACLE_BOG};
        // game level
        int level = Play.GetGameLevel();
        int[] priority = new int[OBSTACLE_APPEAR_KIND[level]];
        for (int i = 0; i < priority.length; i++) priority[i] = obstacle[i];

        // replace each elements
        int[] replace = this.ReplaceMaterials(priority,0);
        // jump -> rock -> bog
        // loop to max value
        for (int kind: replace) {
            if (this.mObstacles[kind].mExistFlag) {
                if (Collision.CollisionCharacter(this.mObstacles[kind], ch, this.mObstacles[kind].mRect, ch.mRect)) {
                    return this.mObstacles[kind].mType;
                }
            }
        }
        return -1;
    }

    /*
        Check overlap argument and that object own preview position.
     */
    private Point CheckOverlapPosition(int currentX, int currentY, int w, int h, int previewX, int previewY) {
        // return value
        Point setPos = new Point(0,0);
        setPos.x = currentX;
        setPos.y = currentY;
        Point screen = GameView.GetScreenSize();    // get screen size
        if (previewX == currentX) {
            // when left side
            if (previewX <= (screen.x>>1)) {
                setPos.x = (screen.x>>1)+(w*MyRandom.GetRandom(2));
                return setPos;
                // right side
            } else if ((screen.x>>1) <= previewX) {
                setPos.x = w*MyRandom.GetRandom(2);
                return setPos;
            }
        }
        return setPos;
    }

    /*
        Check overlap argument and that object own preview position.
     */
    private Point CheckOverlapPosition(BaseCharacter ch1, BaseCharacter ch2) {
        Point pos = new Point(0,0);
        pos.y = ch1.mPos.y;
        pos.x = ch1.mPos.x;
        Point screen = GameView.GetScreenSize();    // get screen size
        // check overlap between ch1 and ch2.
        if (Collision.CollisionCharacter(ch1, ch2)) {
            float ch1W = ch1.mSize.x*ch1.mScale;
            float ch2W = ch2.mSize.x*ch2.mScale;
            // ch1's position is left side, to move to right side
            if (ch1.mPos.x < screen.x>>1) {
                pos.x = (int)((ch2.mPos.x+ch2W)+(ch1W*MyRandom.GetRandom(2)));
            // move to left side
            } else if (screen.x>>1 <= ch1.mPos.x) {
                pos.x = (int)((ch2.mPos.x-ch1W)-(ch1W*MyRandom.GetRandom(2)));
            }
        }
        return pos;
    }

    /*
        Check count
    */
    private boolean CheckCreationCount(int type) {
        // game level
        int level = Play.GetGameLevel();
        if (type == OBSTACLE_ROCK) {
            if (this.mCreatedCount[type] >= OBSTACLE_ROCK_MAX[level]) return true;
        } else if (type == OBSTACLE_JUMP) {
            if (this.mCreatedCount[type] >= OBSTACLE_JUMP_POINT_MAX[level]) return true;
        } else if (type == OBSTACLE_BOG) {
            if (this.mCreatedCount[type] >= OBSTACLE_BOG_MAX[level]) return true;
        }
        return false;
    }

    /*
        Replace each material to show bog to jump to rock.
     */
    private int[] ReplaceMaterials(int[] priority, int start) {
        int max = 0;
        // get count obstacle
        for (int appear: this.mObstaclesCount) max += appear;
        int[] replace = new int[max];
        int cnt = 0;
        // loop to kind, to check bog -> jump -> rock
        for (int j = start; j < priority.length; j++) {
            // loop to obstacle max
            for (int i = 0; i < this.mObstacles.length; i++) {
                if (this.mObstacles[i].mExistFlag) {
                    if (this.mObstacles[i].mType == priority[j]) {
                        replace[cnt] = i;
                        cnt++;
                    }
                }
            }
        }
        return replace;
    }
}