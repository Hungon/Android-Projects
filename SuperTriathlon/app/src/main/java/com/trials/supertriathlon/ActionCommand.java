package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * Created by USER on 2/2/2016.
 */
public class ActionCommand {

    // static variables
    // command
    // size
    public final static Point  COMMAND_SIZE = new Point(96,96);         // circle area = 7234.56
    public final static Point  COMMAND_STARTING_POSITION = new Point(80,600);
    private final static int    COMMAND_SPACE_X = 15;
    // kind of image that correct input or not
    private final static int    COMMAND_TYPE_WAITING = 0;
    private final static int    COMMAND_TYPE_SUCCESS = 1;
    private final static int    COMMAND_TYPE_ERROR   = 2;
    private final static int    COMMAND_TYPE_KIND    = 3;
    // input
    public final static float    INPUT_STARTING_SCALE    = 2.0f;         // scale
    public final static int      INPUT_DEFAULT_ALPHA     = 70;           // alpha

    // the difference based on game level
    private final static float[]    INPUT_SCALE_DOWN_RATE = {0.01f,0.02f,0.03f};

    // Radius based on game level
    private final static int[] DIFFERENCE_RADIUS = {7,6,5};
    // Default command area
    private final static double COMMAND_DEFAULT_AREA        = 7234.56;
    private final static double[] COMMAND_DEFAULT_INNER_AREA  = {
            5278.34, 5538.96, 5805.86
    };
    // Input margin = difference circle area
    // When input area more than command area
    // Easy     area = 2263.94, radius = 26.85
    // Normal   area = 1921.68, radius = 24.73
    // Hard     area = 1585.7,  radius = 22.47
    // When input area less than command area
    // Easy     = 1956.22, radius = 24.95
    // Normal   = 1695.6,  radius = 23.23
    // Hard     = 1428.7,  radius = 21.33
    private final static double[][]   INPUT_MARGIN_ERROR = {
            {(31.85 * 31.85) * 3.14, (29.95 * 29.95) * 3.14},
            {(30.73 * 30.73) * 3.14, (28.23*28.23)*3.14},
            {(27.47*27.47)*3.14, (26.33*26.33)*3.14}
    };
    // the time that show command
    private final static int[]    COMMAND_INTERVAL_TIME = {100,70,40};

    // kind of value
    public final static int        VALUE_PERFECT   = 0;
    public final static int        VALUE_NICE      = 1;
    public final static int        VALUE_BAD       = 2;


    // filed
    private Context             mContext;
    private Image               mImage;
    private int                 mCommandMax;
    private BaseCharacter       mCommand[];
    private BaseCharacter       mInput;
    private int                 mCorrectInput[];
    private int                 mCommandElementNow;
    private static boolean      mCreationF;
    private int                 mInterval;
    private static int          mSuccessCount;
    private static int          mTotalSuccessCount;
    private static int          mConsecutiveSuccess;
    private float               mInputScaleDownRate;
    private double[]            mInputMarginError = new double[2];
    private BaseCharacter       mValue;
    private static int[]        mValueCount = new int[3];

    /*
        Constructor
     */
    public ActionCommand(Context activity, Image image) {
        this.mContext = activity;
        this.mImage = image;
        // command max that available to create
        this.mCommandMax = 3;
        // allot memory
        this.mCommand = new BaseCharacter[this.mCommandMax];      // command
        this.mInput = new BaseCharacter(image);                    // input
        this.mCorrectInput = new int[this.mCommandMax];           // input box
        // command
        for (int i = 0; i < this.mCommand.length; i++) {
            // base character class
            this.mCommand[i] = new BaseCharacter(image);
        }
        // Command value
        this.mValue = new BaseCharacter(image);
    }

