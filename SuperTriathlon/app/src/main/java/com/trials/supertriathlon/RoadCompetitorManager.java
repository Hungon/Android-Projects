package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * Created by USER on 2/29/2016.
 */
public class RoadCompetitorManager {
    // static variables
    // filed
    private static RoadCompetitor  mCompetitor[];
    private int                    mCountMax;
    private static byte            mActionType[];
    private static Point           mPosition[];
    private static FPoint          mWholeSize[];
    /*
        Constructor
    */
    public RoadCompetitorManager(Context context, Image image) {
        // get game level
        int level = Play.GetGameLevel();
        // participant count
        int countMax[] = {1,2,4};
        this.mCountMax = countMax[level];
        // allot memory
        mCompetitor = new RoadCompetitor[this.mCountMax];
        for (int i = 0; i < mCompetitor.length; i++) {
            mCompetitor[i] = new RoadCompetitor(context, image);
        }
        mActionType = new byte[this.mCountMax];
        mPosition = new Point[this.mCountMax];
        mWholeSize = new FPoint[this.mCountMax];
    }

    /*
        Initialize
    */
    public void InitCompetitorManager() {
        for (int i = 0; i < mCompetitor.length; i++) {
            mCompetitor[i].InitRoadCompetitor(i);
        }
    }
    /*
        Initialize to explain the stage
    */
    public void InitToExplain() {
        for (int i = 0; i < mCompetitor.length; i++) {
            mCompetitor[i].InitToExplain(i);
        }
    }
    /*
        Update
    */
    public void UpdateCompetitorManager(RoadObstacles obstacles, RoadPlayer player) {
        // player's current effect
        byte playerEffect = player.GetCurrentEffectType();
        for (int i = 0; i < mCompetitor.length; i++) {
            mCompetitor[i].UpdateRunner(obstacles);
            mCompetitor[i].NotOverlapWithPlayer(player,player.mOverlapArea);
            // check attack collision, competitor attacks to player.
            // when attacker started to attack animation, to check collision
            if (mCompetitor[i].mAni.mType == RoadRunner.ANIMATION_ATTACK_TO_LEFT ||
                mCompetitor[i].mAni.mType == RoadRunner.ANIMATION_ATTACK_TO_RIGHT) {
                if (playerEffect != RoadRunner.EFFECT_ABSOLUTE_AND_SPEED_UP &&
                    playerEffect != RoadRunner.EFFECT_ABSOLUTE) { // when player is absolute, not to check.
                    if (2 < mCompetitor[i].GetAnimationCount()) {
                        player.IsAttacked(      // if player is attacked from competitor, to show the effect.
                                mCompetitor[i].CheckAttackCollision(
                                        mCompetitor[i],
                                        mCompetitor[i].mAttackDirection,
                                        player)
                        );
                    }
                }
            }
            // in the attacker is player,
            if (player.mAni.mType == RoadRunner.ANIMATION_ATTACK_TO_LEFT ||
                player.mAni.mType == RoadRunner.ANIMATION_ATTACK_TO_RIGHT) {
                if (mCompetitor[i].GetCurrentEffectType() != RoadRunner.EFFECT_ABSOLUTE &&
                    mCompetitor[i].GetCurrentEffectType() != RoadRunner.EFFECT_ABSOLUTE_AND_SPEED_UP) {
                    if (2 < player.GetAnimationCount()) {
                        mCompetitor[i].IsAttacked(
                                player.CheckAttackCollision(
                                        player, player.mAttackDirection,
                                        mCompetitor[i])
                        );
                    }
                }
            }
            // Update action type to get function.
            mActionType[i] = mCompetitor[i].GetCurrentActionType();
            // position
            mPosition[i] = mCompetitor[i].GetPosition();
            // whole size
            mWholeSize[i] = mCompetitor[i].GetWholeSize();
        }
        // Check overlap between each competitors.
        if (2 <= this.mCountMax) {
            for (int i = 0; i < mCompetitor.length; i++) {
                for (int j = i+1; j < mCompetitor.length; j++) {
                    Collision.NotOverlapCharacter(
                            mCompetitor[i],
                            mCompetitor[j],
                            mCompetitor[i].mOverlapArea);
                }
            }
        }
    }
    /*
        Update to explain the stage
    */
    public void UpdateToExplain() {
        for (int i = 0; i < mCompetitor.length; i++) {
            mCompetitor[i].UpdateToExplain();
            // position
            mPosition[i] = mCompetitor[i].GetPosition();
            // whole size
            mWholeSize[i] = mCompetitor[i].GetWholeSize();
        }
    }

    /*
        Draw in backward to player
    */
    public void DrawCompetitorBackward() {
        for (RoadCompetitor com : mCompetitor) {
            if (com.mPriority == BaseCharacter.PRIORITY_BACKWARD) {
                com.DrawRoadCompetitor();
            }
        }
    }
    /*
        Draw in forward to player
    */
    public void DrawCompetitorForward() {
        for (RoadCompetitor com : mCompetitor) {
            if (com.mPriority == BaseCharacter.PRIORITY_FORWARD) {
                com.DrawRoadCompetitor();
            }
        }
    }
    /*
        Draw to explain the stage
    */
    public void DrawToExplain() {
        for (RoadCompetitor com : mCompetitor) {
            com.DrawToExplain();
        }
    }

    /*
        Release
     */
    public void ReleaseCompetitorManager() {
        for (RoadCompetitor com : mCompetitor) {
            com.ReleaseRoadCompetitor();
        }
    }
    /*
        Check the event that touched the competitor
    */
    public static int GetTouchedEvent() {
        // camera position
        Point camera = StageCamera.GetCameraPosition();
        if (camera == null) camera = new Point();
        for (int i = 0; i < mCompetitor.length; i++) {
            // when target element that player attempt to the competitor
            if (mCompetitor[i].mExistFlag &&
                GameView.GetTouchAction() == MotionEvent.ACTION_DOWN &&
                Collision.CheckTouch(
                    mCompetitor[i].mPos.x-camera.x,
                    mCompetitor[i].mPos.y-camera.y,
                    mCompetitor[i].mSize.x,
                    mCompetitor[i].mSize.y,
                    mCompetitor[i].mScale)) {
                return i;
            }
        }
        return -1;
    }
    /*************************************************
        Setter functions
    *************************************************/
    /*
        Set action type
    */
    public static void SetTemporaryActionType(byte action) {
        for (int i = 0; i < mActionType.length; i++) mActionType[i] = action;
    }
    /*
        Set exist
    */
    public static void SetExist(boolean exist) {
        for (RoadCompetitor com: mCompetitor) com.SetExist(exist);
    }

    /************************************************
     Getter functions
     ***********************************************/
    /*
        Get current action type
     */
    public static byte[] GetCurrentActionType() { return mActionType; }
    /*
        Position
     */
    public static Point[] GetPosition() { return mPosition; }
    /*
        Whole size
     */
    public static FPoint[] GetWholeSize() { return mWholeSize; }
}