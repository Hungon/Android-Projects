package com.trials.harmony;

import android.content.Context;
import android.graphics.Point;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 4/25/2016.
 */
public class RecognitionButtonManager extends ButtonManager implements HasScene, HasSystem {

    // static variables
    enum REINITIALIZE_TASK {
        MUSIC_LIST_IN_TUTORIAL,
        SELECT_MODE_IN_MAIN_MENU,
        SELECT_LEVEL_IN_MAIN_MENU,
        MUSIC_LIST_IN_MAIN_MENU,
        SELECT_ASSOCIATION,
        ASSOCIATION_IN_EMOTIONS,
        ASSOCIATION_IN_FRUITS,
        ASSOCIATION_IN_ALL
    }
    private final static int LISTING_MUSIC_ID_MAX = 4;
    private final static int LISTING_ASSOCIATION_ID_MAX = 5;
    // filed
    private Context mContext;
    private RecognitionButton       mButtons[];
    private int mSelectedTaskType;
    // the direction to notice the position that finger nears by the button.
    private int mDirection;
    private Utility mUtility;
    private int mElementToAlpha[];
    // task table
    private TaskTable mTaskTable;
    private int mCountToObtain;
    private int mPreviewCountToObtain;
    private float mHiddenTheButton;
    private int mMusicIdArray[];
    private int mIsSelectingMusicId;
    private int mIsSelectingTaskType;
    private static int sLoadingType;
    private static int mGuidanceButtonType;
    private static boolean mIsOpeningTask;
    private static int mTypeToObtain;
    private static REINITIALIZE_TASK mReInitializeTaskType;
    /*
        Constructor
    */
    public RecognitionButtonManager(Context context, Image image) {
        this.mContext = context;
        int element = 1;
        // to allot the memory
        this.mButtons = new RecognitionButton[element];
        for (int i = 0; i < element; i++) {
            this.mButtons[i] = new RecognitionButton(context,image);
        }
        this.mTaskTable = new TaskTable(context,image);
        this.mUtility = new Utility();
    }
    /*
        Initialize
    */
    public void InitManager() {
        Point screen = MainView.GetScreenSize();
        // to set recognition buttons
        Point pos = new Point((screen.x-TASK_BUTTON_SIZE.x)>>1, (screen.y-TASK_BUTTON_SIZE.y)-15);
        int origin = super.ConvertTypeIntoElementToTransit(BUTTON_CALL_TASK_TABLE);
        this.mButtons[0].InitRecognitionButton(
                TASK_BUTTON_FILE_NAME,"click",
                pos,TASK_BUTTON_SIZE,new Point(0,origin*TASK_BUTTON_SIZE.y),
                255,1.5f,BUTTON_CALL_TASK_TABLE);
        this.mIsSelectingTaskType = mTypeToObtain = mGuidanceButtonType = BUTTON_EMPTY;
        this.mSelectedTaskType = -2;
        this.mDirection = RecognitionButton.DISTANCE;
        this.mIsSelectingMusicId = BUTTON_EMPTY;
        // task table
        this.mTaskTable.InitManager();
        this.mPreviewCountToObtain = this.mCountToObtain = 0;
        this.mHiddenTheButton = 0.0f;
        mIsOpeningTask = false;
        mReInitializeTaskType = null;
        mGuidanceButtonType = mTypeToObtain = sLoadingType = BUTTON_EMPTY;
    }
    /*
        Update
        return value is next scene number
    */
    public int UpdateManager() {
        int transition = Scene.SCENE_MAIN;
        int type = BUTTON_EMPTY;
        int direction = RecognitionButton.DISTANCE;
        // the area to seek the button
        Point area = new Point(64, 64);
        // to update buttons
        if (!mIsOpeningTask) {
            for (RecognitionButton r: this.mButtons) {
                /// to update scale
                if (0 < this.mHiddenTheButton) r.SetExist(true);
                r.VariableScaleWhenUpToZeroNoExistence(this.mHiddenTheButton, 1.0f);
                if (r.GetExist() && r.GetScale() == 1.0f) {
                    // to seek the button.
                    direction = r.ToSeekTheButton(area);
                    // when to got the direction, to be break
                    if (direction != RecognitionButton.DISTANCE) {
                        this.mDirection = direction;
                        break;
                    }
                    // to get the scene as is pressed the r.
                    transition = r.IsPressedTheButton();
                    if (r.AsPressesTheButton()) {
                        // when user pressed the button,
                        mGuidanceButtonType = type = r.GetType();
                        // to get changeable type
                        if (r.GetChangeableType() != BUTTON_EMPTY) {
                            // to get own type to diverge process as pressed.
                            mTypeToObtain = r.GetChangeableType();
                        }
                        break;
                    }
                }
            }
            mIsOpeningTask = !this.mButtons[this.mButtons.length-1].GetExist();
        } else {
            // direction is nothing
            this.mDirection = RecognitionButton.DISTANCE;
            this.mIsSelectingTaskType = this.mTaskTable.GetCurrentType();
            if (this.mIsSelectingTaskType != this.mSelectedTaskType) {
                this.mSelectedTaskType = mGuidanceButtonType = this.mIsSelectingTaskType;
            } else {
                mGuidanceButtonType = BUTTON_EMPTY;
            }
            if (this.mIsSelectingTaskType == BUTTON_TUNE_NUMBER) {
                int previewId = this.mTaskTable.GetMusicId();
                if (previewId != this.mIsSelectingMusicId) {
                    // get music id
                    this.mIsSelectingMusicId = previewId;
                    mGuidanceButtonType = BUTTON_TUNE_NUMBER;
                } else {
                    mGuidanceButtonType = BUTTON_EMPTY;
                }
            }
            if (BUTTON_EMPTY < this.mIsSelectingTaskType) {
                // to validate motion and update task table and get the process in the Scene class.
                if (this.validateMotion()) {
                    transition = this.updateTaskTable();
                }
            } else if (this.mIsSelectingTaskType == BUTTON_EMPTY) {
                this.mCountToObtain = 0;
            }
            this.updateTaskTableAsPressedBackKey();
        }

        // reinitialize the task table
        this.reinitializeTaskTable(mReInitializeTaskType);

        // to call the task table as pressed the button to call the task table
        if (mGuidanceButtonType == BUTTON_CALL_TASK_TABLE) this.mTaskTable.ToCallTaskTable();
        // to update task table
        // when during pressing the button,
        // to update task table
        this.mHiddenTheButton = this.mTaskTable.UpdateManager() ? -0.1f : 0.1f;
        if (this.mHiddenTheButton < 0) {
            return transition;
        } else {
            mIsOpeningTask = false;
        }
        if (type == BUTTON_EMPTY) mGuidanceButtonType = BUTTON_EMPTY;
        if (direction == RecognitionButton.DISTANCE) this.mDirection = RecognitionButton.DISTANCE;
        return transition;
    }

