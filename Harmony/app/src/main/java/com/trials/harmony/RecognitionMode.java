package com.trials.harmony;

import android.content.Context;

/**
 * Created by Kohei Moroi on 6/30/2016.
 */
public class RecognitionMode implements HasRecognitionWords, HasScene, HasSystem {
    // static variables
    // to execute each process that recognizer and guiding
    public final static int   EXECUTE_NOTHING     = 0x00;
    public final static int   EXECUTE_RECOGNIZER  = 0x01;
    public final static int   EXECUTE_GUIDING     = 0x02;
    public final static int   EXECUTE_BOTH        = 0x03;
    // the process to execute
    public final static int READY      = -1;
    public final static int INITIALIZE = 0;
    public final static int UPDATE     = 1;
    public final static int RELEASE    = 2;
    public final static int DESTROY    = 3;
    // filed
    private Context mContext;
    private RecognizerManager   mRecognizerManager;
    private MusicSelector        mMusicSelector;
    private Guidance            mGuidance;
    private int mGuidanceElementNow;
    private int mGuidanceElementNext;
    private Utility mUtility;
    // the process number to recognize words
    private static int mProcessToExecute = READY;
    // guidance words
    private String      mGuidanceWords = "";
    // to execute
    private int mExecute;
    // the count that call method is called in current scene.
    private static int mCurrentCountCalled = 0;


