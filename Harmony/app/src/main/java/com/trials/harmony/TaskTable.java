package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * Created by Kohei Moroi on 8/4/2016.
 */
public class TaskTable extends ButtonManager implements HasScene, HasSystem {
    private final static int READY      = -1;
    private final static int INITIALIZE = 0;
    private final static int UPDATE     = 1;
    private final static float STARTING_SCALE_RATE = 0.1f;
    private final static float DEFAULT_ADD_SCALE_RATE = 0.1f;
    private final static float DEFAULT_MAX_SCALE_RATE = 1.0f;
    private final static int TASK_TABLE_POSITION_Y = 440;
    private final static int THE_FIXED_INTERVAL_TIME_TO_NOT_TO_SHOW_THE_TABLE = 1000;
    private Context mContext;
    private Image mImage;
    private TaskButton mButtons[];
    private CharacterEx mTable;
    private CharacterEx mEnterCircle;
    private int mProcess;
    private Utility mUtility;
    private int mCurrentType;
    private int mCurrentMusicId;
    private boolean mAvailableToRotate;
    private int mCurrentPage;

    public TaskTable(Context context, Image image) {
        this.mContext = context;
        this.mImage = image;
        int scene = SceneManager.GetCurrentScene();
        int mode = SystemManager.GetPlayMode();
        int buttons[] = null;
        if (scene == SCENE_OPENING) {
            int table[] = {
                    BUTTON_RECOGNITION,
                    BUTTON_START,
                    BUTTON_TUTORIAL,
                    BUTTON_CREDIT_VIEW,
            };
            buttons = new int[table.length];
            System.arraycopy(table,0,buttons,0,table.length);
            // Main menu
        } else if (scene == SCENE_MAIN_MENU) {
            int table[] = {
                    BUTTON_RECOGNITION,
                    BUTTON_SELECT_MODE,
                    BUTTON_SELECT_TUNE,
                    BUTTON_SELECT_LEVEL,
                    BUTTON_PLAY_THE_GAME,
                    BUTTON_ASSOCIATION_LIBRARY,
                    BUTTON_RETURN_TO_OP,
            };
            buttons = new int[table.length];
            System.arraycopy(table,0,buttons,0,table.length);
            // Tutorial
        } else if (scene == SCENE_TUTORIAL) {
            int table[] = {
                    BUTTON_RECOGNITION,
                    BUTTON_SOUND,
                    BUTTON_SENTENCE,
                    BUTTON_RETURN_TO_OP,
                    BUTTON_RETURN_TO_MA,
            };
            buttons = new int[table.length];
            System.arraycopy(table, 0, buttons, 0, table.length);
        } else if (scene == SCENE_PROLOGUE || scene == SCENE_PLAY) {
            if (mode == MODE_ASSOCIATION_IN_EMOTIONS ||
                mode == MODE_ASSOCIATION_IN_FRUITS ||
                mode == MODE_ASSOCIATION_IN_ALL) {
                int table[] = {
                        BUTTON_RECOGNITION,
                        BUTTON_ASSOCIATION_LIBRARY,
                        BUTTON_RETURN_TO_OP,
                        BUTTON_RETURN_TO_MA
                };
                int extra = (scene == SCENE_PROLOGUE)?1:0;
                buttons = new int[table.length+extra];
                System.arraycopy(table, 0, buttons, 0, table.length);
                if (0 < extra) {    // when the current scene is prologue, to add the button to play the game.
                    buttons[table.length] = BUTTON_PLAY_THE_GAME;
                }
            } else {
                int table[] = {
                        BUTTON_RECOGNITION,
                        BUTTON_RETURN_TO_OP,
                        BUTTON_RETURN_TO_MA,
                };
                buttons = new int[table.length];
                System.arraycopy(table,0,buttons,0,table.length);
            }
        // Other scene
        } else {
            int table[] = {
                    BUTTON_RECOGNITION,
                    BUTTON_RETURN_TO_OP,
                    BUTTON_RETURN_TO_MA,
            };
            buttons = new int[table.length];
            System.arraycopy(table,0,buttons,0,table.length);
        }
        if (buttons != null) {
            this.mButtons = new TaskButton[buttons.length];
            for (int i = 0; i < this.mButtons.length; i++) {
                this.mButtons[i] = new TaskButton(context, image);
                this.mButtons[i].mType = buttons[i];
            }
        }
        this.mTable = new CharacterEx(context,image);
        this.mEnterCircle = new CharacterEx(context,image);
        this.mUtility = new Utility();
    }
    public void InitManager() {
        String frameImage = "taskframe";
        Point screen = MainView.GetScreenSize();
        Point pos = new Point();
        Point size = new Point(320,320);
        Point enterPos = new Point();
        // set table
        this.mTable.mWholeSize.x = size.x*DEFAULT_MAX_SCALE_RATE;
        this.mTable.mWholeSize.y = size.y*DEFAULT_MAX_SCALE_RATE;
        pos.x = (screen.x-(int)this.mTable.mWholeSize.x)>>1;
        pos.y = TASK_TABLE_POSITION_Y;
        this.mTable.InitCharacterEx(frameImage,pos.x,pos.y,size.x,size.y,0,0,255,STARTING_SCALE_RATE,0);
        this.mTable.mExistFlag = false;
        // set each button
        int kind = this.mButtons.length;
        int degree = 360/kind;           // one angle which is positive degree
        int angle;
        int sum;
        size = new Point(64,64);
        int origin;
        for (int i = 0; i < this.mButtons.length; i++) {
            this.mButtons[i].mWholeSize.x = size.x*DEFAULT_MAX_SCALE_RATE;
            this.mButtons[i].mWholeSize.y = size.y*DEFAULT_MAX_SCALE_RATE;
            sum = 270+(degree*i);
            angle = sum%360;
            origin = super.ConvertTypeIntoElementToTransit(this.mButtons[i].mType);
            this.mButtons[i].InitTask("taskbuttons",0,0,size.x,size.y,0,size.y*origin,255,STARTING_SCALE_RATE);
            // set position to relate to the positive degree
            this.mButtons[i].SetAngle(angle,
                    this.mTable.mPos.x+((int)this.mTable.mWholeSize.x>>1)-((int)this.mButtons[i].mWholeSize.x>>1),
                    this.mTable.mPos.y+((int)this.mTable.mWholeSize.y>>1)-((int)this.mButtons[i].mWholeSize.y>>1),
                    (int)this.mTable.mWholeSize.x>>1);
            if (270 == angle) {
                enterPos.x = this.mButtons[i].mPos.x-((int)this.mButtons[i].mWholeSize.x>>2);
                enterPos.y = this.mButtons[i].mPos.y-((int)this.mButtons[i].mWholeSize.y>>2);
            }
            this.mButtons[i].mExistFlag = false;
        }
        // enter image
        this.mEnterCircle.InitCharacterEx(frameImage,enterPos.x,enterPos.y,96,96,0,this.mTable.mSize.y,255,STARTING_SCALE_RATE,1);
        this.mEnterCircle.mExistFlag = false;
        // the process
        this.mProcess = READY;
        // the current type which is button type
        this.mCurrentType = BUTTON_EMPTY;
        this.mAvailableToRotate = true;
        this.mCurrentMusicId = BUTTON_EMPTY;
        this.mCurrentPage = 1;
    }
    public boolean UpdateManager() {
        // task is waiting unless pressing the button to call the task table.
        if (this.mProcess == READY) {
            // each default values
            float subScale = DEFAULT_ADD_SCALE_RATE*-1;
            // when the current process is ready to initialize
            // not to show by the variable scale function.
            this.mTable.VariableScaleWhenReachedTheFixedValueNoExistence(subScale,0.0f);
            this.mEnterCircle.VariableScaleWhenReachedTheFixedValueNoExistence(subScale,0.0f);
            for (TaskButton t: this.mButtons) t.VariableScaleWhenReachedTheFixedValueNoExistence(subScale,0.0f);
            return false;
        } else if (this.mProcess == INITIALIZE) {
            if (this.mUtility.ToMakeTheInterval(100)) {
                this.SetTable();
                this.mProcess = UPDATE;
            }
            return true;
        } else if (this.mProcess == UPDATE) {
            // maybe task manager will get the process as pressed the button.
            // each default values
            float maxScale = 1.0f;
            // variable scale
            this.mTable.VariableScaleWhenReachedTheFixedValueNoExistence(DEFAULT_ADD_SCALE_RATE,maxScale);
            this.mEnterCircle.VariableScaleWhenReachedTheFixedValueNoExistence(DEFAULT_ADD_SCALE_RATE,maxScale);
            this.mTable.mWholeSize.x = this.mTable.mSize.x * this.mTable.mScale;
            this.mTable.mWholeSize.y = this.mTable.mSize.y * this.mTable.mScale;
            this.mEnterCircle.mWholeSize.x = this.mEnterCircle.mSize.x * this.mEnterCircle.mScale;
            this.mEnterCircle.mWholeSize.y = this.mEnterCircle.mSize.y * this.mEnterCircle.mScale;
            // when touched the table image,
            boolean touching = Collision.CheckTouch(
                    this.mTable.mPos.x, this.mTable.mPos.y,
                    this.mTable.mSize.x, this.mTable.mSize.y,
                    this.mTable.mScale-0.1f);
            if (!touching) {
                int action = MainView.GetTouchAction();
                // when not to touch the image after the fixed time,
                // not to appear
                if (this.mUtility.ToMakeTheInterval(THE_FIXED_INTERVAL_TIME_TO_NOT_TO_SHOW_THE_TABLE)) {
                    this.mProcess = READY;
                }
                if (action == MotionEvent.ACTION_DOWN) {
                    this.mProcess = READY;
                }
            }
            for (TaskButton t: this.mButtons) {
                // variable scale rate
                t.VariableScaleWhenReachedTheFixedValueNoExistence(DEFAULT_ADD_SCALE_RATE, maxScale);
                t.mWholeSize.x = t.mSize.x * t.mScale;
                t.mWholeSize.y = t.mSize.y * t.mScale;
                // update alpha
                t.UpdateAlpha();
            }
            if (!this.mAvailableToRotate) {
                this.mAvailableToRotate = this.mUtility.ToMakeTheInterval(30);
                return true;
            }
            // get finger's position
            Point fingerPos = MainView.GetTouchedPosition();
            Point screen = MainView.GetScreenSize();
            // when the current finger's position is left,
            // to rotate to left or otherwise to rotate right
            int add;
            add = (fingerPos.x < screen.x>>1)?-2:2;
            for (int i = 0; i < this.mButtons.length; i++) {
                if (touching) {
                    this.mButtons[i].mAddAngle = add;
                    this.mButtons[i].mJustAngle = -1;
                    if (this.mButtons[i].mAngle < 0) {
                        this.mButtons[i].mAngle = 360-Math.abs(this.mButtons[i].mAngle);
                    } else if (360 <= this.mButtons[i].mAngle) {
                        this.mButtons[i].mAngle = 0;
                    }
                } else {
                    if (this.mButtons[i].mJustAngle == -1) {
                        if (this.mButtons[0].mJustAngle == -1) {
                            int difference;
                            int kind = this.mButtons.length;
                            int degree = 360 / kind;           // one angle which is positive degree
                            int adjust = degree;
                            int sum;
                            int dummy;
                            for (int j = 0; j < kind; j++) {
                                sum = 270 + degree * j;
                                dummy = this.mButtons[0].mAngle % 360;               // the global angel variable must be positive number!
                                adjust = sum % 360;
                                difference = Math.abs(adjust) - Math.abs(dummy);
                                if (Math.abs(difference) < (degree >> 1)) {
                                    this.mButtons[0].mJustAngle = Math.abs(adjust); // also, the global just angle variable must be positive.
                                    adjust = difference;
                                    break;
                                }
                            }
                            this.mButtons[0].mAddAngle = (0 < adjust) ? 2 : -2;
                        } else {
                            int degree = 360/this.mButtons.length;
                            this.mButtons[i].mAddAngle = this.mButtons[0].mAddAngle;
                            this.mButtons[i].mJustAngle = this.mButtons[0].mJustAngle+(degree*i);
                            this.mButtons[i].mJustAngle %= 360;
                            int difference = 270-this.mButtons[i].mJustAngle;
                            if (Math.abs(difference) <= 10) {       // adjust the position to relate to the image of enter
                                this.mButtons[i].mJustAngle = 270;
                            }
                        }
                    }
                    int angle = this.mButtons[i].mAngle % 360;     // angle must be positive number.
                    int difference = this.mButtons[i].mJustAngle - angle;
                    if (Math.abs(difference) <= Math.abs(add)) {
                        this.mButtons[i].mJustAngle %= 360;
                        this.mButtons[i].mAngle = this.mButtons[i].mJustAngle;
                        this.mButtons[i].mAddAngle = 0;
                    }
                }
                // when either button just reached to 270,
                // to return own type which is button type
                int type = this.mButtons[i].UpdateRotate(
                        this.mTable.mPos.x + ((int) this.mTable.mWholeSize.x >> 1) - ((int) this.mButtons[i].mWholeSize.x >> 1),
                        this.mTable.mPos.y + ((int) this.mTable.mWholeSize.y >> 1) - ((int) this.mButtons[i].mWholeSize.y >> 1),
                        (int) this.mTable.mWholeSize.x >> 1);
                if (this.mAvailableToRotate) {
                    if (BUTTON_EMPTY < type && this.mCurrentType != type) {
                        this.mAvailableToRotate = false;
                        this.mCurrentType = type;
                    }
                }
                // get music id
                this.mCurrentMusicId = (-1 < this.mButtons[i].GetMusicId())?this.mButtons[i].GetMusicId():this.mCurrentMusicId;
            }
            return true;
        }
        return false;
    }
    public void DrawManager() {
        this.mTable.DrawCharacterEx();
        this.mEnterCircle.DrawCharacterEx();
        for (TaskButton bt: this.mButtons) {
            bt.DrawTask();
        }
    }
    public void ReleaseManager() {
        this.mEnterCircle.ReleaseCharacterEx();
        this.mEnterCircle = null;
        this.mTable.ReleaseCharacterEx();
        this.mTable = null;
        for (int i = 0; i < this.mButtons.length; i++) {
            this.mButtons[i].ReleaseTask();
            this.mButtons[i] = null;
        }
        this.mUtility.ReleaseUtility();
        this.mUtility = null;
    }
    // Set table
    private void SetTable() {
        // table
        Point size = new Point(320,320);
        Point screen = MainView.GetScreenSize();
        Point enterPos = new Point();
        this.mTable.mWholeSize.x = size.x*DEFAULT_MAX_SCALE_RATE;
        this.mTable.mWholeSize.y = size.y*DEFAULT_MAX_SCALE_RATE;
        this.mTable.mPos.x = (screen.x-(int)this.mTable.mWholeSize.x)>>1;
        this.mTable.mPos.y = TASK_TABLE_POSITION_Y;
        this.mTable.mScale = STARTING_SCALE_RATE;
        this.mTable.mExistFlag = true;
        // set each button
        int kind = this.mButtons.length;
        int degree = 360/kind;           // one angle which is positive degree
        int angle;
        int sum;
        size = new Point(64,64);
        for (int i = 0; i < this.mButtons.length; i++) {
            sum = 270+(degree*i);
            angle = sum%360;
            this.mButtons[i].mWholeSize.x = size.x*DEFAULT_MAX_SCALE_RATE;
            this.mButtons[i].mWholeSize.y = size.y*DEFAULT_MAX_SCALE_RATE;
            // set position to relate to the positive degree
            this.mButtons[i].SetAngle(angle,
                    this.mTable.mPos.x+((int)this.mTable.mWholeSize.x>>1)-((int)this.mButtons[i].mWholeSize.x>>1),
                    this.mTable.mPos.y+((int)this.mTable.mWholeSize.y>>1)-((int)this.mButtons[i].mWholeSize.y>>1),
                    (int)this.mTable.mWholeSize.x>>1);
            if (270 == angle) {
                enterPos.x = this.mButtons[i].mPos.x-((int)this.mButtons[i].mWholeSize.x>>2);
                enterPos.y = this.mButtons[i].mPos.y-((int)this.mButtons[i].mWholeSize.y>>2);
                this.mCurrentType = this.mButtons[i].mType;
            }
            this.mButtons[i].mExistFlag = true;
            this.mButtons[i].mAddAngle = 0;
            this.mButtons[i].mScale = STARTING_SCALE_RATE;
        }
        // enter image
        this.mEnterCircle.mPos.x = enterPos.x;
        this.mEnterCircle.mPos.y = enterPos.y;
        this.mEnterCircle.mExistFlag = true;
        this.mEnterCircle.mScale = STARTING_SCALE_RATE;
    }
    //
    // Set table
    // first operator: array of the button
    //
    public void SetTable(int buttons[],int pageNumber) {
        if (this.mButtons != null) {
            for (int i = 0; i < this.mButtons.length; i++) {
                this.mButtons[i].ReleaseTask();
                this.mButtons[i] = null;
            }
        }
        this.mButtons = new TaskButton[buttons.length];
        for (int i = 0; i < this.mButtons.length; i++) {
            this.mButtons[i] = new TaskButton(this.mContext,this.mImage);
            this.mButtons[i].mType = buttons[i];
        }
        // table
        Point size = new Point(320,320);
        Point screen = MainView.GetScreenSize();
        Point enterPos = new Point();
        this.mTable.mWholeSize.x = size.x*DEFAULT_MAX_SCALE_RATE;
        this.mTable.mWholeSize.y = size.y*DEFAULT_MAX_SCALE_RATE;
        this.mTable.mPos.x = (screen.x-(int)this.mTable.mWholeSize.x)>>1;
        this.mTable.mPos.y = TASK_TABLE_POSITION_Y;
        this.mTable.mScale = STARTING_SCALE_RATE;
        this.mTable.mExistFlag = true;
        // set each button
        int kind = this.mButtons.length;
        int degree = 360/kind;           // one angle which is positive degree
        int angle;
        int sum;
        int origin;
        size = new Point(64,64);
        for (int i = 0; i < this.mButtons.length; i++) {
            sum = 270+(degree*i);
            angle = sum%360;
            this.mButtons[i].mWholeSize.x = size.x*DEFAULT_MAX_SCALE_RATE;
            this.mButtons[i].mWholeSize.y = size.y*DEFAULT_MAX_SCALE_RATE;
            origin = super.ConvertTypeIntoElementToTransit(this.mButtons[i].mType);
            this.mButtons[i].InitTask("taskbuttons",0,0,size.x,size.y,0,size.y*origin,255,STARTING_SCALE_RATE);
            // set position to relate to the positive degree
            this.mButtons[i].SetAngle(angle,
                    this.mTable.mPos.x+((int)this.mTable.mWholeSize.x>>1)-((int)this.mButtons[i].mWholeSize.x>>1),
                    this.mTable.mPos.y+((int)this.mTable.mWholeSize.y>>1)-((int)this.mButtons[i].mWholeSize.y>>1),
                    (int)this.mTable.mWholeSize.x>>1);
            if (270 == angle) {
                enterPos.x = this.mButtons[i].mPos.x-((int)this.mButtons[i].mWholeSize.x>>2);
                enterPos.y = this.mButtons[i].mPos.y-((int)this.mButtons[i].mWholeSize.y>>2);
                this.mCurrentType = this.mButtons[i].mType;
            }
            this.mButtons[i].mExistFlag = true;
            this.mButtons[i].mAddAngle = 0;
            this.mButtons[i].mScale = STARTING_SCALE_RATE;
        }
        // enter image
        this.mEnterCircle.mPos.x = enterPos.x;
        this.mEnterCircle.mPos.y = enterPos.y;
        this.mEnterCircle.mExistFlag = true;
        this.mEnterCircle.mScale = STARTING_SCALE_RATE;
        this.mCurrentPage = pageNumber;
    }
    // Set music id
    public void SetMusicId(int id[]) {
        int count = 0;
        for (TaskButton t: this.mButtons) {
            if (t.mType == BUTTON_TUNE_NUMBER) {
                if (id.length <= count) break;
                t.mMusicId = id[count];
                count++;
            }
        }
    }
    // is called
    public void ToCallTaskTable() { this.mProcess = INITIALIZE; }
    // get the current type which is button type
    public int GetCurrentType() { return this.mCurrentType; }
    // get the current music id
    public int GetMusicId() { return this.mCurrentMusicId; }
    int getCurrentPage() { return this.mCurrentPage; }
}