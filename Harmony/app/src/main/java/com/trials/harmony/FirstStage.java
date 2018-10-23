package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Kohei Moroi on 6/16/2016.
 */
public class FirstStage extends StageCamera {
    // static variable
    // filed
    private CharacterEx mBg[];
    private Utility     mUtility;
    private boolean mEnd;
    /*
        Constructor
    */
    public FirstStage(Context context, Image image) {
        // allot memory
        this.mBg = new CharacterEx[1];
        for (int i = 0; i < this.mBg.length; i++) {
            this.mBg[i] = new CharacterEx(context, image);
        }
        this.mUtility = new Utility();
    }
    /*
        Initialize
    */
    public void InitStage() {
        Point screen = MainView.GetScreenSize();
        Rect entirelySize = new Rect(0,0,screen.x,screen.y);
        this.mBg[0].InitCharacterEx("bgocean",0,0,480,800,255,1.0f,0);
        // Set camera
        super.SetCameraArea(entirelySize);
        super.SetCameraWholeArea(entirelySize);
        this.mEnd = false;
    }
    /*
        Update
        return value is scene number
    */
    public int UpdateStage() {
        Point screen = MainView.GetScreenSize();
        // subtract speed value from distance in the stage.
        Rect area = new Rect(0,0,screen.x,screen.y);
        int duration = Sound.GetDuration();
        int pos = Sound.GetCurrentPlaybackPosition();
        int difference = duration-pos;
        if (0 < duration) {
            if (this.mUtility.ToMakeTheInterval(100)) {
                if (Math.abs(difference) < 100 || pos == 0) {
                    this.mEnd = true;
                }
            }
        }
        // update camera area
        super.SetCameraArea(area);
        if (this.mEnd) {
            // to make the interval and then to transition to result scene.
            if (this.mUtility.ToMakeTheInterval(100)) {
                // to available to create wipe
                Wipe.SetAvailableToCreate(true);
                SceneManager.SetNextScene(SceneManager.SCENE_RESULT);
                return Scene.SCENE_RELEASE;
            }
        }
        return Scene.SCENE_MAIN;
    }
    /*
        Draw
    */
    public void DrawStage() {
        // each image
        for (CharacterEx bg: this.mBg) bg.DrawCharacterEx();
    }
    /*
        Release
    */
    public void ReleaseStage() {
        // each image class
        for (int i = 0; i < this.mBg.length; i++) {
            this.mBg[i].ReleaseCharacterEx();
            this.mBg[i] = null;
        }
        if (this.mUtility != null) {
            this.mUtility.ReleaseUtility();
            this.mUtility = null;
        }
    }
}