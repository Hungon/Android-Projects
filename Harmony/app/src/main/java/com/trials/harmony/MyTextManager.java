package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import com.trials.harmony.CharacterEx.SMOOTH_MOVE;

/**
 * Created by Kohei Moroi on 9/4/2016.
 */
public class MyTextManager implements HasMessageFrame {
    private Image mImage;
    private Context mContext;
    private MyText mMyText[];
    private CharacterEx mBalloon[];
    public Utility mUtility;
    private int mStartingFixedInterval;
    private int mFrame;
    private int mPresetColour;
    private int mPresetSize;
    private int mLineIndex;
    private boolean mIsDisplaying;
    private boolean mAvailableToDisplay;

    MyTextManager(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        this.mUtility = new Utility();
        // to allot the memory for balloon
        this.mBalloon = new CharacterEx[2];
        for (int i = 0; i < this.mBalloon.length; i++) {
            this.mBalloon[i] = new CharacterEx(context, image);
        }
        this.mPresetSize = 20;
        this.mPresetColour = Color.BLACK;
        this.mAvailableToDisplay = false;
    }
    void SetPresetValues(int size, int colour) {
        // set the value as default setting
        this.mPresetSize = size;
        this.mPresetColour = colour;
    }
    public boolean UpdateManager() {
        if (this.mAvailableToDisplay) {
            if (this.mUtility.ToMakeTheInterval(this.mStartingFixedInterval)) {
                for (CharacterEx b:this.mBalloon) b.mExistFlag = true;
                for (MyText t: this.mMyText) t.mExistFlag = true;
                this.mIsDisplaying = true;
                this.mAvailableToDisplay = false;
            }
        }
        int addAlpha;
        float addScale;
        if (this.mIsDisplaying){
            // to make interval
            if (this.mLineIndex < this.mMyText.length) {
                if (this.mUtility.ToMakeTheInterval(this.mFrame)) this.mLineIndex++;
            }
            addAlpha = 3;
            addScale = 0.1f;
        } else {
            addAlpha = -2;
            addScale = -0.1f;
        }
        // update texts
        for (int i = 0; i < this.mLineIndex; i++) {
            if (this.mMyText[i] != null) {
                this.mMyText[i].SetVariableAlpha(addAlpha);
                this.mMyText[i].UpdateMyTextAlpha();
                this.mMyText[i].UpdateMyTextSmoothing();
            }
        }
        // to update the balloon
        for (CharacterEx f:this.mBalloon) {
            if (f.mExistFlag) {
                f.VariableScaleWhenReachedTheFixedValueNoExistence(addScale,1.0f);
            }
        }
        return this.mIsDisplaying;
    }
    public void DrawManager() {
        if (!this.mIsDisplaying) return;
        // to show the balloon behind texts
        for (CharacterEx f:this.mBalloon) f.DrawCharacterEx();
        // to show texts
        for (int i = 0 ; i < this.mLineIndex; i++) {
            if (this.mMyText[i] != null) {
                this.mMyText[i].DrawMyTextByAlpha();
            }
        }
    }
    public void ReleaseManager() {
        if (this.mBalloon != null) {
            for (int i = 0; i < this.mBalloon.length; i++) {
                this.mBalloon[i].ReleaseCharacterEx();
                this.mBalloon[i] = null;
            }
        }
        if (this.mMyText != null) {
            for (int i = 0; i < this.mMyText.length; i++) {
                if (this.mMyText[i] != null) {
                    this.mMyText[i].ReleaseMyText();
                    this.mMyText[i] = null;
                }
            }
        }
    }
    void CreateMyTextWithBalloon(
            String sentence[], Point offsetPos,
            int frame, int fixedInterval, int addAlpha,
            int balloonType) throws NullPointerException {
        // to allot the memory to show sentence
        if (this.mMyText != null) {
            for (int i = 0; i < this.mMyText.length; i++) {
                this.mMyText[i].ReleaseMyText();
                this.mMyText[i] = null;
            }
        }
        this.mMyText = new MyText[sentence.length];
        for (int i = 0; i < this.mMyText.length; i++) {
            this.mMyText[i] = new MyText(this.mContext,this.mImage);
        }
        // to initialize my text class
        int startingX = offsetPos.x + 200;
        PointF move = new PointF(-2.0f,0);
        for (int i = 0; i < this.mMyText.length; i++) {
            this.mMyText[i].InitMyText(
                sentence[i],
                startingX,offsetPos.y+(i*this.mPresetSize),
                this.mPresetSize,this.mPresetColour,
                0,i);
            this.mMyText[i].mExistFlag = false;
            // set add alpha
            this.mMyText[i].SetVariableAlpha(addAlpha);
            // set smoothing parameter
            this.mMyText[i].setMove(move);
            this.mMyText[i].setTerminatePos(offsetPos);
            this.mMyText[i].setSmoothingDirection(SMOOTH_MOVE.DIRECTION_HORIZONTAL);
        }
        this.mFrame = frame;
        this.mStartingFixedInterval = fixedInterval;
        this.mLineIndex = 0;
        // Balloon setting
        // to diverge the initialization from the type
        int alpha[] = {250,100};
        Point screen = MainView.GetScreenSize();
        balloonType = (balloonType < FRAME_SMALL_BALLOON && FRAME_MEDIUM_BALLOON < balloonType)?
                FRAME_SMALL_BALLOON:balloonType;
        for (int i = 0; i < this.mBalloon.length; i++) {
            this.mBalloon[i].InitCharacterEx(
                    BALLOON_FILE_NAME[balloonType],
                    (screen.x-BALLOON_SIZE[balloonType].x)>>1,offsetPos.y-60,
                    BALLOON_SIZE[balloonType].x,BALLOON_SIZE[balloonType].y,
                    0,BALLOON_SIZE[balloonType].y*i,
                    alpha[i],0.0f,balloonType);
            this.mBalloon[i].mExistFlag = false;
        }
        this.mIsDisplaying = false;
        this.mAvailableToDisplay = true;
    }
    public void setIsDisplaying(boolean showing) { this.mIsDisplaying = showing; }
}