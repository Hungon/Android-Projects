package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by USER on 3/17/2016.
 */
public class SeaEnemyManager {
    // static variables
    // each enemies' setting
    // jellyfish
    private final static Point      JELLYFISH_SIZE  = new Point(32,32);
    // shark
    public final static Point      SHARK_SIZE      = new Point(85,42);
    // shoal
    private final static Point      SHOAL_SIZE      = new Point(85,64);
    // sunfish
    public final static Point      SUNFISH_SIZE    = new Point(96,126);
    // ray
    private final static Point      RAY_SIZE        = new Point(160,96);
    // the safety area
    private final static Rect EACH_SAFETY[] = {
            new Rect(3,3,3,3),          // jellyfish
            new Rect(3,5,3,5),          // shark
            new Rect(7,7,7,7),          // shoal
            new Rect(12,13,13,12),      // sunfish
            new Rect(15,15,15,15),      // ray
    };
    // each creation max
    private final static int EACH_CREATION_MAX[][] = {
            {4, 3, 2, 1, 0},        // easy
            {5, 4, 3, 2, 1},        // normal
            {6, 5, 4, 3, 2}         // hard
    };
    // the interval time that create the enemy
    private final  static int  CREATION_INTERVAL_TIME[] = {200,180,160};
    // speed
    private final static float EACH_SPEED[] = {1.0f,2.5f,2.0f,2.0f,2.5f};
    // limit creation max
    private final static int   LIMIT_CREATION_MAX[] = {7,10,13};

    // Each sizes
    private final static Point  EACH_SIZES[]  = {
        JELLYFISH_SIZE,SHARK_SIZE,SHOAL_SIZE,SUNFISH_SIZE,RAY_SIZE
    };
    // type of enemy
    private final static int        TYPE_JELLYFISH  = 0;
    private final static int        TYPE_SHARK      = 1;
    private final static int        TYPE_SHOAL      = 2;
    private final static int        TYPE_SUNFISH    = 3;
    private final static int        TYPE_RAY        = 4;
    private final static int        TYPE_MAX        = 5;
    // the kind that create the enemy
    private final static int        CREATION_KIND[] = {4,5,5};
    // Each files that enemy's
    private final static String[] EACH_FILES = {
            "jellyfish",
            "shark",
            "shoal",
            "sunfish",
            "ray"
    };
    // each likelihood to create the item
    private final static int[][]      ENEMY_ITEM  = {
            {10,10,60,10},      // jellyfish
            {10,90,10,10},      // shark
            {30,30,30,20},      // shoal
            {90,10,10,30},      // sunfish
            {10,10,10,90}       // ray
    };
    // except for shoal, animation setting
    public final static int        ANIMATION_COMMON_FRAME      = 15;
    public final static int        ANIMATION_COMMON_COUNT_MAX  = 3;
    // filed
    private SeaEnemy        mEnemy[];
    private Creation        mCreation;
    private int[]           mEachCreationMax;
    private int             mCreationKind;
    /*
            Constructor
    */
    public SeaEnemyManager(Context context, Image image) {
        // game level
        int level = Play.GetGameLevel();
        int max = LIMIT_CREATION_MAX[level];
        // kind
        this.mCreationKind = CREATION_KIND[level];
        // each creation max
        this.mEachCreationMax = EACH_CREATION_MAX[level];
        // allot the memory
        this.mEnemy = new SeaEnemy[max];   // for enemy
        for (int i = 0; i < max; i++) {
            this.mEnemy[i] = new SeaEnemy(context,image);
        }
        this.mCreation = new Creation(CREATION_INTERVAL_TIME[level]);
    }
    /*
        Initialize
    */
    public void InitSeaEnemyManager() {
        // to reset each enemies
        for (SeaEnemy en: this.mEnemy) en.InitCharacter();
    }
    /*
        Update
    */
    public void UpdateEnemyManager(SeaPlayer player, SeaItem item) {
        // count up interval to create the enemy
        this.mCreation.mIntervalCount++;
        // whenever count up 200 frame, to create the enemy.
        if (this.mCreation.CreationInterval()) {
            // player's position
            Point playerPos = SeaPlayer.GetPosition();
            // screen size
            Point screen = GameView.GetScreenSize();
            // enemy id
            int id = MyRandom.GetRandom(this.mCreationKind);
            int actionType = id;
            for (SeaEnemy en : this.mEnemy) {
                Point pos = new Point(
                        (playerPos.x+screen.x)+MyRandom.GetRandom(150),
                        MyRandom.GetRandom(screen.y-EACH_SIZES[id].y));
                // when created the enemy, count up the creation count.
                if (en.CreateEnemy(EACH_FILES[id], pos, EACH_SIZES[id], 1.0f, 255, EACH_SPEED[id], id, actionType)) {
                    // set likelihood to create the item
                    en.SetLikelihood(ENEMY_ITEM[id]);
                    // set safety area
                    en.SetSafetyArea(EACH_SAFETY[id]);
                    // except for jellyfish, initialize the animation
                    if (id != TYPE_JELLYFISH) {
                        en.InitAnimation(
                                0, 0,
                                EACH_SIZES[id].x, EACH_SIZES[id].y,
                                ANIMATION_COMMON_COUNT_MAX,
                                ANIMATION_COMMON_FRAME,
                                SeaEnemy.ANIMATION_TYPE_PRESENCE);
                    }
                    // count up creation
                    if (this.mCreation.CheckCreatedCount(this.mEachCreationMax[id])) break;
                }
            }
        }
        // Update each enemies' action and to check the overlap between the enemy and player.
        for (SeaEnemy en: this.mEnemy) {
            en.UpdateCharacter();           // update enemy's action
            en.CollisionEnemy(player,item); // to check the overlap between the enemy and player.
        }
        // to check the overlap between each enemies.
        for (int j = 0; j < this.mEnemy.length; j++) {
            if (!this.mEnemy[j].GetExist()) continue;
            for (int i = j+1; i < this.mEnemy.length; i++) {
                Collision.NotOverlapCharacter(
                    this.mEnemy[j],
                    this.mEnemy[i],
                    this.mEnemy[j].mRect);
            }
        }
    }
    /*
        Draw
    */
    public void DrawSeaEnemyManager() {
        for (SeaEnemy en: this.mEnemy) en.DrawCharacter();
    }
    /*
        Release
    */
    public void ReleaseSeaEnemyManager() {
        // each allotted memory
        // enemy
        for (int i = 0; i < this.mEnemy.length; i++) {
            this.mEnemy[i].ReleaseCharacter();
            this.mEnemy[i] = null;
        }
        this.mCreation = null;
    }
}
