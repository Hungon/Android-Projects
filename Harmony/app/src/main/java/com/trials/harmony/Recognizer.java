package com.trials.harmony;


/**
 * Created by USER on 4/26/2016.
 */
public class Recognizer {

    // static variables
    // the duration to recognize
    private final static int DURATION_TO_UPDATE = 600;
    // the processes to execute recognizer
    public final static int READY      = 0;
    public final static int START      = 1;
    public final static int UPDATE     = 2;
    public final static int FINISH     = 3;
    // filed
    private Utility mUtility;
    // the fixed time that limit to count time to guide.
    private int mFixedIntervalTime;
    // the process to update recognition
    private int mRecognitionProcess;
    private int mCountMissed;

    /*
        Constructor
    */
    public Recognizer() {}
    /*************************************************
     Each methods are made for recognition scene.
     These are Initialization and Update.
     ************************************************/
    /*
        Initialize
    */
    public void InitRecognizer(int intervalTime) {
        // allot the memory
        if (this.mUtility == null) this.mUtility = new Utility();
        // to reset interval
        this.mUtility.ResetInterval();
        // the process that to execute recognizer
        this.mRecognitionProcess = READY;
        // the fixed time to make the interval time to guide
        this.mFixedIntervalTime = intervalTime;
        this.mCountMissed = 0;
    }
    /*
        Update
        return value is process status.
    */
    public int UpdateRecognizer() {
        // user may speak to execute the process.
        // to update the recognizer
        int recProcess = Harmony.GetRecognizerProgress();
        if (recProcess == Harmony.RECOGNIZER_READY_TO_LISTEN) {
            Harmony.SetRecognitionProgress(Harmony.RECOGNIZER_LISTENING);
            // set volume to down
            Sound.SetBGMVolume(0.1f,0.1f);
        }
        // when recognizer is listening, to update
        else if (recProcess == Harmony.RECOGNIZER_LISTENING) {
            // first impression
            if (this.mRecognitionProcess == READY) {
                this.mUtility.ResetInterval();
                this.mRecognitionProcess = START;
            }
            // to start the process that recognize the user's speech after the blank time.
            else if (this.mRecognitionProcess == START) {
                if (this.mUtility.ToMakeTheInterval(this.mFixedIntervalTime)) {
                    // when id of guidance is starting point, to begin to recognize.
                    Harmony.StartRecognition();
                    this.mRecognitionProcess = UPDATE;
                }
            }
            // to update to guide.
            // when not input voice after interval, to restart recognizer.
            else if (this.mRecognitionProcess == UPDATE) {
                // waiting for recognizer user's voice for about 5 seconds.
                if (this.mUtility.ToMakeTheInterval(DURATION_TO_UPDATE)) {
                    this.mCountMissed++;
                    this.mRecognitionProcess = READY;
                }
            }
        }
        // when to stop the speech, to get the id.
        else if (recProcess == Harmony.RECOGNIZER_STOP_SPEAK) {
            this.mRecognitionProcess = FINISH;
        }
        // when progress of recognizer is end or error, to finish the loop process
        else if (recProcess == Harmony.RECOGNIZER_ERROR ||
                recProcess == Harmony.RECOGNIZER_END) {
            this.mRecognitionProcess = FINISH;
        }
        return this.mRecognitionProcess;
    }
    /*
        Release
    */
    public void ReleaseRecognizer() {
        if (this.mUtility != null) this.mUtility = null;
        // to reset the progress
        Harmony.ResetProgress();
    }
    public int getCountMissed() { return this.mCountMissed; }
}