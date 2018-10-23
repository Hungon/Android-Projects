package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.provider.Settings;

import org.jetbrains.annotations.Contract;

/**
 * Created by USER on 5/5/2016.
 */
class LeadingManager extends DescriptionsManager implements HasSystem, HasScene, HasMessageFrame, FileManager {
    // static variables
    final static int RESUME_FILE        = 0;
    final static int RESUME_DESCRIPTION = 1;
    final static int INITIAL_DESCRIPTION_TO_PLAY = 2;
    // the current process to initialize guidance words
    private final static int WAIT_TO_INITIALIZE        = 0x01;
    private final static int AVAILABLE_TO_INITIALIZE   = 0x02;
    private final static int ALREADY_INITIALIZED       = 0x04;
    private final static int NEW_TEXT_FILE             = 0x08;
    private final static int AVAILABLE_TO_UPDATE_TEXT  = 0x10;
    private final static int RESTART_READING           = 0x20;
    private final static int PRIOR_END                 = 0x40;
    private final static int UP_TO_END                 = 0x80;
    private final static int AVAILABLE_TO_DESCRIPTION  = 0x100;
    // the fixed time that between each process
    private final static int    FIXED_TIME_BETWEEN_EACH_PROCESS = 200;
    // filed
    private Context mContext;
    private Image mImage;
    private LeadingWithVoice    mLead;        // to read from the text file in local directory.
    private MyTextManager       mDescription; // to read from the raw text which is description
    private int                 mCurrentScene;
    private Utility             mUtility;
    private String              mGuidanceWords[];
    private int mPreviewTalkProcess;
    private String mReadingFileName;
    private boolean mIsSpeeching;
    private int mPreviewButtonType;
    private int mPreviewMusicId;
    private static int          mFileIndex;
    private static int          mProcess;
    private static int          mCountReadFile;
    // the count pause time in a text file.
    private static int mPauseCount;
    private static int mResumeType;