    /*
        Initialize
     */
    public void InitActionCommand() {
        // load image file
        // command
        for (int i = 0; i < this.mCommand.length; i++) {
            this.mCommand[i].LoadCharaImage(this.mContext, "accommand");
        }
        // input
        this.mInput.LoadCharaImage(this.mContext, "accommand");
        // value
        this.mValue.LoadCharaImage(this.mContext, "commandvalue");

        // the flag that create action command
        mCreationF = false;
        // the element that is treating command.
        this.mCommandElementNow = 0;
        // the count that continue to input correct command.
        mConsecutiveSuccess = 0;
        // the count that current success count.
        mSuccessCount = 0;
        // total success count
        mTotalSuccessCount = 0;
        // the rate that input scale down
        // game level
        int level = Play.GetGameLevel();
        this.mInputScaleDownRate = INPUT_SCALE_DOWN_RATE[level];
        // input margin
        this.mInputMarginError = INPUT_MARGIN_ERROR[level];
        // the interval that create command
        this.mInterval = COMMAND_INTERVAL_TIME[level];
        // value count
        for (int i = 0; i < mValueCount.length; i++) mValueCount[i] = 0;


        // setting
        // command
        for (int i = 0; i < this.mCommand.length; i++) {
            this.mCommand[i].mSize.x = COMMAND_SIZE.x;
            this.mCommand[i].mSize.y = COMMAND_SIZE.y;
            this.mCommand[i].mExistFlag = false;
            this.mCommand[i].mOriginPos.y = 0;
            this.mCommand[1].mOriginPos.x = level*this.mCommand[i].mSize.x;
            // command type
            this.mCorrectInput[i] = COMMAND_TYPE_WAITING;
        }
        // input
        this.mInput.mSize.x = COMMAND_SIZE.x;
        this.mInput.mSize.y = COMMAND_SIZE.y;
        this.mInput.mScale = INPUT_STARTING_SCALE;
        this.mInput.mAlpha = INPUT_DEFAULT_ALPHA;
        this.mInput.mExistFlag = false;

        // command value
        this.mValue.mSize.x = 96;
        this.mValue.mSize.y = 32;
    }
    /*
        Update
        return value is the flag that is creating command.
     */
    public boolean UpdateActionCommand(boolean creationF) {

        // not to create
        if (!creationF) return false;

        // Initialize action command
        if (!mCreationF) this.InitCreation();

        // if creation-flag is true, execute to create command to player's action.
        if (mCreationF) mCreationF = this.UpdateCreation();

        // Update input area
        this.UpdateInputArea();

        // update input info
        this.CheckCommandInfo();

        // check command error and update image that command.
        // if found an error, reset consecutive count.
        if (this.CheckCommandError()) mConsecutiveSuccess = 0;

        return mCreationF;
    }
    /*
        Draw
     */
    public void DrawActionCommand() {
        // command
        for (int i = 0; i < this.mCommandMax; i++) {
            if (this.mCommand[i].mExistFlag) {
                this.mImage.DrawAlpha(
                        this.mCommand[i].mPos.x,
                        this.mCommand[i].mPos.y,
                        this.mCommand[i].mSize.x,
                        this.mCommand[i].mSize.y,
                        this.mCommand[i].mOriginPos.x,
                        this.mCommand[i].mOriginPos.y,
                        this.mCommand[i].mAlpha,
                        this.mCommand[i].mBmp
                );
            }
        }
        // input
        if (this.mInput.mExistFlag) {
            this.mImage.DrawAlphaAndScale(
                    this.mInput.mPos.x,
                    this.mInput.mPos.y,
                    this.mInput.mSize.x,
                    this.mInput.mSize.y,
                    this.mInput.mOriginPos.x,
                    this.mInput.mOriginPos.y,
                    this.mInput.mAlpha,
                    this.mInput.mScale,
                    this.mInput.mBmp
            );
        }
        // Value
        if (this.mValue.mExistFlag) {
            this.mImage.DrawScale(
                    this.mValue.mPos.x,
                    this.mValue.mPos.y,
                    this.mValue.mSize.x,
                    this.mValue.mSize.y,
                    this.mValue.mOriginPos.x,
                    this.mValue.mOriginPos.y,
                    this.mValue.mScale,
                    this.mValue.mBmp
            );
        }
    }
    /*
        Release
     */
    public void ReleaseActionCommand() {
        this.mContext = null;
        this.mImage = null;
        for (int i = 0; i < this.mCommand.length; i++) {
            this.mCommand[i].ReleaseCharaBmp();
            this.mCommand[i] = null;
        }
        // value
        this.mValue.ReleaseCharaBmp();
        this.mValue = null;
    }

    /*
        Initialize command to create
     */
    private void InitCreation() {
        // command
        for (int i = 0; i < this.mCommandMax; i++) {
            this.mCommand[i].mPos.x = COMMAND_STARTING_POSITION.x + (this.mCommand[i].mSize.x * i) + COMMAND_SPACE_X;
            this.mCommand[i].mPos.y = COMMAND_STARTING_POSITION.y;
        }
        // set the flag that is creating command.
        mCreationF = true;
        // reset consecutive count
        mSuccessCount = 0;
    }

