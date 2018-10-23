package com.trials.harmony;

import android.graphics.Point;
import android.graphics.Rect;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 1/31/2016.
 */
public class StageCamera {
    // static variables
    // filed
    // camera's position
    private static Point        mPos;
    // the whole area that camera's position.
    // left, top, right, bottom
    private static Rect         mCameraArea;
    private static Rect         mWholeArea;

    /*
        Constructor
    */
    public StageCamera() {
        mPos = new Point(0,0);
        mCameraArea = new Rect(0,0,0,0);
        mWholeArea = new Rect(0,0,0,0);
    }

    /*
        Set camera's position
    */
    public static void SetCamera(int x, int y) {

        // get position
        mPos.x = x;
        mPos.y = y;
        Point screen = MainView.GetScreenSize();
        // check camera's position.
        // left
        if (mPos.x <= mCameraArea.left) mPos.x = mCameraArea.left;
        // top
        if (mPos.y <= mCameraArea.top) mPos.y = mCameraArea.top;
        // right
        if (mPos.x >= mCameraArea.right - screen.x) {
            mPos.x = mCameraArea.right - screen.x;
        }
        // bottom
        if (mPos.y >= mCameraArea.bottom - screen.y) {
            mPos.y = mCameraArea.bottom - screen.y;
        }
    }
    
    /*
        Set camera's whole area
    */
    public void SetCameraWholeArea(Rect area) {
        mWholeArea.left = area.left;
        mWholeArea.top = area.top;
        mWholeArea.right = area.right;
        mWholeArea.bottom = area.bottom;
    }
    
    /*
        Set camera area that is available to camera's position.
    */
    public void SetCameraArea(Rect area) {
        mCameraArea.left = area.left;
        mCameraArea.top = area.top;
        mCameraArea.right = area.right;
        mCameraArea.bottom = area.bottom;
    }

    /*
        Check overlap between character and camera's area.
    */
    public static boolean CollisionCamera(CharacterEx ch) {
        // the camera's area
        CharacterEx rect = new CharacterEx();
        Point screen = MainView.GetScreenSize();
        // set camera's area
        rect.mPos.x = mPos.x - (screen.y / 2);
        rect.mPos.y = mPos.y - (screen.y / 2);
        rect.mSize.x = screen.x * 2;
        rect.mSize.y = screen.y * 2;
        // check overlap
        return Collision.CollisionCharacter(rect, ch);
    }
    
    /*
        Rest camera
    */
    public static void ResetCamera() {
        if (mPos != null) {
            mPos.x = 0;
            mPos.y = 0;
        }
        if (mCameraArea != null) {
            mCameraArea.left = 0;
            mCameraArea.top = 0;
            mCameraArea.right = 0;
            mCameraArea.bottom = 0;
        }
        if (mWholeArea != null) {
            mWholeArea.left = 0;
            mWholeArea.top = 0;
            mWholeArea.right = 0;
            mWholeArea.bottom = 0;
        }
    }

    /*
        Get camera's position
    */
    @Contract(pure = true)
    public static Point GetCameraPosition() { return mPos; }
    /*
        Get camera's area
    */
    @Contract(pure = true)
    public static Rect GetCameraArea() { return mCameraArea; }
    /*
        Get whole area
    */
    @Contract(pure = true)
    public static Rect GetCameraWholeArea() { return mWholeArea; }
}