    /*
        Constructor
    */
    LeadingManager(Context context, Image image, int scene) {
        super(context);
        // to get the current scene
        this.mCurrentScene = scene;
        this.mLead = new LeadingWithVoice(context,image);
        this.mUtility = new Utility();
        this.mContext = context;
        this.mImage = image;
    }
    /*
        Initialize
    */
    public void InitManager(int experience) {
        int mode = SystemManager.GetPlayMode();
        // the file's index
        mFileIndex = 0;
        // the count read file
        mCountReadFile = 0;
        // reset pause count
        mPauseCount = 0;
        mResumeType = -1;
        // preview process in talk class
        this.mPreviewTalkProcess = Talk.READY;
        int readFromFile = 0;
        String initialSentence = "";
        int fixedEx = Insert.insertFixedExperience(mode,this.mCurrentScene);
        switch (this.mCurrentScene) {
            case SCENE_OPENING:
                if ((experience & fixedEx) == fixedEx) {
                    mFileIndex = FILE_INDEX_RESUME;
                }
                break;
            case SCENE_TUTORIAL:
                if ((experience & fixedEx) == fixedEx) {
                    mFileIndex = FILE_INDEX_DONE;
                }
                break;
            case SCENE_MAIN_MENU:
                if ((experience & fixedEx) == fixedEx) {
                    mFileIndex = FILE_INDEX_RESUME;
                }
                break;
            case SCENE_PROLOGUE:
                int preview = SceneManager.GetPreviewScene();
                int index = -1;
                // set the file's index
                if (mode == MODE_SOUND) {
                    index = mFileIndex = FILE_INDEX_SOUND_MODE_TO_EXPLAIN;
                } else if (mode == MODE_SENTENCE) {
                    index = mFileIndex = FILE_INDEX_SENTENCE_MODE_TO_EXPLAIN;
                } else if (mode == MODE_ASSOCIATION_IN_EMOTIONS ||
                        mode == MODE_ASSOCIATION_IN_FRUITS ||
                        mode == MODE_ASSOCIATION_IN_ALL) {
                    index = FILE_INDEX_ASSOCIATION_MODE_TO_EXPLAIN;
                }
                if (preview != SCENE_TUTORIAL) {    // guide will ask user to check explanation again
                    if ((experience & fixedEx) == fixedEx) {
                        mFileIndex = FILE_INDEX_DONE+index;
                        readFromFile = 1;
                    } else {
                        mFileIndex = index;
                    }
                } else  {           // to do explanation
                    mFileIndex = index;
                }
                if (readFromFile == 1) initialSentence = super.insertDescriptionToExplain();
                break;
            case SCENE_PLAY:
                readFromFile = 2;
                break;
            default:
        }
        // default values
        Point pos = new Point(50, 200);
        int size = 18;
        int colour = Color.BLUE;
        int frame = 3;
        // to diverge to initialize from current scene.
        if (readFromFile == 0) {
            this.mLead.InitLeadingWithVoiceFromLocalFile(
                    FILE_STARTING_GUIDANCE[this.mCurrentScene] + Integer.toString(mFileIndex),
                    pos, frame, size, colour, FRAME_SMALL_BALLOON);
            mProcess = AVAILABLE_TO_UPDATE_TEXT;
        } else if (readFromFile == 1){
            this.mLead.InitLeadingWithVoiceByRawText(
                    initialSentence,
                    pos, frame, size, colour, FRAME_SMALL_BALLOON);
            mProcess = AVAILABLE_TO_UPDATE_TEXT;
        } else if (readFromFile == 2) {
            mProcess = AVAILABLE_TO_DESCRIPTION;
        }
        // to set the file name to read.
        this.mReadingFileName = FILE_STARTING_GUIDANCE[this.mCurrentScene];
        // with leading with voice
        this.mIsSpeeching = true;
        // the preview button type
        this.mPreviewMusicId = this.mPreviewButtonType = BUTTON_EMPTY;
    }
    /*
        Update
        return value is boolean which is available to initialize guidance words
    */
    public boolean UpdateManager(int buttonType, int musicId) {
        boolean res = false;
        // when talking process is up to the last sentence,
        // not to dive into these below process.
        // That is not to initialize guidance message.
        if ((mProcess&UP_TO_END)==UP_TO_END) {
            if (this.mUtility.ToMakeTheInterval(200)) {
                mFileIndex = FILE_INDEX_UP_TO_END;
                this.mLead.SwitchShowingWindowMessage(false);
                mProcess |= AVAILABLE_TO_DESCRIPTION;
                mProcess &= ~UP_TO_END;
            }
            // when other talk file finished,
            // to show the describe message by selected the button.
        } else if ((mProcess&AVAILABLE_TO_DESCRIPTION)==AVAILABLE_TO_DESCRIPTION) {
            // when is displaying talk file, not to show
            if (this.mLead.getIsDisplaying()) this.mLead.SwitchShowingWindowMessage(false);
            if (this.mDescription == null) {
                this.mDescription = new MyTextManager(this.mContext,this.mImage);
            } else {
                this.mDescription.UpdateManager();
            }
            return this.UpdateDescription(buttonType,musicId,mResumeType);
        }
        // when talk class reached to the last word in text file,
        // to initialize next file.
        else if ((mProcess&NEW_TEXT_FILE)==NEW_TEXT_FILE) {
            this.mLead.InitLeadingWithVoiceFromLocalFile(
                    this.mReadingFileName+Integer.toString(mFileIndex));
            // reset process
            mProcess = AVAILABLE_TO_UPDATE_TEXT;
            // reset count to pause talking process
            mPauseCount = 0;
            // to release description
            if (this.mDescription != null) {
                this.mDescription.ReleaseManager();
                this.mDescription = null;
            }
        }
        boolean guiding = GuidanceManager.GetIsGuiding();
        // to update talk file, return value is the current process in Talk class
        int leading = this.mLead.UpdateLeadingWithVoice();
        switch (leading) {
            case Talk.READY:
            case Talk.READING:
            case Talk.DISPLAYED:
                if ((mProcess&PRIOR_END)==PRIOR_END) {
                    mProcess &= ~PRIOR_END;
                    mProcess |= UP_TO_END;
                    return false;
                }
                break;
            case Talk.FINISH_TO_READ:
                if ((mProcess&AVAILABLE_TO_UPDATE_TEXT)==AVAILABLE_TO_UPDATE_TEXT) {
                    mProcess &= ~AVAILABLE_TO_UPDATE_TEXT;
                    mProcess |= WAIT_TO_INITIALIZE; // is waiting to initialize guidance
                    // get talk preview process
                    this.mPreviewTalkProcess = Talk.FINISH_TO_READ;
                }
                break;
            case Talk.DELETE:
                if ((mProcess&AVAILABLE_TO_UPDATE_TEXT)==AVAILABLE_TO_UPDATE_TEXT) {
                    mProcess &= ~AVAILABLE_TO_UPDATE_TEXT;
                    mProcess |= PRIOR_END;
                    // to count read file
                    mCountReadFile++;
                }
                break;
            // if talking process is pausing,
            case Talk.PAUSE:
                if ((mProcess&AVAILABLE_TO_UPDATE_TEXT)==AVAILABLE_TO_UPDATE_TEXT) {
                    mProcess &= ~AVAILABLE_TO_UPDATE_TEXT;
                    mProcess |= WAIT_TO_INITIALIZE; // is waiting to initialize guidance
                    // get talk preview process
                    this.mPreviewTalkProcess = Talk.PAUSE;
                    // to count pause time
                    mPauseCount++;
                }
                break;
        }
        // when no guiding,
        // the guidance words must be initialized in Guidance Manager class.
        if (!guiding) {
            // to diverge the current process to initialize guidance
            // when talk class is waiting to read, to get showing text
            if ((mProcess&WAIT_TO_INITIALIZE)==WAIT_TO_INITIALIZE &&
                    (mProcess&AVAILABLE_TO_INITIALIZE)!=AVAILABLE_TO_INITIALIZE) {
                mProcess &= ~WAIT_TO_INITIALIZE;
                mProcess |= AVAILABLE_TO_INITIALIZE;
                // to get showing text in Talk class
                if (this.mGuidanceWords != null) this.mGuidanceWords = null;
                this.mGuidanceWords = new String[this.mLead.GetShowingText().length];
                this.mGuidanceWords = this.mLead.GetShowingText();
                // available to initialize guidance
                res = this.mIsSpeeching;   // when the global variable is false, not to guide
                // the process is prior to the end
            } else if ((mProcess&PRIOR_END)==PRIOR_END) {
                // to get showing text in Talk class
                if (this.mGuidanceWords != null) this.mGuidanceWords = null;
                this.mGuidanceWords = new String[this.mLead.GetShowingText().length];
                this.mGuidanceWords = this.mLead.GetShowingText();
                // available to initialize guidance
                res = this.mIsSpeeching;   // when the global variable is false, not to guide
                // when already initialized guidance words, to go to next line after fixed interval
                // the preview process is finish to read in talk class
            } else if ((mProcess&ALREADY_INITIALIZED)==ALREADY_INITIALIZED &&
                    this.mPreviewTalkProcess == Talk.FINISH_TO_READ) {
                if (this.mUtility.ToMakeTheInterval(FIXED_TIME_BETWEEN_EACH_PROCESS)) {
                    this.mLead.GoToNewLine();
                    mProcess &= ~AVAILABLE_TO_INITIALIZE;
                    mProcess &= ~ALREADY_INITIALIZED;
                    mProcess |= AVAILABLE_TO_UPDATE_TEXT;
                }
                // during talk process is pausing,
                // the preview process is pause in talk class
            } else if ((mProcess&RESTART_READING)==RESTART_READING &&
                    this.mPreviewTalkProcess == Talk.PAUSE) {
                if (this.mUtility.ToMakeTheInterval(FIXED_TIME_BETWEEN_EACH_PROCESS)) {
                    this.mLead.GoToNewLine();
                    mProcess &= ~RESTART_READING;
                    mProcess &= ~AVAILABLE_TO_INITIALIZE;
                    mProcess |= AVAILABLE_TO_UPDATE_TEXT;
                }
            }
        }
        return res;
    }
    /*
        Draw
    */
    public void DrawManager() {
        if (this.mLead != null) {
            this.mLead.DrawLeadingWithVoice();
        }
        if (this.mDescription != null) {
            this.mDescription.DrawManager();
        }
    }
    /*
        Release
    */
    public void ReleaseManager() {
        // leading
        if (this.mLead != null) {
            this.mLead.ReleaseLeadingWithVoice();
            this.mLead = null;
        }
        if (this.mGuidanceWords != null) this.mGuidanceWords = null;
        if (this.mReadingFileName != null) this.mReadingFileName = null;
        if (this.mDescription != null) {
            this.mDescription.ReleaseManager();
            this.mDescription = null;
        }
    }
    /*
        Update description in the opening and mainmenu.
    */
    private boolean UpdateDescription(int buttonType, int musicId, int resumeType) {
        if (this.mCurrentScene == SCENE_OPENING || this.mCurrentScene == SCENE_MAIN_MENU ||
            this.mCurrentScene == SCENE_PROLOGUE || this.mCurrentScene == SCENE_PLAY) {
            Point pos = new Point(50,200);
            int size = 18;
            int colour = Color.BLUE;
            int frame = 30;
            int startingInterval = 60;
            int addAlpha = 2;
            int balloonType = FRAME_SMALL_BALLOON;
            String dummy[] = null;

            if (resumeType == INITIAL_DESCRIPTION_TO_PLAY) {
                dummy = super.insertInitialDescriptionToPlay();
                balloonType = FRAME_MEDIUM_BALLOON;         // to change the balloon type.
                mResumeType = -1;
            } else if (resumeType == RESUME_DESCRIPTION) {
                dummy = super.insertRepeatableDescriptionWhenResume();
                mResumeType = -1;
            } else if (buttonType == BUTTON_TUNE_NUMBER) {
                this.mPreviewButtonType = buttonType;
                if (this.mPreviewMusicId != musicId) {
                    this.mPreviewMusicId = musicId;
                    balloonType = FRAME_MEDIUM_BALLOON;      // to change the balloon type.
                    dummy = super.insertMusicInfoByTheId(musicId);
                }
            } else {
                // when the button type is empty, nothing to do.
                if (buttonType == BUTTON_EMPTY) return false;
                if (this.mPreviewButtonType != buttonType) {
                    this.mPreviewButtonType = buttonType;
                    int loadingType = RecognitionButtonManager.getLoadingType();
                    if (loadingType == BUTTON_EMPTY) {
                        dummy = super.ConvertTheButtonTypeIntoSentence(this.mCurrentScene, buttonType);
                    } else if (loadingType == BUTTON_ASSOCIATION_LIBRARY ||
                            loadingType == BUTTON_ASSOCIATION_IN_EMOTION ||
                            loadingType == BUTTON_ASSOCIATION_IN_FRUITS) {
                        balloonType = (loadingType==BUTTON_ASSOCIATION_LIBRARY)?FRAME_SMALL_BALLOON:FRAME_MEDIUM_BALLOON;
                        dummy = super.referToLibrary(loadingType,buttonType);
                    }
                }
            }
            if (dummy != null) {
                mProcess = AVAILABLE_TO_UPDATE_TEXT;
                mProcess |= AVAILABLE_TO_DESCRIPTION;
                this.mDescription.SetPresetValues(size,colour);
                this.mDescription.CreateMyTextWithBalloon(
                        dummy, pos,
                        frame,startingInterval,addAlpha,balloonType);
                if (this.mGuidanceWords != null) this.mGuidanceWords = null;
                int divide = 2;
                int allot = dummy.length/divide;
                if (allot == 0) allot = 1;
                this.mGuidanceWords = new String[allot];
                for (int i = 0; i < this.mGuidanceWords.length; i++) this.mGuidanceWords[i] = "";
                String space = "";
                int count = 0;
                for (int i = 0; i < dummy.length; i++) {
                    if (0 < i) space = " ";
                    if (divide <= i) {
                        if (i % divide == 0 && count < this.mGuidanceWords.length-1) count++;
                    }
                    this.mGuidanceWords[count] += dummy[i]+space;
                }
                return true;
            }
        }
        return false;
    }
    /*
        Is already initialized guidance words
    */
    public void IsAlreadyInitializedGuidanceWords() { mProcess |= ALREADY_INITIALIZED; }