    /*
        Update to create command
     */
    private boolean UpdateCreation() {

        boolean creationF = true;

        // loop to command max
        for (int i = 0; i < this.mCommandMax; i++) {
            // when command is created, to next
            if (this.mCommand[i].mExistFlag) continue;
            // count time
            this.mCommand[i].mTime++;
            // to show command whenever counted setting time.
            if (this.mCommand[i].mTime % (this.mInterval * (i + 1)) == 0) {
                this.mCommand[i].mExistFlag = true;
                this.mCommandElementNow = i;           // element of command
                this.mCommand[i].mTime = 0;           // reset count time

                // Initialize input
                this.InitInput(i);

                // when over setting time, to show error.
                if (0 < i && i <= this.mCommandMax) {   // check error
                    if (this.mCorrectInput[i - 1] != COMMAND_TYPE_SUCCESS &&
                            this.mCorrectInput[i - 1] != COMMAND_TYPE_ERROR) {
                        // substitute error to input variable
                        this.mCorrectInput[i - 1] = COMMAND_TYPE_ERROR;
                        // change image
                        this.mCommand[i - 1].mOriginPos.y = this.mCommand[i - 1].mSize.y*COMMAND_TYPE_ERROR;
                        // reset consecutive success
                        mConsecutiveSuccess = 0;
                        // not to show value
                        this.mValue.mExistFlag = false;
                    }
                }
                return true;
            }
        }
        // when showed final command, not to show command after counted setting time.
        if (this.mCommand[this.mCommandMax - 1].mExistFlag) {
            // count time
            this.mCommand[this.mCommandMax - 1].mTime++;
            // to reached setting time.
            if (this.mCommand[this.mCommandMax - 1].mTime % this.mInterval == 0) {
                // reset element
                this.mCommandElementNow = -1;
                // reset count time
                this.mCommand[this.mCommandMax - 1].mTime = 0;
                // reset setting that origin position and correct input info
                for (int i = 0; i < this.mCommandMax; i++) {
                    this.mCommand[i].mExistFlag = false;
                    this.mCorrectInput[i] = COMMAND_TYPE_WAITING;
                }
                this.mInput.mExistFlag = creationF = false;
                // not to show value
                this.mValue.mExistFlag = false;
            }
        }
        return creationF;
    }

    /*
        Check command type
     */
    private boolean CheckCommandError() {
        // error flag
        boolean error = false;
        // kind of command type
        int correctType[] = {
                COMMAND_TYPE_WAITING,
                COMMAND_TYPE_SUCCESS,
                COMMAND_TYPE_ERROR,
        };

        // when creation flag is true, go to creation
        if (mCreationF && 0 <= this.mCommandElementNow && this.mCommandElementNow < this.mCommandMax) {
            // loop to command kind
            for (int j = 0; j < COMMAND_TYPE_KIND; j++) {
                for (int i = this.mCommandElementNow; i < this.mCommandMax; i++) {
                    // change image that correct or not.
                    if (this.mCorrectInput[i] == correctType[j]) {
                        // when error input
                        if (this.mCorrectInput[i] == COMMAND_TYPE_ERROR) error = true;
                        // change origin position
                        this.mCommand[i].mOriginPos.y = correctType[j] * this.mCommand[i].mSize.y;
                    }
                }
            }
        }
        return error;
    }

