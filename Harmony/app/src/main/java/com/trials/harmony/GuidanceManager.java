package com.trials.harmony;

import android.content.Context;
import android.view.MotionEvent;

/**
 * Created by USER on 4/18/2016.
 */
public class GuidanceManager implements HasScene, HasSystem, HasMusicInfo {
    // static variables
    // the guidance sentence for score.
    public final static String  GUIDANCE_SENTENCE_FOR_SCORE[] = {
            "Aggregate count is ",
            "Chain max is ",
            "Total point is "
    };
    private final static int    STARTING_GUIDANCE_WORDS_NOTHING = -1;
    // to notice the button at near.
    private final static String FINGER_NEARS_BY_THE_BUTTON[] = {
            "there is a button below",
            "there is a button left",
            "there is a button above",
            "there is a button right",
    };
    // the progress that notice the position
    private final static int    PROGRESS_NOTHING        = -1;
    private final static int    PROGRESS_NEAR_BY_BUTTON = 0;
    private final static int    PROGRESS_NOW_ON_BUTTON  = 1;
    // the fixed time that not to speak.
    private final static int FIXED_TIME_TO_STARTING = 30;
    private final static int FIXED_TIME_TO_INTERVAL_BETWEEN_EACH_GUIDANCE = 120;
    // filed
    private Guidance mGuidance;
    // the element that to do guide
    private int mNextGuidanceId;
    // the current guidance Id
    private int mCurrentGuidanceId;
    // to make the interval to update guidance
    private Utility mUtility;
    // to notice the presence of the button
    private int mNotificationInterval;
    // the fixed time that limit to count time to guide.
    private int mFixedIntervalTime;
    // words to guide
    private String  mGuidanceWords[];
    // the flag that is guiding user in the scene
    private static boolean mIsGuiding;
    // the progress that to notice the position
    private int     mNotificationProgress;
    // current scene
    private int     mCurrentScene;
    private String mPreviewWord;
    /*
        Constructor
    */
    public GuidanceManager(Context context, int scene) {
        // allot the memory
        this.mGuidance = new Guidance(context);
        this.mUtility = new Utility();
        this.mCurrentScene = scene;
        this.mPreviewWord = "";
    }
    /*
        Initialize starting guidance
    */
    public void InitStartingGuidance() {
        // to reset the interval
        if (this.mUtility != null) this.mUtility.ResetInterval();
        // to notice presence of button
        this.mNotificationInterval = 0;
        // the guidance id
        this.mNextGuidanceId = 0;
        // the current guidance Id
        this.mCurrentGuidanceId = STARTING_GUIDANCE_WORDS_NOTHING;
        // the fixed time to make the interval time to guide
        this.mFixedIntervalTime = FIXED_TIME_TO_STARTING;
        // the progress
        this.mNotificationProgress = PROGRESS_NOTHING;
        // to release the memory
        if (this.mGuidanceWords != null) this.mGuidanceWords = null;

        // To diverge the guidance from the current scene and user's experience.
        if (this.mCurrentScene == SCENE_PLAY) {
            this.mGuidanceWords = new String[1];
            this.mGuidanceWords[0] = "You are in the play scene";
        } else if (this.mCurrentScene == SCENE_RESULT) {
            this.mGuidanceWords = new String[1];
            this.mGuidanceWords[0] = "You are in the result scene";
        } else if (this.mCurrentScene == SCENE_CREDIT_VIEW) {
            int max = 1+EACH_CONTRIBUTOR.length;
            this.mGuidanceWords = new String[max];
            this.mGuidanceWords[0] = "Contributors are";
            System.arraycopy(EACH_CONTRIBUTOR,0,this.mGuidanceWords,1,EACH_CONTRIBUTOR.length);
            // In below scenes, to initialize the guidance words from text file.
        }
        // set sign that is guiding
        mIsGuiding = true;
    }
    /*
        When to need the guidance,
        To initialize this function.
    */
    public void InitGuidance(String words[]) {
        // to reset the interval
        if (this.mUtility != null) this.mUtility.ResetInterval();
        // to notice presence of button
        this.mNotificationInterval = 0;
        // the guidance id
        this.mNextGuidanceId = 0;
        // the current guidance Id
        this.mCurrentGuidanceId = STARTING_GUIDANCE_WORDS_NOTHING;
        // the fixed time to make the interval time to guide
        this.mFixedIntervalTime = FIXED_TIME_TO_STARTING;
        // to release the memory
        if (this.mGuidanceWords != null) this.mGuidanceWords = null;
        // to set the words to guide
        this.mGuidanceWords = new String[words.length];
        System.arraycopy(words,0,this.mGuidanceWords,0,words.length);
        // is guiding
        mIsGuiding = true;
        // the progress
        this.mNotificationProgress = PROGRESS_NOTHING;
    }
    /*
        Update to speak the words that initialized in InitGuidanceManager.
    */
    public boolean UpdateGuidanceManager() {
        // to check to update guidance,
        // when return value is false, not to update guidance.
        mIsGuiding = this.ToCheckToUpdateGuidance();
        // when to update to guide, to return
        if (this.mGuidance.GetSpeaking() || !mIsGuiding) return false;
        // to loop to the length of mGuidanceWords
        if (0 <= this.mNextGuidanceId && this.mNextGuidanceId < this.mGuidanceWords.length) {
            // to update guiding after fixed interval.
            if (this.mUtility.ToMakeTheInterval(this.mFixedIntervalTime)) {
                //  to initialize the words to guide and setting available to play.
                this.mGuidance.InitGuidance(this.mGuidanceWords[this.mNextGuidanceId]);
                // to get current guidance Id
                this.mCurrentGuidanceId = this.mNextGuidanceId;
                // when current id is one, to change the fixed interval.
                if (this.mNextGuidanceId == 0) this.mFixedIntervalTime = FIXED_TIME_TO_INTERVAL_BETWEEN_EACH_GUIDANCE;
                // id to next
                this.mNextGuidanceId++;
            }
            // when initialized available to guide ,
            // to update to guide by the id.
            this.mGuidance.UpdateGuidance();
        }
        // to check to update the guidance
        // when finish the guidance, to return value is false.
        return mIsGuiding;
    }
    /*
        To notice the button as pressed the button
    */
    public void IsPressedTheButton(String words) {
        if (this.mGuidance.GetSpeaking()) return;
        if (!this.mPreviewWord.equals(words)) {
            this.mPreviewWord = words;
            // when finish the guidance, to notice the button as pressed the button.
            if (!mIsGuiding) this.mNotificationInterval = 0;
        } else {
            // when finish the guidance, to notice the button as pressed the button.
            if (!mIsGuiding) {
                this.mNotificationInterval++;
                // to guide after fixed interval
                this.mNotificationInterval = (this.mFixedIntervalTime<this.mNotificationInterval)?0:this.mNotificationInterval;
            }
        }
        if (this.mNotificationInterval == 0) {
            // to set the progress
            this.mNotificationProgress = PROGRESS_NOW_ON_BUTTON;
            // to set the text to notice the button
            this.mGuidance.InitGuidance(words);
            // to update
            this.mGuidance.UpdateGuidance();
        }
    }
    /*
        To notice the position between finger and a button.
    */
    public void FingerNearsByTheButton(int direction) {
        // to return
        if (this.mGuidance.GetSpeaking() || direction == RecognitionButton.DISTANCE) return;
        // to reset the interval
        if (0 < this.mNotificationInterval) {
            // to get motion action
            int action = MainView.GetTouchAction();
            if (action == MotionEvent.ACTION_CANCEL ||
                    action == MotionEvent.ACTION_UP) this.mNotificationInterval = 0;
            // when preview process is notification that finger now on button
            // to reset the interval
            if (this.mNotificationProgress == PROGRESS_NOW_ON_BUTTON) {
                this.mNotificationInterval = 0;
            }
        }
        // when finish the guidance, to notice the button as pressed the button.
        if (!mIsGuiding) {
            // to make interval
            if (this.mNotificationInterval == 0) {
                // to set the progress
                this.mNotificationProgress = PROGRESS_NEAR_BY_BUTTON;
                // to set the text to notice the button
                this.mGuidance.InitGuidance(FINGER_NEARS_BY_THE_BUTTON[direction]);
                // to update
                this.mGuidance.UpdateGuidance();
            }
            this.mNotificationInterval++;
            if (this.mFixedIntervalTime < this.mNotificationInterval) {
                this.mNotificationInterval = 0;
            }
        }
    }
    /*
        Release
    */
    public void ReleaseGuidanceManager() {
        if (this.mGuidanceWords != null) this.mGuidanceWords = null;
        if (this.mGuidance != null) {
            this.mGuidance.ReleaseGuidance();
            this.mGuidance = null;
        }
        if (this.mUtility != null) {
            this.mUtility.ReleaseUtility();
            this.mUtility = null;
        }
    }
    /*
        To stop to guide
    */
    public void StopGuidanceManager() { this.mGuidance.StopGuidance(); }
    /*
        To check to finish the guidance
    */
    private boolean ToCheckToUpdateGuidance() {
        // to avoid NullException
        if (this.mGuidanceWords == null) return false;
        // to check to finish the guidance
        if (this.mCurrentGuidanceId == this.mGuidanceWords.length-1 && !this.mGuidance.GetSpeaking()) return false;
        // is updating the guidance process in the scene.
        return true;
    }
    /****************************************************
     Each getter functions
     ***************************************************/
    /*
        Get flag that is guiding
    */
    public static boolean GetIsGuiding() { return mIsGuiding; }
}