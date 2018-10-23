package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by USER on 4/29/2016.
 */
public class TransitionManager implements HasButtons, HasScene {
    // static variables
    // filed
    // ResponseManager class
    private ResponseManager             mResponseManager;
    // Guidance manager
    private GuidanceManager             mGuidanceManager;
    // Indication images
    private IndicationImages            mIndicationImages;
    // Leading manager
    private LeadingManager              mLeading;
    // Recognition button
    private RecognitionButtonManager    mButtonManager;
    // Utility
    private Utility                     mUtility;
    // the scene that to transition to next process in the scene
    private int     mNextSceneNumber;
    // the fixed interval
    private int     mFixedInterval;
    /*
        Constructor
    */
    public TransitionManager(Context context, Image image, int maxId) {
        // get play mode
        int mode = SystemManager.GetPlayMode();
        int scene = SceneManager.GetCurrentScene();
        // the default guidance id which is to transition to another scene to call the recognizer.
        RecognizerManager.SetGuidanceId(RecognitionMode.GUIDANCE_MESSAGE_TRANSITION);
        // ResponseManager class
        this.mResponseManager = new ResponseManager(mode,maxId);
        // Guidance manager
        this.mGuidanceManager = new GuidanceManager(context,scene);
        // Indication images
        if (scene != SCENE_PLAY) {
            this.mIndicationImages = new IndicationImages(context, image, scene);
        }
        // Leading manager
        if (scene != SCENE_CREDIT_VIEW && scene != SCENE_RESULT) {
            this.mLeading = new LeadingManager(context, image, scene);
        }
        // Utility
        this.mUtility = new Utility();
        // Button manager
        this.mButtonManager = new RecognitionButtonManager(context,image);
        // to initialize the max id to recognize
        RecognizerManager.SetMaxIdToRecognize(maxId);
    }
    /*
        Initialize
    */
    public void InitManager() {
        int experience = SystemManager.GetUserExperience();
        // to initialize the response class
        this.mResponseManager.InitResponseManager();
        // guidance manager
        this.mGuidanceManager.InitStartingGuidance();
        // Indication images
        if (this.mIndicationImages != null) this.mIndicationImages.InitImage();
        // Leading manager
        if (this.mLeading != null) this.mLeading.InitManager(experience);
        // Button manager
        this.mButtonManager.InitManager();
        // the scene that to transition to next process in the scene
        this.mNextSceneNumber = Scene.SCENE_MAIN;
        // the fixed interval
        this.mFixedInterval = 0;
    }
    /*
        Update
        return value is scene number.
    */
    public int UpdateManager(int buttonType, int direction, int sceneNumber, int fixedInterval) {
        // when other update method returns Scene_Release and not to set next scene,
        // to go to Release process after counted fixed interval.
        if (this.mNextSceneNumber == Scene.SCENE_MAIN && sceneNumber == Scene.SCENE_RELEASE) {
            this.mNextSceneNumber = sceneNumber;
            // to set fixed interval
            this.mFixedInterval = fixedInterval;
        }
        // to diverge to transit to next scene from current scene number.
        if (this.mNextSceneNumber == Scene.SCENE_MAIN) {
            int guidanceButtonType = this.mButtonManager.GetGuidanceButtonType();
            int guidanceMusicId = this.mButtonManager.GetIsSelectingMusicId();
            int guidanceDirectionType = this.mButtonManager.GetDirectionToNotice();
            // To update leading manager
            // when available to initialize guidance words,
            // to set the texts which is showing in the display.
            if (this.mLeading != null) {
                if (this.mLeading.UpdateManager(guidanceButtonType, guidanceMusicId)) {
                    this.mGuidanceManager.InitGuidance(this.mLeading.GetShowingText());
                    // already initialized guidance words
                    this.mLeading.IsAlreadyInitializedGuidanceWords();
                }
            }
            // to update the Response:
            // the return value will back next process which are initialization of guidance or
            // transition of scene.
            int response = this.mResponseManager.UpdateResponseManager();
            // initialize words to notice the response
            if (response == Response.INITIALIZE_GUIDANCE) {
                this.mGuidanceManager.InitGuidance(this.mResponseManager.GetResponseWords());
                // initialize the next scene
            } else if (response == Response.TRANSITION_TO_NEXT_SCENE) {
                this.mNextSceneNumber = Scene.SCENE_RELEASE;
            }
            // to check to call recognizer.
            // when calling recognizer, not to guide
            if (this.mNextSceneNumber == Scene.SCENE_MAIN) {
                if (!this.mResponseManager.IsCallingRecognizer()) {
                    // update button
                    this.mNextSceneNumber = this.mButtonManager.UpdateManager();
                    // update to guide
                    // when not to guide, to update other notification.
                    if (!this.mGuidanceManager.UpdateGuidanceManager()) {
                        // when not to get the type, to get the guidance type
                        buttonType = (buttonType == BUTTON_EMPTY)?guidanceButtonType:buttonType;
                        direction = (direction == RecognitionButton.DISTANCE)?guidanceDirectionType:direction;
                        // insert sentence
                        String word = Insert.insertSentenceForButtonType(buttonType);
                        if (buttonType == BUTTON_TUNE_NUMBER) word += Integer.toString(this.mButtonManager.GetIsSelectingMusicId()+1);
                        this.mGuidanceManager.IsPressedTheButton(word);
                        // to notice the position between finger and a button.
                        this.mGuidanceManager.FingerNearsByTheButton(direction);
                    }
                }
            }
        }
        // Eventually, when next process is release,
        // to create wipe and to transition to next scene.
        if (this.mNextSceneNumber == Scene.SCENE_RELEASE) {
            // when to set the fixed interval, to make the interval
            if (this.mUtility.ToMakeTheInterval(this.mFixedInterval)) {
                // to force to stop the voice's processing
                this.mGuidanceManager.StopGuidanceManager();
                return Scene.SCENE_RELEASE;
            }
        }

        // Update each title image in the current scene.
        if (this.mIndicationImages != null) this.mIndicationImages.UpdateImageAlpha();

        return Scene.SCENE_MAIN;
    }
    /*
        Draw
    */
    public void DrawManager() {
        // Indication images
        if (this.mIndicationImages != null) this.mIndicationImages.DrawImage();
        // the button to call task table
        this.mButtonManager.DrawManager();
        // texts
        if (this.mLeading != null) this.mLeading.DrawManager();
    }
    /*
        Release
    */
    public void ReleaseManager() {
        // ResponseManager class
        if (this.mResponseManager != null) {
            this.mResponseManager.ReleaseResponseManager();
            this.mResponseManager = null;
        }
        // Guidance manager
        if (this.mGuidanceManager != null) {
            this.mGuidanceManager.StopGuidanceManager();
            this.mGuidanceManager.ReleaseGuidanceManager();
            this.mGuidanceManager = null;
        }
        // Indication images
        if (this.mIndicationImages != null) {
            this.mIndicationImages.ReleaseImage();
            this.mIndicationImages = null;
        }
        // Leading manager
        if (this.mLeading != null) {
            this.mLeading.ReleaseManager();
            this.mLeading = null;
        }
        // Button manager
        if (this.mButtonManager != null) {
            this.mButtonManager.ReleaseManager();
            this.mButtonManager = null;
        }
    }
}