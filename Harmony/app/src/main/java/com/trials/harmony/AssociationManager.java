package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;


/**
 * Created by Kohei Moroi on 11/2/2016.
 */

public class AssociationManager extends PlayManager implements HasScene {
    private AssociationColour mColours[];
    private MyText mText[];
    private Utility mCreation;
    private int mCurrentMode;

    AssociationManager(Context context, Image image, int idMax) {
        super(6,idMax);
        int level = SystemManager.GetGameLevel();
        this.mCurrentMode = SystemManager.GetPlayMode();
        this.mColours = new AssociationColour[idMax];
        this.mText = new MyText[idMax];
        for (int i = 0; i < this.mColours.length; i++) {
            this.mColours[i] = new AssociationColour(context, image);
            this.mText[i] = new MyText(context,image);
        }
        super.initAppearance(level,this.mCurrentMode);
        this.mCreation = new Utility();
    }
    @Override
    public void initManager() {
        int scene = SceneManager.GetCurrentScene();
        for (AssociationColour a:this.mColours) {
            a.initColour(
                COLOUR_CIRCLE_IMAGE_FILE,
                new Point(-100, -100),
                COLOUR_CIRCLE_IMAGE_SIZE,
                255, 1.0f, -1);
            a.setVariableAlpha(4, 2);
        }
        if (scene == SCENE_PLAY) this.mProcess = PROCESS_TO_PLAY.INITIALIZE;
        // when the current scene prologue, the process is still ready.
    }

