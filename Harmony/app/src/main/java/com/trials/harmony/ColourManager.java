package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Process;
import android.view.MotionEvent;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 5/3/2016.
 */
public class ColourManager extends PlayManager implements HasScene {

    // static variables
    // the se's files
    private final static String SOUND_FILES[] = {
            "red",
            "blue",
            "yellow",
            "green",
            "white",
            "black",
            "violet",
            "pink"
    };
    private final static int        INITIALIZE  = 0;
    private final static int        UPDATE      = 1;
    private final static int        FINISH      = 2;
    // scale
    private final static float  COLOUR_DEFAULT_SCALE = 1.0f;
    // filed
    private ColourToNotice  mColourToNotice;
    private ColourToPlay    mColourToPlay;
    private int             mCurrentScene;
    // the direction to notice the position that finger nears by the button.
    private static int mDirection;
    // available to play each sound of colour
    private static boolean sAvailableToPlayEverySound;

    /*
        Constructor
    */
    public ColourManager(Context context, Image image, int idMax) {
        super(6,idMax);       // set kind of appearance
        this.mCurrentScene = SceneManager.GetCurrentScene();
        if (this.mCurrentScene == SCENE_PROLOGUE) {
            this.mColourToNotice = new ColourToNotice(context,image,mAppearKind.length);
        } else if (this.mCurrentScene == SCENE_PLAY) {
            this.mColourToPlay = new ColourToPlay(context,image,idMax,mAppearKind.length);
        }
    }
    /*
        Initialize
    */
    @Override
    public void initManager() {
        // set kind of appearance
        int level = SystemManager.GetGameLevel();
        super.initAppearance(level,MODE_SOUND);
        // to diverge the initialization from the current scene.
        if (this.mCurrentScene == SCENE_PROLOGUE) {
            this.mColourToNotice.InitColour(mAppearKind);
        } else if (this.mCurrentScene == SCENE_PLAY) {
            this.mColourToPlay.InitColour(mAppearKind);
        }
        mDirection = RecognitionButton.DISTANCE;
        this.mProcess = PROCESS_TO_PLAY.UPDATE;
    }
    /*
        Update
    */
    @Override
    public int updateManager() {
        // in prologue scene
        if (this.mCurrentScene == SCENE_PROLOGUE) {
            // when finish to execute the leading process,
            // to update the recognition character
            // to update to play every sound of colour when available to play
            // in Response Manager class
            if (this.mProcess.equals(PROCESS_TO_PLAY.INITIALIZE)) {
                this.mColourToNotice.setAvailableToPlayEverySound();
                this.mProcess = PROCESS_TO_PLAY.UPDATE;
            } else if (this.mProcess.equals(PROCESS_TO_PLAY.UPDATE)) {
                sTypeToGet[0] = this.mColourToNotice.ToPlayEverySoundOfColour();
                if (!sAvailableToPlayEverySound) {
                    sTypeToGet[0] = this.mColourToNotice.UpdateColour();
                    mDirection = this.mColourToNotice.GetDirectionToNotice();
                }
            }
            if (sAvailableToUpdate) {
                sAvailableToUpdate = false;
                this.mProcess = PROCESS_TO_PLAY.INITIALIZE;
            }
        } else if (this.mCurrentScene == SCENE_PLAY) {
            sTypeToGet = this.mColourToPlay.UpdateColour();
        }
        return BUTTON_EMPTY;
    }
    /*
        Draw
    */
    @Override
    public void drawManager() {
        // to diverge the drawing from the current scene.
        if (this.mCurrentScene == SCENE_PROLOGUE) {
            this.mColourToNotice.DrawColour();
        } else if (this.mCurrentScene == SCENE_PLAY) {
            this.mColourToPlay.DrawColour();
        }
    }
    /*
        Release
    */
    @Override
    public void releaseManager() {
        super.release();
        if (this.mCurrentScene == SCENE_PROLOGUE) {
            this.mColourToNotice.ReleaseColour();
            this.mColourToNotice = null;
        } else if (this.mCurrentScene == SCENE_PLAY) {
            this.mColourToPlay.ReleaseColour();
            this.mColourToPlay = null;
        }
    }
    /*
         Get the direction to notice the position that finger nears by the button.
     */
    @Contract(pure = true)
    public static int GetDirectionToNotice() { return mDirection; }
    /*
         Get available to play colours
    */
    @Contract(pure = true)
    public static boolean getAvailableToPlay() { return sAvailableToPlayEverySound; }