    private boolean validateMotion() {
        int motionEvent = MainView.GetTouchAction();
        if (motionEvent == MotionEvent.ACTION_DOWN) {
            this.mCountToObtain++;
        }
        if (1 < this.mCountToObtain) {
            if (this.mCountToObtain % 2 == 0 && motionEvent == MotionEvent.ACTION_UP) {
                this.mCountToObtain++;
            }
        }
        if (this.mPreviewCountToObtain != this.mCountToObtain) {
            this.mPreviewCountToObtain = this.mCountToObtain;
        } else {
            if (this.mUtility.ToMakeTheInterval(160)) {
                this.mPreviewCountToObtain = this.mCountToObtain = 0;
            }
        }
        return (4<this.mCountToObtain);
    }

    // the return value is the process in Scene class.
    private int updateTaskTable() {
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2);
        int transition = Scene.SCENE_MAIN;
        // to obtain the process of the button type
        // to call recognizer as pressed recognition button.
        if (this.mIsSelectingTaskType == BUTTON_RECOGNITION) RecognitionMode.CallRecognizer();
        // Set system variable
        boolean obtain = false;
        int count = 0;
        do {
            if (count == 0) {
                obtain = SetNextScene(this.mIsSelectingTaskType);
                transition = (obtain)?Scene.SCENE_RELEASE:Scene.SCENE_MAIN;
            } else if (count == 1) {
                obtain = SetLevel(this.mIsSelectingTaskType);
            } else if (count == 2) {
                this.SetMode(this.mIsSelectingTaskType);
            } else if (count == 3) {
                obtain = this.SetEachMenu(this.mIsSelectingTaskType);
            } else if (count == 4) {
                obtain = this.LoadMusicId(this.mIsSelectingTaskType);
            } else if (count == 5) {
                obtain = this.setAssociation(this.mIsSelectingTaskType);
            } else if (count == 6) {
                this.loadAssociation(this.mIsSelectingTaskType);
                break;
            }
            count++;
        } while(!obtain);
        this.mCountToObtain = 0;
        // get the type to obtain process
        mTypeToObtain = this.mIsSelectingTaskType;
        int id = this.mIsSelectingMusicId;
        if (id != BUTTON_EMPTY) {
            MusicSelector.SetCurrentElementToPlayMusic(id);
            MusicSelector.SetMusicElementRecognized(id);
        }
        return transition;
    }

    private void updateTaskTableAsPressedBackKey() {
        // as pressed back-key
        if (MainView.GetKeyEvent()==KeyEvent.KEYCODE_BACK) {
            int scene = SceneManager.GetCurrentScene();
            int buttons[] = null;
            if (scene == SCENE_MAIN_MENU) {
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
                System.arraycopy(table, 0, buttons, 0, table.length);
                mGuidanceButtonType = BUTTON_BACK_KEY_TO_MAIN_MENU;
                // when the current mode is Association, to set the something mode relative to association.
                SystemManager.checkInvalidMode();
            } else if (scene == SCENE_PROLOGUE || scene == SCENE_PLAY) {
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
                mGuidanceButtonType = BUTTON_BACK_KEY_TO_MAIN_PROLOGUE;
            }
            if (buttons != null) {
                mTypeToObtain = sLoadingType = BUTTON_EMPTY;
                this.mTaskTable.SetTable(buttons, 1);
            }
        }
    }

    private boolean SetLevel(int type) {
        if (type == BUTTON_EMPTY) return false;
        int level[] = {LEVEL_EASY,LEVEL_NORMAL,LEVEL_HARD};
        int buttons[] = {BUTTON_EASY,BUTTON_NORMAL,BUTTON_HARD};
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == type) {
                SystemManager.SetGameLevel(level[i]);
                return true;
            }
        }
        return false;
    }

    /*
        Set mode
    */
    private void SetMode(int type) {
        if (type == BUTTON_EMPTY || sLoadingType == BUTTON_ASSOCIATION_LIBRARY) return;
        int mode[] = {
                MODE_SOUND,MODE_SENTENCE,
                MODE_ASSOCIATION,MODE_ASSOCIATION_IN_EMOTIONS,
                MODE_ASSOCIATION_IN_FRUITS
        };
        int buttons[] = {
                BUTTON_SOUND,BUTTON_SENTENCE,
                BUTTON_ASSOCIATION,BUTTON_ASSOCIATION_IN_EMOTION,
                BUTTON_ASSOCIATION_IN_FRUITS
        };
        for (int i = 0 ; i < buttons.length; i++) {
            if (buttons[i] == type) {
                SystemManager.SetPlayMode(mode[i]);
            }
        }
    }
    /*
        Set next scene by the button type
    */
    private boolean SetNextScene(int type) {
        if (type == BUTTON_EMPTY) return false;
        int buttons[] = {
                BUTTON_START,
                BUTTON_TUTORIAL,
                BUTTON_CREDIT_VIEW,
                BUTTON_RETURN_TO_OP,
                BUTTON_RETURN_TO_MA,
        };
        int scenes[] = {
                SCENE_MAIN_MENU,
                SCENE_TUTORIAL,
                SCENE_CREDIT_VIEW,
                SCENE_OPENING,
                SCENE_MAIN_MENU
        };
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == type) {
                SceneManager.SetNextScene(scenes[i]);
                Wipe.SetAvailableToCreate(true);
                return true;
            }
        }
        return false;
    }
    // Update in main menu scene
    private boolean SetEachMenu(int type) {
        int buttonTypes[] = {
                BUTTON_SELECT_MODE, BUTTON_SELECT_TUNE,
                BUTTON_SELECT_LEVEL,BUTTON_ASSOCIATION_LIBRARY,
                BUTTON_ASSOCIATION
        };
        int taskButtons[][] = {
                {
                        BUTTON_RECOGNITION,
                        BUTTON_SOUND,BUTTON_SENTENCE,
                        BUTTON_ASSOCIATION
                },
                {
                        BUTTON_RECOGNITION,
                        BUTTON_PREVIEW, BUTTON_NEXT,
                        BUTTON_TUNE_NUMBER,BUTTON_TUNE_NUMBER,
                        BUTTON_TUNE_NUMBER,BUTTON_TUNE_NUMBER
                },           // to show music number which is positive.
                {
                        BUTTON_RECOGNITION,
                        BUTTON_EASY, BUTTON_NORMAL,
                        BUTTON_HARD
                },
                {
                        BUTTON_RECOGNITION,
                        BUTTON_ASSOCIATION_IN_EMOTION,
                        BUTTON_ASSOCIATION_IN_FRUITS
                },
                {
                        BUTTON_RECOGNITION,
                        BUTTON_ASSOCIATION_IN_EMOTION, BUTTON_ASSOCIATION_IN_FRUITS,
                        BUTTON_ASSOCIATION_IN_ALL
                },
        };
        boolean res = false;
        for (int j = 0; j < buttonTypes.length; j++) {
            if (type == buttonTypes[j]) {
                // set task table
                // to release each button and then to initialize each new button
                this.mTaskTable.SetTable(taskButtons[j],1);
                res = true;
                sLoadingType = (type==BUTTON_ASSOCIATION_LIBRARY)?type:BUTTON_EMPTY;
                break;
            }
        }
        // when user selected the music button first,
        // set music number
        if (type == BUTTON_SELECT_TUNE) {
            sLoadingType = BUTTON_SELECT_TUNE;      // get type to load
            if (this.mMusicIdArray != null) this.mMusicIdArray = null;
            this.mMusicIdArray = new int[LISTING_MUSIC_ID_MAX];
            for (int i = 0; i < this.mMusicIdArray.length; i++) this.mMusicIdArray[i] = i;
            this.mTaskTable.SetMusicId(this.mMusicIdArray);
        }
        return res;
    }
    private boolean setAssociation(int type) {
        boolean init = false;
        if (sLoadingType == BUTTON_ASSOCIATION_LIBRARY) {
            int buttonType[] = {
                    BUTTON_ASSOCIATION_IN_EMOTION, BUTTON_ASSOCIATION_IN_FRUITS
            };
            for (int b : buttonType) {
                if (b == type) {
                    sLoadingType = b;
                    init = true;
                }
            }
            if (!init) return false;
            int common[] = {BUTTON_RECOGNITION};
            int con[] = {BUTTON_PREVIEW, BUTTON_NEXT};
            int taskButtons[] = new int[8];
            // set common types
            System.arraycopy(common, 0, taskButtons, 0, common.length);
            if (sLoadingType == BUTTON_ASSOCIATION_IN_EMOTION) {
                System.arraycopy(BUTTON_ASSOCIATIONS_IN_EMOTIONS, 0, taskButtons, common.length, LISTING_ASSOCIATION_ID_MAX);
            } else if (sLoadingType == BUTTON_ASSOCIATION_IN_FRUITS) {
                System.arraycopy(BUTTON_ASSOCIATIONS_IN_FRUITS, 0, taskButtons, common.length, LISTING_ASSOCIATION_ID_MAX);
            }
            System.arraycopy(con, 0, taskButtons, LISTING_ASSOCIATION_ID_MAX + common.length, con.length);
            this.mTaskTable.SetTable(taskButtons, 1);
        }
        return init;
    }
    private boolean loadAssociation(int buttonType) {
        boolean init = (sLoadingType == BUTTON_ASSOCIATION_IN_EMOTION || sLoadingType == BUTTON_ASSOCIATION_IN_FRUITS &&
                buttonType == BUTTON_NEXT || buttonType == BUTTON_PREVIEW);
        if (!init) return false;
        int mode = this.convertButtonTypeIntoMode(sLoadingType);
        int max = PlayManager.getColourKindMax(mode);
        int common[] = { BUTTON_RECOGNITION };
        int con[] = { BUTTON_PREVIEW,BUTTON_NEXT };
        int pageNumber = this.mTaskTable.getCurrentPage();      // get the current page number
        int allot = 0;
        int page = 0;
        int startingNum = 0;
        if (pageNumber == 1) {
            if (buttonType == BUTTON_NEXT) {
                page = 2;
                startingNum = LISTING_ASSOCIATION_ID_MAX;
                allot = max-LISTING_ASSOCIATION_ID_MAX;
            }
        } else if (pageNumber == 2) {
            if (buttonType == BUTTON_NEXT) {
                page = 1;
                allot = LISTING_ASSOCIATION_ID_MAX;
            }
        }
        if (buttonType == BUTTON_PREVIEW) {
            this.SetEachMenu(BUTTON_ASSOCIATION_LIBRARY);
            return true;
        }
        if (allot == 0) return false;
        int maxAllot = common.length+con.length+allot;
        int taskButtons[] = new int[maxAllot];
        System.arraycopy(common,0,taskButtons,0,common.length);
        if (sLoadingType == BUTTON_ASSOCIATION_IN_EMOTION) {
            System.arraycopy(BUTTON_ASSOCIATIONS_IN_EMOTIONS, startingNum, taskButtons, common.length, allot);
        } else if (sLoadingType == BUTTON_ASSOCIATION_IN_FRUITS) {
            System.arraycopy(BUTTON_ASSOCIATIONS_IN_FRUITS, startingNum, taskButtons, common.length, allot);
        }
        System.arraycopy(con,0,taskButtons,allot+common.length,con.length);
        this.mTaskTable.SetTable(taskButtons,page);
        return true;
    }
    // to load new music id as pressed next button in task table
    private boolean LoadMusicId(int buttonType) throws NullPointerException
    {
        if (this.mMusicIdArray == null || sLoadingType != BUTTON_SELECT_TUNE) return false;
        int indication[] = {BUTTON_RECOGNITION,BUTTON_PREVIEW,BUTTON_NEXT};
        int priorId = this.mMusicIdArray[0];
        boolean set = false;
        int variable = 0;
        if (buttonType == BUTTON_NEXT) {
            variable = LISTING_MUSIC_ID_MAX;
            set = true;
        } else if (buttonType == BUTTON_PREVIEW) {
            variable = LISTING_MUSIC_ID_MAX*-1;
            set = true;
        }
        if (set) {
            int buttons[];
            int setId;
            int maxId = MusicSelector.GetMaxId();
            buttons = new int[indication.length + LISTING_MUSIC_ID_MAX];
            System.arraycopy(indication, 0, buttons, 0, indication.length);
            for (int i = indication.length; i < buttons.length; i++)
                buttons[i] = BUTTON_TUNE_NUMBER;
            // set table
            // firstly, to initialize the each button
            this.mTaskTable.SetTable(buttons,1);
            // set music id
            this.mMusicIdArray = null;
            this.mMusicIdArray = new int[LISTING_MUSIC_ID_MAX];
            for (int i = 0; i < this.mMusicIdArray.length; i++) {
                    setId = (priorId+variable) + i;
                    if (maxId < setId) {
                        setId -= maxId+1;
                    } else if (setId < 0) {
                        setId += maxId+1;
                    }
                this.mMusicIdArray[i] = setId;
            }
            this.mTaskTable.SetMusicId(this.mMusicIdArray);
        }
        return set;
    }
    // reinitialize the task table
    private void loadMusicList() {
        int taskButtons[] = {
                BUTTON_RECOGNITION,
                BUTTON_TUNE_NUMBER,BUTTON_TUNE_NUMBER,
                BUTTON_TUNE_NUMBER,BUTTON_TUNE_NUMBER,
                BUTTON_TUNE_NUMBER
        };
        this.mTaskTable.SetTable(taskButtons,1);
        if (this.mMusicIdArray != null) this.mMusicIdArray = null;
        this.mMusicIdArray = new int[LISTING_MUSIC_ID_MAX];
        for (int i = 0; i < this.mMusicIdArray.length; i++) this.mMusicIdArray[i] = i;
        this.mTaskTable.SetMusicId(this.mMusicIdArray);
    }

    // reinitialize task table
    private void reinitializeTaskTable(REINITIALIZE_TASK task) {
        int initType = -1;       // 0 = load music, 1 = setEachMenu, 2 = setAssociation
                                 // 3 = backToMainMenu
        int buttonType = BUTTON_EMPTY;
        if (task == null) return;
        if (task.equals(REINITIALIZE_TASK.MUSIC_LIST_IN_TUTORIAL)) {
            initType = 0;
        } else if (task.equals(REINITIALIZE_TASK.MUSIC_LIST_IN_MAIN_MENU)) {
            initType = 1;
            buttonType = BUTTON_SELECT_TUNE;
        } else if (task.equals(REINITIALIZE_TASK.SELECT_LEVEL_IN_MAIN_MENU)) {
            initType = 1;
            buttonType = BUTTON_SELECT_LEVEL;
        } else if (task.equals(REINITIALIZE_TASK.SELECT_MODE_IN_MAIN_MENU)) {
            initType = 1;
            buttonType = BUTTON_SELECT_MODE;
        } else if (task.equals(REINITIALIZE_TASK.SELECT_ASSOCIATION)) {
            initType = 1;
            buttonType = BUTTON_ASSOCIATION;
        } else if (task.equals(REINITIALIZE_TASK.ASSOCIATION_IN_EMOTIONS)) {
            initType = 2;
            buttonType = BUTTON_ASSOCIATION_IN_EMOTION;
        } else if (task.equals(REINITIALIZE_TASK.ASSOCIATION_IN_FRUITS)) {
            initType = 2;
            buttonType = BUTTON_ASSOCIATION_IN_FRUITS;
        } else if (task.equals(REINITIALIZE_TASK.ASSOCIATION_IN_ALL)) {
            initType = 2;
            buttonType = BUTTON_ASSOCIATION_IN_ALL;
        }
        // initialize task table
        switch(initType) {
            case 0:
                this.loadMusicList();
                break;
            case 1:
                this.SetEachMenu(buttonType);
                break;
            case 2:
                this.setAssociation(buttonType);
                break;
            default:
        }
        mReInitializeTaskType = null;
    }
    /*
        Draw
    */
    public void DrawManager() {
        // task table
        this.mTaskTable.DrawManager();
        // to draw buttons
        for (RecognitionButton button: this.mButtons) {
            button.DrawRecognitionButton();
        }
    }
    /*
        Release
    */
    public void ReleaseManager() {
        for (int i = 0; i < this.mButtons.length; i++) {
            this.mButtons[i].ReleaseRecognitionButton();
            this.mButtons[i] = null;
        }
        if (this.mUtility != null) this.mUtility = null;
        if (this.mElementToAlpha != null) this.mElementToAlpha = null;
        if (this.mMusicIdArray != null) this.mMusicIdArray = null;
        // Task table
        if (this.mTaskTable != null) {
            this.mTaskTable.ReleaseManager();
            this.mTaskTable = null;
        }
    }

    private int convertButtonTypeIntoMode(int type) {
        switch(type) {
            case BUTTON_SOUND:
                return MODE_SOUND;
            case BUTTON_SENTENCE:
                return MODE_SENTENCE;
            case BUTTON_ASSOCIATION_IN_EMOTION:
                return MODE_ASSOCIATION_IN_EMOTIONS;
            case BUTTON_ASSOCIATION_IN_FRUITS:
                return MODE_ASSOCIATION_IN_FRUITS;
            case BUTTON_ASSOCIATION_IN_ALL:
                return MODE_ASSOCIATION_IN_ALL;
        }
        return -1;
    }

    static void toCallTaskTable() { mGuidanceButtonType = BUTTON_CALL_TASK_TABLE; }
    
    /*************************************************************
     Each setter functions
     ************************************************************/
    // set the task type to reinitialize
    static void reInitializeTaskTable(REINITIALIZE_TASK type) {
        mReInitializeTaskType = type;
    }
    /******************************************************************
     * Each getter functions
     ****************************************************************/
    /*
        Get the direction to notice the position that finger nears by the button.
    */
    public int GetDirectionToNotice() { return this.mDirection; }
    /*
        Get the type of button
    */
    public int GetGuidanceButtonType() { return mGuidanceButtonType; }
    // Get the music id as selecting
    public int GetIsSelectingMusicId() { return this.mIsSelectingMusicId; }
    /*
        Get the button type to obtain process
    */
    @Contract(pure = true)
    public static int GetButtonTypeToObtainProcess() { return mTypeToObtain; }
    // Get is opening task
    @Contract(pure = true)
    public static boolean GetIsOpeningTask() { return mIsOpeningTask; }
    static int getLoadingType() { return sLoadingType; }
}