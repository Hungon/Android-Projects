package com.trials.harmony;


/**
 * Created by Kohei Moroi on 7/24/2016.
 */
abstract class Response implements HasRecognitionId, HasRecognitionWords,
        HasSystem, HasScene, HasMusicInfo, FileManager {
    // static variables
    // the fixed time to repeat guidance and calling recognizer
    protected final static int FIXED_TIME_TO_REPEAT = 400;
    // Each process to execute in difference scene or class.
    public final static int EMPTY = -1;
    public final static int INITIALIZE_GUIDANCE = 0;
    public final static int TRANSITION_TO_NEXT_SCENE = 1;
    // to switch the element to execute each process
    // task ends up to finish
    protected final static int UP_TO_FINISH = 0x100;
    protected final static int TO_CALL_RECOGNIZER = 0x080;

    // filed
    public String mResponseWords[];
    public int mSwitchElement;
    protected int mPreviewCountCalledRecognizer;
    protected int mRecognitionMessageId;
    protected Utility mUtility;
    protected int mPreviewElementToGuide;
    protected int mUserExperience;
    protected int mNextTransitionScene;

    // Constructor
    public Response() {
        this.mUtility = new Utility();
        // get user's experience
        this.mUserExperience = SystemManager.GetUserExperience();
    }

    /*
        Initialize
    */
    protected void InitResponse() {
        this.mPreviewCountCalledRecognizer = 0;
        this.mSwitchElement = 0x00;
        // id to initialize guidance message in recognition.
        this.mRecognitionMessageId = RECOGNITION_ID_EMPTY;
        // reset preview element to guide
        this.mPreviewElementToGuide = -1;
        // next scene
        this.mNextTransitionScene = SceneManager.SCENE_NOTHING;
    }
    // Update
    abstract protected int UpdateResponse();
    /*
        Release
    */
    protected void ReleaseResponse() {
        if (this.mUtility != null) this.mUtility = null;
        if (this.mResponseWords != null) this.mResponseWords = null;
    }

    protected int convertModeIntoGuidanceType(int mode) {
        switch(mode) {
            case MODE_SOUND:
                return GUIDANCE_MESSAGE_COLOUR;
            case MODE_SENTENCE:
                return GUIDANCE_MESSAGE_SENTENCE;
            case MODE_ASSOCIATION:
                return GUIDANCE_MESSAGE_SELECT_ASSOCIATION;
            case MODE_ASSOCIATION_IN_EMOTIONS:
                return GUIDANCE_MESSAGE_ASSOCIATION_IN_EMOTIONS;
            case MODE_ASSOCIATION_IN_FRUITS:
                return GUIDANCE_MESSAGE_ASSOCIATION_IN_FRUITS;
            case MODE_ASSOCIATION_IN_ALL:
                return GUIDANCE_MESSAGE_ASSOCIATION_IN_ALL;
        }
        return GUIDANCE_MESSAGE_EMPTY;
    }

    /*
        To check current Id to guide and then
        if that id is calling recognizer number,
        to call recognizer.
    */
    protected boolean ToCheckIdToCallRecognizer() {
        if (GuidanceManager.GetIsGuiding()) return false;
        if(this.mRecognitionMessageId == GUIDANCE_MESSAGE_EMPTY) {
            return false;
        } else if ((this.mSwitchElement&TO_CALL_RECOGNIZER)==TO_CALL_RECOGNIZER){
            // Reset the id recognized
            RecognizerManager.ResetIdRecognized();
        }
        // when to set the shift bit to call the recognizer,
        if ((this.mSwitchElement & TO_CALL_RECOGNIZER) == TO_CALL_RECOGNIZER) {
            // to find id to call recognizer
            // to make the interval until calling recognizer
            if (this.mUtility.ToMakeTheInterval(100)) {
                // set guidance sentence
                RecognitionMode.CallRecognizer(this.mRecognitionMessageId);
                // already set the guidance words hence not to initialize and update.
                // reset
                this.mSwitchElement &= ~TO_CALL_RECOGNIZER;
                this.mRecognitionMessageId = GUIDANCE_MESSAGE_EMPTY;
                return true;
            }
        }
        return false;
    }
    /*
        To validate selection by recognised id
    */
    protected boolean ValidateSelectionByRecognisedId(int guidanceId) {
        if (guidanceId == RECOGNITION_ID_EMPTY) return false;
        int id[] = RecognizerManager.GetRecognitionId();
        for (int i: id) {
            if (i == RECOGNITION_ID_EMPTY) continue;
            for (int v : RECOGNITION_TO_EXECUTE[guidanceId]) {
                if (i == GUIDANCE_MESSAGE_SELECT_TUNE || i == GUIDANCE_MESSAGE_SELECT_TUNE_AFTER_LISTEN_TO_TUNE) {
                    if(MusicSelector.GetMusicElementRecognized() == MusicSelector.TUNE_ELEMENT_EMPTY) {
                        continue;
                    }
                }
                if (i == v) return true;
            }
        }
        return false;
    }

    /*
        To ask user about checking again prologue
    */
    protected int ToAskCheckingAgain(int nextScene, int experience) {
        int call = RecognitionMode.GetCurrentCountCalledRecognizer();
        boolean guiding = GuidanceManager.GetIsGuiding();
        int id = RecognizerManager.GetRecognitionId()[0];
        if (this.mResponseWords != null) this.mResponseWords = null;
        if (!guiding && LeadingManager.GetPauseCountInATextFile() == 1 &&
                (mSwitchElement & 0x01) != 0x01) {
            mSwitchElement |= TO_CALL_RECOGNIZER;
            mSwitchElement |= 0x01;
            this.mRecognitionMessageId = GUIDANCE_MESSAGE_ANSWER;
            return EMPTY;
            // to asking about to repeat guidance
        } else if (!guiding && (mSwitchElement & 0x01) == 0x01 && id == RECOGNITION_ID_EMPTY) {
            if (this.mUtility.ToMakeTheInterval(FIXED_TIME_TO_REPEAT)) {
                mSwitchElement &= ~0x01;
            }
            return EMPTY;
            // when answer is yes, to explain again.
        } else if (!guiding && 1 <= call &&
                (mSwitchElement & 0x01) == 0x01 && (mSwitchElement & 0x02) != 0x02 &&
                id == RECOGNITION_ID_YES) {
            mSwitchElement |= 0x02;
            // to increment the file's index.
            LeadingManager.GoToNewLine();
            return EMPTY;
            // when ends up to last word in the text file,
            // to call the recognizer and to explain again.
        } else if ((!guiding && 1 <= call &&
                (mSwitchElement & 0x02) == 0x02) && LeadingManager.GetCountReadFile() == 1) {
            // to explain the tutorial
            this.mUserExperience &= ~experience;
            // Reset count called recognizer
            RecognitionMode.ResetCountCalledRecognizer();
            LeadingManager.loadTheFileToExplainAgain();
            mSwitchElement = 0x00;
            return EMPTY;
            // when answer is no, to transition to opening scene.
        } else if (!guiding && 1 <= RecognitionMode.GetCurrentCountCalledRecognizer() &&
                (mSwitchElement & 0x01) == 0x01 && id == RECOGNITION_ID_NO) {
            mSwitchElement |= UP_TO_FINISH;
            // to set guidance words before to call recognizer.
            this.mResponseWords = new String[1];
            this.mResponseWords[0] = "OK, "+SceneManager.TRANSITION_GUIDANCE[nextScene];
            // set next scene
            this.mNextTransitionScene = nextScene;
            return INITIALIZE_GUIDANCE;
        }
        return EMPTY;
    }
}