    /**************************************************************************************
    *   ColourToNotice class
    *   using in prologue scene.
    *
    **************************************************************************************/
    private class ColourToNotice implements HasButtons, HasSystem, HasColourImage {
        private final static int    FIXED_TIME_TO_PLAY_COLOUR = 120;
        // the process to play the sound effect
        // the fixed interval time to play the SE
        private final static int        FIXED_INTERVAL_TIME = 100;
        // filed
        private Context                 mContext;
        private Image                   mImage;
        private Colour                  mColours[];
        private Utility                 mUtility;
        // the direction to notice the position that finger nears by the button.
        private int             mDirection;
        // the current type to play the sound of colour.
        private int         mCurrentColourTypeToPlay;
        // the preview type to play
        private int                mPreviewColourTypeToPlay;
        // the progress to play the SE
        private int             mProcessToPlay;
        // the preview element to play the SE
        private int             mPreviewElement;
        // the interval to play the SE
        private int             mIntervalCount;

        // Constructor
        private ColourToNotice(Context context, Image image, int appearKind) {
            this.mContext = context;
            this.mImage = image;
            this.mColours = new Colour[appearKind];
            for (int i = 0; i < this.mColours.length; i++) {
                this.mColours[i] = new Colour(this.mContext, this.mImage);
            }
            this.mUtility = new Utility();
        }
        /*
            Initialize
        */
        private void InitColour(int[] appearKind) {
            Point screen = MainView.GetScreenSize();
            // whole size
            PointF wholeSize = new PointF(
                    COLOUR_RECTANGLE_IMAGE_SIZE.x * ColourManager.COLOUR_DEFAULT_SCALE,
                    COLOUR_RECTANGLE_IMAGE_SIZE.y * ColourManager.COLOUR_DEFAULT_SCALE);
            // starting position-Y
            int startingPosY = 180;
            int spaceY = 20;
            for (int i = 0; i < this.mColours.length; i++) {
                int srcY = PlayManager.convertTypeIntoElement(MODE_SOUND,appearKind[i]);
                this.mColours[i].InitColour(
                        ColourManager.SOUND_FILES[srcY], COLOUR_RECTANGLE_IMAGE_FILE,
                        new Point((screen.x - COLOUR_RECTANGLE_IMAGE_SIZE.x) >> 1, startingPosY + (((int) wholeSize.y + spaceY) * i)),
                        COLOUR_RECTANGLE_IMAGE_SIZE,
                        new Point(0, COLOUR_RECTANGLE_IMAGE_SIZE.y * srcY),
                        100, ColourManager.COLOUR_DEFAULT_SCALE,
                        appearKind[i]);
            }
            // the process to play
            this.mProcessToPlay = INITIALIZE;
            // the preview
            this.mPreviewColourTypeToPlay = BUTTON_EMPTY;
            // the preview element to play the SE
            this.mPreviewElement = 0;
            // to reset interval
            this.mIntervalCount = 0;
            // direction between finger or character and a colour.
            this.mDirection = RecognitionButton.DISTANCE;
            // available to play every sound of colour
            sAvailableToPlayEverySound = false;
            // the current colour's type
            mCurrentColourTypeToPlay = BUTTON_COLOUR_RED;
        }
        /*
            Update
        */
        private int UpdateColour() {
            int type = BUTTON_EMPTY;
            int direction = RecognitionButton.DISTANCE;
            int element = -1;
            // the area to seek the button
            Point area = new Point(30,30);
            if (sAvailableToPlayEverySound) return type;
            // to update buttons
            for (int i = 0; i < this.mColours.length; i++) {
                if (!this.mColours[i].GetExist()) continue;
                // to seek the colour.
                // return value is direction
                // that between the colour's position and finger's position.
                // to notice the presence of colour after the interval
                if (this.mUtility.ToMakeTheInterval(60)) {
                    direction = this.mColours[i].ToSeekTheColour(area);
                }
                // when to got the direction, to be break
                if (direction != RecognitionButton.DISTANCE) {
                    this.mDirection = direction;
                    break;
                }
                // to get the scene as is pressed the colour.
                this.mColours[i].IsPressedTheColour();
                if (this.mColours[i].AsPressesTheButton()) {
                    // when user pressed the colour,
                    type = this.mColours[i].GetType();
                    // to show clearly
                    this.mColours[i].SetAlpha(255);
                    element = i;
                    break;
                }
            }
            // to play the colour's sound
            this.ToMakeIntervalToPlaySound(element, type);
            if (direction == RecognitionButton.DISTANCE) this.mDirection = RecognitionButton.DISTANCE;
            return type;
        }
        /*
            To update to play every sound of colour
            return value is colour's type
        */
        private int ToPlayEverySoundOfColour() {
            if (mCurrentColourTypeToPlay == BUTTON_EMPTY) return BUTTON_EMPTY;
            // when not to available to play every sound of colour, to return.
            if (!sAvailableToPlayEverySound) return BUTTON_EMPTY;
            int res = BUTTON_EMPTY;
            if (mCurrentColourTypeToPlay != this.mPreviewColourTypeToPlay) {
                // to show clearly the current colour
                if (this.mPreviewElement < this.mColours.length && this.mColours[this.mPreviewElement].GetAlpha() == 100) {
                    this.mColours[this.mPreviewElement].SetAlpha(255);
                    res = mCurrentColourTypeToPlay;
                    this.mColours[this.mPreviewElement].AvailableToPlay(true);
                }
                // to make the interval to play
                if (this.mUtility.ToMakeTheInterval(FIXED_TIME_TO_PLAY_COLOUR)) {
                    // to show obliviously the preview colour
                    this.mColours[this.mPreviewElement].SetAlpha(100);
                    // to get the preview
                    this.mPreviewColourTypeToPlay = mCurrentColourTypeToPlay;
                    this.mPreviewElement++;
                    if (0 <= this.mPreviewElement && this.mPreviewElement < this.mColours.length) {
                        mCurrentColourTypeToPlay = this.mColours[this.mPreviewElement].GetType();
                    }
                    // to limit
                    if (this.mColours.length <= this.mPreviewElement) {
                        this.mPreviewElement = 0;
                        mCurrentColourTypeToPlay = BUTTON_EMPTY;
                        sAvailableToPlayEverySound = false;
                    }
                }
            }
            // to play the colour
            if (BUTTON_EMPTY < res && res <= BUTTON_COLOURS[COLOUR_KIND-1]) this.mColours[this.mPreviewElement].ToPlayColour();
            return res;
        }
        /*
            To play the colour's sound as pressed the colour
        */
        private void ToMakeIntervalToPlaySound(int element, int colourType) {
            // when colour type is existing, to go to process.
            // to diverge the process from current progress
            switch (this.mProcessToPlay) {
                case INITIALIZE:
                    // to execute within bounds of colour
                    if (BUTTON_COLOURS[0] <= colourType && colourType <= BUTTON_COLOURS[COLOUR_KIND-1]) {
                        // to select the sound effect of colour
                        this.mPreviewElement = element;
                        if (element == -1) return;
                        // when finish guiding, to play the SE
                        this.mColours[this.mPreviewElement].AvailableToPlay(true);
                        this.mProcessToPlay = UPDATE;
                    }
                    break;
                case UPDATE:
                    // to play the SE
                    this.mColours[this.mPreviewElement].ToPlayColour();
                    this.mProcessToPlay = FINISH;
                    break;
                case FINISH:
                    // to finish playing the SE
                    this.mColours[this.mPreviewElement].AvailableToPlay(false);
                    // to substitute the flag after count time reaches to fixed time
                    this.mIntervalCount++;
                    if (FIXED_INTERVAL_TIME < this.mIntervalCount) {
                        this.mIntervalCount = 0;
                        this.mProcessToPlay = INITIALIZE;
                    }
                    break;
                default:
                    break;
            }
        }
        /*
            Draw
        */
        private void DrawColour() {
            for (Colour c: this.mColours) {
                c.DrawColour();
            }
        }
        /*
            Release
        */
        private void ReleaseColour() {
            for (int i = 0; i < this.mColours.length; i++) {
                this.mColours[i].ReleaseColour();
                this.mColours[i] = null;
            }
            // Utility
            this.mUtility.ReleaseUtility();
            this.mUtility = null;
        }
        /*
            Set available to play every sound of colour
        */
        private void setAvailableToPlayEverySound() {
            mCurrentColourTypeToPlay = BUTTON_COLOUR_RED;
            sAvailableToPlayEverySound = true;
        }
        /*
            Get the direction to notice the position that finger nears by the button.
        */
        private int GetDirectionToNotice() { return this.mDirection; }
    }
    /***************************************************************************************
    *   ColourToPlay class
    *   using in play scene.
    **************************************************************************************/
    private class ColourToPlay implements HasButtons, HasSystem, HasColourImage {

