package com.trials.harmony;

import android.content.Context;
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
    private int               mSceneNum;         // current scene
    private RecognitionMode   mRecognitionMode;     // Recognition mode

    // to diverge scene from current scene number.
    public final boolean UpdateScene(Context context, Image image, Wipe wipe) {

        switch(this.mSceneNum) {
            case Scene.SCENE_INIT :               // Initialize scene
                this.mSceneNum = this.Init();    // scene
                this.mRecognitionMode = new RecognitionMode(context);
                break;
            case Scene.SCENE_MAIN :         // Update scene
                // when wipe is processing, to stop the scene process.
                int transiting = wipe.UpdateWipe();
                // when not to exist the wipe, to update each scene's update.
                if (transiting == Wipe.DESTROYED) {
                    // when not to execute recognition process,
                    // to update each scene
                    // I mean when recognition's task is Ready, to update each scene.
                    int process = this.mRecognitionMode.MainExecution();
                    if (process == RecognitionMode.READY) {
                        this.mSceneNum = this.Update();
                    } else if (process == RecognitionMode.DESTROY) {
                        this.mSceneNum = SCENE_RELEASE;
                    }
                    // eventually, when to change current scene,
                    // to create wipe and to transition to next scene.
                    if (this.mSceneNum == SCENE_RELEASE) {
                        // to create the wipe
                        wipe.CreateWipe(SceneManager.GetNextScene(), Wipe.TYPE_PENETRATION);
                        break;
                    }
                // when to finish updating the process of wipe
                // to release the wipe.
                } else if (transiting == Wipe.RELEASE) {
                    wipe.ReleaseWipe();
                }
                // drawing process
                image.lock();                // lock surface
                Point screen = MainView.GetScreenSize();
                // fill back surface with black.
                image.fillRect(-50, -50, screen.x + 100, screen.y + 100, Color.BLACK);
                // for not to draw next scene immediately.
                if (wipe.GetChangeScene()) this.Draw();
                // Draw wipe
                if (transiting == Wipe.UPDATE) wipe.DrawWipe();
                image.unlock();             // unlock surface

                break;
            case Scene.SCENE_RELEASE :      // release scene
                this.mSceneNum = this.Release();
                // Destroy recognizer manager
                this.mRecognitionMode.Destroy();
                this.mRecognitionMode = null;
                break;
            case Scene.SCENE_END :          // when scene ends, transition to next scene.
                // to reset the words recognized
                Harmony.ResetRecognizedWords();
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
}