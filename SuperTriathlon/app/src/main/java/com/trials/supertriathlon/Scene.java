package com.trials.supertriathlon;

import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by USER on 1/26/2016.
 */
abstract class Scene {
    // static variables
    public final static int     SCENE_INIT      = 0;        // Initialize scene
    public final static int     SCENE_MAIN      = 1;        // Main process
    public final static int     SCENE_RELEASE   = 2;        // Release
    public final static int     SCENE_END       = 3;        // End scene
    public final static int     SCENE_ERROR     = 4;        // Error

    // field
    private int     mSceneNum;         // current scene
    protected static boolean mPlayingF = false;

    // to diverge scene from current scene number.
    public final boolean Scene(Image image) {

        switch(this.mSceneNum) {
            case Scene.SCENE_INIT :               // Initialize scene
                this.mSceneNum = this.Init();    // scene
                break;
            case Scene.SCENE_MAIN :         // Update scene

                // when showing the ad, not to execute the processes
                if (!GameView.IsShowedAd()) {
                    // when wipe is processing, to stop the scene process.
                    if (!Wipe.UpdateWipe()) this.mSceneNum = this.Update();

                    image.lock();                // lock surface
                    Point screen = GameView.GetScreenSize();
                    // fill back surface with white.
                    image.fillRect(-50, -50, screen.x + 100, screen.y + 100, Color.BLACK);
                    // for not to draw next scene immediately.
                    if (Wipe.GetChangeScene()) this.Draw();
                    Wipe.DrawWipe();            // Draw wipe
                    image.unlock();             // unlock surface
                }
                break;

            case Scene.SCENE_RELEASE :      // release scene
                this.mSceneNum = this.Release();
                break;
            case Scene.SCENE_END :          // when scene ends, transition to next scene.
                return true;
            case Scene.SCENE_ERROR :
                break;

        }
        return false;
    }

    // Initialize scene
    public abstract int Init();
    // Update scene
    public abstract int Update();
    // Draw scene
    public abstract void Draw();
    // Release scene
    public abstract int Release();

    /*
        The flag that available to play race.
    */
    public static void SetAvailableToPlay(boolean playing) { mPlayingF = playing; }
}