    // constructor
    public RecognitionMode(Context context) {
        this.mContext = context;
        this.mRecognizerManager = new RecognizerManager();
        // when current scene is Select mode, to allot memory
        this.mMusicSelector = new MusicSelector(context);
        // ready to initialize
        mProcessToExecute = READY;
        // Utility
        this.mUtility = new Utility();
        // reset count that called recognizer
        mCurrentCountCalled = 0;
    }
    /*
        Main execution
        return value is current process to execute.
    */
    public final int MainExecution() {
        // get current guidance id
        int guidanceId = RecognizerManager.GetGuidanceId();
        // diverge process from current process number.
        if (mProcessToExecute == READY) {   // this process for waiting call method to recognizer.
            // when this process is changed in another class by calling method,
            // to be initialization.
        } else if (mProcessToExecute == INITIALIZE) {
            // initialize recognizer manager
            this.mRecognizerManager.InitManager();
            // when current scene is Select mode and guidance id is GUIDANCE_MESSAGE_SELECT_TUNE,
            // to initialize MusicSelector.
            if (guidanceId == GUIDANCE_MESSAGE_SELECT_TUNE_AFTER_LISTEN_TO_TUNE) {
                this.mMusicSelector.InitSelector();
            }
            // initialize guidance by got id of guidance words
            if (this.mGuidance == null) this.mGuidance = new Guidance(this.mContext);
            this.mGuidanceElementNow = -1;
            this.mGuidanceElementNext = 0;
            // next process is update
            mProcessToExecute = UPDATE;
            // reset guidance words
            this.mGuidanceWords = "";
            // to execute each process
            this.mExecute = EXECUTE_NOTHING;
            // update process within MainScene function in Scene class.
        } else if (mProcessToExecute == UPDATE) {
            // when not to guide, to go to below process.
            if (!this.mGuidance.GetSpeaking()) {
                // When is selecting tune to play in the Play scene,
                // to got to below.
                if (guidanceId == GUIDANCE_MESSAGE_SELECT_TUNE_AFTER_LISTEN_TO_TUNE) {
                    // get current element to play tune
                    int elementMusic = MusicSelector.GetCurrentElement();
                    // playing tune is no loop
                    int processMusic = this.mMusicSelector.UpdateSelector(false);
                    // when ready to play tune, to guide
                    if (processMusic == MusicSelector.READY && !this.mGuidanceWords.equals(Integer.toString(elementMusic + 1))) {
                        this.mExecute |= EXECUTE_GUIDING;
                        this.mGuidanceElementNext = elementMusic;
                        this.mGuidanceWords = Integer.toString(elementMusic + 1);
                    } else if (processMusic == MusicSelector.UP_TO_END) {
                        if (this.mGuidanceElementNow == MusicSelector.GetMaxId()) {
                            this.mGuidanceElementNext = 0;
                            this.mGuidanceElementNow = -1;
                        }
                        this.mExecute |= EXECUTE_BOTH;    // when current element reached to last element,
                        // to execute both process.
                        this.mGuidanceWords = GUIDANCE_WORDS[guidanceId][this.mGuidanceElementNext];
                    }
                } else {
                    this.mExecute |= EXECUTE_BOTH;
                    // to set guidance words by current id and element.
                    this.mGuidanceWords = GUIDANCE_WORDS[guidanceId][this.mGuidanceElementNext];
                }
                // when not to speak, to go to recognizer process
                if (EXECUTE_NOTHING < this.mExecute) {
                    // when playing tune, not to execute recognizer.
                    if ((this.mExecute & EXECUTE_RECOGNIZER) == EXECUTE_RECOGNIZER) {
                        mProcessToExecute = this.mRecognizerManager.UpdateManager();
                        int status = this.mRecognizerManager.GetProcessStatus();
                        // when recognition process is finish,
                        if (status == Recognizer.UPDATE || status == Recognizer.FINISH) {
                            this.mExecute &= ~EXECUTE_RECOGNIZER;
                        }
                    }
                    // when process is recognizing in Recognizer class,
                    // to update guidance
                    if ((this.mExecute & EXECUTE_GUIDING) == EXECUTE_GUIDING) {
                        int recognizerProcess = this.mRecognizerManager.GetProcessStatus();
                        if (recognizerProcess == Recognizer.READY) {
                            if (this.mGuidanceElementNext != this.mGuidanceElementNow) {
                                this.mGuidanceElementNow = this.mGuidanceElementNext;
                                this.mGuidance.InitGuidance(this.mGuidanceWords);
                                this.mGuidance.UpdateGuidance();
                                // done execution
                                this.mExecute &= ~EXECUTE_GUIDING;
                            }
                        } else if (recognizerProcess == Recognizer.UPDATE) {
                            if (this.mGuidanceElementNext == this.mGuidanceElementNow) {
                                this.mGuidanceElementNext++;
                                this.mGuidanceElementNext %= GUIDANCE_WORDS[guidanceId].length;
                            }
                        }
                    }
                }
            }
        }
        // Eventually, current process is already release, to execute release
        if (mProcessToExecute == RELEASE) {
            // count called recognizer
            mCurrentCountCalled++;
            this.Release();
        }

        // when to transition to other scene,
        // to destroy each class and then to return the status to MainScene in Scene class.
        return mProcessToExecute;
    }
    /*
        Destroy each class and allocated filed
    */
    public void Destroy() {
        this.mRecognizerManager.DestroyRecognize();
        this.mRecognizerManager = null;
        if (this.mMusicSelector != null) {
            this.mMusicSelector.ReleaseSelector();
            this.mMusicSelector = null;
        }
        if (this.mGuidance != null) {
            this.mGuidance.StopGuidance();
            this.mGuidance.ReleaseGuidance();
            this.mGuidance = null;
        }
        if (this.mUtility != null) {
            this.mUtility.ReleaseUtility();
            this.mUtility = null;
        }
    }
    /*
        Release
    */
    private void Release() {
        this.mRecognizerManager.ReleaseManager();
        if (this.mGuidance != null) {
            this.mGuidance.StopGuidance();
            this.mGuidance.ReleaseGuidance();
            this.mGuidance = null;
        }
        if (this.mUtility != null) this.mUtility.ResetInterval();
        mProcessToExecute = READY;
    }
    /*
        Call recognizer
    */
    public static void CallRecognizer(int guidanceId) {
        // next process is initialization
        if (mProcessToExecute == READY) {
            mProcessToExecute = INITIALIZE;
        }
        // set guidance id
        if (0 <= guidanceId && guidanceId < GUIDANCE_WORDS.length) {
            RecognizerManager.SetGuidanceId(guidanceId);
        }
    }
    /*
        Call recognizer
    */
    public static void CallRecognizer() {
        int mode = SystemManager.GetPlayMode();
        int id = RecognizerManager.GetGuidanceId();
        // next process is initialization
        if (mProcessToExecute == READY) {
            mProcessToExecute = INITIALIZE;
        }
        if (id == GUIDANCE_MESSAGE_EMPTY) {
            RecognizerManager.SetGuidanceId(GUIDANCE_MESSAGE_TRANSITION);
        } else if (mode == MODE_ASSOCIATION) {
            RecognizerManager.SetGuidanceId(GUIDANCE_MESSAGE_SELECT_ASSOCIATION);
        }
    }
    /*
        Reset the count called the recognizer
    */
    public static void ResetCountCalledRecognizer() { mCurrentCountCalled = 0; }
    /**************************************************
        Each getter functions
    **************************************************/
    /*
         Get current count that called call method to recognize
     */
    public static int   GetCurrentCountCalledRecognizer() { return mCurrentCountCalled; }
    /**************************************************
        Each setter functions
     **************************************************/
}