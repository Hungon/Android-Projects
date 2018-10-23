package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 4/30/2016.
 */
public class RecognitionCharacter {
    // static variables
    // image setting
    public final static Point          RE_CHARA_SIZE = new Point(136,144);
    // field
    private Image           mImage;
    private CharacterEx     mChara[];
    // to get the move
    private static PointF   mToGetMove = new PointF();
    // to get position
    private static Point    mToGetPosition = new Point();
    // to get whole size
    private static float   sScaleToGet;
    /*
        Constructor
    */
    public RecognitionCharacter(Context context, Image image) {
        // to allot the memory
        this.mImage = image;
        this.mChara = new CharacterEx[1];
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i] = new CharacterEx(context,image);
        }
        sScaleToGet = 0.0f;
    }
    /*
        Initialize
    */
    public void InitChara() {
        // to set the character
        Point screen = MainView.GetScreenSize();
        this.mChara[0].InitCharacterEx(
                "r",
                (screen.x-RE_CHARA_SIZE.x)>>1, 550,
                RE_CHARA_SIZE.x,
                RE_CHARA_SIZE.y,
                0, 1.0f, 0);
        // to set the speed
        this.mChara[0].mMove.y = 1.0f;
        this.mChara[0].mSpeed = 5.0f;
        // to set existing flag
        this.mChara[0].mExistFlag = true;
    }
    /*
        Update
    */
    public CharacterEx UpdateChara() {
        // to check to touch the character
        if (this.mChara[0].mExistFlag) {
            // to move process that is only x-coordination.
            this.mChara[0].mPos.x += this.mChara[0].CharacterMoveToTouchedPositionEx().x*this.mChara[0].mSpeed;
            // to increase the alpha to draw
            if (this.mChara[0].mAlpha < 255) this.mChara[0].VariableAlpha(2,2);
            // to constrain to move
            this.mChara[0].ConstrainMove();
            // to set the move to get the value
            mToGetMove.x = this.mChara[0].mMove.x;
            mToGetMove.y = this.mChara[0].mMove.y;
            // position
            mToGetPosition.x = this.mChara[0].mPos.x;
            mToGetPosition.y = this.mChara[0].mPos.y;
            // whole size
            sScaleToGet = this.mChara[0].mScale;
        }
        return this.mChara[0];
    }
    /*
        Draw
    */
    public void DrawChara() {
        this.mChara[0].DrawCharacterEx();
    }
    /*
        Release
    */
    public void ReleaseChara() {
        this.mImage = null;
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i].ReleaseCharacterEx();
            this.mChara[i] = null;
        }
    }
    /*************************************************************
        Each getter functions
    ***********************************************************/
    public static PointF GetMove() { return mToGetMove; }
    @Contract(pure = true)
    public static Point GetPosition() { return mToGetPosition; }
    @Contract(pure = true)
    static float getScale() { return sScaleToGet; }
    @Contract(pure = true)
    public static PointF GetWholeSize() {
        return new PointF(RE_CHARA_SIZE.x*sScaleToGet,RE_CHARA_SIZE.y*sScaleToGet);
    }
}