    // return values is button type to guide
    @Override
    public int updateManager() {
        int type;
        int scene = SceneManager.GetCurrentScene();
        if (this.mProcess.equals(PROCESS_TO_PLAY.READY)) {
            if (scene == SCENE_PLAY) {
                if (!sAvailableToUpdate) {
                    sAvailableToUpdate = this.mCreation.ToMakeTheInterval(10);
                }
            }
            if (sAvailableToUpdate) {
                int count = 0;
                for (MyText t:this.mText) {
                    if (!t.UpdateMyTextAlpha()) count++;
                }
                if (count == this.mText.length) {
                    for (MyText t:this.mText) {
                        t.SetVariableAlpha(-2);
                    }
                }
                if (this.mText[0].GetVariableAlpha() == -2) {
                    for (AssociationColour c : this.mColours) {
                        if (!c.feedOut()) count++;
                    }
                    if (count == this.mColours.length<<1) {
                        this.mProcess = PROCESS_TO_PLAY.INITIALIZE;
                        sAvailableToUpdate = false;
                        super.toBeOrdinary();
                    }
                }
            }
        } else if (this.mProcess.equals(PROCESS_TO_PLAY.INITIALIZE)) {
            for (int i = 0; i < this.mColours.length; i++) {
                this.createAssociation(i);
            }
        } else if (this.mProcess.equals(PROCESS_TO_PLAY.UPDATE)) {
            int updated = 0;
            for (int i = 0; i < this.mColours.length; i++) {
                if (this.mColours[i].updateColour()) {
                    // do nothing now
                } else {
                    updated++;
                }
            }
            if (updated == this.mColours.length) {
                if (scene == SCENE_PLAY) {
                    this.mProcess = PROCESS_TO_PLAY.CALLING;
                } else {
                    this.mProcess = PROCESS_TO_PLAY.READY;
                }
            }
        } else if (this.mProcess.equals(PROCESS_TO_PLAY.CALLING)) {
            if (!GuidanceManager.GetIsGuiding() && sTerminate) {
                if (this.mCreation.ToMakeTheInterval(100)) {
                    int guidance;
                    switch(this.mCurrentMode) {
                        case MODE_ASSOCIATION_IN_EMOTIONS:
                            guidance = GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS;
                            break;
                        case MODE_ASSOCIATION_IN_FRUITS:
                            guidance = GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS;
                            break;
                        case MODE_ASSOCIATION_IN_ALL:
                            guidance = GUIDANCE_MESSAGE_ASSOCIATION_IN_ALL;
                            break;
                        default:
                            guidance = GUIDANCE_MESSAGE_EMPTY;
                    }
                    // to call the recognizer
                    RecognitionMode.CallRecognizer(guidance);
                    this.mProcess = PROCESS_TO_PLAY.READY;
                    // set colour's image and its text for variable alpha
                    for (AssociationColour c:this.mColours) c.setVariableAlpha(-2,1);
                    for (MyText t:this.mText) {
                        t.SetVariableAlpha(2);
                        t.setIntervalForAlpha(1);
                    }
                }
            }
        }
        type = super.getButtonTypeToGuide(10);
        return type;
    }
    @Override
    public void drawManager() {
        for (AssociationColour a:this.mColours) a.drawColour();
        for (MyText t:this.mText) t.DrawMyTextByAlpha();
    }
    @Override
    public void releaseManager() {
        super.release();
        for (int i = 0;i < this.mColours.length; i++) {
            this.mColours[i].releaseColour();
            this.mColours[i] = null;
            this.mText[i].ReleaseMyText();
            this.mText[i] = null;
        }
        if (this.mCreation != null) this.mCreation = null;
    }
    /*
    Set bezier position that terminate
    return value is that position.
    */
    private Point setBezierTerminatePosition(int element, int spaceX) {
        // position
        int row = this.mColours.length;
        PointF size = this.mColours[element].getWholeSize();
        Point screen = MainView.GetScreenSize();
        int intervalSection = (row-1)*spaceX;
        int wholeSection = (int)size.x*row+intervalSection;
        Point startingPos = new Point(
                (screen.x-wholeSection)>>1, ((screen.y-(int)size.y)>>1)+20);
        startingPos.x += (size.x+spaceX)*element;
        return startingPos;
    }
    // do creation
    private void createAssociation(int element) {
        if (this.mCreation.ToMakeTheInterval(10)) {
            String pool[] = null;
            // 0 to 5
            int ran = this.mCreation.GetRandom(mAppearKind.length);
            int type = mAppearKind[ran];
            sTypeToGet[element] = type;             // get the type
            int pos = PlayManager.convertTypeIntoElement(this.mCurrentMode,type);
            int obtain = this.mCurrentMode;
            // when the current mode is all, to get random number
            if (this.mCurrentMode == MODE_ASSOCIATION_IN_ALL) {
                ran = this.mCreation.GetRandom(101);
                ran = (ran==0)?1:ran;
                obtain = (ran%2==0)?MODE_ASSOCIATION_IN_EMOTIONS:MODE_ASSOCIATION_IN_FRUITS;
            }
            // set association word
            if (obtain == MODE_ASSOCIATION_IN_EMOTIONS) {
                pool = new String[ASSOCIATION_WORDS_IN_EMOTIONS[pos].length];
                System.arraycopy(ASSOCIATION_WORDS_IN_EMOTIONS[pos],0,pool,0,ASSOCIATION_WORDS_IN_EMOTIONS[pos].length);
            } else if (obtain == MODE_ASSOCIATION_IN_FRUITS) {
                pool = new String[ASSOCIATION_WORDS_IN_FRUITS[pos].length];
                System.arraycopy(ASSOCIATION_WORDS_IN_FRUITS[pos],0,pool,0,ASSOCIATION_WORDS_IN_FRUITS[pos].length);
            }
            String word = "";
            if (pool != null) {
                ran = this.mCreation.GetRandom(pool.length);
                word = pool[ran];
            }
            Point bezierPos = this.setBezierTerminatePosition(element,70);
            int length = word.length();
            int fontSize = 21;
            int spaceY = ((element+1) % 2 == 0)?45:-5;
            Point textPos = new Point(bezierPos.x-(length>>1)*(fontSize/3),bezierPos.y+spaceY);
            // to create object
            if (this.mColours[element].createObject(bezierPos,new Point(100,-100), type, pos, word)) {
                // set alpha parameter
                this.mColours[element].setAlphaParameter(100,2,2);
                // initialize text
                this.mText[element].InitMyText(word,textPos.x,textPos.y,fontSize, Color.BLACK,0,type);
                this.mCount++;
                if (this.mCount == this.mColours.length) {
                    this.mProcess = PROCESS_TO_PLAY.UPDATE;
                }
            }
        }
    }
}
