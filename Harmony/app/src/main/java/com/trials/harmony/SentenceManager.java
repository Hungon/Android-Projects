package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;

import org.jetbrains.annotations.Contract;


/**
 * Created by Kohei Moroi on 7/27/2016.
 */
public class SentenceManager extends PlayManager implements HasScene {
    // static variables
    // filed
    private SentenceColour  mSentenceColours[];
    private int             mCurrentScene;
    private Utility         mCreation;
    private int mIdMax;

    // Constructor
    public SentenceManager(Context context, Image image, int idMax) {
        super(6,idMax);       // set kind of appearance
        this.mCurrentScene = SceneManager.GetCurrentScene();
        this.mSentenceColours = new SentenceColour[idMax];
        for (int i = 0; i < this.mSentenceColours.length; i++) {
            this.mSentenceColours[i] = new SentenceColour(context, image);
        }
        this.mCreation = new Utility();
        this.mIdMax = idMax;
    }
    /*
        Initialize
    */
    @Override
    public void initManager() {
        // set kind of appearance
        int level = SystemManager.GetGameLevel();
        super.initAppearance(level,MODE_SENTENCE);
        if (this.mCurrentScene == SCENE_PROLOGUE) {
            // set button type to practice
            for (int i = 0; i < sTypeToGet.length; i++) {
                int element = this.mCreation.GetRandom(mAppearKind.length);
                sTypeToGet[i] = mAppearKind[element];
            }
        } else if (this.mCurrentScene == SCENE_PLAY) {
            for (SentenceColour s: this.mSentenceColours) {
                s.InitSentence(
                        COLOUR_CIRCLE_IMAGE_FILE,
                        new Point(-100,-100),
                        COLOUR_CIRCLE_IMAGE_SIZE,
                        255,1.0f,-1);
                // set add alpha and fixed time
                s.setVariableAlpha(4,2);
            }
        }
        if (this.mCurrentScene == SCENE_PLAY) this.mProcess = PROCESS_TO_PLAY.INITIALIZE;
        // when the current scene prologue, the process is still ready.
    }
    /*
        Update
    */
    @Override
    public int updateManager() {
        int type = BUTTON_EMPTY;
        if (this.mCurrentScene == SCENE_PROLOGUE) {
            if (this.mProcess.equals(PROCESS_TO_PLAY.READY)) {
                if (sAvailableToUpdate) {
                    sAvailableToUpdate = false;
                    this.mProcess = PROCESS_TO_PLAY.INITIALIZE;
                }
            } else if (this.mProcess.equals(PROCESS_TO_PLAY.INITIALIZE)) {
                this.mProcess = PROCESS_TO_PLAY.UPDATE;
            } else if (this.mProcess.equals(PROCESS_TO_PLAY.UPDATE)) {
                // when leading manager has reached to stage of pause,
                // to get each button type to guide.
                if (LeadingManager.GetPauseCountInATextFile() == 1) {
                    if (GuidanceManager.GetIsGuiding()) return BUTTON_EMPTY;
                    // to make interval between each sentence.
                    return super.getButtonTypeToGuide(150);
                }
                if (sTerminate) this.mProcess = PROCESS_TO_PLAY.READY;
            }
        } else if (this.mCurrentScene == SCENE_PLAY) {
            if (this.mProcess == PROCESS_TO_PLAY.READY) {
                super.toBeOrdinary();
                this.mProcess = PROCESS_TO_PLAY.INITIALIZE;
            } else if (this.mProcess == PROCESS_TO_PLAY.INITIALIZE) {
                for (int i = 0; i < this.mSentenceColours.length; i++) {
                    // to create sentences
                    this.CreateSentences(i);
                }
            } else if (this.mProcess == PROCESS_TO_PLAY.UPDATE) {
                int count = 0;
                for (int i = 0; i < this.mSentenceColours.length; i++) {
                    // when the image is updating either process,
                    // to return true.
                    if (this.mSentenceColours[i].UpdateSentence()) {
                        // when image's current position reached to the end position,
                        // to get the type of button.
                        // the image is going to the character.
                        if (this.mSentenceColours[i].GetCurrentBezierIndex() == 1 && this.mSentenceColours[i].GetEndPosition()) {
                            if (sTypeToGet[i] == BUTTON_EMPTY) {
                                sTypeToGet[i] = this.mSentenceColours[i].GetType();
                            }
                        }
                    } else {
                        count++;
                        if (count == this.mSentenceColours.length) this.mProcess = PROCESS_TO_PLAY.CALLING;
                    }
                }
            }
            // when not to update each image,
            // to substitute the value to available to create after fixed interval.
            else if (this.mProcess == PROCESS_TO_PLAY.CALLING) {
                if (!GuidanceManager.GetIsGuiding() && sTerminate) {
                    if (this.mCreation.ToMakeTheInterval(100)) {
                        // to call the recognizer
                        RecognitionMode.CallRecognizer(GUIDANCE_MESSAGE_SENTENCE);
                        this.mProcess = PROCESS_TO_PLAY.READY;
                    }
                }
            }
            type = super.getButtonTypeToGuide(10);
        }
        return type;
    }
    /*
        Set bezier position that terminate
        return value is that position.
    */
    private Point SetBezierTerminate(int element) {
        // position
        int interval = 100;
        int row = 3, line = this.mIdMax;
        PointF size = this.mSentenceColours[element].GetWholeSize();
        Point screen = MainView.GetScreenSize();
        Point startingPos = new Point((screen.x-(int)size.x)>>1,70);
        // to detect position-X by random
        int get = this.mCreation.GetRandom(row);
        for (int i = -1; i < row - 1; i++) {
            if (get == i+1) {
                startingPos.x += interval*i;
                break;
            }
        }
        element %= line;
        startingPos.y += interval*element;
        return startingPos;
    }
    /*
        Draw
    */
    @Override
    public void drawManager() {
        if (this.mCurrentScene == SCENE_PLAY) {
            for (CharaBasic s: this.mSentenceColours) {
                s.Draw();
            }
        }
    }
    /*
        Release
    */
    @Override
    public void releaseManager() {
        super.release();        
        if (this.mCurrentScene == SCENE_PLAY) {
            for (int i = 0; i < this.mSentenceColours.length; i++) {
                if (this.mSentenceColours[i] != null) {
                    this.mSentenceColours[i].Release();
                    this.mSentenceColours[i] = null;
                }
            }
        }
        if (this.mCreation != null) {
                this.mCreation.ReleaseUtility();
                this.mCreation = null;
        }
    }
    /*
        Create sentence images
    */
    private void CreateSentences(int element) {
        // to create the image whenever counted fixed time
        if (this.mCreation.ToMakeTheInterval(10)) {
            // 0 to 5
            int ran = this.mCreation.GetRandom(mAppearKind.length);
            int type = mAppearKind[ran];
            Point bezierPos = this.SetBezierTerminate(element);
            // to create sentence image
            if (this.mSentenceColours[element].CreateObject(bezierPos, type, PlayManager.convertTypeIntoElement(MODE_SENTENCE,type))) {
                this.mCount++;
                if (this.mCount == this.mSentenceColours.length) {
                    this.mProcess = PROCESS_TO_PLAY.UPDATE;
                }
            }
        }
    }
}