        private Context                 mContext;
        private Image                   mImage;
        private Colour                  mColours[];
        private Utility                 mUtility;
        private int      mIsPressedTheColourType[];
        // the count that created colour
        private int             mCreatedCount;
        // the max of count to appear
        private int             mAppearMax;
        // the fixed interval to create colour
        private int mFixedTimeToCreate;
        // the progress to play the SE
        private int             mProcessToPlay;
        // the preview element to play the SE
        private int             mPreviewElement;
        private int mAppearKind[];

        // Constructor
        private ColourToPlay(Context context, Image image, int idMax, int appearKind) {
            this.mContext = context;
            this.mImage = image;
            // to allot the memory
            this.mColours = new Colour[idMax];
            this.mIsPressedTheColourType = new int[idMax];
            this.mUtility = new Utility();
            // the max of count that appear
            this.mAppearMax = idMax;
            this.mAppearKind = new int[appearKind];
            for (int i = 0; i < this.mAppearKind.length; i++) {
                this.mAppearKind[i] = -1;
            }
        }
        /*
            Initialize
        */
        private void InitColour(int[] appearKind) {
            // get game level
            int level = SystemManager.GetGameLevel();
            // the fixed time table
            int fixedTime[] = {20,15,10};
            for (int i = 0; i < this.mColours.length; i++) {
                this.mColours[i] = new Colour(this.mContext,this.mImage);
            }
            for (Colour c: this.mColours) {
                c.InitColour("", COLOUR_RECTANGLE_IMAGE_FILE, new Point(),
                        COLOUR_RECTANGLE_IMAGE_SIZE, new Point(),
                        1, ColourManager.COLOUR_DEFAULT_SCALE, -1);
                // to set the flag that show the colour.
                // firstly, not to show.
                c.SetExist(false);
            }
            // the process to play
            this.mProcessToPlay = UPDATE;
            // the count that created colour
            this.mCreatedCount = 0;
            // the fixed time to create colour
            this.mFixedTimeToCreate = fixedTime[level];
            // the preview element to play the SE
            this.mPreviewElement = -1;
            for (int i = 0; i < this.mIsPressedTheColourType.length; i++) {
                this.mIsPressedTheColourType[i] = BUTTON_EMPTY;
            }
            // set kind of appearance
            System.arraycopy(appearKind,0,this.mAppearKind,0,appearKind.length);
        }
        /*
            Update
        */
        private int[] UpdateColour() {
            // when return from recognizer, to initialize colours
            if (this.mProcessToPlay == INITIALIZE) {
                // reset parameter to create colour
                this.ResetParameterToCreate();
                // to next process
                this.mProcessToPlay = UPDATE;
            } else if (this.mProcessToPlay == UPDATE) {
                // when not to reach to the creation max, to make the process
                if (this.mCreatedCount < this.mAppearMax) {
                    // to create the colour after counted time reached to the interval time.
                    if (this.mUtility.ToMakeTheInterval(this.mFixedTimeToCreate)) {
                        int colourType = this.mAppearKind[this.mUtility.GetRandom(this.mAppearKind.length)];
                        Point screen = MainView.GetScreenSize();
                        this.CreateColour(
                                (screen.x - (int) this.mColours[0].GetWholeSize().x) >> 1,
                                -100, colourType);
                    }
                }
                // to update buttons
                CharacterEx ch = new CharacterEx();
                Point pos = RecognitionCharacter.GetPosition();
                ch.mPos.x = pos.x;
                ch.mPos.y = pos.y;
                ch.mSize = RecognitionCharacter.RE_CHARA_SIZE;
                ch.mScale = RecognitionCharacter.getScale();
                for (int i = 0; i < this.mColours.length; i++) {
                    if (!this.mColours[i].GetExist()) continue;
                    // update move
                    this.mColours[i].SetPosition(
                            this.mColours[i].GetPosition().x,
                            this.mColours[i].GetPosition().y += 2);
                    // as touched colour to process
                    if (this.mColours[i].IsTouchedColour(ch)) {
                        this.ToCheckOrderlyElement(this.mColours[i].GetType(), i);
                        this.ToPlaySoundOnlyOnce(this.mPreviewElement);     // to play sound
                        break;
                    }
                    // when current element reach to last element,
                    // to call recognizer.
                    if (this.mPreviewElement == this.mColours.length - 1) {
                        this.mProcessToPlay = FINISH;
                    }
                }
                // when counted time reached to fixed time, to call recognizer
            } else if (this.mProcessToPlay == FINISH) {
                if (this.mUtility.ToMakeTheInterval(120)) {
                    RecognitionMode.CallRecognizer(RecognitionMode.GUIDANCE_MESSAGE_COLOUR);
                    // to stop each sound
                    this.StopEachSound();
                    // next process is initialization
                    this.mProcessToPlay = INITIALIZE;
                }
            }
            return this.mIsPressedTheColourType;
        }
        /*
            To play the colour's sound as character touched a colour
        */
        private void ToPlaySoundOnlyOnce(int element) {
            // to bound of the colour
            if (0 <= element && element < this.mColours.length) {
                // when the sound has already played, to return
                if (0 < this.mColours[element].GetCountPlayedSound()) return;
                this.mColours[element].AvailableToPlay(true);
                // To play the sound of a colour
                this.mColours[element].ToPlayColour();
            }
        }
        /*
            Create the colour
        */
        private void CreateColour(int x, int y, int type) {
            for (Colour c: this.mColours) {
                if (c.GetExist()) continue;
                int ele = PlayManager.convertTypeIntoElement(MODE_SOUND,type);
                // to set each setting
                c.SetPosition(x, y);
                c.SetType(type);
                c.SetOriginPosition(0,COLOUR_RECTANGLE_IMAGE_SIZE.y*ele);
                c.SetAlpha(1);
                c.SetExist(true);
                // when generate the sound, to reset the count value that played the sound.
                c.SetSoundFile(ColourManager.SOUND_FILES[ele]);
                // to increase the count that created colour
                this.mCreatedCount++;
                break;
            }
        }
        /*
            Reset parameter to create colour
        */
        private void ResetParameterToCreate() {
            this.mCreatedCount = 0;
            this.mUtility.ResetInterval();
            this.mPreviewElement = -1;
            // reset colours' condition
            for (Colour c: this.mColours) {
                c.SetExist(false);
            }
            for (int i = 0; i < this.mIsPressedTheColourType.length; i++) {
                this.mIsPressedTheColourType[i] = BUTTON_EMPTY;
            }
        }
        /*
            To check to get the type of colour.
            when to already get that type, to increase the element
            to put into the type's pool.
            return value is the element to order the type of number.
        */
        private void ToCheckOrderlyElement(int type, int element) {
            if (this.mColours.length <= this.mPreviewElement) return;
            this.mPreviewElement = element;
            // when user pressed the colour,
            if (this.mIsPressedTheColourType[this.mPreviewElement] == BUTTON_EMPTY) {
                this.mIsPressedTheColourType[this.mPreviewElement] = type;
            }
        }
        /*
            To stop each colour's sound
        */
        private void StopEachSound() {
            for (Colour c: this.mColours) c.StopSound();
        }
        /*
            Draw colours
        */
        private void DrawColour() {
            for (Colour c: this.mColours) {
                c.DrawColour();
            }
        }
        /*
            Release
        */
        private void ReleaseColour() {
            for (int i = 0; i < this.mColours.length; i++) {
                this.mColours[i].ReleaseColour();
                this.mColours[i] = null;
            }
            // Utility
            this.mUtility.ReleaseUtility();
            this.mUtility = null;
            this.mIsPressedTheColourType = null;
        }
    }
}