    /*
        Check the input information that touched command.
     */
    private void CheckCommandInfo() {

        for (int i = 0; i < this.mCommandMax; i++) {
            if (this.mCommand[i].mExistFlag &&
                    this.mCorrectInput[i] == COMMAND_TYPE_WAITING) {
                // calculate the area of circle which is input.
                double inputRadius = (double) (this.mInput.mSize.x * this.mInput.mScale) / 2;
                double inputArea = (inputRadius * inputRadius) * 3.14;
                // Game level
                int level = Play.GetGameLevel();
                // the difference margin between input image and command image.
                // outer
                double inputRadiusOuter = inputRadius - DIFFERENCE_RADIUS[level];
                double inputAreaOuter = (inputRadiusOuter * inputRadiusOuter) * 3.14;
                double differenceOuter = inputAreaOuter - COMMAND_DEFAULT_AREA;
                // inner
                double differenceInner = inputArea-COMMAND_DEFAULT_INNER_AREA[level];
                // to check overlap between touched position and command position.
                if (Collision.CheckTouch(
                        this.mCommand[i].mPos.x,
                        this.mCommand[i].mPos.y,
                        this.mCommand[i].mSize.x,
                        this.mCommand[i].mSize.y,
                        this.mCommand[i].mScale
                )) {
                    // if calculated difference margin is around the command size.
                    // and only just touched input image.
                    if (GameView.GetTouchAction() == MotionEvent.ACTION_DOWN &&
                            inputArea < COMMAND_DEFAULT_AREA &&
                            (-1*this.mInputMarginError[1])/2 < differenceInner &&
                            differenceInner < this.mInputMarginError[1]) {
                        // substitute success info to variable.
                        this.mCorrectInput[i] = COMMAND_TYPE_SUCCESS;
                        // total success
                        mTotalSuccessCount++;
                        // update count that consecutive input correct command.
                        mConsecutiveSuccess++;
                        // current success count
                        mSuccessCount++;

                        // Check value
                        // perfect
                        double perfect = this.mInputMarginError[1]*0.5;
                        if (Math.abs(differenceInner) <= perfect) {
                            this.mValue.mType = VALUE_PERFECT;
                            // count
                            mValueCount[VALUE_PERFECT]++;
                            // add point
                            RaceScore.UpdateTotalPoint(100);
                        } else {
                            this.mValue.mType = VALUE_NICE;
                            mValueCount[VALUE_NICE]++;
                            // add point
                            RaceScore.UpdateTotalPoint(50);
                        }
                        // Initialize value
                        this.InitCommandValue(this.mCommand[i].mPos.x,this.mCommand[i].mPos.y);

                    // Input area is outer
                    } else if ( GameView.GetTouchAction() == MotionEvent.ACTION_DOWN &&
                            COMMAND_DEFAULT_AREA <= inputArea &&
                            0 < differenceOuter &&
                            differenceOuter <= this.mInputMarginError[0]) {
                        // substitute success info to variable.
                        this.mCorrectInput[i] = COMMAND_TYPE_SUCCESS;
                        // total success
                        mTotalSuccessCount++;
                        // update count that consecutive input correct command.
                        mConsecutiveSuccess++;
                        // current success count
                        mSuccessCount++;

                        // Check value
                        // perfect
                        double perfect = this.mInputMarginError[0]*0.5;
                        if (Math.abs(differenceOuter) <= perfect) {
                            this.mValue.mType = VALUE_PERFECT;
                            // add point
                            RaceScore.UpdateTotalPoint(100);
                        } else {
                            this.mValue.mType = VALUE_NICE;
                            // add point
                            RaceScore.UpdateTotalPoint(50);
                        }
                        // Initialize value
                        this.InitCommandValue(this.mCommand[i].mPos.x,this.mCommand[i].mPos.y);
                    } else {
                        // incorrect input and substitute error to input variable
                        this.mCorrectInput[i] = COMMAND_TYPE_ERROR;
                        // reset consecutive success
                        mConsecutiveSuccess = 0;
                        // Initialize command value
                        this.mValue.mType = VALUE_BAD;
                        mValueCount[VALUE_BAD]++;
                        this.InitCommandValue(this.mCommand[i].mPos.x,this.mCommand[i].mPos.y);
                    }
                }
            }
        }
    }

    /*
        Initialize value
     */
    private void InitCommandValue(int x, int y) {
        this.mValue.mExistFlag = true;
        this.mValue.mPos.x = x;
        this.mValue.mPos.y = y-this.mValue.mSize.y;
        this.mValue.mOriginPos.y = this.mValue.mSize.y*this.mValue.mType;
    }

    /*
        Initialize input
     */
    private void InitInput(int element) {
        // initialize input
        this.mInput.mScale = INPUT_STARTING_SCALE;
        this.mInput.mPos.x = this.mCommand[element].mPos.x;
        this.mInput.mPos.y = this.mCommand[element].mPos.y;
        this.mInput.mExistFlag = true;
    }
    /*
        Update input area
     */
    private void UpdateInputArea() {
        if (this.mInput.mExistFlag && 0.05f < this.mInput.mScale) {
            this.mInput.mScale -= this.mInputScaleDownRate;
        } else if (this.mInput.mScale <= 0.05f) {
            this.mInput.mScale = 0.05f;
        }
    }

    /*
        Getter functions
     */
    /*
        Get current success count
     */
    public static int GetSuccessCount() { return mSuccessCount; }
    /*
    Get consecutive success count
 */
    public static int GetConsecutiveCount() { return mConsecutiveSuccess; }
    /*
        Get total success count
     */
    public static int GetTotalSuccess() { return mTotalSuccessCount; }
    /*
        Get the flag that is creating command
     */
    public static boolean GetCreationFlag() { return mCreationF; }
    /*
        Get value count
     */
    public static int[] GetValueCount() { return mValueCount; }
}