    static void loadTheFileToExplainAgain() {
        int scene = SceneManager.GetCurrentScene();
        boolean init = (scene == SCENE_TUTORIAL || scene == SCENE_PROLOGUE);
        if (init) {
            if (scene == SCENE_TUTORIAL) {
                mFileIndex = FILE_INDEX_DONE+1;
            }
            // when the current scene is prologue,
            // the file was initialized in init function.
            mPauseCount = 0;
            mCountReadFile = 0;
            mProcess = NEW_TEXT_FILE;
        }
    }
    /*
        go to next line
    */
    public static void GoToNewLine() {
        mProcess |= RESTART_READING;
        mProcess &= ~UP_TO_END;     // the substitution that dive into update process.
    }
    static void GoToNextFile(int type) {
        if (type == RESUME_FILE) {          // reading from the file
            if (mFileIndex == FILE_INDEX_UP_TO_END) {
                if (RecognitionButtonManager.GetButtonTypeToObtainProcess() == BUTTON_PLAY_THE_GAME) return;
                mFileIndex = FILE_INDEX_RESUME;
            }
            mProcess = NEW_TEXT_FILE;
        } else if (type == RESUME_DESCRIPTION) {
            if (mFileIndex == FILE_INDEX_UP_TO_END) {
                mProcess = AVAILABLE_TO_DESCRIPTION;
            } else {
                if (RecognitionButtonManager.GetButtonTypeToObtainProcess() == BUTTON_PLAY_THE_GAME) return;
                mProcess = NEW_TEXT_FILE;       // the file will be back to beginning
            }
        } else if (type == INITIAL_DESCRIPTION_TO_PLAY) {
            mProcess = AVAILABLE_TO_DESCRIPTION;        // to load the description right away.
        } else {
            return;
        }
        mResumeType = type;
    }
    static void setFileIndex(int index) {
        mFileIndex = index;
        mProcess = NEW_TEXT_FILE;
    }
    /*********************************************************
     Each getter functions
     ********************************************************/
    /*
        Get showing text in Talk class
    */
    public String[] GetShowingText() { return (this.mGuidanceWords == null) ? new String[1] : this.mGuidanceWords; }
    /*
        Get count read file
    */
    @Contract(pure = true)
    public static int GetCountReadFile() { return mCountReadFile; }
    /*
        Get the count in a text file
    */
    @Contract(pure = true)
    public static int GetPauseCountInATextFile() { return mPauseCount; }
}
